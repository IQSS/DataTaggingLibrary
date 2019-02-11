package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.model.metadata.GroupAuthorData;
import edu.harvard.iq.datatags.model.metadata.ModelReference;
import edu.harvard.iq.datatags.model.metadata.PersonAuthorData;
import java.util.Arrays;
import java.util.TreeSet;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class LocalizedModelDataParserTest {
    
    static String MINIMAL_MODEL = "<localized-model>\n" +
                                  "  <title>  \n A Sample Title \n\n\n</title>\n" +
                                  "  <keywords>repository, open, Force11</keywords>\n" +
                                  "  <authors>\n" +
                                  "    <person orcid=\"123-2345-67890\">\n" +
                                  "      <name>Jane Doe</name>\n" +
                                  "      <affiliation>Force 11</affiliation>\n" +
                                  "      <email>jane.d@institute.edu</email>\n" +
                                  "    </person>\n" +
                                  "    <group>\n" +
                                  "      <name>Name of group</name>\n" +
                                  "      <contact>Someone@scwg-dt.org</contact>\n" +
                                  "    </group>\n" +
                                  "  </authors>\n" +
                                  "  <references>\n" +
                                  "    <reference doi=\"1234567890\" url=\"http://url\">\n" +
                                  "      ref1 text\n" +
                                  "    </reference>\n" +
                                  "  </references>\n" +
                                  "</localized-model>";
    
    
    @Test
    public void testMinimalModel() throws LocalizationException {
        LocalizedModelDataParser sut = new LocalizedModelDataParser("test-lang");
        
        LocalizedModelData lmd = sut.read(MINIMAL_MODEL);
        assertEquals(1, lmd.getReferences().size());
        assertEquals(2, lmd.getAuthors().size());
        assertEquals(new TreeSet<>(Arrays.asList("repository", "open", "force11")), lmd.getKeywords());
        assertEquals("A Sample Title", lmd.getTitle());
        
        PersonAuthorData person = new PersonAuthorData();
        person.setAffiliation("Force 11");
        person.setOrcid("123-2345-67890");
        person.setEmail("jane.d@institute.edu");
        person.setName("Jane Doe");
        GroupAuthorData group = new GroupAuthorData();
        group.setContact("Someone@scwg-dt.org");
        group.setName("Name of group");
        assertEquals(person, lmd.getAuthors().get(0));
        assertEquals(group, lmd.getAuthors().get(1));
        assertEquals( Arrays.asList(person, group), lmd.getAuthors() );
        
        ModelReference ref = new ModelReference();
        ref.setDoi("1234567890");
        ref.setUrl("http://url");
        ref.setText("ref1 text");
        assertEquals(Arrays.asList(ref), lmd.getReferences());
        
    } 
}
