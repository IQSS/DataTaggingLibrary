package edu.harvard.iq.datatags.webapp.parsers;

import edu.harvard.iq.datatags.runtime.Answer;
import edu.harvard.iq.datatags.questionnaire.DecisionNode;
import edu.harvard.iq.datatags.webapp.parsers.QuestionnaireJsonParser.NameParseResult;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Parsing test - contains both unit tests and file-based ones.
 * 
 * @author michael
 */
public class QuestionnaireJsonParserTest {
	
	public static void main(String[] args) throws IOException {
		QuestionnaireJsonParser prsr = new QuestionnaireJsonParser();
		
//		InputStream input = QuestionnaireJsonParserTest.class
//									.getClassLoader().getResourceAsStream("META-INF/simple.json");
		
		prsr.parse(QuestionnaireJsonParserTest.class
									.getClassLoader().getResource("META-INF/accord.json"));
		
		System.out.println("prsr.name = " + prsr.getName());
		for ( String name : prsr.getTopLevelNames() ) {
			System.out.println(" -> " + name );
		}
		System.out.println("Done. Creating GV... ");
		gvNode( prsr.getRoot(), new TreeSet<String>() );
		
	}

	private static void gvNode(DecisionNode dNode, Set<String> seen) {
		System.out.println("\"" + dNode.getId() + "\"[label=\"" + dNode.getId() + "\\n" + dNode.getTitle() + "\\n" + dNode.getBaseAssumption() +"\"]");
		
		for ( Answer a : Answer.values() ) {
			DecisionNode sn = dNode.getNodeFor(a);
			if ( sn != null ) {
				if ( ! seen.contains(sn.getId()) ) {
					seen.add( sn.getId() );
					gvNode( sn , seen );
				}
				System.out.println("\"" + dNode.getId() + "\" -> \"" + sn.getId() + "\" [label=\"" + a + "\"]");
			}
		}
	}

	@Test
	public void textParseNameField_NoAns() {
		QuestionnaireJsonParser sut = new QuestionnaireJsonParser();
		String input = "4. Arrest and Conviction Records.";
		NameParseResult res = sut.parseNameField(input);
		assertNull( res.ans );
		assertEquals( "4.", res.id );
		assertEquals( "Arrest and Conviction Records", res.title );
		assertNull( res.info );
		
		input = "4. Arrest and Conviction Records. Info text. about thi.g.s.  g.s";
		res = sut.parseNameField(input);
		assertNull( res.ans );
		assertEquals( "4.", res.id );
		assertEquals( "Arrest and Conviction Records", res.title );
		assertEquals( "Info text. about thi.g.s.  g.s", res.info );
				
	}
	
	@Test
	public void textParseNameField_Ans() {
		QuestionnaireJsonParser sut = new QuestionnaireJsonParser();
		String input = "No. 4. Arrest and Conviction Records.";
		NameParseResult res = sut.parseNameField(input);
		assertEquals( Answer.NO, res.ans );
		assertEquals( "4.", res.id );
		assertEquals( "Arrest and Conviction Records", res.title );
		assertNull( res.info );
		
		input = "Yes. 4. Arrest and Conviction Records. Info text. about thi.g.s.  g.s";
		res = sut.parseNameField(input);
		assertEquals( Answer.YES, res.ans );
		assertEquals( "4.", res.id );
		assertEquals( "Arrest and Conviction Records", res.title );
		assertEquals( "Info text. about thi.g.s.  g.s", res.info );
				
	}
	
}
