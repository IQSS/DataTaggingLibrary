package edu.harvard.iq.datatags.parser;

import edu.harvard.iq.datatags.parser.references.AggregateTypeReference;
import edu.harvard.iq.datatags.parser.references.CompoundTypeReference;
import edu.harvard.iq.datatags.parser.references.SimpleTypeReference;
import edu.harvard.iq.datatags.parser.references.TypeReference;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;

/**
 * Parses definitions language files into a type structure.
 * @author michael
 */
public class DataDefinitionParser {
	
	Parser<TypeReference> shortReference() {
		return Parsers.or( shortSimpleType(), shortAggregateType(), shortCompoundType() );
	}
	
	Parser<CompoundTypeReference> shortCompoundType() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator().next(identifierList())
				).map( new Map< Pair<String, List<String>>, CompoundTypeReference>(){
					@Override public CompoundTypeReference map(Pair<String, List<String>> from) {
						return new CompoundTypeReference(from.a, from.b);
				}});
	}
	
	Parser<SimpleTypeReference> shortSimpleType() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next( whitespaced("one", "of") )
									  .next( identifierList() )
				).map( new Map< Pair<String, List<String>>, SimpleTypeReference>(){
					@Override public SimpleTypeReference map(Pair<String, List<String>> from) {
						return new SimpleTypeReference(from.a, from.b);
				}});
	}
	
	Parser<AggregateTypeReference> shortAggregateType() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next( whitespaced("some", "of")  )
									  .next( identifierList() )
				).map( new Map< Pair<String, List<String>>, AggregateTypeReference>(){
					@Override public AggregateTypeReference map(Pair<String, List<String>> from) {
						return new AggregateTypeReference(from.a, from.b);
				}});
	}
	
	
	/**
	 * @return list of identifiers, terminated by a dot (.)
	 */
	Parser<List<String>> identifierList() {
		return Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional())
								   .sepBy( listSeparator() )
								   .endBy( dotDefinitionTerminator())
				.map(new Map<List<List<String>>, List<String>>(){
			@Override
			public List<String> map(List<List<String>> from) {
				return from.get(0);
			}
		});
	}
	
	Parser<Void> definitionSeparator() {
		return separatorWithWhitespace(":");
	}
	
	Parser<Void> listSeparator() {
		return separatorWithWhitespace(",");
	}
	
	Parser<Void> dotDefinitionTerminator() {
		return Scanners.WHITESPACES.skipMany()
				.next(Scanners.string("."));
	}
	
	Parser<Void> separatorWithWhitespace( String sep ) {
			return Scanners.WHITESPACES.skipMany()
				.next(Scanners.string( sep ))
				.next(Scanners.WHITESPACES.skipMany());
	}
	
	Parser<Void> whitespaced( String... words ) {
		Parser<Void> out = Scanners.string(words[0]);
		for ( int i=1; i<words.length; i++ ) {
			out = out.next( Scanners.WHITESPACES.skipMany1())
					.next( Scanners.string(words[i]));
		}
		out = out.next( Scanners.WHITESPACES.skipMany1() );
		return out;
	}
}
