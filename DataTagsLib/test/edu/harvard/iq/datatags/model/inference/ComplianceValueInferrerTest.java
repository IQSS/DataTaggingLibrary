package edu.harvard.iq.datatags.model.inference;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import static edu.harvard.iq.util.PolicySpaceHelper.buildValue;
import java.util.stream.Stream;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author michael
 */
public class ComplianceValueInferrerTest {
    
    private static final String SOURCE_SIMPLE = 
            "SBase: consists of X, Y, I. \n"
            + "X: one of X0, X1, X2.\n"
            + "Y: one of Y0, Y1, Y2.\n"
            + "I: one of I0, I1, I2.";
            
            
    
    CompoundSlot base, compound0, sBase;
    AggregateSlot aggregate0, aggregate1;
    AtomicSlot atomic0, atomic1, x, y, inf;
    
    @Before
    public void setup() throws SyntaxErrorException, SemanticsErrorException {
       sBase = new TagSpaceParser().parse(SOURCE_SIMPLE).buildType("SBase").get(); 
       x = (AtomicSlot) sBase.getSubSlot("X");
       y = (AtomicSlot) sBase.getSubSlot("Y");
       inf = (AtomicSlot) sBase.getSubSlot("I");
       
    }
    
    /**
     * Test of apply method, of class ValueInferrer.
     * 
     * Testing value inferrer:
     * <code>
     * x2    i2
     * x1    i1
     * x0 i0
     *    y0 y1 y2
     * </code>
     */
    @Test
    public void testApplySimple() {
        
        AbstractValueInferrer sut = new ComplianceValueInferrer();
        Stream.of(
                new SupportValueInferrer.InferencePair(buildValue(sBase, "X/X0; Y/Y0"), buildValue(sBase, "I/I0")),
                new SupportValueInferrer.InferencePair(buildValue(sBase, "X/X1; Y/Y1"), buildValue(sBase, "I/I1")),
                new SupportValueInferrer.InferencePair(buildValue(sBase, "X/X2; Y/Y1"), buildValue(sBase, "I/I2")) 
        ).forEach( sut::add );
        
        CompoundValue i0 = buildValue(sBase, "X/X0; Y/Y0; I/I0");
        CompoundValue i1 = buildValue(sBase, "X/X1; Y/Y1; I/I1");
        CompoundValue i2 = buildValue(sBase, "X/X2; Y/Y1; I/I2");
        
        // Add Inf on identity
        assertEquals( i0, sut.apply(buildValue(sBase,"X/X0; Y/Y0")) );
        assertEquals( i1, sut.apply(buildValue(sBase,"X/X1; Y/Y1")) );
        assertEquals( i2, sut.apply(buildValue(sBase,"X/X2; Y/Y1")) );
        
        // Add Inf on lowest supporting
        assertEquals( inf.valueOf("I0"), sut.apply(buildValue(sBase,"X/X1; Y/Y0")).get(inf) );
        assertEquals( inf.valueOf("I0"), sut.apply(buildValue(sBase,"X/X0; Y/Y1")).get(inf) );
        assertEquals( inf.valueOf("I1"), sut.apply(buildValue(sBase,"X/X1; Y/Y2")).get(inf) );
        assertEquals( inf.valueOf("I2"), sut.apply(buildValue(sBase,"X/X2; Y/Y2")).get(inf) );
        
    }
    
}
