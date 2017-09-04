package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.decisiongraph.ContentReader;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.parser.decisiongraph.MemoryContentReader;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.util.DecisionGraphHelper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateUnreachableNodesTest_reachableNodes() throws DataTagsParseException, IOException {
        String code = "[>ask1< ask: {text: Will this work?}\n" +
                        "  {answers:\n" +
                        "    {yes: [call:shouldWork]}}]\n" +
                        "[>end1< end]\n" +
                        "[>shouldWork< ask: \n" +
                        "  {text: This should work.}\n" +
                        "  {answers:\n" +
                        "    {yes: [>reject1< reject: Good it works.]}\n" +
                        "    {no: [>reject2< reject: This should have worked.]}}]";
        
        Map<Path, String> pathToString = new HashMap<>();
        PolicyModelData pmd = new PolicyModelData();
        pmd.setDecisionGraphPath(Paths.get("/main.dg"));
        pmd.setMetadataFile(Paths.get("/test/main.dg"));
        pathToString.put(Paths.get("/main.dg"), code);
        ContentReader contentReader = new MemoryContentReader(pathToString);
        DecisionGraphCompiler dgc = new DecisionGraphCompiler(contentReader);
        decisionGraph = dgc.compile(new CompoundSlot("", ""), pmd, new ArrayList<>());

        List<ValidationMessage> messages = instance.validate(decisionGraph);
        
        Set<Node> expected = Collections.<Node>emptySet();
        Set<Node> actualEntities = new HashSet<>();
        Set<NodeValidationMessage.Level> actualLevels = EnumSet.noneOf(NodeValidationMessage.Level.class);
        
        for ( ValidationMessage vm : messages ) {
            actualEntities.addAll(((NodeValidationMessage)vm).getEntities());
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
        
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        decisionGraph = cu.getDecisionGraph();
        
        List<ValidationMessage> messages = instance.validate(decisionGraph);
        
        Set<Node> expected = Collections.singleton( new EndNode("nr") );
        Set<Node> actualEntities = new HashSet<>();
        Set<ValidationMessage.Level> actualLevels = EnumSet.noneOf(ValidationMessage.Level.class);
        
        messages.forEach( vm -> {
            actualEntities.addAll(((NodeValidationMessage)vm).getEntities());
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
        
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        decisionGraph = cu.getDecisionGraph();
                
        List<ValidationMessage> messages = instance.validate(decisionGraph);
        
        Set<String> expectedEntityIds = new HashSet<>(Arrays.asList("reject4","reject3","end1","ask2"));
        
        Set<String> actualEntitiesIds = new HashSet<>();
        Set<ValidationMessage.Level> actualLevels = EnumSet.noneOf(ValidationMessage.Level.class);
        
        messages.stream().forEach((vm) -> {
            actualEntitiesIds.addAll(
                    ((NodeValidationMessage)vm).getEntities().stream()
                                               .map( Node::getId )
                                               .collect(Collectors.toSet()) );
            actualLevels.add(vm.getLevel());
        });
        
        assertEquals( EnumSet.of(ValidationMessage.Level.WARNING), actualLevels );
        assertEquals( expectedEntityIds, actualEntitiesIds );
    }

    
}
