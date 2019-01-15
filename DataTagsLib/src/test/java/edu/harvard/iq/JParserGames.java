package edu.harvard.iq;

import java.util.Arrays;
import java.util.List;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class JParserGames {

@Test
public void testMany() {
    Parser<String> p1 = Scanners.notChar('a').source();
    Parser<String> p2 = Scanners.among("a").source();
    
    Parser<List<String>> combined = Parsers.sequence(p1.many().source(), p2,
                                                     (s1, s2)->Arrays.asList(s1, s2));
    System.out.println(combined.parse("bba"));
}
    
}
