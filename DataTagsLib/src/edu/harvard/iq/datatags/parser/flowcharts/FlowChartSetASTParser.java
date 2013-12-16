package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.parser.AbstractASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeType;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
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
	
	Pattern NODE_ID = Patterns.among("!@$%^&*_+-").or(Patterns.range('a', 'z')).or(Patterns.range('A', 'Z')).many1();
	
	Parser<String> nodeId() {
		return Scanners.pattern(NODE_ID, "Node id").source();
	}
	
	Parser<NodeType> nodeType() {
		return Scanners.IDENTIFIER.map( new Map<String, NodeType>(){
			@Override
			public NodeType map(String from) {
				char[] raw = from.trim().toLowerCase().toCharArray();
				raw[0] = Character.toUpperCase(raw[0]);
				return NodeType.valueOf(new String(raw));
			}
		} );
	}
}
