package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.model.PolicySpaceIndex;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import static edu.harvard.iq.util.PolicySpaceHelper.fromPath;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author michael
 */
public class SpaceLocalizationParserTest {

    static CompoundSlot spaceBase;
    
    SpaceLocalizationParser sut;
    
    @BeforeClass
    public static void setupClass() throws SyntaxErrorException, SemanticsErrorException {
        String spaceSource = "Base: consists of Cats, Dogs, Rice.\n" +
                             "Cats: some of Tom, Shmil, Mitzi.\n" +
                             "Dogs: some of Rex, Pluto, Lassie.\n" +
                             "Rice: one of White, Full.";
        spaceBase = new TagSpaceParser().parse(spaceSource).buildType("Base").get();
    }
    
    @Before
    public void setup() {
        sut = new SpaceLocalizationParser(new PolicySpaceIndex(spaceBase));
    }
    
    /**
     * Test of parse method, of class SpaceLocalizationParser.
     */
    @Test
    public void isInlineSlotStartYes() {
        assertTrue( sut.isInlineSlotStart("a/b/c/d:lalalal") );
        assertTrue( sut.isInlineSlotStart("a:lalalal") );
        assertTrue( sut.isInlineSlotStart("a/b/c/d: lalalal") );
    }
    
    @Test
    public void isInlineSlotStartNo() {
        assertFalse( sut.isInlineSlotStart("ae / b/c / d:lalalal") );
        assertFalse( sut.isInlineSlotStart(" a:lalalal") );
        assertFalse( sut.isInlineSlotStart("a/b/c/d") );
    }
    
    @Test
    public void parseSpaceTextInlineNoBlankLines() {
        List<String> input = Arrays.asList(
            "Base/Rice: This slot will contain which type of rice is used.",
            "Base/Cats/Tom: Tom, a large cat that sits on fences, staring at the passing cars.",
            "Base/Cats/Shmil: A little-known cat named \"Shmil\".",
            "Base/Cats/Mitzi: A kitten of all trades, Mitzi can eat its rice and have it too."
        );
        Map<String,String> expected = new HashMap<>();
        expected.put("Base/Rice","This slot will contain which type of rice is used.");
        expected.put("Base/Cats/Tom","Tom, a large cat that sits on fences, staring at the passing cars.");
        expected.put("Base/Cats/Shmil","A little-known cat named \"Shmil\".");
        expected.put("Base/Cats/Mitzi","A kitten of all trades, Mitzi can eat its rice and have it too.");
        
        boolean parseRes = sut.parse(input.stream());
        if ( ! parseRes ) { 
            System.out.println("Errors in parsing the input:");
            sut.getMessages().forEach( m->System.out.println("sut message: " + m) );
        }
        assertTrue(parseRes);
        sut.getSpaceEnitiyTexts().forEach( (p,t)->assertEquals( expected.get(fromPath(p)), t) );
    }

    @Test
    public void parseSpaceTextInline() {
        List<String> input = Arrays.asList(
            "Base/Rice: This slot will contain which type of rice is used.",
            "",
            "Base/Cats/Tom: Tom, a large cat that sits on fences, staring at the passing cars.",
            "",
            "Base/Cats/Shmil: A little-known cat named \"Shmil\".",
            "",
            "Base/Cats/Mitzi: A kitten of all trades, Mitzi can eat its rice and have it too."
        );
        Map<String,String> expected = new HashMap<>();
        expected.put("Base/Rice","This slot will contain which type of rice is used.");
        expected.put("Base/Cats/Tom","Tom, a large cat that sits on fences, staring at the passing cars.");
        expected.put("Base/Cats/Shmil","A little-known cat named \"Shmil\".");
        expected.put("Base/Cats/Mitzi","A kitten of all trades, Mitzi can eat its rice and have it too.");
        
        boolean parseRes = sut.parse(input.stream());
        if ( ! parseRes ) { 
            System.out.println("Errors in parsing the input:");
            sut.getMessages().forEach( m->System.out.println("sut message: " + m) );
        }
        assertTrue(parseRes);
        sut.getSpaceEnitiyTexts().forEach( (p,t)->assertEquals( expected.get(fromPath(p)), t) );
    }
    
    @Test
    public void parseSpaceTextWithHeaders() {
        List<String> input = Arrays.asList(
            "# Base/Rice",
            "heRice",
            "",
            "# Base/Cats/Tom:",
            "heTom",
            "small note",
            "---",
            "big note, ",
            "multi lines",
            "#Base/Cats/Shmil:",
            "",
            "---",
            "<-- comment",
            "big note for Shmil",
            "# Base/Dogs/Rex",
            "heRex",
            "---",
            "big note",
            "# Base/Dogs/Pluto",
            "",
            "# Base/Rive/Full",
            "heFull",
            "small note",
            "# Base/Cats/Mitzi",
            "",
            "small note",
            "# Base/Dogs/Lassie",
            "",
            "small note",
            "---",
            "big note"
        );
        Map<String,LocalizationTexts> expected = new HashMap<>();
        expected.put("Base/Dogs/Pluto",  new LocalizationTexts(null, null, null));
        expected.put("Base/Cats/Shmil",  new LocalizationTexts(null, null, "big note for Shmil"));
        expected.put("Base/Cats/Mitzi",  new LocalizationTexts(null, "small note", null));
        expected.put("Base/Dogs/Lassie", new LocalizationTexts(null, "small note", "big note"));
        expected.put("Base/Rice",        new LocalizationTexts("heRice", null, null));
        expected.put("Base/Dogs/Rex",    new LocalizationTexts("heRex", null, "big note"));
        expected.put("Base/Rice/Full",   new LocalizationTexts("heFull", "small note", null));
        expected.put("Base/Cats/Tom",    new LocalizationTexts("heTom", "small note", "big note,\nmulti lines"));
        
        boolean parseRes = sut.parse(input.stream());
        if ( ! parseRes ) { 
            System.out.println("Errors in parsing the input:");
            sut.getMessages().forEach( m->System.out.println("sut message: " + m) );
        }
        assertTrue(parseRes);
        sut.getSpaceEnitiyTexts().forEach( (p,t)->assertEquals( expected.get(fromPath(p)), t) );
    }
    
}
