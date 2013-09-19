package edu.harvard.iq.datatags.webapp.parsers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import edu.harvard.iq.datatags.questionnaire.DecisionNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
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
	
	public void parse( URL source ) throws IOException {
		ObjectMapper omp = new ObjectMapper();
		JsonNode parsedRoot = omp.readTree( source );
		readRoot( parsedRoot );
		dumpNode( parsedRoot, 0 );
	}
	
	/**
	 * Reads the parts that are unique for the root: the name, and the 
	 * category names.
	 * @param parsedRoot 
	 */
	private void readRoot(JsonNode parsedRoot) {
		name = parsedRoot.get("name").asText();
		topLevelNames = new LinkedList<>();
		// TODO add base tags here
		for ( JsonNode nd : Iterator2Iterable.cnv(parsedRoot.get("children").iterator())) {
			String nodeName = nd.get("name").textValue();
			if ( Character.isDigit( nodeName.charAt(0)) ) {
				topLevelNames.add( nodeName.split(" ",2)[1] );
			}
		}
	}
	
	private void dumpNode( JsonNode nd, int depth ) {
		StringBuilder sb = new StringBuilder( depth );
		for ( int i=0; i<depth; i++ ) sb.append( " " );
		String prefix = sb.toString();
		
		System.out.println(prefix + "===  Node [" + nd.getNodeType()  +"]");
		switch ( nd.getNodeType() ) {
			case OBJECT:
				System.out.println(prefix + "names:");
				for ( String str : Iterator2Iterable.cnv(nd.fieldNames()) ) {
					System.out.println(prefix + " " + str + " \t-> " + nd.get(str));
				}
				for ( JsonNode snd : Iterator2Iterable.cnv(nd.elements()) ) {
					dumpNode( snd, depth+1 );
				}
				break;
				
			case ARRAY:
				for ( int i=0; i<nd.size(); i++ ) {
					JsonNode sbnd = nd.get(i);
					System.out.println(String.format(prefix + " [%d]: %s", i, sbnd) );
				}
				for ( int i=0; i<nd.size(); i++ ) {
					dumpNode( nd.get(i), depth+1 );
				}
				break;
				
			default:
				System.out.println(prefix + "value:" + nd.asText());
		}
		System.out.println(prefix + "=== /Node");
	}
	
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

class Iterator2Iterable<T> implements Iterable<T> {
	
	static <S> Iterable<S> cnv( Iterator<S> anIt ) {
		return new Iterator2Iterable(anIt);
	}

	private final Iterator<T> it;

	public Iterator2Iterable(Iterator<T> it) {
		this.it = it;
	}
	
	@Override
	public Iterator<T> iterator() {
		return it;
	}
}