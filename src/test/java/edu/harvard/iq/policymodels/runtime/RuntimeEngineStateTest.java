 package edu.harvard.iq.policymodels.runtime;

import edu.harvard.iq.policymodels.runtime.RuntimeEngineState;
import edu.harvard.iq.policymodels.runtime.RuntimeEngine;
import edu.harvard.iq.policymodels.model.PolicyModel;
import edu.harvard.iq.policymodels.model.decisiongraph.DecisionGraph;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.AskNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.CallNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.EndNode;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.decisiongraph.Answer;
import static edu.harvard.iq.policymodels.model.decisiongraph.Answer.NO;
import static edu.harvard.iq.policymodels.model.decisiongraph.Answer.YES;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ConsiderNode;
import edu.harvard.iq.policymodels.model.policyspace.values.CompoundValue;
import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.policymodels.runtime.listeners.RuntimeEngineSilentListener;
import static edu.harvard.iq.policymodels.util.CollectionHelper.C;
import static edu.harvard.iq.util.DecisionGraphHelper.linearYesChart;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class RuntimeEngineStateTest {

    @Test
    public void testSnapshots() {
        CompoundSlot ct = new CompoundSlot("considerAnswer", "");
        CompoundValue tags = ct.createInstance();
        String chartId = "rec";
        DecisionGraph dg = linearYesChart(chartId, 3);
        AskNode n2 = (AskNode) dg.getNode(chartId + "_2");
        ConsiderNode consider = n2.addAnswer(NO, dg.add(new ConsiderNode("1", null)));
        CallNode caller = consider.setNodeFor(tags, dg.add(new CallNode("Caller")));
        caller.setCalleeNode(dg.getStart());
        caller.setNextNode(new EndNode("CallerEnd"));

        List<Answer> answers = C.list(YES, NO,
                YES, NO,
                YES, NO,
                YES, NO,
                YES, NO);
        RuntimeEngine ngn = new RuntimeEngine();
        PolicyModel pm = new PolicyModel();
        pm.setDecisionGraph(dg);
        pm.setSpaceRoot(new CompoundSlot("", ""));
        
        ngn.setModel(pm);
        ngn.setListener(new RuntimeEngineSilentListener());

        try {
            ngn.start();
            answers.forEach(ngn::consume);

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
