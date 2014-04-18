package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.SimpleType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Writes/Reads a {@link TagValue} into a map of strings. 
 * This format does not support long-term archiving.
 * 
 * @author michael
 */
public class StringMapFormat {
    
    public Map<String,String> format( TagValue value ) {
        final Map<String, String> res = new TreeMap<>();
        
        value.accept( new TagValue.Visitor<Void>() {
            
            List<String> stack = new LinkedList<>();
            
            @Override
            public Void visitToDoValue(ToDoValue v) {
                // ignore
                return null;
            }

            @Override
            public Void visitSimpleValue(SimpleValue v) {
                res.put(pathAsString() + v.getType().getName(), v.getName() );
                return null;
            }

            @Override
            public Void visitAggregateValue(AggregateValue v) {
                StringBuilder sb = new StringBuilder();
                for ( SimpleValue sv : v.getValues() ) {
                    sb.append(sv.getName()).append(",");
                }
                String val = sb.toString();
                res.put( pathAsString() + v.getType().getName(), val.substring(0,val.length()-1) );
                return null;
            }

            @Override
            public Void visitCompoundValue(CompoundValue cv) {
                stack.add( cv.getType().getName() );
                System.out.println("Pushed: " + stack);
                for ( TagType tt : cv.getSetFieldTypes() ) {
                    cv.get(tt).accept(this);
                }
                stack.remove( stack.size()-1 );
                System.out.println("Popped: " + stack);
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
    
    
    public TagValue parse( TagType type, Map<String,String> serializedValue ) {
        final TagValue ret = null;
        return null;
    }
    
//    TagType.Visitor<TagValue> evaluator( final TagType type, final List<String> path, final String values ) {
//        return new TagType.Visitor<TagValue>() {
//
//            @Override
//            public TagValue visitSimpleType(SimpleType t) {
//                return t.valueOf(values);
//            }
//
//            @Override
//            public TagValue visitAggregateType(AggregateType t) {
//                AggregateValue val = t.make();
//                for ( String itemName : values.split(",") ) {
//                    val.add( t.getItemType().valueOf(itemName) );
//                }
//                return val;
//            }
//
//            @Override
//            public TagValue visitCompoundType(CompoundType t) {
//                CompoundValue val = t.createInstance();
//                
//            }
//
//            @Override
//            public TagValue visitTodoType(ToDoType t) {
//                return t.getValue();
//            }
//        };
//    }
    
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
