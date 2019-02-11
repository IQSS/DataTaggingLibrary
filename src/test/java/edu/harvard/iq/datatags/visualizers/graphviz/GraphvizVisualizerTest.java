package edu.harvard.iq.datatags.visualizers.graphviz;

import java.io.IOException;
import java.io.PrintWriter;
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
        Stream.of(".,/~?!()@#$%^&*_+-[] >=\\", "123aAbBzZ").forEach(s -> assertTrue( sut.sanitizeId(s).chars().allMatch(c-> Character.isLetterOrDigit(c) || c == '_') ));
    }
    
    @Test
    public void testWrapNotNeeded() {
        assertEquals( "", sut.wrapAt(null, 10) );
        assertEquals( "", sut.wrapAt("", 10) );
        assertEquals( "12345", sut.wrapAt("12345", 10) );
        assertEquals( "123 567", sut.wrapAt("123 567", 10) );
        assertEquals( "1234567890", sut.wrapAt("1234567890", 10) );
    }
    
    @Test
    public void testWrapNeeded() {
        assertEquals( "hello worl\nwrap me", sut.wrapAt("hello worl wrap me", 10) );
        assertEquals( "hello\nworld wrap\nme", sut.wrapAt("hello world wrap me", 10) );
    }
    
    @Test
    public void testMixedWrapNeeded() {
        assertEquals( "123456\n1234\n6789AB", sut.wrapAt("123456\n1234 6789AB", 10) );
    }
    
}
