package edu.harvard.iq.datatags.parser.decisiongraph;

import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class AstNodeIdProviderTest {
    
    public AstNodeIdProviderTest() {
    }

    /**
     * Test of nextId method, of class AstNodeIdProvider.
     */
    @Test
    public void testNextId() {
        Set<String> ids = new TreeSet<>();
        AstNodeIdProvider sut = new AstNodeIdProvider();
        
        for ( int i=0; i<10; i++ ) {
            ids.add(sut.nextId());
        }
        
        assertEquals(10, ids.size());
    }
    
    @Test
    public void testIsAutoId() {
       AstNodeIdProvider sut = new AstNodeIdProvider();
        
        for ( int i=0; i<10; i++ ) {
            assertTrue( AstNodeIdProvider.isAutoId(sut.nextId()));
        }
        
        assertFalse( AstNodeIdProvider.isAutoId("123") );
        assertFalse( AstNodeIdProvider.isAutoId("abc") );
        assertFalse( AstNodeIdProvider.isAutoId("#abc") );
    }
    
}
