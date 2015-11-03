package edu.harvard.iq.datatags.model.graphs;

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
        assertTrue( Answer.get("a") == Answer.get("a") );
        assertTrue( Answer.get("a") != Answer.get("b") );
        assertTrue( Answer.get("a") != Answer.get("aa") );
    }
    
}
