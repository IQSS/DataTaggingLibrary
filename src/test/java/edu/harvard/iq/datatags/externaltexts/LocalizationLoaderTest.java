package edu.harvard.iq.datatags.externaltexts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class LocalizationLoaderTest {
    
    public LocalizationLoaderTest() {
    }

    @Test
    public void testLoadAnswers() throws Exception {
        List<String> source = Arrays.asList(
                "<-- first answers", 
                "    ",
                "yes:indeed so",
                "no:nay <-- Olde English",
                "maybe  :  how about we table this discussion for now",
                "<-- next line should be ignored",
                "ignore me:<-- since there's no localization."
        );
        
        LocalizationLoader sut = new LocalizationLoader();
        Localization loc = new Localization("test");
        sut.loadAnswers(source.stream(), loc);
        
        Map<String,String> expected = new HashMap<>();
        expected.put("yes", "indeed so");
        expected.put("no", "nay");
        expected.put("maybe", "how about we table this discussion for now");
        expected.put("ignore me", "ignore me");
        expected.put("key not present", "key not present");
        
        expected.keySet().forEach( k -> assertEquals(expected.get(k), loc.localizeAnswer(k)));
    }
    
    @Test
    public void testLoadIllegalAnswers() throws Exception {
        List<String> source = Arrays.asList(
                "no:meh",
                "yes:indeed",
                "so"
        );
        
        LocalizationLoader sut = new LocalizationLoader();
        Localization loc = new Localization("test");
        try {
            sut.loadAnswers(source.stream(), loc);
        } catch ( LocalizationException le ) {
            assertTrue( "Bad error message: " + le.getMessage(), 
                        le.getMessage().contains("at line 3"));
        }
        
        
    }
    
}
