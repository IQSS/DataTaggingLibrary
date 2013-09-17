package edu.harvard.iq.datatags.webapp.parsers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import edu.harvard.iq.datatags.questionnaire.DecisionNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Parses a JSON file into a questionnaire, and a list of titles (needed for the demo).
 * 
 * @author michael
 */
public class QuestionnaireJsonParser {
	
	private static final Logger logger = Logger.getLogger(QuestionnaireJsonParser.class.getName());
	
	private List<String> topLevelNames;
	private DecisionNode root;
	private String name;
	
	public void parse( InputStream strm ) {
		JsonFactory fact = new JsonFactory();
		fact.enable(JsonParser.Feature.ALLOW_COMMENTS);

		try ( JsonParser jp = fact.createParser(strm) ) {
			parse( jp );
			
		} catch ( IOException e ) {
			logger.log( Level.SEVERE, "Error parsing JSON: " + e.getMessage(), e );
		} catch (QuestionnaireParseException ex) {
			logger.log( Level.SEVERE, "Error parsing qustionnaire format: " + ex.getMessage(), ex );
		}
		
		
	}

	public void parse( JsonParser json ) throws IOException, QuestionnaireParseException {
		topLevelNames = new LinkedList<>();
		root = null;
		name = null;
		
		if ( json.nextToken() != JsonToken.START_OBJECT) {
			throw new IOException("Expected data to start with an Object");
		}
		
		String idnt = " ";
		int d=1;
		while ( d != 0 ) {
			JsonToken nextT = json.nextToken();
//			System.out.println("nextT = " + nextT);
			
			if ( nextT == JsonToken.FIELD_NAME ) {
				String fieldName = json.getCurrentName();
				System.out.println( idnt + fieldName + ":" );
				
			} else if ( nextT == JsonToken.START_ARRAY ) {
				while ( json.nextToken() != JsonToken.END_ARRAY ) {
					System.out.println(idnt + " + " + json.getValueAsString() );
				}
				
			} else if ( nextT == JsonToken.START_OBJECT ) {
				idnt = idnt + "-";
				d++;
				
			} else if ( nextT == JsonToken.END_OBJECT ) {
				idnt = idnt.substring(1);
				d--;
						
			} else {
				String value = json.getValueAsString();
				System.out.println( idnt + " +--> " + value );
			}
		}
	}
	
	/**
	 * The main point of this method is to filter out the "about..." nodes,
	 * and to list the names of the other nodes.
	 * @param json 
	 */
	private void parseTopLevelChildern(JsonParser json) throws IOException, QuestionnaireParseException {
		if ( json.getCurrentToken()!= JsonToken.START_ARRAY ) {
			throw new QuestionnaireParseException("Parse childern expects an array");
		}
		
		Pattern validNodes = Pattern.compile("^\\d+\\. .*$");
		json.nextToken(); // start object
		logger.info("json.getCurrentToken() = " + json.getCurrentToken());
		json.nextToken(); // Object data
		logger.info("json.getCurrentToken() = " + json.getCurrentToken());
		String tagName = json.getCurrentName();
		if ( tagName.equals("name") ) {
			json.nextToken();
			String tagValue = json.getValueAsString();
			
			if ( validNodes.matcher(tagValue).matches() ) {
				System.out.println("tagValue = " + tagValue);
				skipToNodeEnd( json );
				
			} else {
				Logger.getLogger(QuestionnaireJsonParser.class.getName()).log(Level.INFO, "Filtering out top level node named ''{0}''", tagValue);
				skipToNodeEnd( json );
			}
			
		}
	}
	
	private void skipToNodeEnd( JsonParser json ) throws IOException {
		int depth = 1;
		while ( depth != 0 ) {
			JsonToken t = json.nextToken();
			if ( t == JsonToken.START_OBJECT ) depth++;
			if ( t == JsonToken.START_ARRAY ) depth++;
			if ( t == JsonToken.END_ARRAY ) depth--;
			if ( t == JsonToken.END_OBJECT ) depth--;
		}
		json.nextToken();
	}
	
	public List<String> getTopLevelNames() {
		return topLevelNames;
	}

	public DecisionNode getRoot() {
		return root;
	}

	public String getName() {
		return name;
	}
	
}
