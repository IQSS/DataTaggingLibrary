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
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.decisiongraph.ContentReader;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.parser.decisiongraph.MemoryContentReader;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.joining;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author mor_vilozni
 */
public class FindSupertypeResultsDgqTest {
    
    PolicyModel policyModel;
    FindSupertypeResultsDgq dgq;
    public DecisionGraphQuery.Listener listener;
    Optional<CompoundSlot> root;
    int foundCount = 0;
    int missCount = 0;
    int rejectCount = 0;
    boolean printLog = false;
        
    @Before
    public void setUp() throws IOException, SyntaxErrorException, SemanticsErrorException {
        printLog = false;
      
        listener = new DecisionGraphQuery.Listener() {
            @Override
            public void started(DecisionGraphQuery dgq) {
                foundCount = 0;
                missCount = 0;
                rejectCount = 0;
            }
            @Override
            public void matchFound(DecisionGraphQuery dgq) {
                foundCount++;
                if ( printLog ) {
                    System.out.println("Match:");
                    System.out.println(dgq.getCurrentTrace().getAnswers());
                    System.out.println(dgq.getCurrentTrace().getNodes().stream().map(n->n.getId()).collect(joining("->")));
                }
            }
            
            @Override
            public void rejectionFound(DecisionGraphQuery dgq) {
                rejectCount++;
                if ( printLog ) {
                    System.out.println("Reject:");
                    System.out.println(dgq.getCurrentTrace().getAnswers());
                    System.out.println(dgq.getCurrentTrace().getNodes().stream().map(n->n.getId()).collect(joining("->")));
                }
            }

            
            @Override
            public void nonMatchFound(DecisionGraphQuery dgq) {
                missCount++;
                if ( printLog ) {
                    System.out.println("Miss:");
                    System.out.println(dgq.getCurrentTrace().getAnswers());
                    System.out.println(dgq.getCurrentTrace().getNodes());
                }
            }
            @Override
            public void done(DecisionGraphQuery dgq) {}
            @Override
            public void loopDetected(DecisionGraphQuery dgq) {
                System.out.println("Loop detected: ");
                System.out.println(dgq.getCurrentTrace().getNodes().stream().map(n->n.getId()).collect(joining("->")));
            }
        };
    }
    
    @Test
    public void testSanity() throws DataTagsParseException, IOException {
        System.out.println("testGet");
        String code = "[set: Atomic=val0]";
        String spaceTags = "PSRoot: consists of Atomic.\n" +
                            "Atomic: one of val0, val1, val2.";
        
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
        
        SetNode sn = createSetNode("Atomic=val0");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",1, foundCount);
        assertEquals("runs",1, missCount+foundCount);
        
        sn = createSetNode("Atomic=val2");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",0, foundCount);
        assertEquals("runs",1, missCount+foundCount);
    }
    
    @Ignore("See #202")
    @Test
    public void testSanitySection() throws DataTagsParseException, IOException {
        System.out.println("testGet");
        String code = "[section: {title:sec}\n"
                    + "   [set: Atomic=val0]\n"
                    + "]";
        
        String spaceTags = "PSRoot: consists of Atomic.\n" +
                            "Atomic: one of val0, val1, val2.";
        
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
        
        SetNode sn = createSetNode("Atomic=val0");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",1, foundCount);
        assertEquals("runs",1, missCount+foundCount);
        
        sn = createSetNode("Atomic=val2");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",0, foundCount);
        assertEquals("runs",1, missCount+foundCount);
    }
    
    @Ignore("See #202")
    @Test
    public void testSanitySection2() throws DataTagsParseException, IOException {
        System.out.println("testGet");
        String code = "[>s1< section: {title:sec}\n"
                    + "   [set: Atomic=val0]\n"
                    + "]"
                    + "[>s2< section: {title:sec}\n"
                    + "   [set: Atomic=val1]\n"
                    + "]"
                ;
        
        String spaceTags = "PSRoot: consists of Atomic.\n" +
                            "Atomic: one of val0, val1, val2.";
        
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
        
        SetNode sn = createSetNode("Atomic=val1");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",1, foundCount);
        assertEquals("runs",1, missCount+foundCount);
        
        sn = createSetNode("Atomic=val2");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",0, foundCount);
        assertEquals("runs",1, missCount+foundCount);
    }
    
    @Ignore("need to change FindSupertypeResultsDgq")
    @Test
    public void testSanityCallToPart() throws DataTagsParseException, IOException {
        System.out.println("testGet");
        String code = "[call:s1][>e< end][-->s1< {title:sec}\n"
                    + "   [set: Atomic=val0]\n"
                    + "--]";
        
        String spaceTags = "PSRoot: consists of Atomic.\n" +
                            "Atomic: one of val0, val1, val2.";
        
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
        
        SetNode sn = createSetNode("Atomic=val0");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",1, foundCount);
        assertEquals("runs",1, missCount+foundCount);
        
        sn = createSetNode("Atomic=val2");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",0, foundCount);
        assertEquals("runs",1, missCount+foundCount);
    }
    
    /*
     * Test of get method, of class FindSupertypeResultsDgq.
     */
    @Test
    public void testGet() throws DataTagsParseException, IOException {
        System.out.println("testGet");
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
                        "[set: Compound/Comp1 = c1_2]\n";
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
        SetNode sn = createSetNode("Aggregate += optA,optB,optC");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",3, foundCount);
        assertEquals("runs",12, missCount+foundCount);
        
        sn = createSetNode("Atomic=val2");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",4, foundCount);
        assertEquals("runs",12, missCount+foundCount);
    }
    
    @Ignore("need to change FindSupertypeResultsDgq")
    @Test
    public void testGetWithCallAndSection() throws DataTagsParseException, IOException {
        //set up
        String code = "[call: dogs]\n" +
                        "[end]\n" +
                        "\n" +
                        "[>dogs< section:\n" +
                        "  {title: Dogs!}\n" +
                        "  [>q-dogType< ask:\n" +
                        "    {text: What type of dogs?}\n" +
                        "    {answers:\n" +
                        "      {none: }\n" +
                        "      {animated: [set: Dogs += Pluto]}\n" +
                        "      {cute: [set: Dogs += Rex, Lassie]}\n" +
                        "      {hounds: [set: Dogs += Pluto, Lassie]}\n" +
                        "    }\n" +
                        "  ]\n" +
                        "  [>dog_section_set< set: SetDogs = dWorks]\n" +
                        "]\n" +
                        "\n" +
                        "[>cats< section:\n" +
                        "  {title: Cats}\n" +
                        "  [>q:cats:group< ask:\n" +
                        "    {text: What cats?}\n" +
                        "    {answers:\n" +
                        "      {all: [set: Cats += Tom, Shmil, Mitzi]}\n" +
                        "      {some: [set: Cats += Tom, Shmil]}\n" +
                        "      {none: }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "  [>cat_section_set< set: SetCats = cWorks]\n" +
                        "]";
        String spaceTags = "Base: consists of Cats, Dogs, Rice, SetDogs, SetCats.\n" +
                            "Cats: some of Tom, Shmil, Mitzi.\n" +
                            "Dogs[A friendly mamle with a tail]: some of\n" +
                            "  Rex [\"King\", in Latin],\n" +
                            "  Pluto [This dog user to be a star, then a planet, now a big rock.],\n" +
                            "  Lassie [Eventually comes home.].\n" +
                            "Rice: one of White, Full.\n" +
                            "SetDogs: one of dWorks.\n" +
                            "SetCats: one of cWorks.\n";
        
        Map<Path,String> pathToString = new HashMap<>();
        PolicyModelData pmd = new PolicyModelData();
        pmd.setDecisionGraphPath(Paths.get("/main.dg"));
        pmd.setMetadataFile(Paths.get("/test/main.dg"));
        pathToString.put(Paths.get("/main.dg"), code);
        ContentReader contentReader = new MemoryContentReader(pathToString);
        DecisionGraphCompiler dgc = new DecisionGraphCompiler(contentReader);
        TagSpaceParser tsp = new TagSpaceParser();
        root = tsp.parse(spaceTags).buildType("Base");
        DecisionGraph chart = dgc.compile(root.get(), pmd, new ArrayList<>());
        policyModel = new PolicyModel();
        policyModel.setDecisionGraph(chart);
        policyModel.setSpaceRoot(root.get());
        
        //test
        SetNode sn = createSetNode("SetDogs = dWorks");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        System.out.println("\n\n\nGetWithCallAndSection");
        dgq.get( listener );
        System.out.println("/ GetWithCallAndSection\n\n");
        assertEquals("runs",4, missCount+foundCount);
        assertEquals("match",4, foundCount);
        
        sn = createSetNode("SetCats = cWorks");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",0, foundCount);
        assertEquals("runs",4, missCount+foundCount);
        
    }
    
    @Ignore("need to change FindSupertypeResultsDgq")
    @Test
    public void testGetWithCallAndSectionDouble() throws DataTagsParseException, IOException {
        System.out.println("testGetWithCallAndSectionDouble");
        //set up
        String code = "[call: dogs]\n" +
                        "[call: cats]\n" +
                        "[end]\n" +
                        "\n" +
                        "[>dogs< section:\n" +
                        "  {title: Dogs!}\n" +
                        "  [>q-dogType< ask:\n" +
                        "    {text: What type of dogs?}\n" +
                        "    {answers:\n" +
                        "      {none: }\n" +
                        "      {animated: [set: Dogs += Pluto]}\n" +
                        "      {cute: [set: Dogs += Rex, Lassie]}\n" +
                        "      {hounds: [set: Dogs += Pluto, Lassie]}\n" +
                        "    }\n" +
                        "  ]\n" +
                        "  [>dog_section_set< set: SetDogs = dWorks]\n" +
                        "]\n" +
                        "\n" +
                        "[>cats< section:\n" +
                        "  {title: Cats}\n" +
                        "  [>q:cats:group< ask:\n" +
                        "    {text: What cats?}\n" +
                        "    {answers:\n" +
                        "      {all: [set: Cats += Tom, Shmil, Mitzi]}\n" +
                        "      {some: [set: Cats += Tom, Shmil]}\n" +
                        "      {none: [set: Rice=Full]}\n" +
                        "    }\n" +
                        "  ]\n" +
                        "  [>cat_section_set< set: SetCats = cWorks]\n" +
                        "]";
        String spaceTags = "Base: consists of Cats, Dogs, Rice, SetDogs, SetCats.\n" +
                            "Cats: some of Tom, Shmil, Mitzi.\n" +
                            "Dogs[A friendly mamle with a tail]: some of\n" +
                            "  Rex [\"King\", in Latin],\n" +
                            "  Pluto [This dog user to be a star, then a planet, now a big rock.],\n" +
                            "  Lassie [Eventually comes home.].\n" +
                            "Rice: one of White, Full.\n" +
                            "SetDogs: one of dWorks.\n" +
                            "SetCats: one of cWorks.\n";
        
        Map<Path,String> pathToString = new HashMap<>();
        PolicyModelData pmd = new PolicyModelData();
        pmd.setDecisionGraphPath(Paths.get("/main.dg"));
        pmd.setMetadataFile(Paths.get("/test/main.dg"));
        pathToString.put(Paths.get("/main.dg"), code);
        ContentReader contentReader = new MemoryContentReader(pathToString);
        DecisionGraphCompiler dgc = new DecisionGraphCompiler(contentReader);
        TagSpaceParser tsp = new TagSpaceParser();
        root = tsp.parse(spaceTags).buildType("Base");
        DecisionGraph chart = dgc.compile(root.get(), pmd, new ArrayList<>());
        policyModel = new PolicyModel();
        policyModel.setDecisionGraph(chart);
        policyModel.setSpaceRoot(root.get());
        
        //test
        SetNode sn = createSetNode("SetDogs = dWorks");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        System.out.println("\n\n\nGetWithCallAndSection");
        dgq.get( listener );
        System.out.println("/ GetWithCallAndSection\n\n");
        assertEquals("runs",12, missCount+foundCount);
        assertEquals("match",12, foundCount);
        
        sn = createSetNode("SetCats = cWorks");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",12, foundCount);
        assertEquals("runs",12, missCount+foundCount);
        
        sn = createSetNode("Rice = Full");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",4, foundCount);
        assertEquals("runs",12, missCount+foundCount);
        
    }
    
    @Ignore
    @Test
    public void testGetWithSections() throws DataTagsParseException, IOException {
        System.out.println("testGetWithSections");
        //set up
        printLog = true;
        String code =   "[>sd< section:\n" +
                        "  {title: Dogs!}\n" +
                        "  [>sda< ask:\n" +
                        "    {text: What type of dogs?}\n" +
                        "    {answers:\n" +
                        "      {animated: [>sds1< set: Dogs += Pluto]}\n" +
                        "      {cute:     [>sds2< set: Dogs += Rex, Lassie]}\n" +
                        "      {hounds:   [>sds3< set: Dogs += Pluto, Lassie]}\n" +
                        "      {none: }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "  [>sdsx< set: SetDogs = dWorks]\n" +
                        "]\n" +
                        "\n" +
                        "[>sc< section:\n" +
                        "  {title: Cats}\n" +
                        "  [>sca< ask:\n" +
                        "    {text: What cats?}\n" +
                        "    {answers:\n" +
                        "      {all:  [>scs1< set: Cats += Tom, Shmil, Mitzi]}\n" +
                        "      {some: [>scs2< set: Cats += Tom, Shmil]}\n" +
                        "      {none: [>scs3< set: Rice=Full]}\n" +
                        "    }\n" +
                        "  ]\n" +
                        "  [>scsx< set: SetCats = cWorks]\n" +
                        "]";
        String spaceTags = "Base: consists of Cats, Dogs, Rice, SetDogs, SetCats.\n" +
                            "Cats: some of Tom, Shmil, Mitzi.\n" +
                            "Dogs[A friendly mamle with a tail]: some of\n" +
                            "  Rex [\"King\", in Latin],\n" +
                            "  Pluto [This dog user to be a star, then a planet, now a big rock.],\n" +
                            "  Lassie [Eventually comes home.].\n" +
                            "Rice: one of White, Full.\n" +
                            "SetDogs: one of dWorks.\n" +
                            "SetCats: one of cWorks.\n";
        
        Map<Path,String> pathToString = new HashMap<>();
        PolicyModelData pmd = new PolicyModelData();
        pmd.setDecisionGraphPath(Paths.get("/main.dg"));
        pmd.setMetadataFile(Paths.get("/test/main.dg"));
        pathToString.put(Paths.get("/main.dg"), code);
        ContentReader contentReader = new MemoryContentReader(pathToString);
        DecisionGraphCompiler dgc = new DecisionGraphCompiler(contentReader);
        TagSpaceParser tsp = new TagSpaceParser();
        root = tsp.parse(spaceTags).buildType("Base");
        DecisionGraph chart = dgc.compile(root.get(), pmd, new ArrayList<>());
        policyModel = new PolicyModel();
        policyModel.setDecisionGraph(chart);
        policyModel.setSpaceRoot(root.get());
        
        //test
        SetNode sn = createSetNode("SetDogs = dWorks");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("runs",12, missCount+foundCount);
        assertEquals("match",12, foundCount);
        
        sn = createSetNode("SetCats = cWorks");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("runs",12, missCount+foundCount);
        assertEquals("match",12, foundCount);
        
        sn = createSetNode("Rice = Full");
        dgq = new FindSupertypeResultsDgq(policyModel, sn.getTags());
        dgq.get( listener );
        assertEquals("match",4, foundCount);
        assertEquals("runs",12, missCount+foundCount);
        
    }
    
    private SetNode createSetNode(String tagValue) throws DataTagsParseException{
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
