package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import static edu.harvard.iq.datatags.model.graphs.Answer.*;
import edu.harvard.iq.datatags.model.graphs.nodes.ContinueNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions.GreaterThanExp;
import edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions.TrueExp;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.ContentReader;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.parser.decisiongraph.MemoryContentReader;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import static edu.harvard.iq.datatags.util.CollectionHelper.*;
import static edu.harvard.iq.util.DecisionGraphHelper.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Here we test chart runs.
 *
 * @author michael
 */
public class ChartRunningTest {

    @Test
    public void linearChart() {
        String flowChartName = "flowChart";
        DecisionGraph c1 = new DecisionGraph(flowChartName);

        AskNode start = c1.add(new AskNode("1"));
        start.addAnswer(YES, c1.add(new AskNode("2")))
                .addAnswer(YES, c1.add(new AskNode("3")))
                .addAnswer(YES, c1.add(new AskNode("4")))
                .addAnswer(YES, c1.add(new EndNode("END")));

        c1.setStart(start);

        assertExecutionTrace(c1,
                Arrays.asList("1", "2", "3", "4", "END"), false);

    }

    @Test
    public void chartWithConsdier() {

        String flowChartName = "flowChart";
        DecisionGraph c1 = new DecisionGraph(flowChartName);
        CompoundSlot ct = new CompoundSlot("topLevel", "");
        CompoundValue tags = ct.createInstance();
        ConsiderNode start = c1.add(new ConsiderNode("1",  null));

        start.setNodeFor(tags, c1.add(new ConsiderNode("2", null)))
                .setNodeFor(tags, c1.add(new ConsiderNode("3",  null)))
                .setNodeFor(tags, c1.add(new ConsiderNode("4",  null)))
                .setNodeFor(tags, c1.add(new EndNode("END")));

        c1.setStart(start);

        assertExecutionTrace(c1,
                Arrays.asList("1", "2", "3", "4", "END"), false);

    }

    @Test
    public void chartWithBranches() {
        String flowChartName = "flowChart";
        DecisionGraph c1 = new DecisionGraph(flowChartName);

        AskNode start = c1.add(new AskNode("1"));
        start.addAnswer(YES, c1.add(new AskNode("2")))
                .addAnswer(NO, c1.add(new AskNode("3")))
                .addAnswer(YES, c1.add(new AskNode("4")))
                .addAnswer(NO, c1.add(new EndNode("END")));

        ((AskNode) c1.getNode("1")).addAnswer(NO, c1.add(new AskNode("x")));
        ((AskNode) c1.getNode("2")).addAnswer(YES, c1.add(new AskNode("xx")));
        ((AskNode) c1.getNode("3")).addAnswer(NO, c1.add(new AskNode("xxx")));
        ((AskNode) c1.getNode("4")).addAnswer(YES, c1.add(new EndNode("xxxx")));

        c1.setStart(start);

        assertExecutionTrace(c1,
                C.list(YES, NO, YES, NO),
                C.list("1", "2", "3", "4", "END"), false);

    }

    @Test
    public void chartWithCall() throws DataTagsParseException, IOException {
        String code = "[>a< todo:a][>b< todo:a][>c< call:n][>e<end][-->n< {title: bla} [>d< end]--]";
        
        Map<Path, String> pathToString = new HashMap<>();
        PolicyModelData pmd = new PolicyModelData();
        pmd.setDecisionGraphPath(Paths.get("/main.dg"));
        pmd.setMetadataFile(Paths.get("/test/main.dg"));
        pathToString.put(Paths.get("/main.dg"), code);
        ContentReader contentReader = new MemoryContentReader(pathToString);
        DecisionGraphCompiler dgc = new DecisionGraphCompiler(contentReader);
        DecisionGraph chart = dgc.compile(new CompoundSlot("", ""), pmd, new ArrayList<>());
        
        assertExecutionTrace(chart, Arrays.asList("[.." + File.separator + "main.dg]a",
                "[.." + File.separator + "main.dg]b", "[.." + File.separator + "main.dg]c", 
                "[.." + File.separator + "main.dg]n","d",
                "e"), false);
    }

    @Test
    public void chartWithRecursion() {
        String chartId = "rec";
        DecisionGraph rec = linearYesChart(chartId, 3);

        AskNode n2 = (AskNode) rec.getNode(chartId + "_2");
        CallNode caller = n2.addAnswer(NO, rec.add(new CallNode("Caller")));
        caller.setCalleeNode(new SectionNode("bla", "sec_1",rec.getStart(), new EndNode("SectionEnd")));
//        caller.setCalleeNode(rec.getStart()); //put the first node
        caller.setNextNode(new EndNode("CallerEnd"));

        assertExecutionTrace(rec,
                C.list(YES, NO,
                        YES, NO,
                        YES, YES, YES),
                C.list("rec_1", "rec_2", "Caller", "sec_1",
                        "rec_1", "rec_2", "Caller", "sec_1",
                        "rec_1", "rec_2", "rec_3", "rec_END",
                        "CallerEnd", "CallerEnd"),
                true);

    }

    /**
     * Same as above, but with more stack frames.
     */
    @Test
    public void chartWithDeeperRecursion() {
        String chartId = "rec";
        DecisionGraph rec = linearYesChart(chartId, 3);

        AskNode n2 = (AskNode) rec.getNode(chartId + "_2");
        CallNode caller = n2.addAnswer(NO, rec.add(new CallNode("Caller")));
        caller.setCalleeNode(new SectionNode("bla", "sec_1",rec.getStart(), new EndNode("SectionEnd")));
        caller.setNextNode(new EndNode("CallerEnd"));

        assertExecutionTrace(rec,
                C.list(YES, NO,
                        YES, NO,
                        YES, NO,
                        YES, NO,
                        YES, NO,
                        YES, YES, YES),
                C.list("rec_1", "rec_2", "Caller", "sec_1",
                        "rec_1", "rec_2", "Caller","sec_1",
                        "rec_1", "rec_2", "Caller", "sec_1",
                        "rec_1", "rec_2", "Caller", "sec_1",
                        "rec_1", "rec_2", "Caller", "sec_1",
                        "rec_1", "rec_2", "rec_3", "rec_END",
                        "CallerEnd", "CallerEnd", "CallerEnd", "CallerEnd", "CallerEnd"),
                true);

    }
    
    @Test
    public void chartWithContinue() throws IOException {
        String code = "[>1< section:\n" +
                    "   [>2< ask:\n" +
                    "     {text:what?}\n" +
                    "     {answers:\n" +
                    "       {yes:[>3<end]}\n" +
                    "       {no:[>4<continue]}\n" +
                    "     }\n" +
                    "  ]\n" +
                    "]\n" +
                    "[>6<todo: todo]";
        Map<Path, String> pathToString = new HashMap<>();
        PolicyModelData pmd = new PolicyModelData();
        pmd.setDecisionGraphPath(Paths.get("/main.dg"));
        pmd.setMetadataFile(Paths.get("/test/main.dg"));
        pathToString.put(Paths.get("/main.dg"), code);
        ContentReader contentReader = new MemoryContentReader(pathToString);
        DecisionGraphCompiler dgc = new DecisionGraphCompiler(contentReader);
        DecisionGraph chart = dgc.compile(new CompoundSlot("", ""), pmd, new ArrayList<>());
        
        String flowChartName = "flowChart";
        DecisionGraph c1 = new DecisionGraph(flowChartName);
        
        AskNode start = c1.add(new AskNode("2"));
        start.addAnswer(YES, c1.add(new EndNode("3")));
        start.addAnswer(NO, c1.add(new ContinueNode("4")));
        SectionNode section = new SectionNode("", "1", start, new ToDoNode("6", "todo").setNextNode(new EndNode("SYN-END")));
        c1.setStart(section);

        assertExecutionTrace(chart,
                C.list(NO),
                C.list("[.." + File.separator + "main.dg]1", "[.." + File.separator + "main.dg]2",
                        "[.." + File.separator + "main.dg]4", "[.." + File.separator + "main.dg]6","[SYN-END]"), false);
        
        assertExecutionTrace(chart,
                C.list(YES),
                C.list("[.." + File.separator + "main.dg]1", "[.." + File.separator + "main.dg]2", "3"), false);
    }

    /**
     * Testing a chart where the main chart consists only from calls to
     * sub-charts.
     */
    @Test
    public void testThreadedCode() {
        DecisionGraph main = new DecisionGraph("threaded-main");

        DecisionGraph subA = linearYesChart("sub_a", 3);
        DecisionGraph subB = linearYesChart("sub_b", 3);
        DecisionGraph subC = linearYesChart("sub_c", 3);

        CallNode start = main.add(new CallNode("1",subA.getStart()));
        start.setNextNode(main.add(new CallNode("2", subB.getStart())))
                .setNextNode(main.add(new CallNode("3",subC.getStart())))
                .setNextNode(main.add(new EndNode("END")));
        main.add(subA.getStart());
        main.add(subB.getStart());
        main.add(subC.getStart());

        main.setStart(start);

        assertExecutionTrace(main,
                C.list("1", "sub_a_1", "sub_a_2", "sub_a_3", "sub_a_END",
                        "2", "sub_b_1", "sub_b_2", "sub_b_3", "sub_b_END",
                        "3", "sub_c_1", "sub_c_2", "sub_c_3", "sub_c_END",
                        "END"),
                false);
    }
    
    @Test
    public void testAtomicBooleanExpression() throws SyntaxErrorException, SemanticsErrorException {
        String code = "DataTags : consists of Bag, Burrito.\n" +
                      "Bag : one of banana_leaf, paper, plastic.\n" +
                      "Burrito : consists of Wrap, Main, Side.\n" +
                      "Wrap : one of corn, full_grain, wheat.\n" +
                      "Main : one of  chicken, tofu, beef.\n" +
                      "Side : some of rice, corn, guacamole, cream, cheese.";
        CompoundSlot lunchType = new TagSpaceParser().parse(code).buildType("DataTags").get();
        CompoundSlot burritoType = (CompoundSlot) lunchType.getSubSlot("Burrito");
        AtomicSlot mainType = (AtomicSlot) burritoType.getSubSlot("Main");
        
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.put( mainType.valueOf("chicken") );
        val.put( burrito );
        
        String flowChartName = "flowChart";
        DecisionGraph c1 = new DecisionGraph(flowChartName);
        
        AskNode start = new AskNode("1");
        Answer ansA = Answer.withName("A");
        Answer ansB = Answer.withName("B");
        Answer ansC = Answer.withName("C");
        Answer ansD = Answer.withName("D");
        
        start.addAnswer(ansA, start)
        .addAnswer(ansB, start, new GreaterThanExp(Arrays.asList("Burrito", "Main"), mainType.valueOf("tofu")))
        .addAnswer(ansC, start)
        .addAnswer(ansD, start);

        Assert.assertEquals(start.getEnabledAnswers(val), Arrays.asList(ansA, ansC, ansD));
    }

}
