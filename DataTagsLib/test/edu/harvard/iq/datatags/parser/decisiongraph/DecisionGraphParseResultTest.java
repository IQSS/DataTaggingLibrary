package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import edu.harvard.iq.util.DecisionGraphHelper;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class DecisionGraphParseResultTest {

    private final CompoundSlot emptyTagSpace = new CompoundSlot("", "");
    private AstNodeIdProvider nodeIdProvider;
    private EndNode endNode;

    public DecisionGraphParseResultTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        nodeIdProvider = new AstNodeIdProvider();
        endNode = new EndNode("[SYN-END]");
        
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTypeIndexBuilding() throws SyntaxErrorException, SemanticsErrorException, DataTagsParseException {
        String typeDef
                = "top: consists of mid1, mid2.\n"
                + "mid1: consists of bottom1, bottom2.\n"
                + "mid2: consists of bottom2, bottom3.\n"
                + "bottom1: one of X11, X12, X13.\n"
                + "bottom2: one of Y21, Y22, Y23.\n"
                + "bottom3: one of Z31, Z32, Z33.";

        TagSpaceParseResult parse = new TagSpaceParser().parse(typeDef);
        CompoundSlot topType = parse.buildType("top").get();

        DecisionGraphCompiler res = new DecisionGraphCompiler();

        res.buildTypeIndex(topType);

        final Map<List<String>, List<String>> typesBySlot = res.fullyQualifiedSlotName;

        Map<List<String>, List<String>> expected = new HashMap<>();
        expected.put(C.list("top", "mid1", "bottom1"), C.list("top", "mid1", "bottom1"));
        expected.put(C.list("top", "mid1", "bottom2"), C.list("top", "mid1", "bottom2"));
        expected.put(C.list("top", "mid2", "bottom2"), C.list("top", "mid2", "bottom2"));
        expected.put(C.list("top", "mid2", "bottom3"), C.list("top", "mid2", "bottom3"));

        expected.put(C.list("mid1", "bottom1"), C.list("top", "mid1", "bottom1"));
        expected.put(C.list("bottom1"), C.list("top", "mid1", "bottom1"));
        expected.put(C.list("mid2", "bottom3"), C.list("top", "mid2", "bottom3"));
        expected.put(C.list("bottom3"), C.list("top", "mid2", "bottom3"));
        expected.put(C.list("mid2", "bottom2"), C.list("top", "mid2", "bottom2"));
        expected.put(C.list("mid1", "bottom2"), C.list("top", "mid1", "bottom2"));

        assertEquals(expected, typesBySlot);
    }

    @Test
    public void todoCallEndTest() throws Exception {

        ToDoNode start = new ToDoNode(nodeIdProvider.nextId(), "this and that");
        final ToDoNode toDoNode = new ToDoNode("ghostbusters", "bla");
        start.setNextNode(new CallNode(nodeIdProvider.nextId(), toDoNode))
                .setNextNode(toDoNode)
                .setNextNode(new EndNode(nodeIdProvider.nextId()));
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart(start);

        String code = "[todo: this and that][call: ghostbusters][>ghostbusters< todo: bla][end]";
        Path codePath = Paths.get("first code");
        DecisionGraphCompiler dgc = DecisionGraphHelper.getDGCompiler(code, codePath);
        DecisionGraph actual = dgc.compile(emptyTagSpace, DecisionGraphHelper.getPmd(codePath), new ArrayList<>());

        normalize(actual);
        normalize(expected);

        expected.nodes().forEach(n -> assertEquals(n, actual.getNode(n.getId())));
        actual.nodes().forEach(n -> assertEquals(n, expected.getNode(n.getId())));

        assertEquals(expected, actual);

    }

    @Test
    public void todoCallRejectTest() throws Exception {

        ToDoNode start = new ToDoNode(nodeIdProvider.nextId(), "this and that");
        final ToDoNode toDoNode = new ToDoNode("ghostbusters", "bla");
        start.setNextNode(new CallNode(nodeIdProvider.nextId(), toDoNode)).
                setNextNode(toDoNode).
                setNextNode(new RejectNode(nodeIdProvider.nextId(), "obvious."));
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart(start);
        expected.addAllReachableNodes();
       
        String code = "[todo: this and that][call: ghostbusters][>ghostbusters< todo: bla][reject: obvious.]";
        
        Path codePath = Paths.get("first code");
        DecisionGraphCompiler dgc = DecisionGraphHelper.getDGCompiler(code, codePath);
        DecisionGraph actual = dgc.compile(emptyTagSpace, DecisionGraphHelper.getPmd(codePath), new ArrayList<>());

        normalize(actual);
        normalize(expected);

        expected.nodes().forEach(n -> assertEquals(n, actual.getNode(n.getId())));
        actual.nodes().forEach(n -> assertEquals(n, expected.getNode(n.getId())));

        assertEquals(expected, actual);

    }

    @Test
    public void considerTest() throws Exception {
        EndNode finalEndNode = new EndNode("2");
        final CallNode whyNotCallNode = new CallNode("wnc", new ToDoNode("duh", "bla"));
        final CallNode whyNotCallNode2 = new CallNode("wnc2", new ToDoNode("duh2", "bla"));
        whyNotCallNode.setNextNode(finalEndNode);
        AtomicSlot t2Items = new AtomicSlot("Subject", "");
        AggregateSlot t2 = new AggregateSlot("Subject", "", t2Items);
        t2Items.registerValue("world", "");
        CompoundSlot ct = new CompoundSlot("topLevel", "");
        ct.addFieldType(t2);
        CompoundValue tags = ct.createInstance();

        tags.set(t2.createInstance());
        ((AggregateValue) tags.get(t2)).add(t2Items.valueOf("world"));

        ConsiderNode start = new ConsiderNode("1", whyNotCallNode);
        start.setNodeFor(ConsiderAnswer.get(tags), whyNotCallNode2);
        DecisionGraph expected = new DecisionGraph();
        expected.add(whyNotCallNode);
        expected.add(start);
        expected.add(new ToDoNode("duh", "bla"));
        expected.add(new ToDoNode("duh2", "bla"));

        expected.setStart(start);

        String code = "[>1< consider: \n"
                + "     {slot:Subject}\n"
                + "    {options:\n"
                + "        {world:[>wnc2< call: duh2]}\n"
                + "\n"
                + "    }\n"
                + "   { else: [>wnc< call: duh]}\n"
                + "]\n"
                + "[>duh< todo: bla]"
                + "[>duh2< todo: bla]"
                + "[>2< end]";

        Path codePath = Paths.get("first code");
        DecisionGraphCompiler dgc = DecisionGraphHelper.getDGCompiler(code, codePath);
        DecisionGraph actual = dgc.compile(ct, DecisionGraphHelper.getPmd(codePath), new ArrayList<>());

        normalize(actual);
        normalize(expected);

        expected.nodes().forEach(n -> assertEquals(n, actual.getNode(n.getId())));
        actual.nodes().forEach(n -> assertEquals(n, expected.getNode(n.getId())));

        assertEquals(expected, actual);
    }

    @Test
    public void askTest() throws Exception {

        AskNode start = new AskNode(nodeIdProvider.nextId());
        ToDoNode callTodo = new ToDoNode("duh", "bla");
        start.setText("why?");
        start.addAnswer(Answer.get("dunno"), new EndNode("de"));
        final CallNode whyNotCallNode = new CallNode("wnc", callTodo);
        start.addAnswer(Answer.get("why not"), whyNotCallNode);

        EndNode finalEndNode = new EndNode(nodeIdProvider.nextId());
        whyNotCallNode.setNextNode(finalEndNode);

        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart(start);
        expected.add(callTodo);

        String code = "[ask: {text: why?} {answers: {dunno:[>de< end]} {why not:[>wnc< call: duh]}}][>duh< todo: bla][end]";
        Path codePath = Paths.get("first code");
        DecisionGraphCompiler dgc = DecisionGraphHelper.getDGCompiler(code, codePath);
        DecisionGraph actual = dgc.compile(emptyTagSpace, DecisionGraphHelper.getPmd(codePath), new ArrayList<>());

        normalize(actual);
        normalize(expected);

        expected.nodes().forEach(n -> assertEquals(n, actual.getNode(n.getId())));
        actual.nodes().forEach(n -> assertEquals(n, expected.getNode(n.getId())));

        assertEquals(expected, actual);

    }

    @Test
    public void askWithImplicitAnswerTest() throws Exception {

        AskNode start = new AskNode(nodeIdProvider.nextId());
        start.setText("Should I?");
        start.addAnswer(Answer.YES, new EndNode("end-yes"));
        start.addAnswer(Answer.NO, new EndNode("end-no"));

        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart(start);

        String code = "[ask: {text: Should I?} {answers: {yes:[>end-yes< end]}}][>end-no< end]";
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(emptyTagSpace, endNode ,new ArrayList<>());
        
        DecisionGraph actual = cu.getDecisionGraph();

        normalize(actual);
        normalize(expected);

        expected.nodes().forEach(n -> assertEquals(n, actual.getNode(n.getId())));
        actual.nodes().forEach(n -> assertEquals(n, expected.getNode(n.getId())));

        assertEquals(expected, actual);

    }
    
    @Test
    public void sectionTest() throws Exception {

        SectionNode start = new SectionNode(nodeIdProvider.nextId());
        ToDoNode callTodo = new ToDoNode("callid", "bla");
        start.setTitle("Section - start");
        ToDoNode sectionStartNode = new ToDoNode("blaID", "bla bla");
        CallNode call = new CallNode("CallID",callTodo);
        sectionStartNode.setNextNode(call);
        start.setStartNode(sectionStartNode);
//        start.setNextNode(callTodo);

        EndNode finalEndNode = new EndNode("[SYN-END]");
        call.setNextNode(finalEndNode);
        start.setNextNode(callTodo).setNextNode(finalEndNode);

        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart(start);

        String code = "[section: {title: Section - start} [>blaID< todo: bla bla] [>CallID< call: callid]][>callid< todo: bla]";

        Path codePath = Paths.get("first code");
        DecisionGraphCompiler dgc = DecisionGraphHelper.getDGCompiler(code, codePath);
        DecisionGraph actual = dgc.compile(emptyTagSpace, DecisionGraphHelper.getPmd(codePath), new ArrayList<>());

        normalize(actual);
        normalize(expected);

        expected.nodes().forEach(n -> assertEquals(n, actual.getNode(n.getId())));
        actual.nodes().forEach(n -> assertEquals(n, expected.getNode(n.getId())));

        assertEquals(expected, actual);

    }

    @Test
    public void setEndTest() throws Exception {

        AtomicSlot t1 = new AtomicSlot("t1", "");
        t1.registerValue("a", "");
        t1.registerValue("b", "");
        t1.registerValue("c", "");

        AtomicSlot t2Items = new AtomicSlot("t2Items", "");
        AggregateSlot t2 = new AggregateSlot("t2", "", t2Items);
        t2Items.registerValue("b", "");
        t2Items.registerValue("c", "");

        CompoundSlot ct = new CompoundSlot("topLevel", "");
        ct.addFieldType(t1);
        ct.addFieldType(t2);

        CompoundValue tags = ct.createInstance();
        tags.set(t1.valueOf("a"));
        tags.set(t2.createInstance());
        ((AggregateValue) tags.get(t2)).add(t2Items.valueOf("b"));
        ((AggregateValue) tags.get(t2)).add(t2Items.valueOf("c"));

        SetNode start = new SetNode(nodeIdProvider.nextId(), tags);
        start.setNextNode(new EndNode(nodeIdProvider.nextId()));
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart(start);
        expected.setId("loremIpsum");
        String code = "[set: t1=a; t2 += b,c][end]";
        
        
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(ct, endNode ,new ArrayList<>());
        
        DecisionGraph actual = cu.getDecisionGraph();
        actual.setId("loremIpsum"); // prevent a false negative over chart id.

        expected.nodes().forEach(n -> assertEquals(n, actual.getNode(n.getId())));
        actual.nodes().forEach(n -> assertEquals(n, expected.getNode(n.getId())));

        assertEquals(expected, actual);

    }

    @Test
    public void complexSetNodeTest() throws SyntaxErrorException, SemanticsErrorException, DataTagsParseException {
        String tsCode = "top: consists of mid1, mid2. "
                + "mid1: one of A, B, C, D. "
                + "mid2: consists of bottom1, bottom2. "
                + "bottom1: one of Q, W, E.\n"
                + "bottom2: some of A,S,D,F.";

        CompoundSlot ts = new TagSpaceParser().parse(tsCode).buildType("top").get();

        CompoundValue expected = ts.createInstance();
        expected.set(((AtomicSlot) ts.getTypeNamed("mid1")).valueOf("B"));
        CompoundValue mid2 = ((CompoundSlot) ts.getTypeNamed("mid2")).createInstance();
        mid2.set(((AtomicSlot) mid2.getType().getTypeNamed("bottom1")).valueOf("W"));
        final AggregateValue bottom2Value = ((AggregateSlot) mid2.getType().getTypeNamed("bottom2")).createInstance();
        bottom2Value.add(bottom2Value.getType().getItemType().valueOf("S"));
        bottom2Value.add(bottom2Value.getType().getItemType().valueOf("D"));
        bottom2Value.add(bottom2Value.getType().getItemType().valueOf("F"));
        mid2.set(bottom2Value);
        expected.set(mid2);

        String dgCode = "[set: mid1=B; bottom1=W; bottom2+=S,D,F][end]";
        
        CompilationUnit cu = new CompilationUnit(dgCode);
        cu.compile(ts, new EndNode("[SYN-END]"),new ArrayList<>());
        
        CompoundValue actual = ((SetNode) cu.getDecisionGraph().getStart()).getTags();

        assertEquals(expected, actual);
    }

    @Test
    public void dualSecondLevelNodeTest() throws SyntaxErrorException, SemanticsErrorException, DataTagsParseException {
        String tsCode = "top: consists of mid1, mid2. "
                + "mid1: one of A, B, C, D. "
                + "mid2: consists of bottom1, bottom2. "
                + "bottom1: one of Q, W, E.\n"
                + "bottom2: some of A,S,D,F.";

        CompoundSlot ts = new TagSpaceParser().parse(tsCode).buildType("top").get();

        CompoundValue mid2 = ((CompoundSlot) ts.getTypeNamed("mid2")).createInstance();
        mid2.set(((AtomicSlot) mid2.getType().getTypeNamed("bottom1")).valueOf("W"));
        final AggregateValue bottom2Value = ((AggregateSlot) mid2.getType().getTypeNamed("bottom2")).createInstance();
        bottom2Value.add(bottom2Value.getType().getItemType().valueOf("S"));
        bottom2Value.add(bottom2Value.getType().getItemType().valueOf("D"));
        bottom2Value.add(bottom2Value.getType().getItemType().valueOf("F"));
        mid2.set(bottom2Value);

        CompoundValue expected = ts.createInstance();
        expected.set(mid2);

        String dgCode = "[set: bottom1=W; bottom2+=S,D,F][end]";
        
        CompilationUnit cu = new CompilationUnit(dgCode);
        cu.compile(ts, new EndNode("[SYN-END]"),new ArrayList<>());
        
        CompoundValue actual = ((SetNode) cu.getDecisionGraph().getStart()).getTags();

        assertEquals(expected, actual);
    }

    @Test
    public void multiLevelAtomicSetNodeTest() throws SyntaxErrorException, SemanticsErrorException, DataTagsParseException {
        String tsCode = "top: consists of mid1, mid2. "
                + "mid1: one of A, B, C, D. "
                + "mid2: consists of bottom1, bottom2. "
                + "bottom1: one of Q, W, E.\n"
                + "bottom2: some of A,S,D,F.";

        CompoundSlot ts = new TagSpaceParser().parse(tsCode).buildType("top").get();

        CompoundValue expected = ts.createInstance();
        expected.set(((AtomicSlot) ts.getTypeNamed("mid1")).valueOf("B"));
        CompoundValue mid2 = ((CompoundSlot) ts.getTypeNamed("mid2")).createInstance();
        mid2.set(((AtomicSlot) mid2.getType().getTypeNamed("bottom1")).valueOf("W"));
        expected.set(mid2);

        String dgCode = "[set: mid1=B; bottom1=W][end]";
        
        CompilationUnit cu = new CompilationUnit(dgCode);
        cu.compile(ts, new EndNode("[SYN-END]"),new ArrayList<>());
        
        CompoundValue actual = ((SetNode) cu.getDecisionGraph().getStart()).getTags();

        assertEquals(expected, actual);
    }

    @Test
    public void singleSecondLevelAtomicSetNodeTest() throws SyntaxErrorException, SemanticsErrorException, DataTagsParseException {
        String tsCode = "top: consists of mid1, mid2. "
                + "mid1: one of A, B, C, D. "
                + "mid2: consists of bottom1, bottom2. "
                + "bottom1: one of Q, W, E.\n"
                + "bottom2: some of A,S,D,F.";

        CompoundSlot ts = new TagSpaceParser().parse(tsCode).buildType("top").get();

        CompoundValue expected = ts.createInstance();
        CompoundValue mid2 = ((CompoundSlot) ts.getTypeNamed("mid2")).createInstance();
        mid2.set(((AtomicSlot) mid2.getType().getTypeNamed("bottom1")).valueOf("W"));
        expected.set(mid2);

        String dgCode = "[set: bottom1=W][end]";
        CompilationUnit cu = new CompilationUnit(dgCode);
        cu.compile(ts, new EndNode("[SYN-END]"),new ArrayList<>());
        
        CompoundValue actual = ((SetNode) cu.getDecisionGraph().getStart()).getTags();

        assertEquals(expected, actual);
    }

    private DecisionGraph normalize(DecisionGraph dg) {
        dg.setId("normalizedId");
        return dg;
    }
}
