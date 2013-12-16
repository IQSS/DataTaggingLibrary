package edu.harvard.iq.datatags.parser.definitions;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.SimpleType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.definitions.references.AggregateTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.definitions.references.CompoundTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.NamedReference;
import edu.harvard.iq.datatags.parser.definitions.references.SimpleTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.ToDoTypeReference;
import edu.harvard.iq.datatags.parser.definitions.references.TypeReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.codehaus.jparsec.error.Location;

/**
 * Parses a string of DTL into {@lint TagType}s.
 * TODO allow identifiers to start with numbers as well (no arithmetics)
 * @author michael
 */
public class DataDefinitionParser {
	
	public TagType parseTagDefinitions( String dtl, String unitName ) throws DataTagsParseException {
		try {
			DataDefinitionASTParser astParser = new DataDefinitionASTParser();
			Collection<TypeReference> typeRefs = astParser.typeDefinitionList().parse(dtl);
			
			// map each type from its name. Cry about duplicates.
			Map<String, TypeReference> typesByName = mapTypes(typeRefs);
			
			// BFS from DataTags to the rest
			Set<String> usedTypes = new TreeSet<>();
			TagType top = buildType("DataTags", typesByName, usedTypes);
			
			// Warn on any unused type
			
			return top;
			
		} catch ( SemanticsErrorException dtpe ) {
			dtpe.getWhere().setLocation(unitName);
			throw dtpe;
			
		} catch ( org.codehaus.jparsec.error.ParserException pe ) {
			throw new SyntaxErrorException(locationRef(unitName, pe.getLocation()),
											pe.getMessage(),
											pe);
		}
	}
	
	interface TagTypeFunc {
		public TagType apply(  Map<String, TypeReference> refs, Set<String> usedTypes ) throws SemanticsErrorException;
	}
	
	private final TypeReference.Visitor<TagTypeFunc> typeBuilder = new TypeReference.Visitor<TagTypeFunc>() {

		@Override
		public TagTypeFunc visitSimpleTypeReference(SimpleTypeReference ref) {
			final SimpleType res = new SimpleType(ref.getTypeName(), null );
			for ( NamedReference nr : ref.getSubValueNames() ) {
				res.make(nr.getName(), nr.getComment() );
			}
			return new TagTypeFunc() {
				@Override
				public TagType apply(Map<String, TypeReference> refs, Set<String> usedTypes) {
					return res;
			}};
		}
		
		@Override
		public TagTypeFunc visitToDoTypeReference(ToDoTypeReference ref) {
			final ToDoType res = new ToDoType(ref.getTypeName(), ref.getComment() );
			
			return new TagTypeFunc() {
				@Override
				public TagType apply(Map<String, TypeReference> refs, Set<String> usedTypes) {
					return res;
			}};
		}

		@Override
		public TagTypeFunc visitAggregateTypeReference(AggregateTypeReference ref) {
			SimpleType itemType = new SimpleType( ref.getTypeName() + "#item",
					"Synthetic item type for " + ref.getTypeName());
			for ( NamedReference nr : ref.getSubValueNames() ) {
				itemType.make(nr.getName(), nr.getComment() );
			}
			final AggregateType res = new AggregateType(ref.getTypeName(), null, itemType );
			return new TagTypeFunc() {
				@Override
				public TagType apply(Map<String, TypeReference> refs, Set<String> usedTypes) {
					return res;
			}};
		}

		@Override
		public TagTypeFunc visitCompoundTypeReference(final CompoundTypeReference ref) {
			return new TagTypeFunc() {
				@Override
				public TagType apply(Map<String, TypeReference> refs, Set<String> usedTypes) throws SemanticsErrorException {
					CompoundType ret = new CompoundType(ref.getTypeName(), null);
					for ( NamedReference nr : ref.getSubValueNames() ) {
						TagType fieldType = buildType(nr.getName(), refs, usedTypes);
						fieldType.setInfo( nr.getComment() );
						ret.addFieldType( fieldType );
					}
					return ret;
				}
			};
		}
		
		
	};
	
	private TagType buildType( String typeName, Map<String, TypeReference> refs, Set<String> usedTypes ) throws SemanticsErrorException {
		TypeReference ref = refs.get(typeName);
		if ( ref == null ) {
			throw new SemanticsErrorException(emptyLoc(), "Type '" + typeName +"' not defined.");
		}
		usedTypes.add( typeName );
		return ref.accept(typeBuilder).apply(refs, usedTypes);
	}
	
	private CompilationUnitLocationReference locationRef( String unitName, Location loc ) {
		return new CompilationUnitLocationReference(loc.line, loc.column, unitName);
	}

	private Map<String, TypeReference> mapTypes(Collection<TypeReference> typeRefs) throws SemanticsErrorException {
		Map<String, TypeReference> out = new TreeMap<>();
		for ( TypeReference tr : typeRefs ) {
			if (out.containsKey(tr.getTypeName()) ) {
				throw new SemanticsErrorException( emptyLoc(), "Type '" + tr.getTypeName() + "' has multiple definitions");
			}
			out.put(tr.getTypeName(), tr);
		}
		
		return out;
	}

	private CompilationUnitLocationReference emptyLoc() {
		return new CompilationUnitLocationReference(-1, -1);
	}
}
