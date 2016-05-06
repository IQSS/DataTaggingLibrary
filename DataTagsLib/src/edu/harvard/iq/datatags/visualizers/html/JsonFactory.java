package edu.harvard.iq.datatags.visualizers.html;


import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * Given a {@link DecisionGraph} and a {@link TagType} of the tag space 
 * top level, instances of this class create json files 
 * for the use of an html visualizing application of the decision graph and tag
 * space.
 
 * @author Yonatan Tzulang
 */
public class JsonFactory {
    
    static String version ="1.0.0";
    
    /**
         creates a json version of the decision grpah and tag space
         the json file have the following: scheme:
          {
              title:                  string - the name of the original file
              decisionGraph:          object - the parsed decision graph
              tagSpace:               object - the parsed tag space  
              data_tag_json_version:  string - the version of the json parser
           }
           
          
        @param topLevel {@link TagType} tag space top level Tag
        @param dg {@link DecisionGraph} 
        @return a {@link JSONObject} of the given data
    */
    public static JSONObject toJson(TagType topLevel, DecisionGraph dg){
        
        JSONObject tagSpace= tagSaceToJSON(topLevel);
        JSONObject decisionGraph = decisionGraphToJSON(dg);
        
        JSONObject res=new JSONObject();
        res.put ("title" ,humanTitle(dg));
        res.put ("decisionGraph", decisionGraph);
        res.put ("tagSpace", tagSpace);
        res.put ("data_tag_json_version", version);
        return res;
        
    }
    
    
    // same as the above function . used to parse only the parse
    // only the tag space . used for testing
    public static JSONObject toJson(TagType topLevel){
        return tagSaceToJSON(topLevel);
    };
    
    
    
    
    
    /**   
        @param dg {@link DecisionGraph}  
        @return a {@link JSONObject} of the given data
    */
    public static JSONObject toJson( DecisionGraph dg){
        JSONObject decisionGraph = decisionGraphToJSON(dg);
        JSONObject res=new JSONObject();
        res.put ("title" ,humanTitle(dg));
        res.put ("decisionGraph", decisionGraph);
        return res;
    }; 
    
    
    
    
    //put a value in a JSONOBJECT affter some sanitaion actions
    private static void sanitizedPut(JSONObject obj ,String key, String value, boolean checkExsit){
        
        if (!checkExsit || (value !=null && value.length()>0))
            obj.put(key, value.trim());
    }
    
    
    
    
    
    // parse String collection to JSON Array
    private static JSONArray parseStringCollection ( Collection c ) {
        
        JSONArray jArr= new JSONArray();
        jArr.addAll(c);
        return jArr;
    };
   
    
    
    
    /**
        Parse the tag space into a json object.
    
        the tag space object have nested tree form 
        of compund slots and  slots , where the leaves
        are the value objects of the slots
    
        the method creates a  TagType.Visitor implementation to
        traverse the tag space. to add a new slot JSON parser 
        one should implement the new slot visitor
    
        @param topLevel {@link TagType} tag space top level Tag
        @return a {@link JSONObject} of the given tagSpace
    */
    protected static JSONObject tagSaceToJSON(TagType topLevel)  {
        
        TagType.Visitor typeStringify = new TagType.Visitor<JSONObject>(){
            @Override
            public JSONObject visitSimpleType(AtomicType t) {
                
                JSONObject obj=new JSONObject();
                JSONArray values = new JSONArray();
                              
                for (AtomicValue value: t.values()){
                    JSONObject JsonValue= new JSONObject();
                    
                    sanitizedPut(JsonValue, "note", value.getNote(),true);
                    JsonValue.put("ordinal", value.getOrdinal());
                    sanitizedPut(JsonValue, "name", value.getName(),false);
                    values.add(JsonValue);
                    
                }
                obj.put("values", values);
                sanitizedPut(obj, "note", t.getNote(),true);
                sanitizedPut(obj, "name", t.getName(),false);
                obj.put("type","AtomicType");
                return obj;
            }
            
            @Override
            public JSONObject visitAggregateType(AggregateType t) {
                JSONObject obj=new JSONObject();
                JSONArray values = new JSONArray();
                
                for (AtomicValue value: t.getItemType().values()) {
                    JSONObject JsonValue= new JSONObject();
                    
                    sanitizedPut(JsonValue, "note", value.getNote(),true);
                    JsonValue.put("ordinal", value.getOrdinal());
                    sanitizedPut(JsonValue, "name", value.getName(),false);
                    values.add(JsonValue);
                }
                obj.put("values", values);
                sanitizedPut(obj, "note", t.getNote(),true);
                sanitizedPut(obj, "name", t.getName(),false);
                obj.put("type","AggregateType");
                return obj;
            }
            
            @Override
            public JSONObject visitCompoundType(CompoundType t) {
                JSONObject obj=new JSONObject();
                JSONArray values = new JSONArray();
                
                for (TagType subTag : t.getFieldTypes() ){
                    values.add( subTag.accept(this));
                }
                
                obj.put("fieldTypes", values);
                sanitizedPut(obj, "note", t.getNote(),true);
                sanitizedPut(obj, "name", t.getName(),false);
                obj.put("type","CompoundType");
                return obj;
            }
            
            @Override
            public JSONObject visitTodoType(ToDoType t) {
                JSONObject obj=new JSONObject();
                
                sanitizedPut(obj, "note", t.getNote(),true);
                sanitizedPut(obj, "name", t.getName(),false);
                obj.put("type","ToDoType");
                
                return obj;
            }
            
            
        };
        
        
        JSONObject res=(JSONObject) topLevel.accept(typeStringify);
        return res;
    }
    
    
    
    
    /**
        Parse the decision graph into a json object.
       
        The decisionGraph json have the following scheme:
            {
                $startNode: the id of the start node
                cluster1: object,
                cluster2: object,
                ...
                clusterN: object
            }
         
        Each cluster is an object that represent an isolated sub-graph 
        of the decisicon graph.
            
            cluster :
                {
                    nodes: an array of the node objects
                    edges: an array of the conecting edges
                }
    
        the method creates a  Node.VoidVisitor implementation to
        traverse the decisicon graph. to add a new node JSON parser 
        one should  implement the new node visitor
    
        @param fc  {@link DecisionGraph}
        @return a {@link JSONObject} of the given decision graph
    */
    protected static JSONObject decisionGraphToJSON(DecisionGraph fc){
        
        
        class DecisionGraphStringify extends  Node.VoidVisitor {
            
            JSONArray   nodes = new JSONArray(),
                        edges = new JSONArray();
            Set<String> visitedIds = new HashSet();
//            Set<Node> targets = new HashSet<>();
            
            private boolean visited(Node node){
                if (visitedIds.contains(node.getId()))
                    return true;
                else {
                    visitedIds.add(node.getId());
                    return false;
                }
            }
            
            public void reset() {
                nodes.clear();
                edges.clear();
            }
            
            // create an edge element and add it to the list of edges.
            // continue travesring the Graph to the next node;
            private void createEdge (Node from , Node to){
                if (to == null || from == null)
                    return;
                JSONObject edge =new JSONObject();
                edge.put ("source", from.getId());
                edge.put ("target", to.getId());
                edges.add(edge);
                to.accept(this);
            }
            
            @Override
            public void visitImpl(AskNode node) throws DataTagsRuntimeException {
                
                if (!visited(node))
                {
                    JSONObject obj   = new  JSONObject();
                    JSONArray  terms = new JSONArray();
                    JSONArray  answers = new JSONArray();
                    
                    // parse terms to a JSON array
                    for (String termName : node.getTermNames()) {
                        JSONObject termObj= new JSONObject();
                        sanitizedPut(termObj,"explanation", node.getTermText(termName),false);
                        sanitizedPut(termObj,"term", termName ,false);
                        terms.add(termObj);
                    }
                    
                    
                    // parse answers and sub-graphs to a JSON array
                    for (Answer answer: node.getAnswers()){
                        JSONObject answerObj= new JSONObject();
                        Node answerNode =node.getNodeFor(answer);
                        sanitizedPut(answerObj,"text", answer.getAnswerText(),false);
                        answerObj.put("answer_sub_graph_id", answerNode.getId());
                        answers.add(answerObj);
                        createEdge(node, answerNode);
                    }
                    
                    obj.put("type", "AskNode");
                    obj.put("terms", terms);
                    sanitizedPut(obj,"id", node.getId(), false);
                    obj.put("question" , node.getText());
                    obj.put("answers", answers);
                    nodes.add(obj);
                    
                }
            }
            
            @Override
            public void visitImpl(ConsiderNode node) throws DataTagsRuntimeException {
                
                if (!visited(node))
                {
                    JSONObject obj   = new  JSONObject();
                    JSONArray  answers = new JSONArray();
                    
                    // parse answers and sub-graphs to a JSON array
                    for (ConsiderAnswer answer: node.getAnswers()){
                        JSONObject answerObj= new JSONObject();
                        Node answerNode = node.getNodeFor(answer);
                        sanitizedPut(answerObj,"text", answer.getAnswerText(),false);
                        answerObj.put("answer_sub_graph_id", answerNode.getId());
                        answers.add(answerObj);
                        createEdge(node, answerNode);
                    }
                    if ( node.getElseNode()!=null ) {
                        JSONObject answerObj= new JSONObject();
                        Node elseNode = node.getElseNode();
                        sanitizedPut(answerObj,"text", "else",false);
                        answerObj.put("answer_sub_graph_id", elseNode.getId());
                        answers.add(answerObj);
                        createEdge(node, elseNode);
                    }
                    
                    obj.put("type", "AskNode");
                    sanitizedPut(obj,"id", node.getId(), false);
                    obj.put("answers", answers);
                    nodes.add(obj);
                    
                }
            }
            
            @Override
            public void visitImpl(CallNode node) throws DataTagsRuntimeException {
                
                if (!visited(node))
                {
                    JSONObject obj   = new  JSONObject();
                    obj.put("type", "CallNode");
                    sanitizedPut(obj,"id", node.getId(), false);
                    sanitizedPut(obj,"CalleeId", node.getCalleeNodeId(), false);
                    nodes.add(obj);
                    createEdge( node , node.getNextNode());
                    
                }
            }
            
            @Override
            public void visitImpl(RejectNode node) throws DataTagsRuntimeException {
                
                if (!visited(node))
                {
                    JSONObject obj   = new  JSONObject();
                    obj.put("type", "RejectNode");
                    sanitizedPut(obj,"id", node.getId(), false);
                    sanitizedPut(obj,"reason", node.getReason(),false);
                    nodes.add(obj);
                    
                }
            }
            
            @Override
            public void visitImpl(TodoNode node) throws DataTagsRuntimeException {
                
                if (!visited(node))
                {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "TodoNode");
                    sanitizedPut(obj,"id", node.getId(), false);
                    sanitizedPut(obj,"text", node.getTodoText(), false);
                    nodes.add(obj);
                    createEdge( node , node.getNextNode());
                }
            }
            
            @Override
            public void visitImpl(SetNode node) throws DataTagsRuntimeException {
                
                TagValue.Visitor<String> AssignmentParser = new TagValue.Visitor<String>(){
                    
                    @Override
                    public String visitToDoValue(ToDoValue v) {

                        return  "\""+v.getInfo()+"\"";
                    }
                    
                    @Override
                     public String visitAtomicValue(AtomicValue v) {
         
                        return "\""+v.getName()+"\"";
                    }
                    
                    @Override
                    public String visitAggregateValue(AggregateValue v) {
                        
                        
                        StringBuilder res= new StringBuilder("[");      
                        v.getValues().forEach(
                            tv -> {
                                    res.append(tv.accept(this));
                                    res.append(", ");
                        });
                        res.setLength(res.length()-2);               
                        res.append("]");
                        return res.toString();
                    }          
                    
                    @Override
                    public String visitCompoundValue(CompoundValue aThis) {
                        
                        StringBuilder res= new StringBuilder("[ ");  
                        for ( TagType tt : aThis.getTypesWithNonNullValues() ) {
                            res.append("{\"").append(tt.getName()).append("\":")
                               .append(aThis.get(tt).accept(this))
                               .append("}, ");
                        }
                        res.setLength(res.length()-2); 
                        res.append("]");
                         
                        return res.toString();
                    }
                };
                
                if (!visited(node))
                {
                    JSONObject obj         = new JSONObject();
                    JSONArray  assignments = new JSONArray();
                    
                    for ( TagType tt : node.getTags().getTypesWithNonNullValues() ) {
                        JSONObject tag= new JSONObject();
                        tag.put(tt.getName(),
                                    JSONValue.parse(
                                    node.getTags().get(tt).
                                            accept(AssignmentParser)));
                        assignments.add(tag);
                    }
                    
                    obj.put("type","setNode");
                    sanitizedPut(obj,"id", node.getId(), false);
                    obj.put("assignments",assignments );
                    nodes.add(obj);
                    createEdge(node, node.getNextNode());
                    
                }
            }
            
            @Override
            public void visitImpl(EndNode node) throws DataTagsRuntimeException {
                
                if (!visited(node))
                {
                    JSONObject obj   = new  JSONObject();
                    obj.put("type", "EndNode");
                    sanitizedPut(obj,"id", node.getId(), false);
                    nodes.add(obj);
                }
            }
            
             
        }
        
        DecisionGraphStringify dgStringify = new DecisionGraphStringify();
        Set<Node> subchartHeads = findSubchartHeades( fc );
        
        JSONObject res= new JSONObject();
        
        
        
        for ( Node chartHead :subchartHeads ) {
               dgStringify.reset();
               JSONArray nodes= new JSONArray();
               JSONArray edges= new JSONArray();
               chartHead.accept(dgStringify);
               nodes.addAll(dgStringify.nodes);
               edges.addAll(dgStringify.edges);
                
               JSONObject cluster= new JSONObject();
               cluster.put ("nodes",nodes);
               cluster.put ("edges",edges);
               res.put(chartHead.getId() , cluster);
        }
        
        
        res.put("$startNode", fc.getStart().getId());
        return res;
    }
    
    
    
    
    
    
    
    // find all cluster of the decision graph
    private static Set<Node> findSubchartHeades(DecisionGraph fc) {
        final Set<Node> candidates = new HashSet<>();
        for ( Node n : fc.nodes() ) { candidates.add(n);}
        for ( Node n : fc.nodes() ) {
            if ( candidates.contains(n) ) {
                n.accept( new Node.VoidVisitor(){
                    
                    @Override
                    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                        for ( Answer n : nd.getAnswers() ) {
                            Node answerNode = nd.getNodeFor(n);
                            candidates.remove(answerNode);
                            answerNode.accept(this);
                        }
                    }
                    
                    @Override
                    public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
                        for ( ConsiderAnswer n : nd.getAnswers() ) {
                            Node answerNode = nd.getNodeFor(n);
                            candidates.remove(answerNode);
                            answerNode.accept(this);
                        }
                        if ( nd.getElseNode() != null ) {
                            Node elseNode = nd.getElseNode();
                            candidates.remove(elseNode);
                            elseNode.accept(this);
                        }
                    }

                    @Override
                    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                        candidates.remove(nd.getNextNode());
                        nd.getNextNode().accept(this);
                    }

                    @Override
                    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {}

                    @Override
                    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                        candidates.remove(nd.getNextNode());
                        nd.getNextNode().accept(this);
                    }

                    @Override
                    public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
                        candidates.remove(nd.getNextNode());
                        nd.getNextNode().accept(this);
                    }

                    @Override
                    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {}
                });
            }
        }
        
        return candidates;
    }
    
    private static String humanTitle(DecisionGraph ent) {
		return (ent.getTitle() != null) ? ent.getTitle() : ent.getId();
	}
    
    
}
