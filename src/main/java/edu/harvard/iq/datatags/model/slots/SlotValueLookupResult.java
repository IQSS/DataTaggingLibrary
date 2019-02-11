package edu.harvard.iq.datatags.model.slots;

import edu.harvard.iq.datatags.model.values.AbstractValue;
import java.util.HashSet;
import java.util.Set;

/**
 * Used for reporting which slot a value should go to, and
 * whether that value exists and is compatible with the slot.
 */
public abstract class SlotValueLookupResult {
    
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
        R visitFailure( SlotValueLookupResult s ) throws E;
    }
    
    public static class SlotNotFound extends SlotValueLookupResult {
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
    
    public static class ValueNotFound extends SlotValueLookupResult {
        private final AbstractSlot tagType;
        private final String valueName;

        public ValueNotFound(AbstractSlot aTagType, String aValueName) {
            tagType = aTagType;
            valueName = aValueName;
        }

        public AbstractSlot getTagType() {
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
    
    public static class Ambiguity extends SlotValueLookupResult {
        private final Set<SlotValueLookupResult.Success> possibilities;

        public Ambiguity( Iterable<SlotValueLookupResult.Success> possibilities ) {
            this.possibilities = new HashSet<>();
            for ( SlotValueLookupResult.Success res : possibilities ) {
                this.possibilities.add( res );
            }
        }

        public Set<SlotValueLookupResult.Success> getPossibilities() {
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
    
    public static class Success extends SlotValueLookupResult {
        
        private final AbstractValue value;

        public Success( AbstractValue value ) {
            this.value = value;
        }

        public AbstractValue getValue() {
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

    static public ValueNotFound ValueNotFound(AbstractSlot tt, String valueName) {
        return new ValueNotFound(tt, valueName);
    }

    static public Success Success(AbstractValue val) {
        return new Success(val);
    }

    static public Ambiguity Ambiguity(Iterable<SlotValueLookupResult.Success> r2) {
        return new Ambiguity(r2);
    }
    
    public abstract <R> R accept( Visitor<R> v );
    
    public <R, E extends Exception> R accept( SuccessFailVisitor<R,E> sfv ) throws E {
        return sfv.visitFailure(this);
    }
}
