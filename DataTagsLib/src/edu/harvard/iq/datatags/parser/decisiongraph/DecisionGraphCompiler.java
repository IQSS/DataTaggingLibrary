package edu.harvard.iq.datatags.parser.decisiongraph;

import static edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstImport;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.DecisionGraphAstValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The resultant AST from parsing decision graph code. Can create an actual decision
 * graph, when provided with a tag space (i.e a @{link CompoundType} instance).
 *
 * @author michael
 */
public class DecisionGraphCompiler {
    
    /** ID of the main compilation unit. */
    public static final String MAIN_CU_ID = "<main>";
    
    /**
     * "Miranda node" - added to nodes that do not have their own end node.
     */
    EndNode endAll = new EndNode("[SYN-END]");

    /**
     * Maps a name of a slot to its fully qualified version (i.e from the top
     * type). For fully qualified names this is an identity function.
     */
    final Map<List<String>, List<String>> fullyQualifiedSlotName = new HashMap<>();

    private CompoundSlot topLevelType;
    
    /**
     * Maps a path to the compilation unit it contains.
     */
    private final Map<String, CompilationUnit> pathToCu = new HashMap<>();
    
    /**
     * Maps a name to a compilation unit. The names are unique, which means that an 
     * imported compilation may be renamed in the compiled graph.
     */
    private final Map<String, CompilationUnit> nameToCu = new HashMap<>();

    private final List<ValidationMessage> messages = new ArrayList<>();

    /**
     * Creates a ready-to-run {@link DecisionGraph} from the parsed nodes and
     * the tagspace.
     *
     * @param tagSpace The tag space used in the graph.
     * @param modelData
     * @param astValidators
     * @return A ready-to-run graph.
     * @throws java.io.IOException
     */
    public DecisionGraph compile(CompoundSlot tagSpace, PolicyModelData modelData,
                                 List<DecisionGraphAstValidator> astValidators) throws IOException {
        
        buildTypeIndex(tagSpace);
        
        // Maps a node to the compliation unit it originally came form
        Map<Node, CompilationUnit> nodeToCu = new HashMap<>();
        
        List<AstImport> needToVisit = new ArrayList();
        CompilationUnit firstCU = new CompilationUnit(modelData.getDecisionGraphPath());
        try {
            firstCU.compile(fullyQualifiedSlotName, topLevelType, endAll, astValidators);
            needToVisit.addAll(firstCU.getParsedFile().getImports());
            pathToCu.put( modelData.getDecisionGraphPath().toString(), firstCU );
            nameToCu.put(MAIN_CU_ID, firstCU);
            firstCU.getDecisionGraph().nodes().forEach(n->nodeToCu.put(n,firstCU));
            
        } catch (DataTagsParseException ex) {
            messages.add(new ValidationMessage(Level.ERROR, "Error parsing decision graph code at main file " + ex.getMessage()));
        }
        
        // Load and compile all compilation units (BFS over CU's imports)
        while ( !needToVisit.isEmpty() ) {
            AstImport astImport = needToVisit.remove(0);
            if ( !pathToCu.containsKey(astImport.getPath()) ) {
                CompilationUnit compilationUnit = new CompilationUnit(modelData.getDecisionGraphPath().resolveSibling(astImport.getPath()));
                try {
                    compilationUnit.compile(fullyQualifiedSlotName, topLevelType, endAll, astValidators);
                    compilationUnit.getDecisionGraph().nodes().forEach(n->nodeToCu.put(n,compilationUnit));
                    needToVisit.addAll(compilationUnit.getParsedFile().getImports());
                    
                    pathToCu.put(astImport.getPath(), compilationUnit );
                    String cuName = astImport.getName();
                    int i=0;
                    while ( nameToCu.containsKey(cuName) ) {
                        cuName = astImport.getName() + "-" + (++i);
                    }
                    nameToCu.put(cuName, compilationUnit);
                    
                } catch (DataTagsParseException ex) {
                    messages.add(new ValidationMessage(Level.ERROR, "Error parsing decision graph code at file - " + astImport.getPath() + ":" +  ex.getMessage()));
                }
            }
        }
        
        // Static Linking Pass
        for ( CompilationUnit cu: pathToCu.values() ){
            Map<String, String> callToCallee = cu.getCallToCalleeID();
            for ( Map.Entry<String,String> callCalleePair: callToCallee.entrySet()) {
                String calleeId = callCalleePair.getValue(); 
                if ( calleeId.contains(">") ) { 
                    //If the callee is from another compilation unit
                    String[] comps = calleeId.split(">");
                    String calleeCuName = comps[0]; //Take the cu in cu>id from callee
                    String calleeName = comps[1];
                    String destinationCUPath = cu.getParsedFile().getImportPath(calleeCuName);
                    CompilationUnit calleeCU = pathToCu.get(destinationCUPath);
                    if ( calleeCU != null ) {
                        Node calleeNode = calleeCU.getDecisionGraph().getNode(calleeName);
                        nameToCu.put(calleeCuName, calleeCU);
                        if ( calleeNode != null ) {
                            CallNode callNode = (CallNode) cu.getDecisionGraph().getNode(callCalleePair.getKey());
                            callNode.setCalleeNode(calleeNode);
                        } else {
                            messages.add(new ValidationMessage(Level.ERROR, "cannot find target node with id " + calleeCuName + ">" +  calleeName));
                        }
                    } else {
                        messages.add(new ValidationMessage(Level.ERROR, "cannot find target file with id " + calleeCuName));
                    }
                    
                } else {
                    // link to node within same compilation unit
                    Node calleeNode = cu.getDecisionGraph().getNode( callCalleePair.getValue() );
                    CallNode callNode = (CallNode) cu.getDecisionGraph().getNode(callCalleePair.getKey());
                    callNode.setCalleeNode(calleeNode);
                }
            }
        }
        
        // Change IDs by (re)named CU.
        nameToCu.entrySet().forEach( ent -> {
            String name = ent.getKey();
            if ( ! name.equals(MAIN_CU_ID)) {
                CompilationUnit cu = ent.getValue();
                cu.getDecisionGraph().prefixNodeIds(ent.getKey()+">");
            }
        });
        
        CompilationUnit mainCu = nameToCu.get(MAIN_CU_ID);
        if ( mainCu != null) {
            mainCu.getDecisionGraph().addAllReachableNodes();
            modelData.addCompilationUnitMapping(nameToCu);
            return mainCu.getDecisionGraph();
            
        } else {
            return null;
        }
    }

    public CompilationUnit put(String key, CompilationUnit value) {
        return pathToCu.put(key, value);
    }
    
    public DecisionGraph linkage() throws DataTagsParseException, IOException {
        
        for (String path: pathToCu.keySet()){
            CompilationUnit cu = pathToCu.get(path);
            List<AstImport> imports = cu.getParsedFile().getImports();
            Map<String, String> callToCallee = cu.getCallToCalleeID();
            String calleeCuPath = null;
            Boolean foundCalleeCU = false;
            for(String call: callToCallee.keySet()){
                if (callToCallee.get(call).contains(">")){ //If the callee is from another compilation unit
                    String calleeCuName = callToCallee.get(call).split(">")[0]; //Take the cu in cu>id from callee
                    String calleeName = callToCallee.get(call).split(">")[1];
                    for (AstImport ai: imports){
                        if (ai.getName().equals(calleeCuName)){
                            calleeCuPath = ai.getPath();
                            foundCalleeCU = true;
                            break;
                        }
                    }
                    if (foundCalleeCU){
                        CompilationUnit calleeCU = pathToCu.get(calleeCuPath);
                        Node calleeNode = calleeCU.getDecisionGraph().getNode(calleeName);
                        if(calleeNode != null){
                            CallNode callNode = (CallNode) cu.getDecisionGraph().getNode(call);
                            callNode.setCalleeNode(calleeNode);
                        }
                        else{
                            messages.add(new ValidationMessage(Level.ERROR, "cannot find target node with id " + calleeCuName + ">" +  calleeName));
                        }
                    }
                    else{
                        messages.add(new ValidationMessage(Level.ERROR, "cannot find target file with id " + calleeCuName));

                    }
                }
                else{
                    Node calleeNode = cu.getDecisionGraph().getNode( callToCallee.get(call) );
                    CallNode callNode = (CallNode) cu.getDecisionGraph().getNode(call);
                    callNode.setCalleeNode(calleeNode);
                }
                foundCalleeCU = false;
            }
        }
        
        
        return pathToCu.get("main path").getDecisionGraph();
    }
    
    /**
     * Maps all unique slot suffixes to their fully qualified version. That is,
     * if we have:
     *
     * <code><pre>
     *  top/mid/a
     *  top/mid/b
     *  top/mid2/b
     * </pre></code>
     *
     * We end up with:      <code><pre>
     *  top/mid/a  => top/mid/a
     *  mid/a      => top/mid/a
     *  a          => top/mid/a
     *  top/mid/b  => top/mid/b
     *  mid/b      => top/mid/b
     *  top/mid2/b => top/mid2/b
     *  mid2/b     => top/mid2/b
     * </pre></code>
     *
     * @param topLevel the top level type to build from.
     */
    Map<List<String>, List<String>> buildTypeIndex(CompoundSlot topLevel) {
        topLevelType = topLevel;

        List<List<String>> fullyQualifiedNames = new LinkedList<>();
        // initial index
        topLevelType.accept(new SlotType.VoidVisitor() {
            LinkedList<String> stack = new LinkedList<>();

            @Override
            public void visitAtomicSlotImpl(AtomicSlot t) {
                addType(t);
            }

            @Override
            public void visitAggregateSlotImpl(AggregateSlot t) {
                addType(t);
            }

            @Override
            public void visitTodoSlotImpl(ToDoSlot t) {
                addType(t);
            }

            @Override
            public void visitCompoundSlotImpl(CompoundSlot t) {
                stack.push(t.getName());
                t.getFieldTypes().forEach(tt -> tt.accept(this));
                stack.pop();
            }

            void addType(SlotType tt) {
                stack.push(tt.getName());
                fullyQualifiedNames.add(C.reverse((List) stack));
                stack.pop();
            }
        });

        fullyQualifiedNames.forEach(n -> fullyQualifiedSlotName.put(n, n));

        // add abbreviations
        Set<List<String>> ambiguous = new HashSet<>();
        Map<List<String>, List<String>> newEntries = new HashMap<>();

        fullyQualifiedNames.forEach(slot -> {
            List<String> cur = C.tail(slot);
            while (!cur.isEmpty()) {
                if (fullyQualifiedSlotName.containsKey(cur) || newEntries.containsKey(cur)) {
                    ambiguous.add(cur);
                    break;
                } else {
                    newEntries.put(cur, slot);
                }
                cur = C.tail(cur);
            }
        });

        ambiguous.forEach(newEntries::remove);
        fullyQualifiedSlotName.putAll(newEntries);
        return fullyQualifiedSlotName;
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }

}
