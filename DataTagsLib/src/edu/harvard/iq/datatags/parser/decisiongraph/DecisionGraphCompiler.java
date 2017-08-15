package edu.harvard.iq.datatags.parser.decisiongraph;

import static edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstImport;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.tools.DecisionGraphAstValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.io.IOException;
import java.nio.file.Path;
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
     * Id of the synthetic [end] node added to all compiled graphs.
     */
    public static final String SYNTHETIC_END_NODE_ID = "[SYN-END]";
    
    /**
     * "Miranda node" - added to nodes that do not have their own end node.
     */
    EndNode endAll = new EndNode(SYNTHETIC_END_NODE_ID);

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
    private final ContentReader contentReader;
    private final List<ValidationMessage> messages = new ArrayList<>();
    
    public DecisionGraphCompiler(){
        this(new StringContentReader());
    }
    
    public DecisionGraphCompiler(ContentReader aContentReader){
        contentReader = aContentReader;
    }
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
        CompilationUnit firstCU = new CompilationUnit(contentReader.getContent(modelData.getDecisionGraphPath()) ,modelData.getDecisionGraphPath());
        try {
            firstCU.compile(fullyQualifiedSlotName, topLevelType, endAll, astValidators);
            String prefixNodes = modelData.getModelDirectoryPath().relativize(firstCU.getSourcePath()).toString();
            firstCU.getDecisionGraph().prefixNodeIds("[" + prefixNodes + "]");
            messages.addAll(firstCU.getValidationMessages());
            needToVisit.addAll(firstCU.getParsedFile().getImports());
            pathToCu.put( modelData.getDecisionGraphPath().normalize().toString(), firstCU );
            nameToCu.put(MAIN_CU_ID, firstCU);
            firstCU.getDecisionGraph().nodes().forEach(n->nodeToCu.put(n,firstCU));
            
        } catch (DataTagsParseException ex) {
            messages.add(new ValidationMessage(Level.ERROR, "Error parsing decision graph code at main file " + ex.getMessage()));
        }
        
        // Load and compile all compilation units (BFS over CU's imports)
        String mainPath = modelData.getDecisionGraphPath().normalize().toString();
        while ( !needToVisit.isEmpty() ) {
            AstImport astImport = needToVisit.remove(0);
            String currentFilePath = getRealPath(astImport.getPath(), astImport.getInitialPath()).toString();
            if ( !pathToCu.containsKey(astImport.getPath().toString()) && (!mainPath.equals(currentFilePath))) {
                String content = contentReader.getContent(getRealPath(astImport.getPath(), astImport.getInitialPath()));
                CompilationUnit compilationUnit = new CompilationUnit(content, getRealPath(astImport.getPath(), astImport.getInitialPath()));
                try {
                    compilationUnit.compile(fullyQualifiedSlotName, topLevelType, endAll, astValidators);
                    String prefixNodes = modelData.getModelDirectoryPath().relativize(compilationUnit.getSourcePath()).toString();
                    compilationUnit.getDecisionGraph().prefixNodeIds("[" + prefixNodes + "]");
                    messages.addAll(compilationUnit.getValidationMessages());
                    compilationUnit.getDecisionGraph().nodes().forEach(n->nodeToCu.put(n,compilationUnit));
                    needToVisit.addAll(compilationUnit.getParsedFile().getImports());
                    
                    pathToCu.put(getRealPath(astImport.getPath(), astImport.getInitialPath()).toString(), compilationUnit );
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
                    Path destinationCUPath = cu.getParsedFile().getImportPath(calleeCuName);
                    CompilationUnit calleeCU = pathToCu.get(getRealPath(destinationCUPath, cu.getSourcePath()).toString());
                    if ( calleeCU != null ) {
                        String prefixNodes = modelData.getModelDirectoryPath().relativize(calleeCU.getSourcePath()).toString();
                        Node calleeNode = calleeCU.getDecisionGraph().getNode("[" + prefixNodes + "]" + calleeName);
                        nameToCu.put(calleeCuName, calleeCU);
                        if ( calleeNode != null ) {
                            prefixNodes = modelData.getModelDirectoryPath().relativize(cu.getSourcePath()).toString();
                            CallNode callNode = (CallNode) cu.getDecisionGraph().getNode("[" + prefixNodes + "]" + callCalleePair.getKey());
                            callNode.setCalleeNode(calleeNode);
                        } else {
                            messages.add(new ValidationMessage(Level.ERROR, "cannot find target node with id " + calleeCuName + ">" +  calleeName));
                        }
                    } else {
                        messages.add(new ValidationMessage(Level.ERROR, "cannot find target file with id " + calleeCuName));
                    }
                    
                } else {
                    // link to node within same compilation unit
                    String prefixNodes = modelData.getModelDirectoryPath().relativize(cu.getSourcePath()).toString();
                    Node calleeNode = cu.getDecisionGraph().getNode( "[" + prefixNodes + "]" + callCalleePair.getValue() );
                    CallNode callNode = (CallNode) cu.getDecisionGraph().getNode("[" + prefixNodes + "]" + callCalleePair.getKey());
                    callNode.setCalleeNode(calleeNode);
                }
            }
        }
       
        
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
    
    private Path getRealPath(Path path, Path parentPath){
        return (parentPath.resolveSibling(path).normalize());
    }

}
