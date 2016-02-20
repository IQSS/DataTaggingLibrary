package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.*;
import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import org.junit.*;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class TagSpaceOptimizerTest {
    private DecisionGraphParser dgp;
    private AstNodeIdProvider nodeIdProvider;

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
    public void doesNothingTest() throws DataTagsParseException {
        String tsCode = "DataTags: consists of A, B, X, Y." +
                        "A: one of AA, BB, CC, DD, EE, FF." +
                        "B: one of AA, BB, CC, DD, EE, FF." +
                        "X: one of AA, BB, CC, DD, EE, FF." +
                        "Y: one of AA, BB, CC, DD, EE, FF.";

        CompoundType ts = new TagSpaceParser().parse(tsCode).buildType("DataTags").get();

        String dgCodeOriginal = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: A=AA; B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; A=AA; B=FF]" +
                "    }" +
                "    {Three:" +
                "       [set: X=AA; Y=FF; A=AA; B=BB]" +
                "    }" +
                "  }" +
                "]";


        String dgCodeOpt = "[set: A=AA]" +
                "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; B=FF]" +
                "    }" +
                "    {Three:" +
                "       [set: X=AA; Y=FF; B=BB]" +
                "    }" +
                "  }" +
                "]";
        DecisionGraph dgOriginal = new DecisionGraphParser().parse(dgCodeOriginal).compile(ts);
        DecisionGraph dgOptimized = new DecisionGraphParser().parse(dgCodeOpt).compile(ts);

        /* Hack since .equals is not a good comparison function (unlike other equals - it compares 'id') */
        dgOptimized.setId(dgOriginal.getId());
//        assertNotEquals(dgOriginal, dgOptimized);

        /* Optimize and compare */
        TagSpaceOptimizer tagSpaceOptimizer = new TagSpaceOptimizer();
        dgOriginal = tagSpaceOptimizer.optimize(dgOriginal);

        dgOptimized.setId(dgOriginal.getId());
        assertEquals(compareDgs(dgOriginal.getStart(), dgOptimized.getStart()), true);

    }

    private boolean compareDgs(Node node1, Node node2) {
        System.out.println("  Node1: " + node1);
        System.out.println("  Node2: " + node2);

        if (node1 instanceof EndNode) {
            System.out.println("    [node1=EndNode]");
            if (!(node2 instanceof EndNode)) {
                System.out.println("    [node1=other]");
                return false;
            }

            return true;
        }

        if (node1 instanceof RejectNode) {
//            System.out.println("    [node1=RejectNode]");
            if (!(node2 instanceof RejectNode)) {
//                System.out.println("    [node1=other]");
                return false;
            }

            return true;
        }

        if (node1 instanceof TodoNode) {
//            System.out.println("    [node1=TodoNode]");
            if (!(node2 instanceof TodoNode)) {
//                System.out.println("    [node1=other]");
                return false;
            }

            TodoNode n1 = (TodoNode) node1;
            TodoNode n2 = (TodoNode) node2;
            // 1. Compare text
            if (!(n1.getTodoText().equals(n2.getTodoText()))) {
                return false;
            }

            // 2. Compare child
            return n1.hasNextNode() && n2.hasNextNode() && compareDgs(n1.getNextNode(), n2.getNextNode());

        }

        if (node1 instanceof CallNode) {
//            System.out.println("    [node1=CallNode]");
            if (!(node2 instanceof CallNode)) {
//                System.out.println("    [node1=other]");
                return false;
            }

        }

        if (node1 instanceof SetNode) {
//            System.out.println("    [node1=SetNode]");
            if (!(node2 instanceof SetNode)) {
//                System.out.println("    [node1=other]");
                return false;
            }

            SetNode n1 = (SetNode) node1;
            SetNode n2 = (SetNode) node2;
            // 1. Compare tags
            if (! (n1.getTags().equals(n2.getTags()))) {
                return false;
            }

            // 2. Compare child
            if (n1.hasNextNode() && n2.hasNextNode()) {
                return compareDgs(n1.getNextNode(), n2.getNextNode());
            }

            return false;
        }

        if (node1 instanceof AskNode) {
//            System.out.println("    [node1=AskNode]");
            if (!(node2 instanceof AskNode)) {
//                System.out.println("    [node1=other]");
                return false;
            }

            AskNode n1 = (AskNode) node1;
            AskNode n2 = (AskNode) node2;

            // Compare question
//            System.out.println(" ### " + n1.getText());
            if (!(n1.getText().equals(n2.getText())))
                return false;

            // Compare answers
//            System.out.println(" ### [1] " + n1.getAnswers());
//            System.out.println(" ### [2] " + n2.getAnswers());
//            System.out.println(" ### [=] " + n1.getAnswers().equals(n2.getAnswers()));
            if (!(n1.getAnswers().equals(n2.getAnswers()))) {
                return false;
            }

            // Recursive check childs
            for (Answer a1 : n1.getAnswers()) {
                if (! compareDgs(n1.getNodeFor(a1), n2.getNodeFor(a1))) {
                    return false;
                }
            }
            return true;
        }

        return true;
    }
}
