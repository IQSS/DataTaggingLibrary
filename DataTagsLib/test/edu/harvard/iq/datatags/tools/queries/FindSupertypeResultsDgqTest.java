/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.tools.queries;

import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.decisiongraph.ContentReader;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.parser.decisiongraph.MemoryContentReader;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mor_vilozni
 */
public class FindSupertypeResultsDgqTest {
    
    PolicyModel policyModel;
    FindSupertypeResultsDgq dgq;
    DecisionGraphQuery.Listener listener;
    Optional<CompoundSlot> root;
    int foundCount = 0;
    int missCount = 0;
    
    public FindSupertypeResultsDgqTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException, SyntaxErrorException, SemanticsErrorException {
        String code = "[set: Atomic=val0]\n" +
                        "[ask:\n" +
                        "  {text: Set atomic }\n" +
                        "  {answers:\n" +
                        "    {to 1: [set: Atomic=val1]}\n" +
                        "    {to 2: [set: Atomic=val2]}\n" +
                        "    {skip:}\n" +
                        "  }\n" +
                        "]\n" +
                        "[>agg< ask:\n" +
                        "  {text: Agg options}\n" +
                        "  {answers:\n" +
                        "    {none:}\n" +
                        "    {one: [set: Aggregate += optA]}\n" +
                        "    {some: [set: Aggregate += optA,optB]}\n" +
                        "    {all: [set: Aggregate += optA,optB,optC]}\n" +
                        "  }\n" +
                        "]\n" +
                        "[set: Compound/Comp1 = c1_2]\n" +
                        "[>idSec< section:\n" +
                        "  {title: Setting the compound}\n" +
                        "  [set: Comp2 = c2_2]\n" +
                        "]\n" +
                        "";
        String spaceTags = "PSRoot: consists of Atomic, Aggregate, Compound.\n" +
                            "\n" +
                            "Atomic: one of val0, val1, val2.\n" +
                            "Aggregate: some of optA, optB, optC.\n" +
                            "Compound : consists of Comp1 , Comp2.\n" +
                            "\n" +
                            "Comp1 : one of c1_0 , c1_1 , c1_2.\n" +
                            "Comp2 : one of c2_0 , c2_1 , c2_2.";
        
        Map<Path,String> pathToString = new HashMap<>();
        PolicyModelData pmd = new PolicyModelData();
        pmd.setDecisionGraphPath(Paths.get("/main.dg"));
        pmd.setMetadataFile(Paths.get("/test/main.dg"));
        pathToString.put(Paths.get("/main.dg"), code);
        ContentReader contentReader = new MemoryContentReader(pathToString);
        DecisionGraphCompiler dgc = new DecisionGraphCompiler(contentReader);
        TagSpaceParser tsp = new TagSpaceParser();
        root = tsp.parse(spaceTags).buildType("PSRoot");
        DecisionGraph chart = dgc.compile(root.get(), pmd, new ArrayList<>());
        policyModel = new PolicyModel();
        policyModel.setDecisionGraph(chart);
        policyModel.setSpaceRoot(root.get());
      
        listener = new DecisionGraphQuery.Listener() {
            @Override
            public void started(DecisionGraphQuery dgq) {}
            @Override
            public void matchFound(DecisionGraphQuery dgq) {
                foundCount++;
            }
            @Override
            public void nonMatchFound(DecisionGraphQuery dgq) {
                missCount++;
            }
            @Override
            public void done(DecisionGraphQuery dgq) {}
            @Override
            public void loopDetected(DecisionGraphQuery dgq) {}
        };
    }

    /**
     * Test of get method, of class FindSupertypeResultsDgq.
     */
    @Test
    public void testGet() throws DataTagsParseException {
        SetNode sn = getSetNode("Aggregate += optA,optB,optC");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",3, foundCount);
        assertEquals("runs",12, missCount+foundCount);
        
        foundCount = 0;
        missCount = 0;
        sn = getSetNode("Atomic=val2");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",4, foundCount);
        assertEquals("runs",12, missCount+foundCount);
    }
    
    private SetNode getSetNode(String tagValue) throws DataTagsParseException{
        String tagValueExpression = "[>x< set: " + tagValue + "]";
        CompilationUnit cu = new CompilationUnit(tagValueExpression);
        try {
            cu.compile(root.get(), new EndNode("SYN-END"), new ArrayList<>());
        } catch ( RuntimeException rte ) {
            if ( rte.getCause() instanceof BadSetInstructionException ) {
                throw (BadSetInstructionException) rte.getCause();
            } else {
                throw rte;
            }
        }
        return (SetNode) cu.getDecisionGraph().getNode("x");
    }
    
}
