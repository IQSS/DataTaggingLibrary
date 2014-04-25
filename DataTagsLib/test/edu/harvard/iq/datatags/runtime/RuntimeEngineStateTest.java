/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.values.Answer;
import static edu.harvard.iq.datatags.model.values.Answer.NO;
import static edu.harvard.iq.datatags.model.values.Answer.YES;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.runtime.listeners.RuntimeEngineSilentListener;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import static edu.harvard.iq.util.ChartHelper.chartSet;
import static edu.harvard.iq.util.ChartHelper.linearYesChart;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        String chartId = "rec";
		FlowChart rec = linearYesChart(chartId, 3);
		
		AskNode n2 = (AskNode)rec.getNode(chartId+"_2");
		CallNode caller = n2.setNodeFor(NO, rec.add(new CallNode("Caller")));
		caller.setCalleeChartId(chartId);
		caller.setCalleeNodeId( chartId + "_1");
		caller.setNextNode( new EndNode("CallerEnd") );
		
		FlowChartSet fcs = chartSet( rec );
		
        List<Answer> answers = C.list(YES, NO, 
						YES, NO,
						YES, NO,
						YES, NO,
						YES, NO); 
        RuntimeEngine ngn = new RuntimeEngine();
		ngn.setChartSet(fcs);
		ngn.setListener( new RuntimeEngineSilentListener() );
		
		try {
			ngn.start( chartId );
			for ( Answer ans : answers ) {
                ngn.consume(ans);
            }
            
            RuntimeEngineState snapshot1 = ngn.createSnapshot();
            
            ngn.consume(YES);
            RuntimeEngineState snapshot2 = ngn.createSnapshot();
            
            assertFalse( snapshot1.equals(snapshot2) ); 
            
            ngn.applySnapShot(snapshot1);
            
            assertEquals( snapshot1, ngn.createSnapshot() );
            
		} catch ( DataTagsRuntimeException ex ) {
			Logger.getLogger(ChartRunningTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		
    }
}
