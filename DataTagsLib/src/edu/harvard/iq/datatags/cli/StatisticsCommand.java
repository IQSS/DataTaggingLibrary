package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
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

class TagCounter extends TagType.VoidVisitor {
    
    int slotsCount=0;
    int valuesCount=0;
    
    @Override
    public void visitAtomicTypeImpl(AtomicType t) {
        valuesCount += t.values().size();
    }

    @Override
    public void visitAggregateTypeImpl(AggregateType t) {
        valuesCount += t.getItemType().values().size();
    }

    @Override
    public void visitCompoundTypeImpl(CompoundType t) {
        slotsCount++;
        for ( TagType tt : t.getFieldTypes() ) {
            slotsCount++;
            tt.accept(this);
        }
    }

    @Override
    public void visitTodoTypeImpl(ToDoType t) {
        valuesCount++;
    }
    
    
}