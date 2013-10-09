package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.model.types.TagType;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A compiled and loaded program for the runtime engine. This object contains
 * all the data needed to tag a data set, including the structure of the 
 * data tags, and the flow charts needed to fill them.
 * 
 * @author michael
 */
public class FlowChartSet extends RuntimeEntity {
	
	private URL source;
	private final Map<String, FlowChart> charts = new TreeMap<>();
	private final Set<TagType> types  = new HashSet<>();
	
	public FlowChartSet(String anId) {
		super(anId);
	}

	public URL getSource() {
		return source;
	}

	public void setSource(URL source) {
		this.source = source;
	}
	
	public void addChart( FlowChart c ) {
		charts.put( c.getId(),  c );
	}
	
	public FlowChart getFlowChart( String id ) {
		return charts.get( id );
	}
	
	public void addType( TagType tt ) {
		types.add( tt );
	}
	
	public Set<TagType> getTypes() {
		return Collections.unmodifiableSet(types);
	}
}
