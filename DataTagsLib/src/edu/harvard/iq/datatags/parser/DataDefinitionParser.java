package edu.harvard.iq.datatags.parser;

import edu.harvard.iq.datatags.parser.references.AggregateTypeReference;
import edu.harvard.iq.datatags.parser.references.CompoundTypeReference;
import edu.harvard.iq.datatags.parser.references.NamedReference;
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
	
	Parser<List<TypeReference>> typeDefinitionList() {
		return typeDefinition().sepBy( Scanners.WHITESPACES );
	}
	
	Parser<TypeReference> typeDefinition() {
		return Parsers.or( simpleTypeDefinition(), aggregateTypeDefinition(), compoundTypeDefinition() );
	}
	
	Parser<SimpleTypeReference> longSimpleType() {
		return null;
	}
	
	
	Parser<NamedReference> namedReference() {
		return Parsers.or( commentedNamedReference(),
				Scanners.IDENTIFIER.map( new Map<String, NamedReference>(){
					@Override
					public NamedReference map(String from) {
						return new NamedReference(from);
					}}
				));
	}
	
	/**
	 * @return A parser that parses detailed value definitions, of the form 
	 *			"valueName: value description."                 
	 */
	Parser<NamedReference> commentedNamedReference() {
		return Parsers.tuple(Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
				Scanners.quoted('(',')'))
				.map( new Map<Pair<String, String>, NamedReference>(){
					@Override
					public NamedReference map(Pair<String, String> from) {
						return new NamedReference(from.a, from.b.substring(1, from.b.length()-1).trim());
			}});
	}
	
	Parser<CompoundTypeReference> compoundTypeDefinition() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next(namedReferenceList())
									  .followedBy( dotDefinitionTerminator() )
				).map( new Map< Pair<String, List<NamedReference>>, CompoundTypeReference>(){
					@Override public CompoundTypeReference map(Pair<String, List<NamedReference>> from) {
						return new CompoundTypeReference(from.a, from.b);
				}});
	}
	
	Parser<SimpleTypeReference> simpleTypeDefinition() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next( whitespaced("one", "of") )
									  .next( namedReferenceList() )
									  .followedBy( dotDefinitionTerminator() )
				).map( new Map< Pair<String, List<NamedReference>>, SimpleTypeReference>(){
					@Override public SimpleTypeReference map(Pair<String, List<NamedReference>> from) {
						return new SimpleTypeReference(from.a, from.b);
				}});
	}
	
	Parser<AggregateTypeReference> aggregateTypeDefinition() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next( whitespaced("some", "of")  )
									  .next( namedReferenceList() )
									  .followedBy( dotDefinitionTerminator() )
				).map( new Map< Pair<String, List<NamedReference>>, AggregateTypeReference>(){
					@Override public AggregateTypeReference map(Pair<String, List<NamedReference>> from) {
						return new AggregateTypeReference(from.a, from.b);
				}});
	}
	
	
	Parser<List<NamedReference>> namedReferenceList() {
		return namedReference().followedBy( Scanners.WHITESPACES.optional())
								   .sepBy( listSeparator() );
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
