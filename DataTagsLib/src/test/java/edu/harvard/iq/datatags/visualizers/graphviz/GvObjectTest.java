/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.datatags.visualizers.graphviz;

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
public class GvObjectTest {
    
    public GvObjectTest() {
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
    public void testEscapeNewline() {
        GvNode nd = new GvNode("id");
        nd.label("hello\nworld");
        final String actual = nd.gv();
        assertEquals("id[ label=\"hello\\nworld\" ]", actual);
    }
    
    @Test
    public void testEscapeQuote() {
        GvNode nd = new GvNode("id");
        nd.label("hello, \"world\"");
        final String actual = nd.gv();
        assertEquals("id[ label=\"hello, 'world'\" ]", actual);
    }
    
}
