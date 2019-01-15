package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.slots.ToDoSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import java.util.List;
import java.util.Objects;

/**
 * Given a path, find the policy space entity pointed to.
 * 
 * TODO use this class on other lookup cases, such as parsing Set nodes.
 * 
 * @author michael
 */
public class PolicySpacePathQuery {
   
    public static abstract class Result {
        
        public static interface Visitor<R> {
            R visit(TagValueResult tvr);
            R visit(SlotTypeResult str);
            R visit(NotFoundResult nfr);
        }
        
        public final List<String> path;

        Result(List<String> path) {
            this.path = path;
        }
        
        public abstract <R>  R accept( Visitor<R> v );
    }
    
    public static class TagValueResult extends Result {
        public final AbstractValue value;

        public TagValueResult(AbstractValue result, List<String> path) {
            super(path);
            this.value = result;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TagValueResult other = (TagValueResult) obj;
            return other.path.equals(this.path) && other.value.equals(this.value);
        }

        @Override
        public String toString() {
            return "[TagValueResult " + path +  "=>" + value + ']';
        }

        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
        
    }
    
    public static class SlotTypeResult extends Result {
        public final AbstractSlot value;

        public SlotTypeResult(AbstractSlot value, List<String> path) {
            super(path);
            this.value = value;
        }
        
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SlotTypeResult other = (SlotTypeResult) obj;
            return other.path.equals(this.path) && other.value.equals(this.value);
        }
        @Override
        public String toString() {
            return "[SlotTypeResult " + path +  "=>" + value + ']';
        }
        
    }
    
    public static class NotFoundResult extends Result {

        public NotFoundResult(List<String> path) {
            super(path);
        }
        
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.path);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Result other = (Result) obj;
            return other.path.equals(this.path);
        }
        @Override
        public String toString() {
            return "[NotFoundResult " + path +']';
        }
        
    }
    
    private final CompoundSlot base;

    public PolicySpacePathQuery(CompoundSlot aBase) {
        base = aBase;
    }
    
    public Result get( final List<String> aPath ) {
        if ( aPath.isEmpty() ) return new NotFoundResult(aPath);
        if ( ! aPath.get(0).equals(base.getName()) ) return new NotFoundResult(aPath);
        
        return base.accept(new AbstractSlot.Visitor<Result>() {
            int idx = 1;
            
            @Override
            public Result visitSimpleSlot(AtomicSlot t) {
                if ( idx==aPath.size() ) return new SlotTypeResult(t, aPath);
                if ( idx==aPath.size()-1 ) {
                    try {
                        return new TagValueResult(t.valueOf(aPath.get(idx)), aPath);
                    } catch( IllegalArgumentException _iae) {
                        return new NotFoundResult(aPath);
                    }
                }
                return new NotFoundResult(aPath);
            }

            @Override
            public Result visitAggregateSlot(AggregateSlot t) {
                if ( idx==aPath.size() ) return new SlotTypeResult(t, aPath);
                if ( idx==aPath.size()-1 ) {
                    try {
                        return new TagValueResult(t.getItemType().valueOf(aPath.get(idx)), aPath);
                    } catch( IllegalArgumentException _iae) {
                        return new NotFoundResult(aPath);
                    }
                }
                return new NotFoundResult(aPath);
            }

            @Override
            public Result visitCompoundSlot(CompoundSlot t) {
                if ( idx==aPath.size() ) return new SlotTypeResult(t, aPath);
                AbstractSlot subSlot = t.getSubSlot(aPath.get(idx));
                idx++;
                return (subSlot!=null) ? subSlot.accept(this) : new NotFoundResult(aPath);
            }

            @Override
            public Result visitTodoSlot(ToDoSlot t) {
                return idx==aPath.size() ? new SlotTypeResult(t, aPath) : new NotFoundResult(aPath);
            }
        });
    }
    
}
