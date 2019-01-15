/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.datatags.tools.processors;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author michael
 */
public class YesNoAnswersSorterTest {
    
    private static final String SPACE = "DataTags: consists of P.\nP: one of A, B, C.";
    private static final String YES_FIRST_CODE = "[>a<ask: {text:a}{answers: {yes: [>y<set: P=A]}{no:[>n<todo: x]}}]";
    private static final String YES_LAST_CODE = "[>a<ask: {text:a}{answers: {no:[>n<todo: x]}{yes: [>y<set: P=A]}}]";
    
    CompoundSlot ts;
    
    @Before
    public void setup() throws SyntaxErrorException, SemanticsErrorException {
        ts = new TagSpaceParser().parse(SPACE).buildType("DataTags").get();
    }
    
    @Test
    public void testGetTitle() {
        YesNoAnswersSorter sut = new YesNoAnswersSorter();
        assertTrue( sut.getTitle().contains("yes first"));
        sut.setYesFirst(false);
        assertTrue( sut.getTitle().contains("yes last"));
        
    }

    @Test
    public void testSortYesFirstSame() throws Exception {

        CompilationUnit cu = new CompilationUnit(YES_FIRST_CODE);
        cu.compile(ts, new EndNode("[SYN-END]"), new ArrayList<>());
        DecisionGraph dgOriginal = cu.getDecisionGraph();
        
        Node originalYes = dgOriginal.getNode("y");
        Node originalNo  = dgOriginal.getNode("n");
        
        YesNoAnswersSorter sut = new YesNoAnswersSorter();
        sut.process(dgOriginal);
        
        AskNode newAskNode = (AskNode) dgOriginal.getNode("a");
        assertEquals(Arrays.asList("yes","no"), answerTexts(newAskNode));
        assertEquals( originalYes, newAskNode.getNodeFor(Answer.YES) );
        assertEquals( originalNo, newAskNode.getNodeFor(Answer.NO) );
    }

    @Test
    public void testSortYesFirstChange() throws Exception {

        CompilationUnit cu = new CompilationUnit(YES_LAST_CODE);
        cu.compile(ts, new EndNode("[SYN-END]"), new ArrayList<>());
        DecisionGraph dgOriginal = cu.getDecisionGraph();
        
        Node originalYes = dgOriginal.getNode("y");
        Node originalNo  = dgOriginal.getNode("n");
        
        YesNoAnswersSorter sut = new YesNoAnswersSorter();
        sut.process(dgOriginal);
        
        AskNode newAskNode = (AskNode) dgOriginal.getNode("a");
        assertEquals(Arrays.asList("yes","no"), answerTexts(newAskNode));
        assertEquals( originalYes, newAskNode.getNodeFor(Answer.YES) );
        assertEquals( originalNo, newAskNode.getNodeFor(Answer.NO) );
    }
    
    @Test
    public void testSortNoFirstSame() throws Exception {

        CompilationUnit cu = new CompilationUnit(YES_LAST_CODE);
        cu.compile(ts, new EndNode("[SYN-END]"), new ArrayList<>());
        DecisionGraph dgOriginal = cu.getDecisionGraph();
        
        Node originalYes = dgOriginal.getNode("y");
        Node originalNo  = dgOriginal.getNode("n");
        
        YesNoAnswersSorter sut = new YesNoAnswersSorter();
        sut.setYesFirst(false);
        sut.process(dgOriginal);
        
        AskNode newAskNode = (AskNode) dgOriginal.getNode("a");
        assertEquals(Arrays.asList("no","yes"), answerTexts(newAskNode));
        assertEquals( originalYes, newAskNode.getNodeFor(Answer.YES) );
        assertEquals( originalNo, newAskNode.getNodeFor(Answer.NO) );
    }
    
    @Test
    public void testSortNoFirstChange() throws Exception {
        
        CompilationUnit cu = new CompilationUnit(YES_FIRST_CODE);
        cu.compile(ts, new EndNode("[SYN-END]"), new ArrayList<>());
        DecisionGraph dgOriginal = cu.getDecisionGraph();
        
        Node originalYes = dgOriginal.getNode("y");
        Node originalNo  = dgOriginal.getNode("n");
        
        YesNoAnswersSorter sut = new YesNoAnswersSorter();
        sut.setYesFirst(false);
        sut.process(dgOriginal);
        
        AskNode newAskNode = (AskNode) dgOriginal.getNode("a");
        assertEquals(Arrays.asList("no","yes"), answerTexts(newAskNode));
        assertEquals( originalYes, newAskNode.getNodeFor(Answer.YES) );
        assertEquals( originalNo, newAskNode.getNodeFor(Answer.NO) );
    }

    
    List<String> answerTexts( AskNode an ) {
        return an.getAnswers().stream().map(n->n.getAnswerText()).collect(toList());
    }
    
}
