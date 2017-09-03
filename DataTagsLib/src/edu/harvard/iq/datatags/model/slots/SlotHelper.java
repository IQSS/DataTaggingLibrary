package edu.harvard.iq.datatags.model.slots;

import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.AbstractValue;

/**
 * Common methods for usage with slots.
 * 
 * @author michael
 */
public class SlotHelper {
    
    /**
     * Gets the value from the tag type. Use when you know the value is there and is distinct.
     * There are some semantic issues:
     * <ul>
     *  <li>For simple value - get the value</li>
     *  <li>For aggregate values - get a value with the {@code valueName} as its only item</li>
     *  <li>For Compound values - undefined (throws RTE)</li>
     * </ul>
     * @param tt 
     * @param slotName
     * @param valueName
     * @return The tag value pointed by the slot name.
     * @throws RuntimeException When the value is not there, or is not distinct.
     */
    static public AbstractValue safeGet( AbstractSlot tt, String slotName, String valueName ) {
        return tt.lookupValue(slotName, valueName).accept(new SlotValueLookupResult.SuccessFailVisitor<AbstractValue, RuntimeException>(){

            @Override
            public AbstractValue visitSuccess(SlotValueLookupResult.Success s) throws RuntimeException {
                return s.getValue();
            }

            @Override
            public AbstractValue visitFailure(SlotValueLookupResult s) throws RuntimeException {
                throw new RuntimeException("Can't find value " + s);
            }
        });
    }
    
    static public AtomicValue getCreateValue( AtomicSlot tt, String valueName, String note ) {
        return (tt.valueOf(valueName) != null ) 
                ? tt.valueOf( valueName )
                : tt.registerValue(valueName, note);
            
    }
    
    static public String formatSlotPath( Iterable<String> path ) {
        StringBuilder sb = new StringBuilder();
        path.forEach( c -> { 
            sb.append(c);
            sb.append("/");
        });
        if ( sb.length() > 0 ) {
            sb.setLength( sb.length()-1 );
        }
        return sb.toString();
    }
}
