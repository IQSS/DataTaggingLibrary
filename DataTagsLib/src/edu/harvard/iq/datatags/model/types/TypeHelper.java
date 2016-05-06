package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.TagValue;

/**
 * Common methods for usage with types.
 * 
 * @author michael
 */
public class TypeHelper {
    
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
    static public TagValue safeGet( TagType tt, String slotName, String valueName ) {
        return tt.lookupValue(slotName, valueName).accept(new TagValueLookupResult.SuccessFailVisitor<TagValue, RuntimeException>(){

            @Override
            public TagValue visitSuccess(TagValueLookupResult.Success s) throws RuntimeException {
                return s.getValue();
            }

            @Override
            public TagValue visitFailure(TagValueLookupResult s) throws RuntimeException {
                throw new RuntimeException("Can't find value " + s);
            }
        });
    }
    
    static public AtomicValue getCreateValue( AtomicType tt, String valueName, String note ) {
        return (tt.valueOf(valueName) != null ) 
                ? tt.valueOf( valueName )
                : tt.registerValue(valueName, note);
            
    }
    
    static public String formatTypePath( Iterable<String> path ) {
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
