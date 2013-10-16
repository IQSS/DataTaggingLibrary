package edu.harvard.iq.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author michael
 */
public class CollectionHelper {
	
	static final public CollectionHelper C = new CollectionHelper();
	
	public <T> Set<T> set( T... items ) {
		return new HashSet<>(Arrays.asList(items));
	}
	
	public <T> List<T> list( T... items ) {
		return Arrays.asList( items );
	}
}
