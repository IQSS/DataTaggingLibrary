package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import java.util.List;

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
        return "Prints statistics about the questionnaire.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        TagCounter cnt = new TagCounter();
        rnr.getDecisionGraph().getTopLevelType().accept(cnt);
        rnr.println("Slot count: %d", cnt.slotsCount);
        rnr.println("Value count: %d", cnt.valuesCount);
        rnr.println("Decision graph nodes: %d", rnr.getDecisionGraph().nodeIds().size() );
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
        for ( SlotType tt : t.getFieldTypes() ) {
            slotsCount++;
            tt.accept(this);
        }
    }

    @Override
    public void visitTodoSlotImpl(ToDoSlot t) {
        valuesCount++;
    }
    
    
}