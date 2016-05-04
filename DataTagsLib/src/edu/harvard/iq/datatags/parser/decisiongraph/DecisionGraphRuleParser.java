package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphTerminalParser.Tags;
import static edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphTerminalParser.nodeStructurePart;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNodeHead;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTermSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTextSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.internal.util.Strings;

/**
 * Parses the terminals of a decision graph code into an AST.
 * This parsers in this class are <em>token level</em> parsers - they expect
 * a token stream input, rather than a String-based one.
 * 
 * @see DecisionGraphTerminalParser
 * 
 * @author michael
 */
public class DecisionGraphRuleParser {
    
    // -------------------------------
    // Node parts and helper parsers.
    // -------------------------------
    
    static Parser<AstNodeHead> nodeHeadWithId( String keyword ){
        return Parsers.sequence(
                Terminals.fragment( Tags.NODE_ID ),
                nodeStructurePart(keyword),
                (  id,  word ) -> new AstNodeHead(id, keyword) );
    }
    
    static Parser<AstNodeHead> nodeHeadNoId(String keyword) {
            return nodeStructurePart(keyword).map(( _w ) -> new AstNodeHead(null, keyword) );
    }
    
    static Parser<AstNodeHead> nodeHead( String keyword ) {
        return Parsers.or( nodeHeadWithId(keyword), nodeHeadNoId(keyword));
    }
    
    final static Parser<String> IDENTIFIER_WITH_KEYWORDS;
    
    static {
        List<Parser<?>> parsers = new ArrayList<>();
        parsers.addAll( DecisionGraphTerminalParser.NODE_STRUCTURE_TOKENS.stream()
                        .filter( t -> t.toLowerCase().matches("^[a-z][a-z0-9]*$") )
                        .map( t -> nodeStructurePart(t) ) 
                            .collect( toList() ) );
        IDENTIFIER_WITH_KEYWORDS =  Parsers.sequence(
                Parsers.or(parsers).many(),
                Terminals.identifier().many()
                ).source().map( String::trim );
    }
    
    final static Parser<String> textbodyUpTo( String terminatingNodePart ) {
        List<Parser<?>> parsers = new ArrayList<>();
        parsers.add( Terminals.fragment(Tags.TEXT_BODY ) );
        parsers.add( Terminals.identifier() );
        DecisionGraphTerminalParser.NODE_STRUCTURE_TOKENS.stream()
                .filter( t -> ! t.equals(terminatingNodePart) )
                .forEach( okPart -> parsers.add(nodeStructurePart(okPart)) );
        
        return Parsers.or(parsers).many().source();
    }
    
    final static Parser<AstSetNode.AtomicAssignment> ATOMIC_ASSIGNMENT_SLOT = Parsers.sequence(
            IDENTIFIER_WITH_KEYWORDS.sepBy(nodeStructurePart("/")),
            nodeStructurePart("="),
            IDENTIFIER_WITH_KEYWORDS,
            (path, _eq, value) -> new AstSetNode.AtomicAssignment(path, value.trim())
        );
    
    final static Parser<AstSetNode.AggregateAssignment> AGGREGATE_ASSIGNMENT_SLOT = Parsers.sequence(
            IDENTIFIER_WITH_KEYWORDS.sepBy(nodeStructurePart("/")),
            nodeStructurePart("+="),
            IDENTIFIER_WITH_KEYWORDS.sepBy( nodeStructurePart(",") ),
            (path, _eq, value) -> new AstSetNode.AggregateAssignment(path, value)
        );
    
    final static Parser<AstTextSubNode> TEXT_SUBNODE = Parsers.sequence(
            nodeStructurePart("{"),
            nodeStructurePart("text"),
            nodeStructurePart(":"),
            textbodyUpTo("}"),
            nodeStructurePart("}"),
            (_s, _t, _c, text, _e) -> new AstTextSubNode(text)
    ); 
    
    final static Parser<AstTermSubNode> TERM_SUBNODE = Parsers.sequence(
            nodeStructurePart("{"),
            textbodyUpTo(":"),
            nodeStructurePart(":"),
            textbodyUpTo("}"),
            nodeStructurePart("}"),
            (_s, name, _c, text, _e) -> new AstTermSubNode(name.trim(), text.trim())
    );
    
    final static Parser<List<AstTermSubNode>> TERMS_SUBNODE = Parsers.sequence(
            nodeStructurePart("{"),
            nodeStructurePart( "terms"),
            nodeStructurePart(":"),
            TERM_SUBNODE.many(),
            nodeStructurePart("}"),
            (_s, _h, _c, termNodes, _e) -> termNodes
    );
    
    final static Parser<AstAnswerSubNode> answerSubNode( Parser<List<? extends AstNode>> bodyParser ) {
        return Parsers.sequence(
            nodeStructurePart("{"),
            textbodyUpTo(":"),
            nodeStructurePart(":"),
            bodyParser,
            nodeStructurePart("}"),
            (_s, name, _c, body, _e) -> new AstAnswerSubNode(name.trim(), body));
    }
    
    final static Parser<List<AstAnswerSubNode>> answersSubNode( Parser<List<? extends AstNode>> bodyParser ) {
        return Parsers.sequence(
            nodeStructurePart("{"),
            nodeStructurePart("answers"),
            nodeStructurePart(":"),
            answerSubNode(bodyParser).many1(),
            nodeStructurePart("}"),
            (_s, _name, _c, answers, _e) -> answers );
    }
    
    // -------------------------------
    // Top-level (instruction) nodes.
    // -------------------------------
    final static Parser<AstEndNode> END_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("end"),
            nodeStructurePart("]"),
            ( _s, nhr, _e ) -> new AstEndNode(nhr.getId()));
    
    final static Parser<AstTodoNode> TODO_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("todo"),
            nodeStructurePart(":"),
            textbodyUpTo("]"),
            nodeStructurePart("]"),
            ( _s, headRef, _clmn, text, _e ) -> new AstTodoNode( headRef.getId(), text.trim()) );
    
    final static Parser<AstCallNode> CALL_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("call"),
            nodeStructurePart(":"),
            textbodyUpTo("]"),
            nodeStructurePart("]"),
            ( _s, headRef, _clmn, text, _e ) -> new AstCallNode( headRef.getId(), text.trim()) );
    
    final static Parser<AstRejectNode> REJECT_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("reject"),
            nodeStructurePart(":"),
            textbodyUpTo("]"),
            nodeStructurePart("]"),
            ( _s, headRef, _clmn, text, _e ) -> new AstRejectNode( headRef.getId(), text.trim()) );
   
    final static Parser<AstSetNode> SET_NODE = Parsers.sequence(
            nodeStructurePart("["),
            nodeHead("set"),
            nodeStructurePart(":"),
            Parsers.or( ATOMIC_ASSIGNMENT_SLOT, AGGREGATE_ASSIGNMENT_SLOT).sepBy(nodeStructurePart(";")),
            nodeStructurePart("]"),
            ( _s, head, _c, slots, _e) -> new AstSetNode(head.getId(), slots));
            
    final static Parser<AstAskNode> askNode( Parser<List<? extends AstNode>> bodyParser ) {
        return Parsers.sequence(
                Parsers.sequence(
                        nodeStructurePart("["),
                        nodeHead("ask"),
                        nodeStructurePart(":"),
                        (_s, h, _c) -> h ),
                TEXT_SUBNODE,
                TERMS_SUBNODE.optional(),
                answersSubNode(bodyParser),
                nodeStructurePart("]"),
                ( head, text, terms, answers, _e) -> new AstAskNode( head.getId(), text, terms, answers ));
    }       
    
    
    // -------------------------------
    // Program-level parsers.
    // -------------------------------
    
    final static Parser<List<? extends AstNode>> graphParser() {
        Parser.Reference<List<? extends AstNode>> nodeListParserRef = Parser.newReference();
        Parser<? extends AstNode> singleAstNode = Parsers.or( END_NODE, CALL_NODE, TODO_NODE, REJECT_NODE, SET_NODE, askNode(nodeListParserRef.lazy()));
        Parser<List<? extends AstNode>> nodeSequence = singleAstNode.many().cast();
        nodeListParserRef.set( nodeSequence );
        
        return nodeSequence;
    }
    
  
}
