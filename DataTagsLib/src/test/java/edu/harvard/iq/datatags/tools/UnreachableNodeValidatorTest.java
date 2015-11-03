package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParseResult;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Naomi
 */
public class UnreachableNodeValidatorTest {
    
    UnreachableNodeValidator instance;
    DecisionGraphParser astParser;
    DecisionGraph decisionGraph;
    
    public UnreachableNodeValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new UnreachableNodeValidator();
        astParser = new DecisionGraphParser();
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateUnreachableNodesTest_reachableNodes() throws DataTagsParseException {
        String code = "[>ask1< ask: {text: Will this work?}\n" +
                        "  {answers:\n" +
                        "    {yes: [call:shouldWork]}}]\n" +
                        "[>end1< end]\n" +
                        "[>shouldWork< ask: \n" +
                        "  {text: This should work.}\n" +
                        "  {answers:\n" +
                        "    {yes: [>reject1< reject: Good it works.]}\n" +
                        "    {no: [>reject2< reject: This should have worked.]}}]";
        DecisionGraphParseResult parseResult = astParser.parse(code);
        decisionGraph = parseResult.compile( new CompoundType("","") );
        List<NodeValidationMessage> messages = instance.validateUnreachableNodes(decisionGraph);
        
        Set<Node> expected = Collections.<Node>emptySet();
        Set<Node> actualEntities = new HashSet<>();
        Set<NodeValidationMessage.Level> actualLevels = EnumSet.noneOf(NodeValidationMessage.Level.class);
        
        for ( NodeValidationMessage vm : messages ) {
            actualEntities.addAll(vm.getEntities());
            actualLevels.add(vm.getLevel());
        }
        System.out.println("actual = " + actualEntities);
        System.out.println("expected = " + expected);
        
        assertEquals( new HashSet<>(), actualLevels);
        assertEquals(expected, actualEntities);
    }
   
    @Test
    public void validateUnreachableNodesTest_minimal() throws BadSetInstructionException, DataTagsParseException {
        String code = "[>r< end][>nr< end]";
        DecisionGraphParseResult parseResult = astParser.parse(code);
        decisionGraph = parseResult.compile( new CompoundType("","") );
        List<NodeValidationMessage> messages = instance.validateUnreachableNodes(decisionGraph);
        
        Set<Node> expected = Collections.singleton( new EndNode("nr") );
        Set<Node> actualEntities = new HashSet<>();
        Set<ValidationMessage.Level> actualLevels = EnumSet.noneOf(ValidationMessage.Level.class);
        
        messages.forEach( vm -> {
            actualEntities.addAll(vm.getEntities());
            actualLevels.add(vm.getLevel());
        });
        
        assertEquals( EnumSet.of(ValidationMessage.Level.WARNING), actualLevels);
        assertEquals(expected, actualEntities);
        
    }
    
    @Test
    public void validateUnreachableNodesTest_unreachableNodes() throws BadSetInstructionException, DataTagsParseException {
        String code = "[>ask1< ask: {text: Will this work?}\n" +
                        "  {answers: {yes: [>reject1< reject: No.]}\n" +
                        "  {no: [>reject2< reject: Still no.]}}]\n" +
                        "[>ask2< ask: {text: This shouldn't work.}\n" +
                        "  {answers: \n" +
                        "    {yes: [>reject3< reject: No.]}\n" +
                        "    {no:  [>reject4< reject: Still no.]}\n" +
                        "  }\n" +
                        "]\n" +
                        "[>end1< end]";
        DecisionGraphParseResult parseResult = astParser.parse(code);
        decisionGraph = parseResult.compile( new CompoundType("","") );
        
        List<NodeValidationMessage> messages = instance.validateUnreachableNodes(decisionGraph);
        
        Set<String> expectedEntityIds = new HashSet<>(Arrays.asList("reject4","reject3","end1","ask2"));
        
        Set<String> actualEntitiesIds = new HashSet<>();
        Set<ValidationMessage.Level> actualLevels = EnumSet.noneOf(ValidationMessage.Level.class);
        
        messages.stream().forEach((vm) -> {
            actualEntitiesIds.addAll(
                    vm.getEntities().stream().map( Node::getId ).collect(Collectors.toSet()) );
            actualLevels.add(vm.getLevel());
        });
        
        assertEquals( EnumSet.of(ValidationMessage.Level.WARNING), actualLevels );
        assertEquals( expectedEntityIds, actualEntitiesIds );
    }

    
}
