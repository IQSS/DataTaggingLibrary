package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.model.PolicyModelData;
import edu.harvard.iq.datatags.model.metadata.GroupAuthorData;
import edu.harvard.iq.datatags.model.metadata.ModelReference;
import edu.harvard.iq.datatags.model.metadata.PersonAuthorData;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.TreeSet;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class PolicyModelDataParserTest {
    
    static String MINIMAL_MODEL = "<policy-model>\n" +
                                  "  <title>  \n A Sample Title \n\n\n</title>\n" +
                                  "  <version doi=\"aDOI\">0.4</version>\n" +
                                  "  <date>2017-04-23</date>\n" +
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
                                  "  <model answers-order=\"yes-first\">\n" +
                                  "    <space root=\"DataTags\">a/b/def.ts</space>\n" +
                                  "    <graph>c/d/gr.dg</graph>\n" +
                                  "  </model>\n" +
                                  "  <references>\n" +
                                  "    <reference doi=\"1234567890\" url=\"http://url\">\n" +
                                  "      ref1 text\n" +
                                  "    </reference>\n" +
                                  "  </references>\n" +
                                  "</policy-model>";
    
    
    @Test
    public void testMinimalModel() throws PolicyModelLoadingException {
        PolicyModelDataParser sut = new PolicyModelDataParser();
        Path basePath = Paths.get("/sample/policy-model.xml");
        
        PolicyModelData pmd = sut.read(MINIMAL_MODEL, basePath);
        assertEquals("aDOI", pmd.getDoi());
        assertEquals(1, pmd.getReferences().size());
        assertEquals(2, pmd.getAuthors().size());
        assertEquals(new TreeSet<>(Arrays.asList("repository", "open", "force11")), pmd.getKeywords());
        assertEquals("A Sample Title", pmd.getTitle());
        assertEquals(LocalDate.of(2017, 4, 23), pmd.getReleaseDate());
        
        PersonAuthorData person = new PersonAuthorData();
        person.setAffiliation("Force 11");
        person.setOrcid("123-2345-67890");
        person.setEmail("jane.d@institute.edu");
        person.setName("Jane Doe");
        GroupAuthorData group = new GroupAuthorData();
        group.setContact("Someone@scwg-dt.org");
        group.setName("Name of group");
        assertEquals(person, pmd.getAuthors().get(0));
        assertEquals(group, pmd.getAuthors().get(1));
        assertEquals( Arrays.asList(person, group), pmd.getAuthors() );
        
        ModelReference ref = new ModelReference();
        ref.setDoi("1234567890");
        ref.setUrl("http://url");
        ref.setText("ref1 text");
        assertEquals(Arrays.asList(ref), pmd.getReferences());
        
        assertEquals(basePath.resolve("a").resolve("b").resolve("def.ts"), pmd.getPolicySpacePath());
        assertEquals(basePath.resolve("c").resolve("d").resolve("gr.dg"), pmd.getDecisionGraphPath());
    }
}
