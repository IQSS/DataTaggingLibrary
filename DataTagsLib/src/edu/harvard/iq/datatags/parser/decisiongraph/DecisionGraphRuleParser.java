package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphTerminalParser.Tags;
import static edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphTerminalParser.nodeStructurePart;
import edu.harvard.iq.datatags.parser.decisiongraph.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.NodeHeadRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.RejectNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.TodoNodeRef;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Pair;

/**
 * Parses the terminals of a decision graph code into AST.
 * 
 * @author michael
 */
public class DecisionGraphRuleParser {
    
    static Parser<NodeHeadRef> nodeHeadWithId( String keyword ){
        return Parsers.sequence(
            Terminals.fragment( Tags.NODE_ID ),
            nodeStructurePart(keyword),
            (  id,  word ) -> new NodeHeadRef(id, keyword) );
    }
    
    static Parser<NodeHeadRef> nodeHeadNoId(String keyword) {
            return nodeStructurePart(keyword).map(( _w ) -> new NodeHeadRef(null, keyword) );
    }
    
    static Parser<NodeHeadRef> nodeHead( String keyword ) {
        return Parsers.or( nodeHeadWithId(keyword), nodeHeadNoId(keyword));
    }
    
    // -------------------------------
    // Node parts and helper parsers.
    // -------------------------------
    
    final static Parser<String> NODE_TEXT_BODY = Parsers.or(
            Terminals.fragment( Tags.NODE_TEXT ),
            Terminals.identifier()
    ).many().source();
            
            
    final static Parser<SetNodeRef.AtomicAssignment> ATOMIC_ASSIGNMENT_SLOT = Parsers.sequence(
            Terminals.identifier().sepBy(nodeStructurePart("/")),
            nodeStructurePart("="),
            Terminals.identifier(),
            (path, _eq, value) -> new SetNodeRef.AtomicAssignment(path, value.trim())
        );
    
    final static Parser<SetNodeRef.AggregateAssignment> AGGREGATE_ASSIGNMENT_SLOT = Parsers.sequence(
            Terminals.identifier().sepBy(nodeStructurePart("/")),
            nodeStructurePart("+="),
            Terminals.identifier().sepBy( nodeStructurePart(",") ),
            (path, _eq, value) -> new SetNodeRef.AggregateAssignment(path, value)
        );
    
    // -------------------------------
    // Top-level (instruction) nodes.
    // -------------------------------
    final static Parser<EndNodeRef> END_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("end"),
            nodeStructurePart("]"),
            ( _s, nhr, _e ) -> new EndNodeRef(nhr.getId()));
    
    final static Parser<TodoNodeRef> TODO_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("todo"),
            nodeStructurePart(":"),
            NODE_TEXT_BODY,
            nodeStructurePart("]"),
            ( _s, headRef, _clmn, text, _e ) -> new TodoNodeRef( headRef.getId(), text.trim()) );
    
    final static Parser<RejectNodeRef> REJECT_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("reject"),
            nodeStructurePart(":"),
            NODE_TEXT_BODY,
            nodeStructurePart("]"),
            ( _s, headRef, _clmn, text, _e ) -> new RejectNodeRef( headRef.getId(), text.trim()) );
   
    
}
