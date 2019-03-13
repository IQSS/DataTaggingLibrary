package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ContinueNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node.VoidVisitor;
import edu.harvard.iq.datatags.model.graphs.nodes.PartNode;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Traverse the questionnaire and gather all used tag values
 * (used in set nodes).
 *
 * @author Naomi
 */
public class QuestionnaireTagValues extends VoidVisitor {
    private final Set<AbstractValue> usedTagValues = new HashSet<>();
    private final AbstractValue.Visitor<Set<AtomicValue>> valueCollector = new ValueCollector();
    
    public Set<AbstractValue> gatherInterviewTagValues( DecisionGraph dg ) {
        dg.nodes().forEach( a -> a.accept(this));
        return usedTagValues;
    }
    @Override
    public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
        // do nothing
    }
    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        usedTagValues.addAll( nd.getTags().accept(valueCollector) );
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        // do nothing
    }
    
    @Override
    public void visitImpl(ContinueNode nd) throws DataTagsRuntimeException {
        // do nothing
    }
    
    @Override
    public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
        // do nothing
    }
    
    @Override
    public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
        // do nothing
    }
    
    
}

class ValueCollector implements AbstractValue.Visitor<Set<AtomicValue>> {

    @Override
    public Set<AtomicValue> visitToDoValue(ToDoValue v) {
        return Collections.emptySet();
    }

    @Override
    public Set<AtomicValue> visitAtomicValue(AtomicValue v) {
        return Collections.singleton(v);
    }

    @Override
    public Set<AtomicValue> visitAggregateValue(AggregateValue v) {
        return v.getValues();
    }

    @Override
    public Set<AtomicValue> visitCompoundValue(CompoundValue v) {
        return v.getNonEmptySubSlots().stream()
                .map( st -> v.get(st) ) // get the slots
                .flatMap( s -> s.accept(this).stream() ) // get the values
                .collect( Collectors.toSet() );
    }
    
}