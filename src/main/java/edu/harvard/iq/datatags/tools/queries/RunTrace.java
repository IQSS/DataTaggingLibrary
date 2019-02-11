package edu.harvard.iq.datatags.tools.queries;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of nodes and possible answers.
 * @author michael
 */
public class RunTrace {
    private final List<Node> nodes;
    private final List<Answer> answers;
    private final CompoundValue value;

    /**
     * Constructs a new run result. Parameters are all copied.
     * @param someNodes
     * @param someAnswers
     * @param aValue 
     */
    public RunTrace( List<Node> someNodes, List<Answer> someAnswers, CompoundValue aValue ) {
        nodes = new ArrayList<>(someNodes);
        answers = new ArrayList<>(someAnswers);
        value = aValue.getOwnableInstance();
    }
    
    public List<Node> getNodes() {
        return nodes;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public CompoundValue getValue() {
        return value;
    }
    
}
