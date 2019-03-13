package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.stream.Stream;
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
public class GraphvizVisualizerTest {
    
    GraphvizVisualizer sut;
    
    public GraphvizVisualizerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        sut = new GraphvizVisualizer(){
            @Override
            protected void printBody(PrintWriter out) throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSanitizeId() {
        Stream.of(".,:/~?!()@#$%^&*_+-[] >=\\", "123aAbBzZ").forEach(s -> assertTrue( sut.sanitizeId(s).chars().allMatch(c-> Character.isLetterOrDigit(c) || c == '_') ));
        
        assertFalse( sut.sanitizeId("sec:cat").contains(":") );
    }
    


    public class GraphvizVisualizerImpl extends GraphvizVisualizer {

        public void printBody(PrintWriter out) throws IOException {
        }
    }
    
}
