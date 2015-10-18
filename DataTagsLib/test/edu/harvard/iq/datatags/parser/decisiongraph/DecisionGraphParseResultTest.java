/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class DecisionGraphParseResultTest {
    
    public DecisionGraphParseResultTest() {
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
    public void testTypeIndexBuilding() throws SyntaxErrorException, SemanticsErrorException, DataTagsParseException {
        String typeDef = 
                  "top: consists of mid1, mid2.\n"
                + "mid1: consists of bottom1, bottom2.\n"
                + "mid2: consists of bottom2, bottom3.\n"
                + "bottom1: one of X11, X12, X13.\n"
                + "bottom2: one of Y21, Y22, Y23.\n"
                + "bottom3: one of Z31, Z32, Z33.";
        
        TagSpaceParseResult parse = new TagSpaceParser().parse(typeDef);
        CompoundType topType = parse.buildType("top").get();
        
        Map<List<String>, TagType> expected = new HashMap<>();
        
        DecisionGraphParseResult res = new DecisionGraphParseResult( Collections.emptyList() );
        
        res.buildTypeIndex( topType );
        res.typesBySlot.entrySet().forEach( ent -> System.out.println(ent.getKey() + "\n\t" + ent.getValue() ));
    }
    
}
