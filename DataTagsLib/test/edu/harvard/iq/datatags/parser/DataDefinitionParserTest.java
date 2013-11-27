/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.parser;

import edu.harvard.iq.datatags.parser.references.AggregateTypeReference;
import edu.harvard.iq.datatags.parser.references.CompoundTypeReference;
import edu.harvard.iq.datatags.parser.references.NamedReference;
import edu.harvard.iq.datatags.parser.references.SimpleTypeReference;
import edu.harvard.iq.datatags.parser.references.ToDoTypeReference;
import edu.harvard.iq.datatags.parser.references.TypeReference;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class DataDefinitionParserTest {
	
	private DataDefinitionASTParser sut;
		
	@Before
	public void setUp() {
		sut = new DataDefinitionASTParser();
	}
	
	@Test
	public void testTypeDefinitionList() {
		List<TypeReference> expected = C.list( new SimpleTypeReference("Smpl", C.list("a","b","c")),
					new CompoundTypeReference("Cmpd", C.list("a","b","c")),
					new AggregateTypeReference("Aggr", C.list("a",new NamedReference("b","test test test"),"c"))
				);

		String source = "Smpl: one of a, b, c.\n"
				      + "Cmpd: a,b,c.\n"
					  + "Aggr: some of a,b (test test test),c.";
		
		assertEquals( expected, sut.typeDefinitionList().parse(source) );
	}
	
	@Test
	public void testNamedReference() {
		assertEquals( new NamedReference("CRIMSON"),
					  sut.namedReference().parse("CRIMSON"));
		assertEquals( new NamedReference("CRIMSON","Color, kinda like dark red"),
					  sut.namedReference().parse("CRIMSON (Color, kinda like dark red)"));
	}
			
	@Test
	public void testNamedReferenceList() {
		assertEquals( C.list(new NamedReference("CRIMSON")),
					  sut.namedReferenceList().parse("CRIMSON"));
		assertEquals( C.list(new NamedReference("CRIMSON","Color. kinda like dark red")),
					  sut.namedReferenceList().parse("CRIMSON (Color. kinda like dark red)"));
		List<NamedReference> expected = C.list( new NamedReference("a","lorem ipsum dolor"),
												new NamedReference("b"),
												new NamedReference("c", "sit amet"),
												new NamedReference("d")
												);
		assertEquals( expected,
				sut.namedReferenceList().parse("a (lorem ipsum dolor), b, c(sit amet), d") 
		);
		expected = C.list( new NamedReference("a"),
							new NamedReference("b","lorem ipsum dolor"),
							new NamedReference("c"),
							new NamedReference("d", "sit amet")
						);
		assertEquals( expected,
				sut.namedReferenceList().parse("a, b(lorem ipsum dolor), c, d(sit amet)"));
		assertEquals( expected,
				sut.namedReferenceList().parse("a,b(lorem ipsum dolor),c,d(sit amet)"));
		assertEquals( expected,
				sut.namedReferenceList().parse("a,b  (lorem ipsum dolor), c , d(sit amet)"));
		assertEquals( expected,
				sut.namedReferenceList().parse("a,b(lorem ipsum dolor) , c , d(sit amet)   "));
	}
	
	@Test
	public void testCommentedNamedReference() {
		assertEquals( new NamedReference("name","some data about the value"),
				sut.commentedNamedReference().parse("name (some data about the value)"));
		assertEquals( new NamedReference("name","some data about the value"),
				sut.commentedNamedReference().parse("name(some data about the value)"));
		assertEquals( new NamedReference("name","some data about the value"),
				sut.commentedNamedReference().parse("name \n\t(\n  some data about the value  \n)"));
	}
	
	@Test
	public void testTypeDefinition() {
		assertEquals( C.list(new AggregateTypeReference("agg", C.list("a","b","c"))), 
						sut.typeDefinition().parse("agg: some of a, b, c.") );
		assertEquals( C.list(new SimpleTypeReference("agg", C.list("a","b","c"))),
						sut.typeDefinition().parse("agg: one of a, b, c.") );
		assertEquals( C.list(new CompoundTypeReference("agg", C.list("a","b","c"))),
						sut.typeDefinition().parse("agg: a, b, c.") );
	}
	
	@Test
	public void testAggregateTypeDefinition() {
		List<AggregateTypeReference> expected = C.list(new AggregateTypeReference("TestType", C.list("Hello","World","Parsers")));
		assertEquals( expected, sut.aggregateTypeDefinition().parse("TestType: some of Hello, World, Parsers.") );
	}
	
	@Test
	public void testTodoTypesDefinition_single() {
		assertEquals( C.list( new ToDoTypeReference("Test", "")),
				sut.todoTypesDefinition().parse("TODO: Test."));
	}
	
	@Test
	public void testTodoTypesDefinition_single_comment() {
		assertEquals( C.list( new ToDoTypeReference("Test", "testing 123")),
				sut.todoTypesDefinition().parse("TODO: Test(testing 123)."));
	}
	
	@Test
	public void testTodoTypesDefinition_multi() {
		assertEquals( C.list( new ToDoTypeReference("T1", "ttt ttt"),new ToDoTypeReference("T2", ""), new ToDoTypeReference("T3", "")),
				sut.todoTypesDefinition().parse("TODO: T1, T2(ttt ttt), T3."));
	}
	
	@Test
	public void testSimpleTypeDefinition() {
		List<SimpleTypeReference> expected = C.list(new SimpleTypeReference("TestType", C.list("Hello","World","Parsers")));
		assertEquals( expected, sut.simpleTypeDefinition().parse("TestType: one of Hello, World, Parsers.") );
		assertEquals( expected, sut.simpleTypeDefinition().parse("TestType : one of Hello, World, Parsers.") );
		assertEquals( expected, sut.simpleTypeDefinition().parse("TestType : one   of Hello, World, Parsers.") );
		assertEquals( expected, sut.simpleTypeDefinition().parse("TestType : one   of      Hello, World, Parsers.") );
		assertEquals( expected, sut.simpleTypeDefinition().parse("TestType : one   of      Hello, World, Parsers.") );
	}

	@Test
	public void testShortSimpleTypeDefinition_Comments() {
		List<SimpleTypeReference> expected = C.list(new SimpleTypeReference("TestType",
				C.list("Hello",new NamedReference("World","comment about it"),"Parsers")));
		assertEquals( expected, sut.simpleTypeDefinition().parse("TestType: one of Hello, World(comment about it), Parsers.") );
	}
	
	@Test
	public void testCompoundTypeDefinition() {
		assertEquals( C.list(new CompoundTypeReference("TestType", C.list("Hello","World","Parsers"))),
				sut.compoundTypeDefinition().parse("TestType: Hello, World, Parsers.") );
		
		List<CompoundTypeReference> expected = C.list( new CompoundTypeReference("CompoundType",
				C.list( new NamedReference("val1"),
						new NamedReference("val2", "comment comment comment"),
						new NamedReference("val3"),
						new NamedReference("val4"))));
		String source = "CompoundType: val1,\n"
				+ "  val2 (comment comment comment),\n"
				+ "val3,\n"
				+ "val4\n"
				+ ".";
		assertEquals( expected, sut.compoundTypeDefinition().parse(source) );
	}
	
	
	@Test 
	public void testWhitespaced() {
		sut.whitespaced("hello","world").parse("hello world ");
		sut.whitespaced("hello","world").parse("hello    world     ");
	}
	
	@Test( expected = org.codehaus.jparsec.error.ParserException.class )
	public void testWhitespacedFail_glued() {
		sut.whitespaced("hello","world").parse("helloworld ");
	}
	@Test( expected = org.codehaus.jparsec.error.ParserException.class )
	public void testWhitespacedFail_noTrailingWhitespace() {
		sut.whitespaced("hello","world").parse("hello world");
	}
}
