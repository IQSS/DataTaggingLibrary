package edu.harvard.iq.datatags.model;

/**
 * A set of (tag type, value) pairs, describing how data sets should
 * be handled. DataTag objects are essentially maps from tag types to tag values.
 * It's slightly more complicated, of course, but that's the gist of it. 
 * 
 * @author michael
 */
public class DataTags {
	
	
	public DataTags composeWith( DataTags other ) {
		return null; //TODO
	}
	
	public boolean isComplete() {
		return false; // TODO
	}
}
