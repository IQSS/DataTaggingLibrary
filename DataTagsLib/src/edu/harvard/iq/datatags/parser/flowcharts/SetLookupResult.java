package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.HashSet;
import java.util.Set;

/**
 * Used for finding which slot a value should go to, and
 * that the value exists and it compatible with the slot.
 * In a perfect world I'd go with another hierarchy and visitors, but
 * due to time constraints we go for a less shiny solution.
 * 
 * LATER make a clean solution
 */
public class SetLookupResult {

    public enum Status {
        SlotNotFound, ValueNotFound, Success, Ambiguous
    }
    public final Status status;
    final Set<TagValue> values = new HashSet<>();
    public final TagType type;
    
    /**
     * Can either mean slot name or value name.
     */
    public final String textValue;
    
    static SetLookupResult SlotNotFound(String slotName) {
        return new SetLookupResult(Status.SlotNotFound, null, slotName);
    }

    static SetLookupResult ValueNotFound(TagType tt, String valueName) {
        return new SetLookupResult(Status.ValueNotFound, tt, valueName);
    }

    static SetLookupResult Success(TagValue val) {
        SetLookupResult res = new SetLookupResult(Status.Success, val.getType(), null);
        res.values.add(val);
        return res;
    }

    static SetLookupResult Ambiguous(Iterable<SetLookupResult> r2) {
        SetLookupResult res = new SetLookupResult(Status.Ambiguous, null, null);
        for (SetLookupResult slr : r2) {
            res.values.addAll(slr.values);
        }
        return res;
    }

    SetLookupResult(Status s, TagType tt, String aSlotName) {
        status = s;
        type = tt;
        textValue = aSlotName;
    }
    
    /**
     * This method only makes sense when there is a single value.
     * @return The only value we have.
     */
    public TagValue get() {
        if (values.size() != 1) {
            throw new IllegalStateException("Calling get() on a SetLookupResult with size=" + values.size());
        }
        return values.iterator().next();
    }

    public Set<TagValue> values() {
        return values;
    }
    
}
