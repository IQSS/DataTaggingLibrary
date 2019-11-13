package edu.harvard.iq.policymodels.model.graphs;

import edu.harvard.iq.policymodels.model.decisiongraph.Answer;
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
public class AnswerTest {
    
    public AnswerTest() {
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
    public void testPooling() {
        assertTrue( Answer.withName("a") == Answer.withName("a") );
        assertTrue( Answer.withName("a") != Answer.withName("b") );
        assertTrue( Answer.withName("a") != Answer.withName("aa") );
    }
    
}
