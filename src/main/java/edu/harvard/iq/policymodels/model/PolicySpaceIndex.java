package edu.harvard.iq.policymodels.model;

import edu.harvard.iq.policymodels.model.policyspace.slots.AggregateSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.ToDoSlot;
import static edu.harvard.iq.policymodels.util.CollectionHelper.C;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;

/**
 * 
 * An index for a tag space: maps String paths to slots/values.
 * 
 * @author michael
 */
public class PolicySpaceIndex {
   
    /**
     * Map from partial slot paths to fully qualified ones.
     */
    private final Map<List<String>,Set<List<String>>> index = new HashMap<>();
    
    /**
     * All absolute paths that lead to a value (rather than a slot).
     */
    private final Set<List<String>> valuePaths = new HashSet<>();
    
    class PathCollector extends AbstractSlot.VoidVisitor {
        
        /**
         * The current stack. note that we use LinkedList as variable type since we need both
         * the stack (Deque) and list functionalities.
         */
        private final LinkedList<String> currentPath = new LinkedList<>();
        
        @Override
        public void visitAtomicSlotImpl(AtomicSlot t) {
            currentPath.push(t.getName());
            addPath();
            t.values().forEach( v-> {
                currentPath.push(v.getName());
                valuePaths.add(addPath());
                currentPath.pop();
            }); 
            currentPath.pop();
        }

        @Override
        public void visitAggregateSlotImpl(AggregateSlot t) {
            currentPath.push(t.getName());
            addPath();
            t.getItemType().values().forEach( v-> {
                currentPath.push(v.getName());
                valuePaths.add(addPath());
                currentPath.pop();
            }); 
            currentPath.pop();
        }

        @Override
        public void visitTodoSlotImpl(ToDoSlot t) {
            currentPath.push(t.getName());
            addPath();
            currentPath.pop();
        }

        @Override
        public void visitCompoundSlotImpl(CompoundSlot t) {
            currentPath.push(t.getName());
            addPath();
            t.getSubSlots().forEach( ft->ft.accept(this) );
            currentPath.pop();
        }
        
        private List<String> addPath() {
            List<String> fullPath = C.reverse((List<String>)currentPath);            
            
            // Add all partial paths ending in this path.
            for ( int startIdx=0; startIdx<=fullPath.size(); startIdx++ ) {
                List<String> partialPath = fullPath.subList(startIdx, fullPath.size());
                Set<List<String>> fullPaths = index.get(partialPath);
                if ( fullPaths == null ) {
                    fullPaths = new HashSet<>(1); // assume code is OK.
                    index.put(partialPath, fullPaths);
                }
                fullPaths.add(fullPath);
            }
            return fullPath;
        }
    };
    
    public PolicySpaceIndex( CompoundSlot topLevelType ) {
        topLevelType.accept( new PathCollector() );
    }
    
    
    /**
     * Returns all possible full paths containing {@code partialPath}. Ideally, 
     * calling code would get a single list, which represents a single type path.
     * If the returned list is empty, there are now types for the parameter (maybe
     * a typo in the input). If more than one list is returned, the user type
     * reference is ambiguous.
     * 
     * @param partialPath 
     * @return list of all absolute type paths containing {@code partialPath}.
     */
    public Set<List<String>> get( List<String> partialPath ) {
       Set<List<String>> retVal = index.get(partialPath);
       return (retVal!=null) ? retVal : Collections.emptySet();
    }
    
    public Set<List<String>> get( String partialPath ) {
        return get(
                Arrays.stream(partialPath.trim().split("/")).collect(toList())
        );
    }
    
    public boolean isValue( List<String> path ) {
        return valuePaths.contains(path);
    }
    
}
