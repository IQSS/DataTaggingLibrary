package edu.harvard.iq.policymodels.externaltexts;

import edu.harvard.iq.policymodels.externaltexts.LocalizedModelData.Direction;
import edu.harvard.iq.util.ModelMetadataUtils;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class LocalizationTest {
    
    Localization sut;
    
    @Before
    public void setUp() {
        sut = new Localization();
    }
        
    /**
     * Test of getLocalizedModelData method, of class Localization.
     */
    @Test
    public void testUiLanguageDefault() {
        assertEquals(Optional.empty(), sut.getLocalizedModelData().getUiLanguage());
    }

    @Test
    public void testDirectionDefault() {
        assertEquals( Direction.LTR, sut.getLocalizedModelData().getDirection() );
    }
    
    @Test
    public void testSetModelData() {
        LocalizedModelData lmd = new LocalizedModelData();
        lmd.setDirection(Direction.RTL);
        lmd.setLanguage("he");
        lmd.setTitle("Test title");
        lmd.setSubTitle("Test sub title");
        lmd.setUiLanguage("he-IL");
        
        lmd.add(ModelMetadataUtils.author("Jane Doe", "jane.doe@int.edu", "inst"));
        
        sut.setLocalizedModelData(lmd);
        
        assertEquals( lmd.getLanguage(), sut.getLanguage() );
        assertEquals( Direction.RTL, sut.getLocalizedModelData().getDirection() );
        assertEquals( Optional.of("he-IL"), sut.getLocalizedModelData().getUiLanguage());
        
        sut.setLocalizedModelData(null);
        
        assertEquals( Direction.LTR, sut.getLocalizedModelData().getDirection() );
        assertEquals( Optional.empty(), sut.getLocalizedModelData().getUiLanguage() );
        
    }
}
