package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.parser.AbstractASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeBodyPart;
import edu.harvard.iq.datatags.parser.flowcharts.references.TypedNodeHeadRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeType;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.StringBodyNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TermNodeRef;
import java.util.Collections;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import static org.codehaus.jparsec.Parsers.*;
import org.codehaus.jparsec.Scanners;
import static org.codehaus.jparsec.Scanners.*;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.pattern.Pattern;
import org.codehaus.jparsec.pattern.Patterns;

/**
 * Parser for the AST of the DataTags flowchart language.
 * Produces references that are later useful for linking the actual flow chart
 * objects.
 * 
 * @author michael
 */
public class FlowChartSetASTParser extends AbstractASTParser {
	
	Pattern NODE_ID = Patterns.among(" \t,/~?!@$%^&*_+-").or(Patterns.range('a', 'z')).or(Patterns.range('A', 'Z')).many1();
	
	<T> Parser<NodeBodyPart<T>> nodeBodyPart( Parser<T> bodyParser ) { 
		return tuple( pattern(NODE_ID, "Node body part").source()
								.followedBy(among(":").followedBy(WHITESPACES.optional())),
						      bodyParser.followedBy( WHITESPACES.optional()) )
				      .map( new Map<Pair<String, T>, NodeBodyPart<T>>(){
			@Override
			public NodeBodyPart<T> map(Pair<String, T> from) {
				return new NodeBodyPart(from.a, from.b);
			}
		}).between(nodeEnd(), WHITESPACES.optional().followedBy(nodeStart()));
	}
	
	Parser<TypedNodeHeadRef> nodeHead() {
        return nodeHeadWithType( nodeType() );
	}
    
	Parser<TypedNodeHeadRef> nodeHeadWithType(Parser<NodeType> nodeTypeParser) {
		return tuple(nodeId().followedBy(WHITESPACES.optional()), nodeTypeParser).map( new Map<Pair<String, NodeType>, TypedNodeHeadRef>(){
                    @Override
                    public TypedNodeHeadRef map(Pair<String, NodeType> from) {
                            return new TypedNodeHeadRef(from.a, from.b);
                    }
		} )
		.or( nodeType().map( new Map<NodeType, TypedNodeHeadRef>() {
                    @Override
                    public TypedNodeHeadRef map(NodeType from) {
                            return new TypedNodeHeadRef(null, from);
                    }
		}));
	}
	
    Parser<TypedNodeHeadRef> nodeHeadWithType( String type ) {
        return nodeHeadWithType( Scanners.string(type).source().map( new Map<String, NodeType>(){
            @Override
            public NodeType map(String from) {
                return NodeType.valueOf(toEnumCase(from));
            }
        }));
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
	
	Parser<NodeType> nodeType() {
		return IDENTIFIER.map( new Map<String, NodeType>(){
            @Override
            public NodeType map(String from) {
                return NodeType.valueOf(toEnumCase(from));
            }
		} );
	}
    
    Parser<Pair<String, String>> setAssignmentPair() {
        return Parsers.tuple( slotReference().followedBy( WHITESPACES.many().optional())
                                .followedBy( Scanners.isChar('=') ).followedBy( WHITESPACES.many().optional()),
                Scanners.IDENTIFIER.many1().source() );
    }

    Parser<String> slotReference() {
        return Scanners.IDENTIFIER.sepBy1(Scanners.isChar('/')).source();
    }
    
    Parser<SetNodeRef> setNode()  {
        return completeNode( tuple( nodeHeadWithType("set").followedBy( nodeHeadEnd() ), 
                setAssignmentPair().sepBy(Scanners.isChar(',').followedBy( WHITESPACES.many().optional()))))
                .map( new Map<Pair<TypedNodeHeadRef, List<Pair<String,String>>>,SetNodeRef>(){

            @Override
            public SetNodeRef map(Pair<TypedNodeHeadRef, List<Pair<String,String>>> from) {
                SetNodeRef ref = new SetNodeRef( from.a );
                for ( Pair<String,String> ap : from.b ) {
                    ref.addAssignment(ap.a, ap.b);
                }
                return ref;
            }
        });
    }
    
    Parser<TermNodeRef> termNode() {
        return completeNode( tuple( notChar(':').many().source().followedBy(among(":")),
                              ANY_CHAR.many().source() )
                       .map( new Map<Pair<String,String>,TermNodeRef>(){
                            @Override
                            public TermNodeRef map(Pair<String, String> from) {
                                return new TermNodeRef(from.a.trim(), from.b.trim());
        }}));
    }
    
    Parser<List<TermNodeRef>> termsNode() {
        return completeNode(
                Parsers.tuple(nodeHeadWithType("terms").followedBy( nodeHeadEnd() ),
                        termNode().sepBy(WHITESPACES.many().optional()))
                .map( new Map<Pair<TypedNodeHeadRef,List<TermNodeRef>>, List<TermNodeRef>>(){
            @Override
            public List<TermNodeRef> map(Pair<TypedNodeHeadRef, List<TermNodeRef>> from) {
                return from.b;
            }
        }));
    }
    
    Parser<InstructionNodeRef> instructionNode() {
        return completeNode(
            nodeHead()
        ).map( new Map<TypedNodeHeadRef, InstructionNodeRef>(){
            @Override
            public InstructionNodeRef map(TypedNodeHeadRef from) {
                return new InstructionNodeRef(from);
            }
        });
    }
    
    Parser<StringBodyNodeRef> simpleNodeRef() {
        return completeNode(
            Parsers.tuple( nodeHead().followedBy( nodeHeadEnd() ),
                 Scanners.ANY_CHAR.many1().source())
            ).map( new Map<Pair<TypedNodeHeadRef, String>, StringBodyNodeRef>(){
            @Override
            public StringBodyNodeRef map(Pair<TypedNodeHeadRef, String> from) {
                return new StringBodyNodeRef(from.a, from.b);
            }
        } );
    }
    
    Parser<StringBodyNodeRef> simpleNodeRef( String type ) {
        return completeNode(
            Parsers.tuple( nodeHeadWithType(type).followedBy( nodeHeadEnd() ),
                 Scanners.ANY_CHAR.many1().source())
            ).map( new Map<Pair<TypedNodeHeadRef, String>, StringBodyNodeRef>(){
            @Override
            public StringBodyNodeRef map(Pair<TypedNodeHeadRef, String> from) {
                return new StringBodyNodeRef(from.a, from.b);
            }
        } );
    }
    
    Parser<AskNodeRef> askNode() {
        Parser<Pair<StringBodyNodeRef, List<TermNodeRef>>> onlyText = 
                simpleNodeRef("text").map( new Map<StringBodyNodeRef, Pair<StringBodyNodeRef, List<TermNodeRef>>>(){
            @Override
            public Pair<StringBodyNodeRef, List<TermNodeRef>> map(StringBodyNodeRef from) {
                return new Pair<>(from, Collections.<TermNodeRef>emptyList());
            }
        });
        Parser<Pair<StringBodyNodeRef, List<TermNodeRef>>> textThenTerms = 
                Parsers.tuple(simpleNodeRef("text"), termsNode());
        Parser<Pair<StringBodyNodeRef, List<TermNodeRef>>> termsThenText = 
                Parsers.tuple(termsNode(), simpleNodeRef("text"))
                        .map( new Map<Pair<List<TermNodeRef>,StringBodyNodeRef>, Pair<StringBodyNodeRef, List<TermNodeRef>>>(){
            @Override
            public Pair<StringBodyNodeRef, List<TermNodeRef>> map(Pair<List<TermNodeRef>,StringBodyNodeRef> from) {
                return new Pair<>(from.b, from.a);
            }
        });
        
        Parser<Pair<StringBodyNodeRef, List<TermNodeRef>>> questionPart = onlyText.or( textThenTerms ).or( termsThenText );
        
        // CONTPOINT make the answer part (yes+no?| no+yes? | answers)
        return null;
    }
    
    <T> Parser<T> completeNode( Parser<T> nodeBodyParser ) {
        return nodeBodyParser.reluctantBetween(nodeEnd(), nodeStart() );
    }
	
	Parser<Void> nodeEnd() { 
            return Scanners.isChar('(', "NODE_START").followedBy(Scanners.WHITESPACES.optional()); 
	}
	
	Parser<Void> nodeStart() { 
            return Scanners.isChar(')', "NODE_END");
	}
    
    Parser<Void> nodeHeadEnd() {
        return Scanners.isChar(':', "NODE_HEAD_END").followedBy( Scanners.WHITESPACES.optional() );
    }
    
    String toEnumCase( String val ) {
        char[] raw = val.trim().toLowerCase().toCharArray();
        raw[0] = Character.toUpperCase(raw[0]);
        return new String( raw );
    }
    
}
