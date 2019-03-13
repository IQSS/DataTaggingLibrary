package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ContinueNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.PartNode;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderOptionSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstImport;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstPartNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSectionNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.NodeIdAdder;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.ParsedFile;
import edu.harvard.iq.datatags.parser.exceptions.BadLookupException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.tools.DecisionGraphAstValidator;
import edu.harvard.iq.datatags.tools.NodeValidationMessage;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import org.jparsec.Parser;
import org.jparsec.error.ParserException;

/**
 * 
 * Represents a single parsed decision graph file. Given some model-wide data,
 * compiles the file into a decision graph whose {@code [call]} nodes are not
 * linked.
 * 
 * @author mor_vilozni
 */
public class CompilationUnit {

    private ParsedFile parsedFile;
    private final Path sourcePath;
    
    private CompoundSlot topLevelType;
    private DecisionGraph product;
    private final List<ValidationMessage> validationMessages = new LinkedList<>();
    
    private Map<List<String>, List<String>> fullyQualifiedSlotName;
    /** 
     * Maps a name of a compilation unit (as defined by the import statement) 
     * to the actual compilation unit.
     */
    private final Map<String, CompilationUnit> nameToCU = new HashMap<>();
    private final Map<String, String> callToCalleeID = new HashMap<>();
    
    
    /**
     * Decision-graph wide end node (added synthetically).
     */
    private EndNode endAll;
    
    private final String source;
    
    public CompilationUnit(String aSource) {
        this(aSource, null);
    }
    
    public CompilationUnit(Path aSource) throws IOException {
        this(new String(Files.readAllBytes(aSource), StandardCharsets.UTF_8), aSource);
    }
    
    CompilationUnit(String aSource, Path aPath) {
        source = aSource;
        sourcePath = aPath;
    }
    
    private void parse() throws DataTagsParseException{
        try{
            Parser<ParsedFile> parser = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.graphParser() );
            parsedFile = parser.parse(source);
            parsedFile.getImports().forEach(im -> im.setInitalPath(sourcePath));
            parsedFile.getImports().stream().collect(Collectors.groupingBy(AstImport::getName)).
                                entrySet().stream().filter(e -> e.getValue().size() > 1).
                                forEach(e -> validationMessages.add(new ValidationMessage(Level.ERROR, "Duplicate import name " + e.getKey() + ", at path - "  + sourcePath)));
            new NodeIdAdder().addIds(parsedFile.getAstNodes());
        }
        catch ( ParserException pe ){
            throw new DataTagsParseException(new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
                                    pe.getMessage(), pe);        
        }
    }
    
    /**
     * Convert the AST nodes to model nodes. Populates the validation messages
     * and import map.
     *
     * @param aFullyQualifiedSlotName
     * @param aTopLevelType
     * @param globalEndNode
     * @param astValidators
     * @throws edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException
     */
    public void compile(Map<List<String>, List<String>> aFullyQualifiedSlotName,
                        CompoundSlot aTopLevelType,
                        EndNode globalEndNode, List<DecisionGraphAstValidator> astValidators) throws DataTagsParseException {
        fullyQualifiedSlotName = aFullyQualifiedSlotName;
        topLevelType = aTopLevelType;
        endAll = globalEndNode;
        product = new DecisionGraph();
        parse();
        
        astValidators.stream().flatMap( v -> v.validate(parsedFile.getAstNodes()).stream())
                                    .forEach(validationMessages::add);
        //Validate that part doesn't contain other parts
        List <AstNode> parts = parsedFile.getAstNodes().stream().filter(node -> node instanceof AstPartNode).collect(Collectors.toList());
          parts.forEach(part -> {
                ((AstPartNode) part).getAstNodes().forEach(node -> {
                    if(node instanceof AstPartNode) {
                        throw new RuntimeException(new DataTagsParseException(node, "Error: part can't contain another part (at node " + node + ")"));
                    }
                });
          });
                                    
        try {
            //Build Parts separately
            List <AstNode> mainNodes = parsedFile.getAstNodes().stream().filter(node -> !(node instanceof AstPartNode)).collect(Collectors.toList());
            product.setStart(buildNodes(mainNodes, endAll));
            parts.forEach(part -> product.addPart(buildNodes(Collections.singletonList(part), endAll)));
        } catch ( RuntimeException re ) {
            Throwable cause = re.getCause();
            if ((cause != null) && (cause instanceof DataTagsParseException)) {
                DataTagsParseException pe = (DataTagsParseException) cause;
                validationMessages.add( new ValidationMessage(Level.ERROR, "At node " + pe.getOffendingNode() + ": " + pe.getMessage()) );
            } else {
                throw re;
            }
        }
        
        product.nodes().forEach(n->n.setCuPath(sourcePath));
    }

    public void compile(CompoundSlot aTopLevelType, EndNode globalEndNode, 
                          List<DecisionGraphAstValidator> astValidators) throws DataTagsParseException {
        DecisionGraphCompiler dgc = new DecisionGraphCompiler();
        Map<List<String>, List<String>> aFullyQualifiedSlotName = dgc.buildTypeIndex(aTopLevelType);
        compile(aFullyQualifiedSlotName, aTopLevelType, globalEndNode, astValidators);
    }   
    
    private AbstractSlot findSlot(List<String> astSlot, CompoundValue topValue, SetNodeValueBuilder valueBuilder) {
        AbstractSlot slot;

        if (astSlot == null || C.last(astSlot).equals(topLevelType.getName())) {
            slot = topLevelType;
        } else {
            try {
                final CompoundValue additionPoint = valueBuilder.descend(C.tail(fullyQualifiedSlotName.get(astSlot)), topValue);
                slot = additionPoint.getSlot().getSubSlot(C.last(astSlot));
            } catch (RuntimeException re) {
                throw new RuntimeException("Tag not found");
            }
        }
        return slot;
    }

    public CompilationUnit put(String key, CompilationUnit value) {
        return nameToCU.put(key, value);
    }

    public DecisionGraph getDecisionGraph() {
        return product;
    }

    public boolean add(NodeValidationMessage e) {
        return validationMessages.add(e);
    }

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    /**
     * Nodes that have only a single "yes" of "no" answer, are considered to
     * implicitly have the reverse boolean answer as well. This method detects
     * that case and generates those answers.
     *
     * @param node
     * @return A list of implied answers (might be empty, never {@code null}).
     */
    private List<Answer> impliedAnswers(AskNode node) {

        List<Answer> answers = node.getAnswers();
        if (answers.size() > 1) {
            return Collections.emptyList();
        }
        if (answers.isEmpty()) {
            return Arrays.asList(Answer.NO, Answer.YES); // special case, where both YES and NO lead to the same options. 
        }
        Answer onlyAns = answers.iterator().next();

        String ansText = onlyAns.getAnswerText().trim().toLowerCase();
        switch (ansText) {
            case "yes":
                return Collections.singletonList(Answer.NO);
            case "no":
                return Collections.singletonList(Answer.YES);
            default:
                return Collections.emptyList();
        }
    }

    /**
     * Compiles the list of nodes to an executable node structure. Note that the
     * node list has to make semantic sense - any nodes that follow a
     * terminating node (in the top-level list) will be ignored.
     *
     * @param astNodes The list of AST nodes to compile.
     * @param defaultNode The node to go to when a list of nodes does not end
     * with a terminating node.
     * @return The starting node for the execution.
     * @throws RuntimeException on errors in the code.
     */
    private Node buildNodes(List<? extends AstNode> astNodes, Node defaultNode) {

        try {
            return astNodes.isEmpty()
                    ? defaultNode
                    : C.head(astNodes).accept(new AstNode.Visitor<Node>() {   

                        @Override
                        // build consider node from ast-consider-node 
                        public Node visit(AstConsiderNode astNode) {
                            CompoundValue topValue = topLevelType.createInstance();
                            SetNodeValueBuilder valueBuilder = new SetNodeValueBuilder(topValue, fullyQualifiedSlotName);

                            Node syntacticallyNext = buildNodes(C.tail(astNodes), defaultNode);
                            Node elseNode = syntacticallyNext;
                            if (astNode.getElseNode() != null) {
                                elseNode = buildNodes(astNode.getElseNode(), syntacticallyNext);
                            }
                            ConsiderNode res = new ConsiderNode(astNode.getId(), elseNode);

                            AbstractSlot slot = findSlot(astNode.getSlot(), topValue, valueBuilder);

                            if (slot instanceof AggregateSlot || slot instanceof AtomicSlot) {
                                // Original node was [consider]
                                for (AstConsiderOptionSubNode astAns : astNode.getOptions()) {
                                    if (astAns.getOptionList() == null) {
                                        throw new RuntimeException(" (consider slot gets only values, not answers)");
                                    }
                                    topValue = topLevelType.createInstance();
                                    valueBuilder = new SetNodeValueBuilder(topValue, fullyQualifiedSlotName);
                                    AstSetNode.Assignment assignment;
                                    if (slot instanceof AggregateSlot) {
                                        assignment = new AstSetNode.AggregateAssignment(astNode.getSlot(), astAns.getOptionList());
                                    } else {
                                        assignment = new AstSetNode.AtomicAssignment(astNode.getSlot(), astAns.getOptionList().get(0).trim());
                                    }

                                    if (assignment == null) {
                                        throw new RuntimeException(new DataTagsParseException(astNode, "Error: bad assignment (at node " + astNode + ")"));
                                    }
                                    try {
                                        assignment.accept(valueBuilder);
                                    } catch (RuntimeException re) {
                                        if (re.getCause() instanceof DataTagsParseException) {
                                            ((DataTagsParseException) re.getCause()).setOffendingNode(astNode);
                                            throw re;
                                        } else {
                                            throw new RuntimeException(re.getMessage() + " (at node " + astNode + ")", re);
                                        }
                                    }
                                    CompoundValue answer = topValue;
                                    res.setNodeFor(answer, buildNodes(astAns.getSubGraph(), syntacticallyNext));
                                }

                            } else if (slot instanceof CompoundSlot) {
                                // Original node was [when]
                                for (AstConsiderOptionSubNode astAns : astNode.getOptions()) {
                                    if (astAns.getAssignments() == null) {
                                        throw new RuntimeException("Expecting some values for the [when] node's options.");
                                    }
                                    topValue = topLevelType.createInstance();
                                    valueBuilder = new SetNodeValueBuilder(topValue, fullyQualifiedSlotName);

                                    List<AstSetNode.Assignment> assignments = astAns.getAssignments();

                                    try {
                                        for (AstSetNode.Assignment asnmnt : assignments) {
                                            asnmnt.accept(valueBuilder);
                                        }
                                    } catch (RuntimeException re) {
                                        if (re.getCause() instanceof DataTagsParseException) {
                                            ((DataTagsParseException) re.getCause()).setOffendingNode(astNode);
                                            throw re;
                                        } else {
                                            throw new RuntimeException(re.getMessage() + " (at node " + astNode + ")", re);
                                        }
                                    }
                                    CompoundValue answer = topValue;
                                    if (res.getNodeFor(answer) == null) {
                                        res.setNodeFor(answer, buildNodes(astAns.getSubGraph(), syntacticallyNext));
                                    }

                                }
                            }
                            return product.add(res);
                        }

                        @Override
                        public Node visit(AstAskNode astNode) {
                            AskNode res = new AskNode(astNode.getId());
                            res.setText(astNode.getTextNode().getText());
                            if (astNode.getTerms() != null) {
                                astNode.getTerms().forEach(t -> res.addTerm(t.getTerm(), t.getExplanation()));
                            }

                            Node syntacticallyNext = buildNodes(C.tail(astNodes), defaultNode);

                            astNode.getAnswers().forEach(ansSubNode -> res.addAnswer(Answer.withName(ansSubNode.getAnswerText()),
                                    buildNodes(ansSubNode.getSubGraph(), syntacticallyNext)));

                            impliedAnswers(res).forEach(ans -> res.addAnswer(ans, syntacticallyNext));

                            return product.add(res);
                        }

                        @Override
                        public Node visit(AstCallNode astNode) {
                            CallNode callNode = new CallNode(astNode.getId());
                            //map id call node -> id collee node
                            callToCalleeID.put(astNode.getId(), astNode.getCalleeId());
                            callNode.setNextNode(buildNodes(C.tail(astNodes), defaultNode));
                            return product.add(callNode);
                        }

                        @Override
                        public Node visit(AstSetNode astNode) {
                            final CompoundValue topValue = topLevelType.createInstance();
                            SetNodeValueBuilder valueBuilder = new SetNodeValueBuilder(topValue, fullyQualifiedSlotName);
                            try {
                                astNode.getAssignments().forEach(asnmnt -> asnmnt.accept(valueBuilder));
                            } catch (RuntimeException re) {
                                if ( re.getCause() instanceof BadLookupException ) {
                                    validationMessages.add(new ValidationMessage(Level.ERROR,re.getMessage() + " (at node " + astNode + ")"));
                                } else {
                                    validationMessages.add(new ValidationMessage(Level.ERROR,re.getMessage() + " (at node " + astNode + ")"));
                                }
                            }

                            final SetNode setNode = new SetNode(astNode.getId(), topValue);
                            setNode.setNextNode(buildNodes(C.tail(astNodes), defaultNode));
                            return product.add(setNode);
                        }

                        @Override
                        public Node visit(AstTodoNode astNode) {
                            final ToDoNode todoNode = new ToDoNode(astNode.getId(), astNode.getTodoText());
                            todoNode.setNextNode(buildNodes(C.tail(astNodes), defaultNode));
                            return product.add(todoNode);
                        }

                        @Override
                        public Node visit(AstRejectNode astNode) {
                            buildNodes(C.tail(astNodes), defaultNode);
                            return product.add(new RejectNode(astNode.getId(), astNode.getReason()));
                        }

                        @Override
                        public Node visit(AstEndNode astNode) {
                            buildNodes(C.tail(astNodes), defaultNode);
                            return product.add(new EndNode(astNode.getId()));
                        }

                        @Override
                        public Node visit(AstSectionNode astNode) {
                            final SectionNode sectionNode = new SectionNode(astNode.getId(), astNode.getInfo() != null ? astNode.getInfo().getText() : "");
                            sectionNode.setNextNode(buildNodes(C.tail(astNodes), defaultNode));
                            sectionNode.setStartNode(buildNodes(astNode.getAstNodes(), new ContinueNode(sectionNode.getId()+"_[CNT]")));
                            
                            return product.add(sectionNode);
                        }
                        
                        @Override
                        public Node visit(AstPartNode astNode) {
                            final PartNode partNode = new PartNode(astNode.getId(), astNode.getInfo() != null ? astNode.getInfo().getText() : "");
                            Node startNode = buildNodes(astNode.getAstNodes(), endAll);
                            partNode.setStartNode(startNode);
                            buildNodes(C.tail(astNodes), defaultNode);
                            return product.add(partNode);
                        }
                    });
            
        } catch (RuntimeException re) {
            Throwable cause = re.getCause();
            if ((cause != null) && (cause instanceof DataTagsParseException)) {
                DataTagsParseException pe = (DataTagsParseException) cause;
                if (pe.getOffendingNode() == null) {
                    pe.setOffendingNode(C.head(astNodes));
                }
                throw new RuntimeException(re.getMessage() + " (in node " + C.head(astNodes) + ")", pe);
            } else {
                throw re;
            }
        }

    }

    public Map<String, String> getCallToCalleeID() {
        return callToCalleeID;
    }

    public ParsedFile getParsedFile() {
        return parsedFile;
    }

    public Path getSourcePath() {
        return sourcePath;
    }
    

}
