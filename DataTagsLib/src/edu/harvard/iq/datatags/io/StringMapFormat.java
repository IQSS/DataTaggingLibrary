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
    // CONTPOINT: Test this, implement the valueOf.
    
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
                StringBuilder sb = new StringBuilder();
                for ( String s : stack ) {
                    sb.append(s).append("/");
                }
                res.put(sb.toString(), v.getName() );
                return null;
            }

            @Override
            public Void visitAggregateValue(AggregateValue v) {
                int i=0;
                for ( TagValue tv : v.getValues() ) {
                    stack.push( "$" + i );
                    tv.accept(this);
                    stack.pop();
                    i++;
                }
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
