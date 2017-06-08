package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import static edu.harvard.iq.datatags.model.graphs.Answer.*;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import static edu.harvard.iq.datatags.util.CollectionHelper.*;
import static edu.harvard.iq.util.DecisionGraphHelper.*;
import java.util.Arrays;
import java.util.List;
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

        start.setNodeFor(ConsiderAnswer.get(tags), c1.add(new ConsiderNode("2", null)))
                .setNodeFor(ConsiderAnswer.get(tags), c1.add(new ConsiderNode("3",  null)))
                .setNodeFor(ConsiderAnswer.get(tags), c1.add(new ConsiderNode("4",  null)))
                .setNodeFor(ConsiderAnswer.get(tags), c1.add(new EndNode("END")));

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
    public void chartWithCall() throws DataTagsParseException {
        String code = "[>a< todo:a][>b< todo:a][>c< call:n][>e<end][>n< end]";
        DecisionGraph chart = new DecisionGraphParser().parse(code).compile(new CompoundSlot("", ""));

        assertExecutionTrace(chart, Arrays.asList("a", "b", "c", "n", "e"), false);
    }

    @Test
    public void chartWithRecursion() {
        String chartId = "rec";
        DecisionGraph rec = linearYesChart(chartId, 3);

        AskNode n2 = (AskNode) rec.getNode(chartId + "_2");
        CallNode caller = n2.addAnswer(NO, rec.add(new CallNode("Caller")));
        caller.setCalleeNode(new ToDoNode(chartId + "_1", ""));
        caller.setNextNode(new EndNode("CallerEnd"));

        assertExecutionTrace(rec,
                C.list(YES, NO,
                        YES, NO,
                        YES, YES, YES),
                C.list("rec_1", "rec_2", "Caller",
                        "rec_1", "rec_2", "Caller",
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
        caller.setCalleeNode(new ToDoNode(chartId + "_1", ""));
        caller.setNextNode(new EndNode("CallerEnd"));

        assertExecutionTrace(rec,
                C.list(YES, NO,
                        YES, NO,
                        YES, NO,
                        YES, NO,
                        YES, NO,
                        YES, YES, YES),
                C.list("rec_1", "rec_2", "Caller",
                        "rec_1", "rec_2", "Caller",
                        "rec_1", "rec_2", "Caller",
                        "rec_1", "rec_2", "Caller",
                        "rec_1", "rec_2", "Caller",
                        "rec_1", "rec_2", "rec_3", "rec_END",
                        "CallerEnd", "CallerEnd", "CallerEnd", "CallerEnd", "CallerEnd"),
                true);

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

        CallNode start = main.add(new CallNode("1", new ToDoNode("sub_a_1", "")));
        start.setNextNode(main.add(new CallNode("2", new ToDoNode("sub_b_1", ""))))
                .setNextNode(main.add(new CallNode("3",new ToDoNode("sub_c_1", ""))))
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

}
