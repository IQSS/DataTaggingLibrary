package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstNode;
import java.util.List;

/**
 * The result of parsing a decision graph code. Can create an actual
 * decision graph, when provided with a tag space (i.e a @{link CompoundType} instance).
 * 
 * @author michael
 */
public class DecisionGraphParseResult {
   
    private final List<? extends AstNode> nodes;
    
    private final Map<String, Node> nodesById = new HashMap<>();
    
    public DecisionGraphParseResult( List<? extends AstNode> someNodes ) {
        nodes = someNodes;
    }

    public Node compile( CompoundType tagSpace ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public List<? extends AstNode> getNodes() {
        return nodes;
    }
    
    
}
