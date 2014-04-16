package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Writes/Reads a {@link CompoundValue} into a map of strings. 
 * This format does not support long-term archiving.
 * 
 * @author michael
 */
public class StringMapFormat {
    
    public Map<String,String> format( CompoundValue value ) {
        final Map<String, String> res = new TreeMap<>();
        
        value.accept( new TagValue.Visitor<Void>() {
            
            Deque<String> stack = new LinkedList<>();
            
            @Override
            public Void visitToDoValue(ToDoValue v) {
                // ignore
                return null;
            }

            @Override
            public Void visitSimpleValue(SimpleValue v) {
                res.put(getPath(), v.getName() );
                return null;
            }

            private String getPath() {
                StringBuilder sb = new StringBuilder();
                for ( String s : stack ) {
                    sb.append(s).append("/");
                }
                return sb.toString();
            }

            @Override
            public Void visitAggregateValue(AggregateValue v) {
                StringBuilder sb = new StringBuilder();
                for ( SimpleValue sv : v.getValues() ) {
                    sb.append(sv.getName()).append(",");
                }
                res.put( getPath(), sb.toString() );
                return null;
            }

            @Override
            public Void visitCompoundValue(CompoundValue cv) {
                stack.push( cv.getType().getName() );
                for ( TagType tt : cv.getSetFieldTypes() ) {
                    cv.get(tt).accept(this);
                }
                stack.pop();
                return null;
            }

        });
        
        return res;
    }
}
