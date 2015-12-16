package edu.harvard.iq.datatags.tools.queries;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of nodes and possible answers
 * @author michael
 */
public class RunTrace {
    private final List<Node> nodes;
    private final List<Answer> answers;

    public RunTrace( List<Node> someNodes, List<Answer> someAnswers ) {
        nodes = new ArrayList<>(someNodes);
        answers = new ArrayList<>(someAnswers);
    }
    
    public List<Node> getNodes() {
        return nodes;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
    
}
