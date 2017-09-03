package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 *
 * @author michael
 */
public class PolicySpaceIndexTest {
    
    static CompoundSlot policySpaceBase;
    
    @BeforeClass
    static public void setupClass() throws SyntaxErrorException, SemanticsErrorException {
        String psSource = "Base: consists of Dogs, Cats, Fish.\n" +
                            "Dogs: one of d1, d2, d3.\n" +
                            "Cats: some of c1, c2, c3.\n" +
                            "Fish: consists of DogFish, Jelly.\n" +
                            "DogFish: one of d1, d2, df0.\n" +
                            "Jelly: some of j1, j2, j3.";
        policySpaceBase = new TagSpaceParser().parse(psSource).buildType("Base").get();
        
    }
    
    PolicySpaceIndex sut;
    
    @Before
    public void setup() {
        sut = new PolicySpaceIndex(policySpaceBase);
    }
    
    @Test
    public void testGetMethod_uniqueEntries() {
        assertEquals( C.set(C.list("Base")), sut.get(C.list("Base")));
        assertEquals( C.set(C.list("Base", "Dogs")), sut.get(C.list("Dogs")));
        assertEquals( C.set(C.list("Base", "Fish", "Jelly")), sut.get(C.list("Jelly")));
        assertEquals( C.set(C.list("Base", "Fish", "Jelly")), sut.get(C.list("Fish", "Jelly")));
        assertEquals( C.set(C.list("Base", "Fish", "Jelly")), sut.get(C.list("Base", "Fish", "Jelly")));
        
        assertEquals( C.set(C.list("Base", "Fish", "Jelly", "j3")), sut.get(C.list("Base", "Fish", "Jelly", "j3")));
        assertEquals( C.set(C.list("Base", "Fish", "Jelly", "j3")), sut.get(C.list("Jelly", "j3")));
        assertEquals( C.set(C.list("Base", "Fish", "Jelly", "j3")), sut.get(C.list("j3")));
    }
    
    @Test
    public void testGetMethod_notPresent() {
        assertEquals( C.set(), sut.get(C.list("not-there")));
    }
    
    @Test
    public void testGetMethod_ambiguous() {
        assertEquals(
                C.set(
                    C.list("Base","Dogs", "d1"),
                    C.list("Base", "Fish", "DogFish", "d1")), 
                sut.get(C.list("d1"))
        );
    }
    
    @Test
    public void testIsValue() {
        assertTrue( sut.isValue(C.list("Base", "Dogs", "d1")) );
        assertTrue( sut.isValue(C.list("Base", "Fish", "Jelly", "j3")) );
        
        assertFalse( sut.isValue(C.list("Base", "Fish", "Jelly", "j3000")) );
        assertFalse( sut.isValue(C.list("BaseX", "Fish", "Jelly", "j3")) );
        assertFalse( sut.isValue(C.list("Base", "XFish", "Jelly", "j3")) );
        assertFalse( sut.isValue(C.list("Base", "Fish", "Jelly")) );
    }
    
}
