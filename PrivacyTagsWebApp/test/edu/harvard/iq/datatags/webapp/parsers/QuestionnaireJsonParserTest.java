package edu.harvard.iq.datatags.webapp.parsers;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parsing test. Note that this is NOT a unit test (it uses files).
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
	}
	
}
