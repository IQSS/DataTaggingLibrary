package edu.harvard.iq.datatags.model.charts;

import edu.harvard.iq.datatags.model.types.TagType;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A compiled and loaded program for the runtime engine. This object contains
 * all the data needed to tag a data set, including the structure of the 
 * data tags, and the flow charts needed to fill them.
 * 
 * @author michael
 */
public class FlowChartSet extends ChartEntity {
	private static final AtomicInteger INDEX = new AtomicInteger(0);
	
	private URL source;
	private final Map<String, FlowChart> charts = new TreeMap<>();
	private final Set<TagType> types  = new HashSet<>();
	
	/**
	 * The id of the chart from which, in the absence of any other requirement,
	 * the execution should start. Normally this means the first chart in the 
	 * file.
	 */
	private String defaultChartId;
	
	public FlowChartSet() {
		this( "FlowChartSet-"+INDEX.incrementAndGet());
	}
	
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

	public Iterable<FlowChart> charts() {
		return charts.values();
	}

	/**
	 * @return the id of the default chart, if any
	 */
	public String getDefaultChartId() {
		return defaultChartId;
	}

	public void setDefaultChartId(String defaultChartId) {
		this.defaultChartId = defaultChartId;
	}
	
}
