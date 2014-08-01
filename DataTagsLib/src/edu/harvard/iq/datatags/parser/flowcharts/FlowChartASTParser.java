package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.parser.AbstractASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.references.AnswerNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeBodyPart;
import edu.harvard.iq.datatags.parser.flowcharts.references.RejectNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeHeadRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TermNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TextNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TodoNodeRef;
import java.util.Collections;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import static org.codehaus.jparsec.Parsers.*;
import org.codehaus.jparsec.Scanners;
import static org.codehaus.jparsec.Scanners.*;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.functors.Tuple3;
import org.codehaus.jparsec.pattern.Pattern;
import org.codehaus.jparsec.pattern.Patterns;

/**
 * Parser for the AST of the DataTags flowchart language.
 * Produces references that are later useful for linking the actual flow chart
 * objects.
 * 
 * @author michael
 */
public class FlowChartASTParser extends AbstractASTParser {
	
	Pattern NODE_ID = Patterns.among(" .\t,/~?!@$%^&*_+-").or(Patterns.range('a', 'z'))
                                .or(Patterns.range('A', 'Z')).or(Patterns.range('0','9')).many1();
    
    // TODO sync with the data tag defintion
    Pattern VALUE_NAME = Patterns.among("!-_?").or(Patterns.range('a', 'z'))
                                .or(Patterns.range('A', 'Z')).or(Patterns.range('0','9')).many1();
	
    static final Parser<List<Void>> whitespaces = WHITESPACES.many().optional();
	static final Parser<Void> nodeStart = Scanners.isChar('(', "NODE_START").followedBy(Scanners.WHITESPACES.optional());
	static final Parser<Void> nodeEnd = Scanners.isChar(')', "NODE_END");
	static final Parser<Void> nodeHeadEnd = Scanners.isChar(':', "NODE_HEAD_END").followedBy( Scanners.WHITESPACES.optional() );
    
    public Parser<List<InstructionNodeRef>> graphParser() {
        Parser.Reference<List<InstructionNodeRef>> ref = Parser.newReference();
        Parser<InstructionNodeRef> topLevelNodes = Parsers.or( callNode(), endNode(), todoNode(), setNode(), rejectNode(), askNode(ref.lazy()) );
        Parser<List<InstructionNodeRef>> graphParser =  topLevelNodes.followedBy( WHITESPACES.many().optional() ).many();
        ref.set( graphParser );
        return graphParser;
    }
    
    
	<T> Parser<NodeBodyPart<T>> nodeBodyPart( Parser<T> bodyParser ) { 
		return tuple( pattern(NODE_ID, "Node body part").source()
								.followedBy(among(":").followedBy(WHITESPACES.optional())),
						      bodyParser.followedBy( WHITESPACES.optional()) )
				      .map( new Map<Pair<String, T>, NodeBodyPart<T>>(){
			@Override
			public NodeBodyPart<T> map(Pair<String, T> from) {
				return new NodeBodyPart(from.a, from.b);
			}
		}).between(nodeEnd, WHITESPACES.optional().followedBy(nodeStart));
	}
	
    Parser<EndNodeRef> endNode() {
        return completeNode(nodeHeadTitled("end").map(new Map<NodeHeadRef, EndNodeRef>(){
            @Override
            public EndNodeRef map(NodeHeadRef from) {
                return new EndNodeRef(from.getId());
            }
        }));
    }
    
    Parser<TodoNodeRef> todoNode() {
        return stringBodyNode("todo").map(new Map<Pair<NodeHeadRef, String>,TodoNodeRef>(){
            @Override
            public TodoNodeRef map(Pair<NodeHeadRef, String> from) {
                return new TodoNodeRef( from.a.getId(), from.b );
            }
        }).or(completeNode( nodeHeadTitled("todo") )
				.map(new Map<NodeHeadRef, TodoNodeRef>(){
                    @Override
                    public TodoNodeRef map(NodeHeadRef from) {
                        return new TodoNodeRef( from.getId(), null );
                    }
        } ));
    }
    
    Parser<CallNodeRef> callNode() {
        return stringBodyNode("call").map(new Map<Pair<NodeHeadRef, String>, CallNodeRef>(){
            @Override
            public CallNodeRef map(Pair<NodeHeadRef, String> from) {
                return new CallNodeRef( from.a.getId(), from.b.trim() );
        }});
    }
    
	Parser<NodeHeadRef> nodeHeadTitled(String nodeTitle) {
        return nodeHeadTitled( Scanners.string(nodeTitle).source() );
    }
    
	Parser<NodeHeadRef> nodeHeadTitled(Parser<String> nodeTitleParser) {
		return tuple(nodeId().followedBy(WHITESPACES.optional()), nodeTitleParser.followedBy(WHITESPACES.optional())).map(new Map<Pair<String, String>, NodeHeadRef>(){
                    @Override
                    public NodeHeadRef map(Pair<String, String> from) {
                        return new NodeHeadRef(from.a, from.b);
                    }
		} )
		.or(nodeTitleParser.map(new Map<String, NodeHeadRef>() {
                    @Override
                    public NodeHeadRef map(String from) {
                        return new NodeHeadRef(null, from);
                    }
		}));
	}
    
    Parser<NodeHeadRef> stringNodeHead() {
        return nodeHeadTitled( pattern(NODE_ID, "Node title").source() );
    }
    
	Parser<String> nodeId() {
        return
            isChar('>').followedBy(
                pattern(NODE_ID, "Node id")).source().followedBy(
                    isChar('<'))
            .map( new Map<String, String>(){
                @Override
                public String map(String from) {
                    return from.substring(1).trim();
            }});
	}
	
    Parser<String> valueName() {
        return pattern(VALUE_NAME, "Value name").source();
    }
    
    Parser<Pair<String, String>> setAssignmentPair() {
        return tuple( slotReference().followedBy( whitespaces )
                                .followedBy( Scanners.isChar('=') ).followedBy( whitespaces ),
                valueName() );
    }

    Parser<String> slotReference() {
        return Scanners.IDENTIFIER.sepBy1(Scanners.isChar('/')).source();
    }
    
    Parser<SetNodeRef> setNode()  {
        return completeNode( tuple( nodeHeadTitled("set").followedBy( nodeHeadEnd ), 
                setAssignmentPair().sepBy(Scanners.isChar(',').followedBy(whitespaces)).followedBy(whitespaces)))
                .map(new Map<Pair<NodeHeadRef, List<Pair<String,String>>>,SetNodeRef>(){

            @Override
            public SetNodeRef map(Pair<NodeHeadRef, List<Pair<String,String>>> from) {
                SetNodeRef ref = new SetNodeRef( from.a.getId() );
                for ( Pair<String,String> ap : from.b ) {
                    ref.addAssignment(ap.a, ap.b);
                }
                return ref;
            }
        });
    }
    
    Parser<TermNodeRef> termNode() {
        return completeNode( tuple( notChar(':').many().source().followedBy(isChar(':')),
                              notChar(')').many().source() )
                       .map( new Map<Pair<String,String>,TermNodeRef>(){
                            @Override
                            public TermNodeRef map(Pair<String, String> from) {
                                return new TermNodeRef(from.a.trim(), from.b.trim());
        }}));
    }
    
    Parser<List<TermNodeRef>> termsNode() {
        return completeNode(tuple(nodeHeadTitled("terms").followedBy( nodeHeadEnd ),
                        termNode().sepBy(whitespaces).followedBy(whitespaces))
                .map(new Map<Pair<NodeHeadRef,List<TermNodeRef>>, List<TermNodeRef>>(){
            @Override
            public List<TermNodeRef> map(Pair<NodeHeadRef, List<TermNodeRef>> from) {
                return from.b;
            }
        }));
    }
    
    Parser<Pair<NodeHeadRef, String>> stringBodyNode( String type ) {
        return stringBodyNode(nodeHeadTitled(type));
    }
	
    Parser<Pair<NodeHeadRef, String>> stringBodyNode( Parser<NodeHeadRef> nodeHead ) {
        return completeNode(
            tuple( nodeHead.followedBy( nodeHeadEnd ),
                 notChar(')').many1().source())
            );
    }
    
    Parser<AskNodeRef> askNode( Parser<List<InstructionNodeRef>> bodyParser ) {
        
        Parser<Pair<TextNodeRef, List<TermNodeRef>>> questionPart = 
                askNodeQuestionPart( );
        
        return completeNode( Parsers.tuple(nodeHeadTitled("ask").followedBy(nodeHeadEnd),
                                            questionPart.followedBy(whitespaces),
                                            answerNode(bodyParser).sepBy(whitespaces)).followedBy(whitespaces))
          .map( new Map<Tuple3<NodeHeadRef, Pair<TextNodeRef, List<TermNodeRef>>, List<AnswerNodeRef>>, AskNodeRef>(){
            @Override
            public AskNodeRef map(Tuple3<NodeHeadRef, Pair<TextNodeRef, List<TermNodeRef>>, List<AnswerNodeRef>> from) {
                return new AskNodeRef(from.a.getId(), from.b.a, from.b.b, from.c );
            }
        } );
    }

    Parser<Pair<TextNodeRef, List<TermNodeRef>>> askNodeQuestionPart() {
        Parser<Pair<TextNodeRef, List<TermNodeRef>>> textOnly =
                textNode().map( new Map<TextNodeRef, Pair<TextNodeRef, List<TermNodeRef>>>(){
                    @Override
                    public Pair<TextNodeRef, List<TermNodeRef>> map(TextNodeRef from) {
                        return new Pair<>(from, Collections.<TermNodeRef>emptyList());
                    }
                });
        
        Parser<Pair<TextNodeRef, List<TermNodeRef>>> textThenTerms = 
                tuple(textNode().followedBy(whitespaces),termsNode());
        
        Parser<Pair<TextNodeRef, List<TermNodeRef>>> termsThenText = 
                tuple(termsNode().followedBy(whitespaces),textNode()
                ).map( new Map<Pair<List<TermNodeRef>,TextNodeRef>, Pair<TextNodeRef, List<TermNodeRef>>>(){
                    @Override
                    public Pair<TextNodeRef, List<TermNodeRef>> map(Pair<List<TermNodeRef>,TextNodeRef> from) {
                        return new Pair<>(from.b, from.a);
                    }});
        
        Parser<Pair<TextNodeRef, List<TermNodeRef>>> questionPart = textThenTerms.or( termsThenText ).or( textOnly );
        
        return questionPart;
    }
    
    Parser<TextNodeRef> textNode() {
        return stringBodyNode( "text" ).map(new Map<Pair<NodeHeadRef, String>, TextNodeRef>(){
            @Override
            public TextNodeRef map(Pair<NodeHeadRef, String> from) {
                return new TextNodeRef(from.a.getId(), from.b);
            }
        });
    }
    
    Parser<RejectNodeRef> rejectNode() {
        return stringBodyNode( "reject" ).map(new Map<Pair<NodeHeadRef, String>, RejectNodeRef>(){
            @Override
            public RejectNodeRef map(Pair<NodeHeadRef, String> from) {
                return new RejectNodeRef(from.a.getId(), from.b);
            }
        });
    }
    
    
    Parser<AnswerNodeRef> answerNode( Parser<List<InstructionNodeRef>> bodyParser ) {
        return completeNode(Parsers.tuple(stringNodeHead().followedBy(nodeHeadEnd), bodyParser).map(new Map<Pair<NodeHeadRef,List<InstructionNodeRef>>, AnswerNodeRef>(){
            @Override
            public AnswerNodeRef map(Pair<NodeHeadRef, List<InstructionNodeRef>> from) {
                return new AnswerNodeRef(from.a, from.b);
            }
        }));
    }
    
    <T> Parser<T> completeNode( Parser<T> nodeBodyParser ) {
        return nodeBodyParser.between(nodeStart, nodeEnd);
    }
    
    String toEnumCase( String val ) {
        char[] raw = val.trim().toLowerCase().toCharArray();
        raw[0] = Character.toUpperCase(raw[0]);
        return new String( raw );
    }
    
}
