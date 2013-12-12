package edu.harvard.iq.datatags.model.values;

/**
 * A single answer, given to a {@link DecisionTreeNode} by the user.
 * @author michael
 */
public enum Answer {
	YES, NO;
	
	public static Answer valueOfOpt( String val ) {
		try {
			return valueOf( val );
		} catch ( IllegalArgumentException e ) {
			return null;
		}
	}
}
