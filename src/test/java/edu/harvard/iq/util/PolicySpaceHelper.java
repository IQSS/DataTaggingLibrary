package edu.harvard.iq.util;

import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author michael
 */
public class PolicySpaceHelper {
    
    public static List<String> toPath(String spaceRef) {
        return Arrays.stream(spaceRef.split("/")).collect(toList());
    }
    
    public static String fromPath( List<String> path ) {
        return path.stream().collect(joining("/"));
    }
    
    /**
     * 
     * @param slot
     * @param values "A/v1; B/v2"
     * @return ComoundValue for the passed slot with the values in {@code values}.
     */
    public static CompoundValue buildValue( CompoundSlot slot, String values ) {
        CompoundValue out = slot.createInstance();
        
        Arrays.stream(values.split(";"))
                .map( s -> s.trim() )
                .forEach( p -> {
                    String[] comps = p.split("/");
                    AtomicSlot slt = (AtomicSlot) slot.getSubSlot(comps[0].trim());
                    out.put( slt.valueOf(comps[1].trim()) );
                });
        
        return out;
    }
}
