package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.graphs.Answer;
import static edu.harvard.iq.datatags.model.graphs.Answer.NO;
import static edu.harvard.iq.datatags.model.graphs.Answer.YES;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.runtime.listeners.RuntimeEngineSilentListener;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import static edu.harvard.iq.util.DecisionGraphHelper.linearYesChart;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class RuntimeEngineStateTest {

    public RuntimeEngineStateTest() {
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
    public void testSnapshots() {
        CompoundSlot ct = new CompoundSlot("considerAnswer", "");
        CompoundValue tags = ct.createInstance();
        String chartId = "rec";
        DecisionGraph dg = linearYesChart(chartId, 3);
        dg.setTopLevelType(new CompoundSlot("", ""));
        AskNode n2 = (AskNode) dg.getNode(chartId + "_2");
        ConsiderNode consider = n2.setNodeFor(NO, dg.add(new ConsiderNode("1", null)));
        CallNode caller = consider.setNodeFor(ConsiderAnswer.get(tags), dg.add(new CallNode("Caller")));
        caller.setCalleeNodeId(chartId + "_1");
        caller.setNextNode(new EndNode("CallerEnd"));

        List<Answer> answers = C.list(YES, NO,
                YES, NO,
                YES, NO,
                YES, NO,
                YES, NO);
        RuntimeEngine ngn = new RuntimeEngine();
        ngn.setDecisionGraph(dg);
        ngn.setListener(new RuntimeEngineSilentListener());

        try {
            ngn.start();
            for (Answer ans : answers) {
                ngn.consume(ans);
            }

            RuntimeEngineState snapshot1 = ngn.createSnapshot();

            ngn.consume(YES);
            RuntimeEngineState snapshot2 = ngn.createSnapshot();

            assertFalse(snapshot1.equals(snapshot2));

            ngn.applySnapshot(snapshot1);

            assertEquals(snapshot1, ngn.createSnapshot());

        } catch (DataTagsRuntimeException ex) {
            Logger.getLogger(ChartRunningTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
