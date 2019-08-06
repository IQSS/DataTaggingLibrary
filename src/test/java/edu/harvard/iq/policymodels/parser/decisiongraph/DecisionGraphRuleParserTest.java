package edu.harvard.iq.policymodels.parser.decisiongraph;

import edu.harvard.iq.policymodels.parser.decisiongraph.DecisionGraphRuleParser;
import edu.harvard.iq.policymodels.parser.decisiongraph.DecisionGraphTerminalParser;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstConsiderOptionSubNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstConsiderNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstContinueNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstImport;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstInfoSubNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstNodeHead;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstPartNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstSectionNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstTermSubNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstTextSubNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.ParsedFile;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.error.ParserException;
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
public class DecisionGraphRuleParserTest {

    public DecisionGraphRuleParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void nodeHeadNoId() {
        Parser<AstNodeHead> joe = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.nodeHead("end"));
        assertEquals(new AstNodeHead(null, "end"), joe.parse("end"));
        assertEquals(new AstNodeHead(null, "end"), joe.parse(" end"));
        assertEquals(new AstNodeHead(null, "end"), joe.parse("end "));
        assertEquals(new AstNodeHead(null, "end"), joe.parse(" end <-- sut "));
    }

    @Test
    public void nodeHeadWithId() {
        Parser<AstNodeHead> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.nodeHead("end"));
        assertEquals(new AstNodeHead("id", "end"), sut.parse(">id<end"));
        assertEquals(new AstNodeHead("id", "end"), sut.parse(">id< end"));
        assertEquals(new AstNodeHead("id", "end"), sut.parse(">id<    end"));
        assertEquals(new AstNodeHead("id", "end"), sut.parse("   >id<    end   "));
        assertEquals(new AstNodeHead("177", "end"), sut.parse("   >177<    end   "));
        assertEquals(new AstNodeHead("abcdEFG123-._", "end"), sut.parse("   >abcdEFG123-._<    end   "));

    }

    @Test
    public void endNodeWithId() {
        Parser<AstEndNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.END_NODE);
        assertEquals(new AstEndNode("123"), sut.parse("[>123< end]"));
        assertEquals(new AstEndNode("123"), sut.parse("[ >123< end]"));
        assertEquals(new AstEndNode("123"), sut.parse("[>123< end ]"));
        assertEquals(new AstEndNode("123"), sut.parse("[>123<end]"));
        assertEquals(new AstEndNode("123"), sut.parse("[>123<\nend]"));
        assertEquals(new AstEndNode("123"), sut.parse("[>123< <-- That's the id? " + "\nend]"));
    }

    @Test
    public void endNodeNoId() {
        Parser<AstEndNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.END_NODE);
        assertEquals(new AstEndNode(null), sut.parse("[end]"));
        assertEquals(new AstEndNode(null), sut.parse("[ end]"));
        assertEquals(new AstEndNode(null), sut.parse("[ end ]"));
        assertEquals(new AstEndNode(null), sut.parse("[end ]"));
        assertEquals(new AstEndNode(null), sut.parse("[\nend]"));
        assertEquals(new AstEndNode(null), sut.parse("[<-- What, no id? " + "\nend]"));
    }

    @Test
    public void todoNodeWithId() {
        Parser<AstTodoNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.TODO_NODE);
        assertEquals(new AstTodoNode("123", "finalize this"), sut.parse("[>123< todo: finalize this]"));
        assertEquals(new AstTodoNode("123", "finalize this"), sut.parse("[  >123< todo:\nfinalize this  ]"));
        assertEquals(new AstTodoNode("123", "finalize this"), sut.parse("[>123<todo:finalize this]"));
    }

    @Test
    public void todoNodeNoId() {
        Parser<AstTodoNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.TODO_NODE);
        assertEquals(new AstTodoNode(null, "finalize this"), sut.parse("[ todo: finalize this]"));
        assertEquals(new AstTodoNode(null, "finalize this"), sut.parse("[todo:\nfinalize this]"));
        assertEquals(new AstTodoNode(null, "finalize this"), sut.parse("[todo:finalize this]"));
    }

    @Test
    public void callNodeNoId() {
        Parser<AstCallNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.CALL_NODE);
        assertEquals(new AstCallNode(null, "abcd#$%"), sut.parse("[ call: abcd#$%]"));
        assertEquals(new AstCallNode(null, "abcd#$%"), sut.parse("[call:\nabcd#$%]"));
        assertEquals(new AstCallNode(null, "abcd#$%"), sut.parse("[call:abcd#$%]"));
    }

    @Test
    public void callNodeWITHId() {
        Parser<AstCallNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.CALL_NODE);
        assertEquals(new AstCallNode("CN", "abcd#$%"), sut.parse("[>CN< call: abcd#$%]"));
        assertEquals(new AstCallNode("CN", "abcd#$%"), sut.parse("[>CN< call:\nabcd#$%]"));
        assertEquals(new AstCallNode("CN", "abcd#$%"), sut.parse("[>CN<call:abcd#$%]"));
    }

    @Test
    public void rejectNodeWithId() {
        Parser<AstRejectNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.REJECT_NODE);
        assertEquals(new AstRejectNode("123", "finalize this"), sut.parse("[>123< reject: finalize this]"));
        assertEquals(new AstRejectNode("123", "finalize this"), sut.parse("[  >123< reject:\nfinalize this  ]"));
        assertEquals(new AstRejectNode("123", "finalize this"), sut.parse("[>123<reject:finalize this]"));
    }

    @Test
    public void rejectNodeNoId() {
        Parser<AstRejectNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.REJECT_NODE);
        assertEquals(new AstRejectNode(null, "finalize this"), sut.parse("[ reject: finalize this]"));
        assertEquals(new AstRejectNode(null, "finalize this"), sut.parse("[reject:\nfinalize this]"));
        assertEquals(new AstRejectNode(null, "finalize this"), sut.parse("[reject:finalize this\n\n\n]"));

        assertEquals(new AstRejectNode(null, "finalize\nthis"), sut.parse("[ reject: finalize\nthis]"));
        assertEquals(new AstRejectNode(null, "finalize 16 of these !@#!%!#$!$"), sut.parse("[ reject: finalize 16 of these !@#!%!#$!$]"));
    }

    @Test
    public void rejectNodeWithCommas() {
        Parser<AstRejectNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.REJECT_NODE);
        String rejectText = "unfortunately, we cannot accept your dataset. You're likely breaching §7.6.1(ii) of BANANAA. Can we ask why?";
        assertEquals(new AstRejectNode(null, rejectText), sut.parse("[reject: " + rejectText + "]"));
    }

    @Test
    public void sectionNodeNoId() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();
        Parser<AstSectionNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.sectionNode(bodyParser));
        assertEquals(new AstSectionNode(null, new AstInfoSubNode("bla bla"),
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[ section: {title: bla bla}\n[todo: bla] ]"));
        assertEquals(new AstSectionNode(null,
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[ section: \n\n [todo: bla]\n\n\n ]"));
        assertEquals(new AstSectionNode(null, new AstInfoSubNode("bla bla"),
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[section:{title: bla bla}[todo: bla]]"));
    }

    @Test
    public void sectionNodeWithId() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();
        Parser<AstSectionNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.sectionNode(bodyParser));
        assertEquals(new AstSectionNode("id", new AstInfoSubNode("bla bla"),
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[>id< section: {title: bla bla}\n[todo: bla] ]"));
        assertEquals(new AstSectionNode("id",
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[>id<section: \n\n [todo: bla]\n\n\n ]"));
        assertEquals(new AstSectionNode("id", new AstInfoSubNode("bla bla"),
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[>id<section:{title: bla bla}[todo: bla]]"));
    }
    
    @Test(expected = ParserException.class) 
    public void partNodeNoId() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();
        Parser<AstPartNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.partNode(bodyParser));
        sut.parse("[-- {title: bla bla}\n[todo: bla] ]");
    }

    @Test
    public void partNodeWithId() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();
        Parser<AstPartNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.partNode(bodyParser));
        assertEquals(new AstPartNode("id", new AstInfoSubNode("bla bla"),
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[-->id< {title: bla bla}\n[todo: bla] --]"));
        assertEquals(new AstPartNode("id",
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[-->id< \n\n [todo: bla]\n\n\n --]"));
        assertEquals(new AstPartNode("id", new AstInfoSubNode("bla bla"),
                asList(new AstTodoNode(null, "bla"))),
                sut.parse("[-->id<{title: bla bla}[todo: bla]--]"));
    }
    
    @Test
    public void continueNodeWithId() {
        Parser<AstContinueNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.CONTINUE_NODE);
        assertEquals(new AstContinueNode("123"), sut.parse("[>123< continue]"));
        assertEquals(new AstContinueNode("123"), sut.parse("[ >123< continue]"));
        assertEquals(new AstContinueNode("123"), sut.parse("[>123< continue ]"));
        assertEquals(new AstContinueNode("123"), sut.parse("[>123<continue]"));
        assertEquals(new AstContinueNode("123"), sut.parse("[>123<\ncontinue]"));
        assertEquals(new AstContinueNode("123"), sut.parse("[>123< <-- That's the id? " + "\ncontinue]"));
    }
    
    @Test
    public void continueNodeWithoutId() {
        Parser<AstContinueNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.CONTINUE_NODE);
        assertEquals(new AstContinueNode(null), sut.parse("[ continue]"));
        assertEquals(new AstContinueNode(null), sut.parse("[ continue ]"));
        assertEquals(new AstContinueNode(null), sut.parse("[continue]"));
        assertEquals(new AstContinueNode(null), sut.parse("[\ncontinue]"));
        assertEquals(new AstContinueNode(null), sut.parse("[ <-- That's the id? " + "\ncontinue]"));
    }

    @Test
    public void atomicAssignmentSlotTest() {
        Parser<AstSetNode.AtomicAssignment> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.ATOMIC_ASSIGNMENT_SLOT);

        assertEquals(new AstSetNode.AtomicAssignment(asList("s"), "v"), sut.parse("s=v"));
        assertEquals(new AstSetNode.AtomicAssignment(asList("s"), "v"), sut.parse("s =v"));
        assertEquals(new AstSetNode.AtomicAssignment(asList("s"), "v"), sut.parse("s= v"));
        assertEquals(new AstSetNode.AtomicAssignment(asList("s"), "v"), sut.parse("s = v "));
        assertEquals(new AstSetNode.AtomicAssignment(asList("s"), "v"), sut.parse("s = v"));
        assertEquals(new AstSetNode.AtomicAssignment(asList("top", "mid", "bottom"), "aValue"), sut.parse("top/mid/bottom=aValue"));
        assertEquals(new AstSetNode.AtomicAssignment(asList("top", "mid", "bottom"), "aValue"),
                sut.parse("top / mid / bottom = aValue"));

    }

    @Test
    public void atomicAssignmentSlotKeywordTest() {
        Parser<AstSetNode.AtomicAssignment> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.ATOMIC_ASSIGNMENT_SLOT);
        assertEquals(new AstSetNode.AtomicAssignment(asList("gsk"), "set"), sut.parse("gsk=set"));
        assertEquals(new AstSetNode.AtomicAssignment(asList("ask"), "set"), sut.parse("ask=set"));
        assertEquals(new AstSetNode.AtomicAssignment(asList("ask", "askyy", "askset", "askMMset"), "set"), sut.parse("ask/askyy/askset/askMMset=set"));
    }

    @Test
    public void aggregateAssignmentSlotTest() {
        Parser<AstSetNode.AggregateAssignment> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.AGGREGATE_ASSIGNMENT_SLOT);
        assertEquals(new AstSetNode.AggregateAssignment(asList("s"), Arrays.asList("v")), sut.parse("s+=v"));
        assertEquals(new AstSetNode.AggregateAssignment(asList("top", "mid", "bottom"),
                asList("val1", "val2", "val3")),
                sut.parse("top/mid/bottom+=val1, val2, val3"));
    }

    @Test
    public void aggregateAssignmentSlotWithKeywordsTest() {
        Parser<AstSetNode.AggregateAssignment> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.AGGREGATE_ASSIGNMENT_SLOT);
        assertEquals(new AstSetNode.AggregateAssignment(asList("set"), Arrays.asList("ask")), sut.parse("set+=ask"));
        assertEquals(new AstSetNode.AggregateAssignment(asList("xset"), Arrays.asList("xask")), sut.parse("xset+=xask"));
        assertEquals(new AstSetNode.AggregateAssignment(asList("setx"), Arrays.asList("askx")), sut.parse("setx+=askx"));
        assertEquals(new AstSetNode.AggregateAssignment(asList("xsetx"), Arrays.asList("xaskx")), sut.parse("xsetx+=xaskx"));
        assertEquals(new AstSetNode.AggregateAssignment(asList("xsetxsetx"), Arrays.asList("xaskxsetx")), sut.parse("xsetxsetx+=xaskxsetx"));
    }

    @Test
    public void setNodeSimple() {
        Parser<AstSetNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.SET_NODE);
        AstSetNode expected = new AstSetNode(null, asList(new AstSetNode.AtomicAssignment(asList("k"), "v")));
        assertEquals(expected, sut.parse("[set: k=v]"));
        assertEquals(expected, sut.parse("[set:k=v]"));
        assertEquals(expected, sut.parse("[set: k=v ]"));
    }

    @Test
    public void setNodeSimpleWithId() {
        Parser<AstSetNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.SET_NODE);
        AstSetNode expected = new AstSetNode("Set-._", asList(new AstSetNode.AtomicAssignment(asList("k"), "v")));
        assertEquals(expected, sut.parse("[>Set-._<set: k=v]"));
        assertEquals(expected, sut.parse("[>Set-._< set: k=v]"));
        assertEquals(expected, sut.parse("[>Set-._< set: k = v ]"));
    }

    @Test
    public void setNodeSimpleAggregate() {
        Parser<AstSetNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.SET_NODE);
        AstSetNode expected = new AstSetNode("Set-._", asList(new AstSetNode.AggregateAssignment(asList("k"), asList("v"))));
        assertEquals(expected, sut.parse("[>Set-._<set: k+=v]"));
        assertEquals(expected, sut.parse("[>Set-._< set: k += v]"));
        assertEquals(expected, sut.parse("[>Set-._< set: k+=v ]"));
    }

    @Test
    public void setNodeAggregate() {
        Parser<AstSetNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.SET_NODE);
        AstSetNode expected = new AstSetNode("anId",
                asList(new AstSetNode.AggregateAssignment(asList("k"), asList("v1", "v2"))));
        assertEquals(expected, sut.parse("[>anId<set: k += v1, v2]"));
        assertEquals(expected, sut.parse("[>anId<set: k+=v1, v2]"));
        assertEquals(expected, sut.parse("[>anId<set: k+=v1,v2]"));

        expected = new AstSetNode("anId",
                asList(new AstSetNode.AggregateAssignment(asList("k1", "k2"), asList("v"))));

        assertEquals(expected, sut.parse("[>anId< set: k1/k2 += v]"));
        assertEquals(expected, sut.parse("[>anId< set: k1/k2+=v ]"));

        expected = new AstSetNode("anId",
                asList(new AstSetNode.AggregateAssignment(asList("k1", "k2", "k3"), asList("v1", "v2", "v3"))));
        assertEquals(expected, sut.parse("[>anId< set: k1/k2/k3 += v1, v2, v3]"));
        assertEquals(expected, sut.parse("[>anId< set: k1/k2/k3 += v1,v2,v3]"));
        assertEquals(expected, sut.parse("[>anId<set:k1/k2/k3+=v1,v2,v3]"));
    }

    @Test
    public void setNode() {
        Parser<AstSetNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.SET_NODE);
        AstSetNode expected = new AstSetNode("anId",
                asList(new AstSetNode.AggregateAssignment(asList("k"), asList("v1", "v2")),
                        new AstSetNode.AggregateAssignment(asList("j", "j2"), asList("v")),
                        new AstSetNode.AtomicAssignment(asList("r"), "vee"),
                        new AstSetNode.AggregateAssignment(asList("l1", "l2", "l3"), asList("v1", "v2", "v3"))));

        assertEquals(expected, sut.parse("[>anId<set: k += v1, v2; j/j2+=v; r=vee; l1/l2/l3+=v1,v2,v3 ]"));
    }

    @Test
    public void textSubNode() {
        Parser<AstTextSubNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.TEXT_SUBNODE);

        assertEquals(new AstTextSubNode("lorem ipsum"), sut.parse("{text: lorem ipsum}"));
        assertEquals(new AstTextSubNode("lorem 333ipsum"), sut.parse("{text: lorem 333ipsum}"));
        assertEquals(new AstTextSubNode("lorem ]]]ipsum"), sut.parse("{text: lorem ]]]ipsum}"));
        assertEquals(new AstTextSubNode("38905673n;jlqrsgh-12957gh1kqjng5379sa  fqwrgwgt <> ouou 5"),
                sut.parse("{text: 38905673n;jlqrsgh-12957gh1kqjng5379sa  fqwrgwgt <> ouou 5}"));
    }

    @Test
    public void termSubNode() {
        Parser<AstTermSubNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.TERM_SUBNODE);

        assertEquals(new AstTermSubNode("lorem", "lorem ipsum"), sut.parse("{lorem: lorem ipsum}"));
        assertEquals(new AstTermSubNode("lorem ipsum", "lorem 333ipsum"), sut.parse("{lorem ipsum: lorem 333ipsum}"));
        assertEquals(new AstTermSubNode("lorem", "lorem ]]]ipsum"), sut.parse("{lorem: lorem ]]]ipsum}"));
    }

    @Test
    public void importNode() {
        Parser<AstImport> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.IMPORT);
        assertEquals(new AstImport("SimpleValue", "pp1"), sut.parse("[#import pp1: SimpleValue]"));
        assertEquals(new AstImport("C://User/Docs/dg1.dg", "pp2"), sut.parse("[#import pp2: C://User/Docs/dg1.dg]"));
        assertEquals(new AstImport("/users/joe/pm/pm-9/dg.dg", "pp3"), sut.parse("[#import pp3: /users/joe/pm/pm-9/dg.dg]"));
        assertEquals(new AstImport("C://file with space", "pp4"), sut.parse("[#import pp4: C://file with space]"));
    }

    @Test
    public void termsSubNode() {
        Parser<List<AstTermSubNode>> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.TERMS_SUBNODE);

        assertEquals(Collections.<AstTermSubNode>emptyList(),
                sut.parse("{terms:}"));
        assertEquals(asList(new AstTermSubNode("lorem", "lorem ipsum")),
                sut.parse("{terms: {lorem: lorem ipsum}}"));
        List<AstTermSubNode> expectedTermList = asList(new AstTermSubNode("lorem", "lorem ipsum"),
                new AstTermSubNode("dolor", "lorem 333ipsum"),
                new AstTermSubNode("Gobala cabana", "#dolor sit #amet"));

        assertEquals(expectedTermList,
                sut.parse("{terms: {lorem: lorem ipsum}{dolor: lorem 333ipsum}{Gobala cabana: #dolor sit #amet}}"));
        assertEquals(expectedTermList,
                sut.parse("{terms:{lorem: lorem ipsum}\n"
                        + "{dolor: lorem 333ipsum} \n"
                        + "{Gobala cabana: #dolor sit #amet}}"));
        assertEquals(expectedTermList,
                sut.parse("{terms:{lorem: lorem ipsum}\n"
                        + "{dolor: lorem 333ipsum} <-- can someone validate this?!\n"
                        + "{Gobala cabana: #dolor sit #amet}}"));
        assertEquals(expectedTermList,
                sut.parse("{terms:{lorem: lorem ipsum}\n"
                        + "<* {dolor: lorem 333ipsum} old version, can we remove this? *>\n"
                        + "{dolor: lorem 333ipsum} <-- can someone validate this?!\n"
                        + "{Gobala cabana: #dolor sit #amet}}"));

    }

    @Test
    public void answerNodeTest() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();

        Parser<AstAnswerSubNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.answerSubNode(bodyParser));

        assertEquals(new AstAnswerSubNode("yes", asList(new AstTodoNode("td", "do this"), new AstTodoNode("te", "do that"))),
                sut.parse("{yes: [>td< todo:do this][>te< todo: do that]}"));

        assertEquals(new AstAnswerSubNode("Dorothy Stein", asList(new AstTodoNode("td", "do this"), new AstTodoNode("te", "do that"))),
                sut.parse("{Dorothy Stein: [>td< todo:do this][>te< todo: do that]}"));
    }

    @Test
    public void answersNodeTest() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();

        Parser<List<AstAnswerSubNode>> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.answersSubNode(bodyParser));

        assertEquals(asList(new AstAnswerSubNode("yes", asList(new AstTodoNode("td", "do this"), new AstTodoNode("te", "do that")))),
                sut.parse("{answers: {yes: [>td< todo:do this][>te< todo: do that]}}"));

        assertEquals(asList(new AstAnswerSubNode("yes", asList(new AstTodoNode("td", "do this"))),
                new AstAnswerSubNode("no", asList(new AstTodoNode("td", "do this"), new AstTodoNode("te", "do that"))),
                new AstAnswerSubNode("maybe so", asList(new AstTodoNode("td", "do this"), new AstEndNode(null)))),
                sut.parse("{answers: "
                        + "{yes: [>td< todo:do this]}"
                        + "{no: [>td< todo:do this][>te< todo: do that]}"
                        + "{maybe so: [>td< todo:do this][end]}"
                        + "}"));
    }

    @Test
    public void testConsiderNode() {
        String program = "[>44< consider:\n"
                + "  {slot:Greeting } \n"
                + "  {options: \n"
                + "  	{hello:[set: Subject+= world] \n}}"
                + "  {else:  [set:Subject+=planet] }]\n"
                + "[end]";
        List<? extends AstNode> expected = asList(new AstConsiderNode("44", asList("Greeting"),
                        asList(new AstConsiderOptionSubNode(
                                asList("hello"),
                                asList(new AstSetNode(null, asList(new AstSetNode.AggregateAssignment(asList("Subject"), asList("world"))))))),
                        asList(new AstSetNode(null, asList(new AstSetNode.AggregateAssignment(asList("Subject"), asList("planet")))))),
                new AstEndNode(null)
        );

        Parser<ParsedFile> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.graphParser());

        assertEquals(expected, sut.parse(program).getAstNodes());
    }

    @Test
    public void testWhenNode() {
        String program = "[>44< when:\n"
                       + "  {Greeting=hello:[set: Subject+= world] \n}"
                       + "  {else:  [set:Subject+=planet] }]\n"
                       + "[end]";
        List<? extends AstNode> expected = asList(new AstConsiderNode("44", null,
                        asList(new AstConsiderOptionSubNode(
                                asList(new AstSetNode.AtomicAssignment(asList("Greeting"), "hello")),
                                asList(new AstSetNode(null, asList(new AstSetNode.AggregateAssignment(asList("Subject"), asList("world"))))))),
                        asList(new AstSetNode(null, asList(new AstSetNode.AggregateAssignment(asList("Subject"), asList("planet")))))),
                new AstEndNode(null)
        );

        Parser<ParsedFile> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.graphParser());

        assertEquals(expected, sut.parse(program).getAstNodes());
    }

    @Test
    public void askNodeTest() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();
        Parser<AstAskNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.askNode(bodyParser));

        assertEquals(new AstAskNode("id",
                new AstTextSubNode("why do that?"),
                asList(new AstTermSubNode("that", "a thingy")),
                asList(new AstAnswerSubNode("yes", asList(new AstEndNode(null))),
                        new AstAnswerSubNode("no", asList(new AstEndNode("no-end"))))),
                sut.parse("[>id< ask:"
                        + " {text: why do that?} "
                        + "  {terms: {that: a thingy} }"
                        + "   {answers: "
                        + "    {yes: [end]} "
                        + "    {no: [>no-end< end]} "
                        + "}]"));

        assertEquals(new AstAskNode("id",
                new AstTextSubNode("why do that?"),
                null,
                asList(new AstAnswerSubNode("yes", asList(new AstEndNode(null))),
                        new AstAnswerSubNode("no", asList(new AstEndNode("no-end"))))),
                sut.parse("[>id< ask:"
                        + " {text: why do that?} "
                        + "   {answers: "
                        + "    {yes: [end]} "
                        + "    {no: [>no-end< end]} "
                        + "}]"));
    }

    @Test
    public void sectionNodeTest() {
        Parser<List<? extends AstNode>> bodyParser
                = Parsers.or(DecisionGraphRuleParser.END_NODE, DecisionGraphRuleParser.TODO_NODE).many().cast();
        Parser<AstSectionNode> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.sectionNode(bodyParser));

        assertEquals(new AstSectionNode("id",
                new AstInfoSubNode("TO-DO List"),
                asList(new AstTodoNode(null, "first thing"),
                        new AstTodoNode(null, "second thing"))),
                sut.parse("[>id< section:"
                        + " {title: TO-DO List} "
                        + "  [todo: first thing]"
                        + "   [todo: second thing] "
                        + "]"));
    }

    @Test
    public void smallProgramTest() {
        String program = "[>44< ask:\n"
                + "	{text: Do the data concern living persons?} <-- The big question here\n"
                + "  {answers: \n"
                + "  	{yes:\n"
                + "  		[set: identity=personData] <--- Oh, no you don't\n"
                + "  		[call: medRecs ]\n"
                + "  		[todo: Arrest and Conviction Records]}\n"
                + "  	{no:  [set:identity=noPersonData] }\n"
                + "    {not sure: [reject:please check]}}]\n"
                + "[call: dua]\n"
                + "[end]";
        List<? extends AstNode> expected = asList(
                new AstAskNode("44", new AstTextSubNode("Do the data concern living persons?"), null,
                        asList(new AstAnswerSubNode("yes",
                                asList(new AstSetNode(null, asList(new AstSetNode.AtomicAssignment(asList("identity"), "personData"))),
                                        new AstCallNode(null, "medRecs"),
                                        new AstTodoNode(null, "Arrest and Conviction Records"))),
                                new AstAnswerSubNode("no",
                                        asList(new AstSetNode(null, asList(new AstSetNode.AtomicAssignment(asList("identity"), "noPersonData"))))),
                                new AstAnswerSubNode("not sure",
                                        asList(new AstRejectNode(null, "please check"))))),
                new AstCallNode(null, "dua"),
                new AstEndNode(null)
        );

        Parser<ParsedFile> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.graphParser());

        assertEquals(expected, sut.parse(program).getAstNodes());
    }
    
    
    @Test
    public void parsedFileTest_singleImport() {
        String program = "[#import somefile: C://somefile]\n" +
                         "[todo: something]";
        Parser<ParsedFile> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.graphParser());
        
        List<AstImport> imports = Arrays.asList( new AstImport("C://somefile", "somefile"));
        List<AstNode> nodes = Arrays.asList( new AstTodoNode(null, "something") );
        
        ParsedFile actual = sut.parse(program);
        
        assertEquals( imports, actual.getImports() );
        assertEquals( nodes, actual.getAstNodes()  );
    }
    
    @Test
    public void parsedFileTest_doubleImport() {
        String program = "[#import somefile: C://somefile]\n" +
                         "[#import someOtherFile: C://somefile2]\n" +
                         "[todo: something]";
        Parser<ParsedFile> sut = DecisionGraphTerminalParser.buildParser(DecisionGraphRuleParser.graphParser());
        
        List<AstImport> imports = Arrays.asList( new AstImport("C://somefile", "somefile"), new AstImport("C://somefile2", "someOtherFile"));
        List<AstNode> nodes = Arrays.asList( new AstTodoNode(null, "something") );
        
        ParsedFile actual = sut.parse(program);
        
        assertEquals( imports, actual.getImports() );
        assertEquals( nodes, actual.getAstNodes()  );
    }
}
