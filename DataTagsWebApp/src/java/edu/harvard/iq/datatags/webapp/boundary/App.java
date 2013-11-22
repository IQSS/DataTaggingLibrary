package edu.harvard.iq.datatags.webapp.boundary;

import edu.harvard.iq.datatags.tags.AuthenticationType;
import edu.harvard.iq.datatags.tags.DuaAgreementMethod;
import edu.harvard.iq.datatags.tags.EncryptionType;
import edu.harvard.iq.datatags.tags.HarmLevel;
import edu.harvard.iq.datatags.tags.DataTags;
import edu.harvard.iq.datatags.questionnaire.Answer;
import edu.harvard.iq.datatags.questionnaire.DecisionNode;
import edu.harvard.iq.datatags.webapp.parsers.QuestionnaireJsonParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

/**
 * The application object, container of the questionnaire itself(!).
 * @author michael
 */
@ManagedBean( name="App" )
@ApplicationScoped
public class App {
	private static final Logger logger = Logger.getLogger(App.class.getName());
	private DecisionNode questionnaireRoot;
	private final Map<String, DecisionNode> dtnById = new HashMap<>(); 
	private List<String> topLevelTopics;
	private String questionnareVersion;
	
	/**
	 * Creates a new instance of App
	 */
	public App() {
		try {
			QuestionnaireJsonParser prsr = new QuestionnaireJsonParser();
			prsr.parse(getClass().getClassLoader().getResource("META-INF/accord.json"));
			setQuestionnaire( prsr.getRoot() );
			topLevelTopics = prsr.getTopLevelNames();
			questionnareVersion = prsr.getName();
			
		} catch (IOException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public DecisionNode getQuestionnaireRoot() {
		return questionnaireRoot;
	}
	
	public DecisionNode getDecisionNode( String id ) {
		return dtnById.get(id);
	}
	
	public List<HarmLevel> harmLevels() {
		return Arrays.asList( HarmLevel.values() );
	}

	private void setQuestionnaire( DecisionNode root ) {
		dtnById.clear();
		questionnaireRoot = root;
		root.setBaseAssumption( new DataTags() );
		List<DecisionNode> queue = new LinkedList<>();
		queue.add( questionnaireRoot );
		Set<DecisionNode> seen = new HashSet<>();
		while ( ! queue.isEmpty() ) {
			DecisionNode nd = queue.remove(0);
			dtnById.put( nd.getId(), nd );
			System.out.println("added " + nd + "=>" + HarmLevel.levelFor(nd.getAbsoluteAssumption()));
			for ( Answer a : Answer.values() ) {
				DecisionNode subNode = nd.getNodeFor(a);
				if ( subNode != null && ! seen.contains(subNode) ) {
					seen.add( subNode );
					queue.add( subNode );
//					if ( subNode.getBaseAssumption() == null ) {
//						subNode.setBaseAssumption( improveOn(nd.getAbsoluteAssumption()));
//					}
				}
			}
		}
	}

	public List<String> getTopLevelTopics() {
		return topLevelTopics;
	}

	public String getQuestionnareVersion() {
		return questionnareVersion;
	}
	
	

	private DecisionNode makeDummyQuestionnaire() {
		DecisionNode root = new DecisionNode( Integer.toString(0) );
		root.setTitle("First Question title");
		root.setQuestionText("This is the text for the first question.");
		root.setHelpText("The rest of the nodes are randomly generated for now.");
		
		for ( int i=0; i<50; i++ ) {
			DecisionNode parent = null;
			DecisionNode subNode = root;
			// get to a leaf
			Answer lastAnswer = Answer.YES;
			int depth = 0;
			while (subNode != null) {
				parent = subNode;
				lastAnswer =  random.nextBoolean() ? Answer.YES : Answer.NO;
				subNode = parent.getNodeFor(lastAnswer);
				depth++;
			}

			subNode = sampleNode();
			// append node
			if ( parent != null ) {
				if (!parent.getAbsoluteAssumption().isComplete()) {
					parent.setNodeFor(lastAnswer, subNode);
					subNode.setBaseAssumption( improveOn(subNode.getAbsoluteAssumption()) );
				}
			} else {
				logger.severe("parent is null");
			}
		}
		
		return root;
	}
	int lastIdx = 0;
	Random random = new Random();
	private static final List<String> WORDS = Arrays.asList("health", "misery", "nimbi",
		"strut", "define", "hover", "unhad", "small", "stoon", "rabble", "nimbus", "bay",
		"bejel", "song","pure","up","mouse", "addict", "stump","prod", "snippet", "behold",
		"adhere", "decals", "medical", "elephant", "morning", "cirque", "asset", "slinky");
	
	DecisionNode sampleNode() {
		DecisionNode retVal = new DecisionNode( Integer.toString(++lastIdx) );
		
		retVal.setTitle( randomText(4+random.nextInt(4)) );
		retVal.setQuestionText(randomText(10+random.nextInt(20)) );
		if ( random.nextBoolean() ) {
			retVal.setHelpText(randomText(10+random.nextInt(40)) );
		}
		
		return retVal;
	}
	
	String randomText( int count ) {
		StringBuilder sb = new StringBuilder();
		for ( int i=0; i<count; i++ ) {
			sb.append( WORDS.get(random.nextInt(WORDS.size())) );
			sb.append( " " );
		}
		
		return sb.toString();
	}

	private DataTags improveOn(DataTags asm) {
		DataTags res = asm.makeCopy();
		if ( res.getAuthenticationType() == null ) {
			res.setAuthenticationType(randOf(AuthenticationType.values()));
			return res;
		}
		
		if ( res.getDuaAgreementMethod()== null ) {
			res.setDuaAgreementMethod(randOf(DuaAgreementMethod.values()));
			return res;
		}
		
		if ( res.getStorageEncryptionType()== null ) {
			res.setStorageEncryptionType(randOf(EncryptionType.values()));
			return res;
		}
		
		if ( res.getTransitEncryptionType()== null ) {
			res.setTransitEncryptionType(randOf(EncryptionType.values()));
		}
		
		return res;
	}
	
	private  <T> T randOf( T[] arr ) {
		return arr[random.nextInt(arr.length)];
	}
}
