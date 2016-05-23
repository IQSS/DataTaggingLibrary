package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.HashSet;
import java.util.Set;

/**
 * Used for reporting which slot a value should go to, and
 * whether the value exists and is compatible with the slot.
 */
public abstract class TagValueLookupResult {
    
    public interface Visitor<R> {
        R visit(SlotNotFound snf);
        R visit(ValueNotFound vnf);
        R visit(Ambiguity amb);
        R visit(Success scss);
    }
    
    /**
     * Convenience class for visitors that don't return anything.
     */
    public static abstract class VoidVisitor implements Visitor<Void> {

        @Override
        public Void visit(SlotNotFound snf) {
            visitImpl(snf);
            return null;
        }

        @Override
        public Void visit(ValueNotFound vnf) {
            visitImpl(vnf);
            return null;
        }

        @Override
        public Void visit(Ambiguity amb) {
            visitImpl(amb);
            return null;
        }

        @Override
        public Void visit(Success scss) {
            visitImpl( scss );
            return null;
        }
        
        protected abstract void visitImpl(SlotNotFound snf);
        protected abstract void visitImpl(ValueNotFound vnf);
        protected abstract void visitImpl(Ambiguity amb);
        protected abstract void visitImpl(Success scss);
    }
    
    public interface SuccessFailVisitor<R,E extends Exception> {
        R visitSuccess( Success s ) throws E;
        R visitFailure( TagValueLookupResult s ) throws E;
    }
    
    public static class SlotNotFound extends TagValueLookupResult {
        private final String slotName;

        public SlotNotFound(String aSlotName) {
            this.slotName = aSlotName;
        }

        public String getSlotName() {
            return slotName;
        }
        
        @Override
        public String toString() {
            return "[SlotNotFound slotName=" + getSlotName() + "]";
        }

        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
    
    public static class ValueNotFound extends TagValueLookupResult {
        private final SlotType tagType;
        private final String valueName;

        public ValueNotFound(SlotType aTagType, String aValueName) {
            tagType = aTagType;
            valueName = aValueName;
        }

        public SlotType getTagType() {
            return tagType;
        }

        public String getValueName() {
            return valueName;
        }
        
        @Override
        public String toString() {
            return "[ValueNotFound valueName=" + getValueName() + " tagType=" + getTagType() + "]";
        }

        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
    
    public static class Ambiguity extends TagValueLookupResult {
        private final Set<TagValueLookupResult.Success> possibilities;

        public Ambiguity( Iterable<TagValueLookupResult.Success> possibilities ) {
            this.possibilities = new HashSet<>();
            for ( TagValueLookupResult.Success res : possibilities ) {
                this.possibilities.add( res );
            }
        }

        public Set<TagValueLookupResult.Success> getPossibilities() {
            return possibilities;
        }

        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit( this );
        }
        
        @Override
        public String toString() {
            return "[Ambiguity possibilities=" + getPossibilities() + "]";
        }
    }
    
    public static class Success extends TagValueLookupResult {
        
        private final TagValue value;

        public Success( TagValue value ) {
            this.value = value;
        }

        public TagValue getValue() {
            return value;
        }

        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
        
        @Override
        public <R, E extends Exception> R accept( SuccessFailVisitor<R,E> sfv ) throws E {
            return sfv.visitSuccess(this);
        }
        
        @Override
        public String toString()  {
            return "[Success value=" + getValue() + "]";
        }
        
    }
    
    static public SlotNotFound SlotNotFound(String slotName) {
        return new SlotNotFound(slotName);
    }

    static public ValueNotFound ValueNotFound(SlotType tt, String valueName) {
        return new ValueNotFound(tt, valueName);
    }

    static public Success Success(TagValue val) {
        return new Success(val);
    }

    static public Ambiguity Ambiguity(Iterable<TagValueLookupResult.Success> r2) {
        return new Ambiguity(r2);
    }
    
    public abstract <R> R accept( Visitor<R> v );
    
    public <R, E extends Exception> R accept( SuccessFailVisitor<R,E> sfv ) throws E {
        return sfv.visitFailure(this);
    }
}
