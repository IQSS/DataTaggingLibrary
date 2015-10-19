package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Collections;
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
    
    private final CompoundType emptyTagSpace = new CompoundType("","");
    private DecisionGraphParser dgp;
    private AstNodeIdProvider nodeIdProvider;
    
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
        dgp = new DecisionGraphParser();
        nodeIdProvider = new AstNodeIdProvider();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testTypeIndexBuilding() throws SyntaxErrorException, SemanticsErrorException, DataTagsParseException {
        String typeDef = 
                  "top: consists of mid1, mid2.\n"
                + "mid1: consists of bottom1, bottom2.\n"
                + "mid2: consists of bottom2, bottom3.\n"
                + "bottom1: one of X11, X12, X13.\n"
                + "bottom2: one of Y21, Y22, Y23.\n"
                + "bottom3: one of Z31, Z32, Z33.";
        
        TagSpaceParseResult parse = new TagSpaceParser().parse(typeDef);
        CompoundType topType = parse.buildType("top").get();
        
        DecisionGraphParseResult res = new DecisionGraphParseResult( Collections.emptyList() );
        
        res.buildTypeIndex( topType );
        
        final Map<List<String>, TagType> typesBySlot = res.typesBySlot;
        
        Map<List<String>, TagType> expected = new HashMap<>();
        expected.put( C.list("top","mid1","bottom1"), typesBySlot.get( C.list("top","mid1","bottom1")) );
        expected.put( C.list("top","mid1","bottom2"), typesBySlot.get( C.list("top","mid1","bottom2")) );
        expected.put( C.list("top","mid2","bottom2"), typesBySlot.get( C.list("top","mid2","bottom2")) );
        expected.put( C.list("top","mid2","bottom3"), typesBySlot.get( C.list("top","mid2","bottom3")) );
        
        expected.put( C.list("mid1","bottom1"), typesBySlot.get( C.list("top","mid1","bottom1")) );
        expected.put( C.list("bottom1"), typesBySlot.get( C.list("top","mid1","bottom1")) );
        expected.put( C.list("mid2","bottom3"), typesBySlot.get( C.list("top","mid2","bottom3")) );
        expected.put( C.list("bottom3"), typesBySlot.get( C.list("top","mid2","bottom3")) );
        expected.put( C.list("mid2","bottom2"), typesBySlot.get( C.list("top","mid2","bottom2")) );
        expected.put( C.list("mid1","bottom2"), typesBySlot.get( C.list("top","mid1","bottom2")) );
        
        assertEquals( expected, typesBySlot );
    }
    
    @Test
    public void todoCallEndTest() throws Exception {
        
        TodoNode start = new TodoNode(nodeIdProvider.nextId(), "this and that");
        start.setNextNode( new CallNode(nodeIdProvider.nextId(), "ghostbusters")).setNextNode( new EndNode(nodeIdProvider.nextId()));
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart( start );
        
        String code = "[todo: this and that][call: ghostbusters][end]";
        DecisionGraph actual = dgp.parse(code).compile(emptyTagSpace);
        actual.setTopLevelType(emptyTagSpace);
        
        normalize( actual );
        normalize( expected );
        
        expected.nodes().forEach( n -> assertEquals( n, actual.getNode(n.getId())) );
        actual.nodes().forEach( n -> assertEquals( n, expected.getNode(n.getId())) );
        
        expected.equals(actual);
        assertEquals( expected, actual );
        
    }
    
    @Test
    public void todoCallRejectTest() throws Exception {
        
        TodoNode start = new TodoNode(nodeIdProvider.nextId(), "this and that");
        start.setNextNode( new CallNode(nodeIdProvider.nextId(), "ghostbusters")).setNextNode( new RejectNode(nodeIdProvider.nextId(), "obvious."));
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart( start );
        
        String code = "[todo: this and that][call: ghostbusters][reject: obvious.]";
        DecisionGraph actual = dgp.parse(code).compile(emptyTagSpace);
        actual.setTopLevelType(emptyTagSpace);
        
        normalize( actual );
        normalize( expected );
        
        expected.nodes().forEach( n -> assertEquals( n, actual.getNode(n.getId())) );
        actual.nodes().forEach( n -> assertEquals( n, expected.getNode(n.getId())) );
        
        expected.equals(actual);
        assertEquals( expected, actual );
        
    }
  
    @Test
    public void askTest() throws Exception {
        
        AskNode start = new AskNode(nodeIdProvider.nextId());
        start.setText("why?");
        start.setNodeFor(new Answer("dunno"), new EndNode("de"));
        final CallNode whyNotCallNode = new CallNode("wnc", "duh");
        start.setNodeFor(new Answer("why not"),  whyNotCallNode );
        
        EndNode finalEndNode = new EndNode( nodeIdProvider.nextId() );
        whyNotCallNode.setNextNode(finalEndNode);
        
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart( start );
        
        String code = "[ask: {text: why?} {answers: {dunno:[>de< end]} {why not:[>wnc< call: duh]}}][end]";
        DecisionGraph actual = dgp.parse(code).compile(emptyTagSpace);
        actual.setTopLevelType(emptyTagSpace);
        
        normalize( actual );
        normalize( expected );
        
        expected.nodes().forEach( n -> assertEquals( n, actual.getNode(n.getId())) );
        actual.nodes().forEach( n -> assertEquals( n, expected.getNode(n.getId())) );
        
        expected.equals(actual);
        assertEquals( expected, actual );
        
    }
    
    @Test
    public void askWithImplicitAnswerTest() throws Exception {
        
        AskNode start = new AskNode(nodeIdProvider.nextId());
        start.setText("Should I?");
        start.setNodeFor( Answer.YES, new EndNode("end-yes"));
        start.setNodeFor( Answer.NO,  new EndNode("end-no") );
        
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart( start );
        
        String code = "[ask: {text: Should I?} {answers: {yes:[>end-yes< end]}}][>end-no< end]";
        DecisionGraph actual = dgp.parse(code).compile(emptyTagSpace);
        actual.setTopLevelType(emptyTagSpace);
        
        normalize( actual );
        normalize( expected );
        
        expected.nodes().forEach( n -> assertEquals( n, actual.getNode(n.getId())) );
        actual.nodes().forEach( n -> assertEquals( n, expected.getNode(n.getId())) );
        
        expected.equals(actual);
        assertEquals( expected, actual );
        
    }
    
    @Test
    public void setEndTest() throws Exception {
        
        AtomicType t1 = new AtomicType("t1", "");
        t1.registerValue("a", "");
        t1.registerValue("b", "");
        t1.registerValue("c", "");
        
        AtomicType t2Items = new AtomicType("t2Items","");
        AggregateType t2 = new AggregateType("t2","", t2Items);
        t2Items.registerValue("b", "");
        t2Items.registerValue("c", "");
        
        CompoundType ct = new CompoundType("topLevel", "");
        ct.addFieldType(t1);
        ct.addFieldType(t2);
        
        CompoundValue tags = ct.createInstance();
        tags.set( t1.valueOf("a"));
        tags.set( t2.createInstance() );
        ((AggregateValue)tags.get( t2 )).add( t2Items.valueOf("b") );
        ((AggregateValue)tags.get( t2 )).add( t2Items.valueOf("c") );
        
        SetNode start = new SetNode(nodeIdProvider.nextId(), tags);
        start.setNextNode( new EndNode(nodeIdProvider.nextId()));
        DecisionGraph expected = new DecisionGraph();
        expected.add(start);
        expected.setStart( start );
        expected.setTopLevelType(ct);
        expected.setId("loremIpsum");
        String code = "[set: t1=a; t2 += b,c][end]";
        DecisionGraph actual = dgp.parse(code).compile(ct);
        actual.setId("loremIpsum"); // prevent a false negative over chart id.
        
        
        expected.nodes().forEach( n -> assertEquals( n, actual.getNode(n.getId())) );
        actual.nodes().forEach( n -> assertEquals( n, expected.getNode(n.getId())) );
        
        expected.equals(actual);
        assertEquals( expected, actual );
        
    }
    
    private DecisionGraph normalize( DecisionGraph dg ){
        dg.setId("normalizedId");
        dg.setTopLevelType(emptyTagSpace);
        return dg;
    }
}
