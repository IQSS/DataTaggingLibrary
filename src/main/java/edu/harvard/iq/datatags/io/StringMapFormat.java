package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.slots.ToDoSlot;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Writes/Reads a {@link AbstractValue} into a map of strings. 
 * This format does not support long-term archiving.
 * 
 * @author michael
 */
public class StringMapFormat {
    
    public Map<String,String> format( AbstractValue value ) {
        final Map<String, String> res = new TreeMap<>();
        if ( value==null ) return res;
        
        value.accept(new AbstractValue.Visitor<Void>() {
            
            List<String> stack = new LinkedList<>();
            
            @Override
            public Void visitToDoValue(ToDoValue v) {
                // ignore
                return null;
            }

            @Override
            public Void visitAtomicValue(AtomicValue v) {
                res.put(pathAsString() + v.getSlot().getName(), v.getName() );
                return null;
            }

            @Override
            public Void visitAggregateValue(AggregateValue v) {
                StringBuilder sb = new StringBuilder();
                for ( AtomicValue sv : v.getValues() ) {
                    sb.append(sv.getName()).append(",");
                }
                String val = sb.toString();
                res.put( pathAsString() + v.getSlot().getName(), val.substring(0,val.length()-1) );
                return null;
            }

            @Override
            public Void visitCompoundValue(CompoundValue cv) {
                stack.add( cv.getSlot().getName() );
                for ( AbstractSlot tt : cv.getNonEmptySubSlots() ) {
                    cv.get(tt).accept(this);
                }
                stack.remove( stack.size()-1 );
                return null;
            }
            
            private String pathAsString() {
                StringBuilder sb = new StringBuilder();
                for ( String s : stack ) {
                    sb.append(s).append("/");
                }
                return sb.toString();
            }
        });
        
        return res;
    }
    
    
    /**
     * Builds a {@link AbstractValue} from a map created by {@link #format}.
     * @param type The expected type of the resulting tag value.
     * @param serializedValue Tag of the expected type, serialized by this format.
     * @return The value, or {@code null} for empty map.
     * @see #parseCompoundValue(edu.harvard.iq.datatags.model.types.CompoundSlot, java.util.Map) 
     */
    public AbstractValue parse( AbstractSlot type, Map<String,String> serializedValue ) {
        return serializedValue.isEmpty() 
                ? null
                : evaluate( type, makeTrie(serializedValue).getSingleChild() );
    }
    
    /**
     * Builds a {@link AbstractValue} from a map created by {@link #format}.
     * @param type The expected type of the resulting tag value.
     * @param serializedValue Tag of the expected type, serialized by this format.
     * @return The value (empty map translates to empty value).
     */
    public CompoundValue parseCompoundValue( CompoundSlot type, Map<String,String> serializedValue ) {
        return serializedValue.isEmpty() 
                ? type.createInstance()
                : (CompoundValue)evaluate( type, makeTrie(serializedValue).getSingleChild() );
    }
    
    AbstractValue evaluate( final AbstractSlot type, final TrieNode node ) {
        if ( node == null ) return null;
        
        return type.accept(new AbstractSlot.Visitor<AbstractValue>() {

            @Override
            public AbstractValue visitSimpleSlot(AtomicSlot t) {
                // We expect a single value.
                return t.valueOf(node.getSingleKey());
            }

            @Override
            public AbstractValue visitAggregateSlot(AggregateSlot t) {
                AggregateValue val = t.createInstance();
                String values = node.getSingleKey();
                for ( String itemName : values.split(",") ) {
                    val.add( t.getItemType().valueOf(itemName) );
                }
                return val;
            }

            @Override
            public AbstractValue visitCompoundSlot(CompoundSlot t) {
                CompoundValue val = t.createInstance();
                
                for ( AbstractSlot fieldType : t.getSubSlots() ) {
                    AbstractValue fieldValue = evaluate(fieldType, node.get(fieldType.getName()));
                    if ( fieldValue != null ) {
                        val.put( fieldValue );
                    }
                }
                
                return val;
            }

            @Override
            public AbstractValue visitTodoSlot(ToDoSlot t) {
                return t.getValue();
            }
        });
    }
    
    public static class TrieNode {
        final Map<String, TrieNode> childs = new TreeMap<>();
        public boolean isValue() {
            return childs.isEmpty();
        }

        public TrieNode get(String key) {
            return childs.get(key);
        }

        public TrieNode put(String key, TrieNode value) {
            return childs.put(key, value);
        }
        
        public TrieNode getSingleChild() {
            if ( childs.size() != 1 ) {
                throw new RuntimeException("Child number is " + childs.size() + " " + childs.keySet() );
            }
            return childs.values().iterator().next();
        }
        
        public String getSingleKey() {
            if ( childs.size() != 1 ) {
                throw new RuntimeException("Child number is " + childs.size() + " " + childs.keySet() );
            }
            return childs.keySet().iterator().next();
        }
        
    }
    
    public TrieNode makeTrie( Map<String, String> input ) {
        TrieNode root = new TrieNode();
        
        for ( Map.Entry<String,String> ent : input.entrySet() ) {
            buildNode( Arrays.asList(ent.getKey().split("/")), ent.getValue(), root );
        }
        
        return root;
    }
    
    public void buildNode( List<String> path, String value, TrieNode base ) {
        if ( path.isEmpty() ) {
            base.put( value, new TrieNode() );
            
        } else {
            if ( base.get(C.head(path))==null ) {
                base.put(C.head(path), new TrieNode());
            }
            buildNode( C.tail(path), value, base.get(C.head(path)));
        }
    }
}
