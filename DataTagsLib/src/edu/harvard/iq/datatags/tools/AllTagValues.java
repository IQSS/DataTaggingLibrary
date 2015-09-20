package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.TagType.VoidVisitor;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.HashSet;
import java.util.Set;

/**
 * Traverse all TagTypes and gather all
 * existing TagValues.
 * 
 * @author Naomi
 */
public class AllTagValues extends VoidVisitor {
    private final Set<TagValue> definedTagValues = new HashSet<>();
    
    public Set<TagValue> gatherAllTagValues(FlowChartSet fcs) {
        fcs.getTopLevelType().accept(this);
        return definedTagValues;
    }

    @Override
    public void visitSimpleTypeImpl(AtomicType t) {
        for (AtomicValue simpleValue : t.values()) {
            definedTagValues.add(simpleValue);
        }
    }

    @Override
    public void visitAggregateTypeImpl(AggregateType t) {
        t.getItemType().accept(this);
    }

    @Override
    public void visitCompoundTypeImpl(CompoundType t) {
        for (TagType tagType : t.getFieldTypes()) {
            tagType.accept(this);
        }
    }

    @Override
    public void visitTodoTypeImpl(ToDoType t) {
        definedTagValues.add(t.getValue());
    }
    
}
