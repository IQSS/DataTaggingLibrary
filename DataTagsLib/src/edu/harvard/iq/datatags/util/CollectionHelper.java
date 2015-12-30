package edu.harvard.iq.datatags.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The missing parts of the the Java collection framework.
 * @author michael
 */
public class CollectionHelper {
	
	public static final CollectionHelper C = new CollectionHelper();
	
	public static class OneOfFunction<T> {
		private final T t;
		
		OneOfFunction( T aT ) {
			t = aT;
		}
		
		public boolean isOneOf( T... otherTs ) {
			return C.set(otherTs).contains(t);
		}
	}
	
	private CollectionHelper(){};
	
	public <T> List<T> dropLast( List<T> in ) {
		return in.subList(0, in.size()-1);
	}
	
	public <T> Iterable<T> reverse( Deque<T> in ) {
		ArrayList<T> arr = new ArrayList<>(in);
        in.stream().forEach((t) -> {
            arr.add(t);
        });
        return reverse(arr);
    }
    
	public <T> List<T> reverse( List<T> in ) {
		ArrayList<T> arr = new ArrayList<>(in);
		Collections.reverse(arr);
		return arr;
	}
	
	public <T> T last ( T[] arr ) {
		return arr[arr.length-1];
	}
	
	public <T> T last( List<T> list ) {
		return list.get( list.size()-1 );
	}
	
	public <T> Set<T> set( T... items ) {
		return new HashSet<>( Arrays.asList(items) );
	}
	
	public <T> T head( List<T> list ) {
		return list.get(0);
	}
	
	public <T> List<T> tail( List<T> list ) {
		return ( list.size() < 2 )
				? Collections.<T>emptyList()
				: list.subList(1, list.size());
	}
	
	public <T> OneOfFunction item( T t ) {
		return new OneOfFunction(t);
	}

	public <T> List<T> list( T... items ) {
		return Arrays.asList( items );
	}
	
	public <T> List<T> list( Collection<T> ct ) {
		return new LinkedList<>(ct);
	}
	
    public <T> List<T> immutableList( Collection<T> ct ) {
        return Collections.unmodifiableList( list(ct) );
    }

	public <T> Set<T> unionSet( Collection<T> c1, Collection<T> c2 ) {
		Set<T> out = new HashSet<>( c1 );
		out.addAll( c2 );
		return out;
	}

	public <T> Set<T> intersectSet( Collection<T> c1, Collection<T> c2 ) {
		Set<T> out = new HashSet<>( c1 );
		out.retainAll( c2 );
		return out;
	}
	
	public <T> T first(Iterable<T> itr ) {
		return ( itr == null ) ? null
							   : itr.iterator().next();
	}
}
