package edu.harvard.iq.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author michael
 */
public class CollectionHelper {
	
	static final public CollectionHelper C = new CollectionHelper();
	
	public <T> Set<T> setOf( T... items ) {
		return new HashSet<>(Arrays.asList(items));
	}
}
