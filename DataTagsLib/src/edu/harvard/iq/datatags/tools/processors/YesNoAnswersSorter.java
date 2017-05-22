package edu.harvard.iq.datatags.tools.processors;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/** 
 * Organizes the yes/no answer in answer nodes.
 * @author michael
 */
public class YesNoAnswersSorter implements DecisionGraphProcessor {
    
    private boolean yesFirst = true;
    
    @Override
    public String getTitle() {
        return "Yes/No answers sorter (yes " + (yesFirst?"first":"last") + ")";
    }

    @Override
    public DecisionGraph process(DecisionGraph fcs) {
        Node.Visitor sorter = new Node.VoidVisitor(){
            @Override
            public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                List<Answer> answers = nd.getAnswers();
                if ( answers.size() != 2 ) return; // quick circuit breaker
                Set<String> answerTexts = answers.stream().map(Answer::getAnswerText)
                                                 .map(String::toLowerCase).collect(toSet());
                
                if ( answerTexts.containsAll(Arrays.asList("yes","no")) ) {
                    Answer ansYes = nd.getAnswers().get(0);
                    Answer ansNo = nd.getAnswers().get(1);
                    Node nfYes = nd.getNodeFor(ansYes);
                    Node nfNo = nd.getNodeFor(ansNo);
                    if ( ansYes.getAnswerText().toLowerCase().equals("no") ) {
                        Answer ta = ansYes;
                        ansYes = ansNo;
                        ansNo = ta;
                        Node tn = nfYes;
                        nfYes = nfNo;
                        nfNo = tn;
                    }
                    
                    // Invariant: ansYes is the yes, and ansNo is the no.
                    
                    nd.removeAnswer(ansNo);
                    nd.removeAnswer(ansYes);
                    if ( yesFirst ) {
                        nd.addAnswer(ansYes, nfYes);
                        nd.addAnswer(ansNo,  nfNo);
                    } else {
                        nd.addAnswer(ansNo,  nfNo);
                        nd.addAnswer(ansYes, nfYes);
                    }
                }
            }

            @Override
            public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {}

            @Override
            public void visitImpl(SetNode nd) throws DataTagsRuntimeException {}

            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {}

            @Override
            public void visitImpl(CallNode nd) throws DataTagsRuntimeException {}

            @Override
            public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {}

            @Override
            public void visitImpl(EndNode nd) throws DataTagsRuntimeException {}
        };
        
        for ( Node nd : fcs.nodes() ) {
            nd.accept(sorter);
        }
        
        return fcs;
    }

    public void setYesFirst(boolean yesFirst) {
        this.yesFirst = yesFirst;
    }

    public boolean isYesFirst() {
        return yesFirst;
    }
    
}
