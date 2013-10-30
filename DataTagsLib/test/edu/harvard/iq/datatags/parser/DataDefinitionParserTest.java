/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.parser;

import edu.harvard.iq.datatags.parser.references.AggregateTypeReference;
import edu.harvard.iq.datatags.parser.references.CompoundTypeReference;
import edu.harvard.iq.datatags.parser.references.SimpleTypeReference;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class DataDefinitionParserTest {
	
	private DataDefinitionParser sut;
		
	@Before
	public void setUp() {
		sut = new DataDefinitionParser();
	}
	
	@Test
	public void testShortTypeDefinitions() {
		assertEquals( new AggregateTypeReference("agg", C.list("a","b","c")),
						sut.shortReference().parse("agg: some of a, b, c.") );
		assertEquals( new SimpleTypeReference("agg", C.list("a","b","c")),
						sut.shortReference().parse("agg: one of a, b, c.") );
		assertEquals( new CompoundTypeReference("agg", C.list("a","b","c")),
						sut.shortReference().parse("agg: a, b, c.") );
	}
	
	@Test
	public void testShortAggregateTypeDefinition() {
		AggregateTypeReference expected = new AggregateTypeReference("TestType", C.list("Hello","World","Parsers"));
		assertEquals( expected, sut.shortAggregateType().parse("TestType: some of Hello, World, Parsers.") );
	}
	
	@Test
	public void testShortSimpleTypeDefinition() {
		SimpleTypeReference expected = new SimpleTypeReference("TestType", C.list("Hello","World","Parsers"));
		assertEquals( expected, sut.shortSimpleType().parse("TestType: one of Hello, World, Parsers.") );
		assertEquals( expected, sut.shortSimpleType().parse("TestType : one of Hello, World, Parsers.") );
		assertEquals( expected, sut.shortSimpleType().parse("TestType : one   of Hello, World, Parsers.") );
		assertEquals( expected, sut.shortSimpleType().parse("TestType : one   of      Hello, World, Parsers.") );
		assertEquals( expected, sut.shortSimpleType().parse("TestType : one   of      Hello, World, Parsers.") );
	}

	@Test
	public void testShortCompoundTypeDefinition() {
		assertEquals( new CompoundTypeReference("TestType", C.list("Hello","World","Parsers")),
				sut.shortCompoundType().parse("TestType: Hello, World, Parsers.") );
	}
	
	@Test
	public void testIdentifierList() {
		assertEquals( Arrays.asList("a","b","c"), sut.identifierList().parse("a,b,c.") );
		assertEquals( Arrays.asList("a","b","c"), sut.identifierList().parse("a, b, c.") );
		assertEquals( Arrays.asList("a","b","c"), sut.identifierList().parse("a , b , c.") );
		assertEquals( Arrays.asList("a","b","c"), sut.identifierList().parse("a ,b ,c.") );
		assertEquals( Arrays.asList("a","b","c"), sut.identifierList().parse("a ,b ,c .") );
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
