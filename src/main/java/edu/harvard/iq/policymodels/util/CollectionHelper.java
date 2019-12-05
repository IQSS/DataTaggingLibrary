package edu.harvard.iq.policymodels.util;

import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.CompoundValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

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
	
    public static class DoubleSetDiff<E> {
        public final Set<E> inAOnly;
        public final Set<E> inBOnly;

        public DoubleSetDiff(Set<E> inAOnly, Set<E> inBOnly) {
            this.inAOnly = inAOnly;
            this.inBOnly = inBOnly;
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
        if ( list instanceof LinkedList ) {
            return ((LinkedList<T>)list).getLast();
        } else {
            return list.get( list.size()-1 );
        }
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
    
	public <T> Set<T> set( T... items ) {
		return new HashSet<>( Arrays.asList(items) );
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

	public <T> Set<T> subtractSet( Collection<T> c1, Collection<T> c2 ) {
		Set<T> out = new HashSet<>( c1 );
		out.removeAll( c2 );
		return out;
	}
    
    public <E> DoubleSetDiff<E> diff( Set<E> a, Set<E> b ) {
        return new DoubleSetDiff( subtractSet(a,b), subtractSet(b,a) );
    }
    
	public <T> T first(Iterable<T> itr ) {
		return ( itr == null ) ? null
							   : itr.iterator().next();
	}
    
    public Collector<CompoundValue, Set<CompoundValue>, CompoundValue> compose( final CompoundSlot base ) {
        return new Collector<CompoundValue, Set<CompoundValue>, CompoundValue>(){
            @Override
            public Supplier<Set<CompoundValue>> supplier() {
                return ()->new HashSet<>();
            }

            @Override
            public BiConsumer<Set<CompoundValue>, CompoundValue> accumulator() {
                return (set, val)->set.add(val);
            }

            @Override
            public BinaryOperator<Set<CompoundValue>> combiner() {
                return (s1, s2) -> {
                    Set<CompoundValue> s3 = new HashSet<>();
                    s3.addAll(s1);
                    s3.addAll(s2);
                    return s3;
                };
            }

            @Override
            public Function<Set<CompoundValue>, CompoundValue> finisher() {
                return (set) -> {
                    CompoundValue out = base.createInstance();
                    for ( CompoundValue v:set ) {
                        out = out.composeWith(v);
                    }
                    return out;
                };
            }

            @Override
            public Set<Collector.Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }
}
