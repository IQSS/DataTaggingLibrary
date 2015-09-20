package edu.harvard.iq.datatags.parser.definitions;

import edu.harvard.iq.datatags.parser.AbstractASTParser;
import edu.harvard.iq.datatags.parser.definitions.references.AggregateTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.CompoundTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.NamedReference;
import edu.harvard.iq.datatags.parser.definitions.references.SimpleTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.ToDoTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.TypeReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;

/**
 * Parses definitions language files into a list of type definitions.
 * @author michael
 */
public class DataDefinitionASTParser extends AbstractASTParser {
	
	Parser<Collection<TypeReference>> typeDefinitionList() {
		// we take all the type defined, and combine to a single list.
		// Basically "flatmap"
		return typeDefinition().sepBy( Scanners.WHITESPACES.optional() )
					.followedBy( Scanners.WHITESPACES.optional() )
				.map((List<List<? extends TypeReference>> from) -> {
                    List<TypeReference> types = new LinkedList<>();
                    from.stream().forEach( l -> types.addAll(l) );
                    return types;
        });
	}
	
	Parser<List<? extends TypeReference>> typeDefinition() {
		return Parsers.or( todoTypesDefinition(), simpleTypeDefinition(), aggregateTypeDefinition(), compoundTypeDefinition() );
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
	
	Parser<List<ToDoTypeReference>> todoTypesDefinition() {
		return Parsers.tuple( Scanners.string("TODO")
					.followedBy( Scanners.WHITESPACES.optional() )
					.followedBy( definitionSeparator() ),
				namedReferenceList()
					.followedBy( dotDefinitionTerminator() )
				).map( new Map<Pair<Void, List<NamedReference>>, List<ToDoTypeReference>>(){
					@Override public List<ToDoTypeReference> map(Pair<Void, List<NamedReference>> from) {
						List<ToDoTypeReference> types = new ArrayList<>( from.b.size() );
						
						for ( NamedReference nref : from.b ) {
							types.add(new ToDoTypeReference(nref.getName(), nref.getComment()) );
						}
						
						return types;
				}});
	}
	
	Parser<List<CompoundTypeReference>> compoundTypeDefinition() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next(namedReferenceList())
									  .followedBy( dotDefinitionTerminator() )
				).map( new Map< Pair<String, List<NamedReference>>, List<CompoundTypeReference>>(){
					@Override public List<CompoundTypeReference> map(Pair<String, List<NamedReference>> from) {
						return Collections.singletonList(new CompoundTypeReference(from.a, from.b) );
				}});
	}
	
	Parser<List<SimpleTypeReference>> simpleTypeDefinition() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next( whitespaced("one", "of") )
									  .next( namedReferenceList() )
									  .followedBy( dotDefinitionTerminator() )
				).map( new Map< Pair<String, List<NamedReference>>, List<SimpleTypeReference>>(){
					@Override public List<SimpleTypeReference> map(Pair<String, List<NamedReference>> from) {
						return Collections.singletonList(new SimpleTypeReference(from.a, from.b));
				}});
	}
	
	Parser<List<AggregateTypeReference>> aggregateTypeDefinition() {
		return Parsers.tuple( Scanners.IDENTIFIER.followedBy( Scanners.WHITESPACES.optional()),
							  definitionSeparator()
									  .next( whitespaced("some", "of")  )
									  .next( namedReferenceList() )
									  .followedBy( dotDefinitionTerminator() )
				).map( new Map< Pair<String, List<NamedReference>>, List<AggregateTypeReference>>(){
					@Override public List<AggregateTypeReference> map(Pair<String, List<NamedReference>> from) {
						return Collections.singletonList(new AggregateTypeReference(from.a, from.b));
				}});
	}
	
	
	Parser<List<NamedReference>> namedReferenceList() {
		return namedReference().followedBy( Scanners.WHITESPACES.optional())
								   .sepBy( listSeparator() );
	}
	
	Parser<Void> definitionSeparator() {
		return separatorWithWhitespace(":");
	}
	
	
	Parser<Void> dotDefinitionTerminator() {
		return Scanners.WHITESPACES.skipMany()
				.next(Scanners.string("."));
	}
	
}
