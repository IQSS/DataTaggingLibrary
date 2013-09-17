package edu.harvard.iq.datatags.webapp.parsers;

import java.io.InputStream;

/**
 * Parsing test. Note that this is NOT a unit test (it uses files).
 * 
 * @author michael
 */
public class QuestionnaireJsonParserTest {
	
	public static void main(String[] args) {
		QuestionnaireJsonParser prsr = new QuestionnaireJsonParser();
		
		InputStream input = QuestionnaireJsonParserTest.class
									.getClassLoader().getResourceAsStream("META-INF/simple.json");
		
		prsr.parse(input);
		
		System.out.println("prsr.name = " + prsr.getName());
		
	}
	
}
