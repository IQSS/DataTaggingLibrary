package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Prints statistics about the questionnaire.
 * @author michael
 */
public class StatisticsCommand implements CliCommand {

    @Override
    public String command() {
        return "stats";
    }

    @Override
    public String description() {
        return "Prints statistics about the model.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        TagCounter cnt = new TagCounter();
        rnr.getModel().getSpaceRoot().accept(cnt);
        rnr.println("Slot count: %d", cnt.slotsCount);
        rnr.println("Value count: %d", cnt.valuesCount);
        rnr.println("Decision graph nodes: %d", rnr.getModel().getDecisionGraph().nodeIds().size() );
        final Map<Class<?>, AtomicInteger> countsByClass = new HashMap<>();
        rnr.getModel().getDecisionGraph().nodes().forEach( 
                nd -> countsByClass.computeIfAbsent(nd.getClass(), c -> new AtomicInteger(0)).incrementAndGet() );
        
        final Map<String, Integer> counts = new HashMap<>();
        countsByClass.entrySet().forEach( ent -> {
            String className = C.last(ent.getKey().getName().split("\\."));
            counts.put(className, ent.getValue().intValue());
        });
        
        counts.entrySet().stream().sorted((e1,e2)->e2.getValue().compareTo(e1.getValue()))
                .forEach( ent -> rnr.println("  %s\t%d", ent.getKey(), ent.getValue()));
    }
    
}

class TagCounter extends SlotType.VoidVisitor {
    
    int slotsCount=0;
    int valuesCount=0;
    
    @Override
    public void visitAtomicSlotImpl(AtomicSlot t) {
        valuesCount += t.values().size();
    }

    @Override
    public void visitAggregateSlotImpl(AggregateSlot t) {
        valuesCount += t.getItemType().values().size();
    }

    @Override
    public void visitCompoundSlotImpl(CompoundSlot t) {
        slotsCount++;
        t.getFieldTypes().forEach( tt -> {
            slotsCount++;
            tt.accept(this);
        });
    }

    @Override
    public void visitTodoSlotImpl(ToDoSlot t) {
        valuesCount++;
    }
    
    
}