package edu.harvard.iq.datatags.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;

/**
 * Base class for AST parsers. Holds some reusable methods.
 * @author michael
 */
public abstract class AbstractASTParser {
	
	public Parser<Void> separatorWithWhitespace( String sep ) {
			return Scanners.WHITESPACES.skipMany()
				.next(Scanners.string( sep ))
				.next(Scanners.WHITESPACES.skipMany());
	}
	
	public Parser<Void> listSeparator() {
		return separatorWithWhitespace(",");
	}

	public Parser<Void> whitespaced(String... words) {
		Parser<Void> out = Scanners.string(words[0]);
		for (int i = 1; i < words.length; i++) {
			out = out.next(Scanners.WHITESPACES.skipMany1()).next(Scanners.string(words[i]));
		}
		out = out.next(Scanners.WHITESPACES.skipMany1());
		return out;
	}
	
}
