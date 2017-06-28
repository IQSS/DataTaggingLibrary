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
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
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
import java.util.logging.Logger;
import org.codehaus.jparsec.error.ParserException;

/**
 * The resultant AST from parsing decision graph code. Can create an actual decision
 * graph, when provided with a tag space (i.e a @{link CompoundType} instance).
 *
 * @author michael
 */
public class DecisionGraphCompiler {
     
    EndNode endAll = new EndNode("[SYN-END]");
    
    /**
     * Maps a name of a slot to its fully qualified version (i.e from the top
     * type). For fully qualified names this is an identity function.
     */
    final Map<List<String>, List<String>> fullyQualifiedSlotName = new HashMap<>();

    private CompoundSlot topLevelType;

    private final Map<String, CompilationUnit> pathToCU = new HashMap<>();
    
    private final Map<String, CompilationUnit> nameToCU = new HashMap<>();

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

        List<AstImport> needToVisit = new ArrayList();
        CompilationUnit firstCU = new CompilationUnit(modelData.getDecisionGraphPath());
        try {
            firstCU.compile(fullyQualifiedSlotName, topLevelType, endAll, astValidators);
            needToVisit.addAll(firstCU.getParsedFile().getImports());
            pathToCU.put( modelData.getDecisionGraphPath().toString(), firstCU );
        } catch (DataTagsParseException ex) {
            messages.add(new ValidationMessage(Level.ERROR, "Error parsing decision graph code at main file " + ex.getMessage()));
        }
        
        while (! needToVisit.isEmpty()){
            AstImport astImport = needToVisit.remove(0);
            if (! pathToCU.containsKey(astImport.getPath())){
                CompilationUnit compilationUnit = new CompilationUnit(modelData.getDecisionGraphPath().resolveSibling(astImport.getPath()));
                try {
                    compilationUnit.compile(fullyQualifiedSlotName, topLevelType, endAll, astValidators);
                    needToVisit.addAll(compilationUnit.getParsedFile().getImports());
                    pathToCU.put(astImport.getPath(), compilationUnit );
                } catch (DataTagsParseException ex) {
                    messages.add(new ValidationMessage(Level.ERROR, "Error parsing decision graph code at file - " + astImport.getPath() + ":" +  ex.getMessage()));
                }

            }
        }
        
        //Static Linking Pass
        Map<String, Map<Node,String>> nodeIdToNodeAndCU = new HashMap<>();

        for (String path: pathToCU.keySet()){
            CompilationUnit cu = pathToCU.get(path);
            List<AstImport> imports = cu.getParsedFile().getImports();
            Map<String, String> callToCallee = cu.getCallToCalleeID();
            String calleeCuPath = null;
            Boolean foundCalleeCU = false;
            for(String call: callToCallee.keySet()){
                if ( callToCallee.get(call).contains(">") ) { //If the callee is from another compilation unit
                    String calleeCuName = callToCallee.get(call).split(">")[0]; //Take the cu in cu>id from callee
                    String calleeName = callToCallee.get(call).split(">")[1];
                    for (AstImport ai: imports){
                        if (ai.getName().equals(calleeCuName)){
                            calleeCuPath = ai.getPath();
                            foundCalleeCU = true;
                            nameToCU.put(ai.getName(),pathToCU.get(calleeCuPath));
                            break;
                        }
                    }
                    if ( foundCalleeCU ) {
                        CompilationUnit calleeCU = pathToCU.get(calleeCuPath);
                        Node calleeNode = calleeCU.getDecisionGraph().getNode(calleeName);
                        if(calleeNode != null){
                            CallNode callNode = (CallNode) cu.getDecisionGraph().getNode(call);
                            callNode.setCalleeNode(calleeNode);
                            if(nodeIdToNodeAndCU.containsKey(calleeName)){
                                Map<Node,String> nodeNcu = nodeIdToNodeAndCU.get(calleeName);
                                nodeNcu.put(calleeNode, calleeCuName);
                                nodeIdToNodeAndCU.put(calleeName, nodeNcu);
                                for(Node innerNode: calleeCU.getDecisionGraph().nodes()){
                                    Map<Node,String> innernNodeNcu = nodeIdToNodeAndCU.get(innerNode.getId());
                                    innernNodeNcu.put(innerNode, calleeCuName);
                                    nodeIdToNodeAndCU.put(innerNode.getId(), innernNodeNcu);
                                }
                            } else {
                                Map<Node,String> nodeNcu = new HashMap<>();
                                nodeNcu.put(calleeNode, calleeCuName);
                                nodeIdToNodeAndCU.put(calleeName, nodeNcu);
                                for ( Node innerNode: calleeCU.getDecisionGraph().nodes() ) {
                                    Map<Node,String> innernNodeNcu = new HashMap<>();
                                    innernNodeNcu.put(innerNode, calleeCuName);
                                    nodeIdToNodeAndCU.put(innerNode.getId(), innernNodeNcu);
                                }
                            }
                        } else {
                            messages.add(new ValidationMessage(Level.ERROR, "cannot find target node with id " + calleeCuName + ">" +  calleeName));
                        }
                    } else {
                        messages.add(new ValidationMessage(Level.ERROR, "cannot find target file with id " + calleeCuName));
                    }
                } else {
                    Node calleeNode = cu.getDecisionGraph().getNode( callToCallee.get(call) );
                    CallNode callNode = (CallNode) cu.getDecisionGraph().getNode(call);
                    callNode.setCalleeNode(calleeNode);
                }
                foundCalleeCU = false;
            }
        }
        
        // Change IDs
        for(String nodeID: nodeIdToNodeAndCU.keySet()){
            Map<Node,String> nodes = nodeIdToNodeAndCU.get(nodeID);
            for (Node node: nodes.keySet()){
                String cuName = nodes.get(node);
                node.setId(cuName + ">" + node.getId());
            }
        }
        if (pathToCU.get(modelData.getDecisionGraphPath().toString()) != null){
            DecisionGraph dg = pathToCU.get(modelData.getDecisionGraphPath().toString()).getDecisionGraph();
            for (String nodeID: nodeIdToNodeAndCU.keySet()){
                for(Node node: nodeIdToNodeAndCU.get(nodeID).keySet()){
                    Node nodeWithoutCU = dg.getNode((node.getId().split(">")[1]));
                    if(node.equals(nodeWithoutCU)){
                        dg.remove(nodeWithoutCU);
                    }
                    dg.add(node);

                }
            }
            modelData.addCompilationUnitMapping(nameToCU);
            return pathToCU.get(modelData.getDecisionGraphPath().toString()).getDecisionGraph();
        }
        else{
            return null;
        }
    }

    public CompilationUnit put(String key, CompilationUnit value) {
        return pathToCU.put(key, value);
    }
    
    public DecisionGraph linkage() throws DataTagsParseException, IOException {
        
        for (String path: pathToCU.keySet()){
            CompilationUnit cu = pathToCU.get(path);
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
                        CompilationUnit calleeCU = pathToCU.get(calleeCuPath);
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
        
        
        return pathToCU.get("main path").getDecisionGraph();
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
