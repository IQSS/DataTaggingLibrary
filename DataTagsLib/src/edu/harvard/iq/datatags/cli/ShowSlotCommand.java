package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
import static edu.harvard.iq.datatags.model.types.TypeHelper.formatTypePath;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Prints a slot to the console.
 *
 * @author michael
 */
public class ShowSlotCommand implements CliCommand {

    @Override
    public String command() {
        return "show-slot";
    }

    @Override
    public String description() {
        return "Prints a slot's description to the console.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        if (args.size() < 2) {
            rnr.printWarning("Please provide a path to the slot.");
        }
        final String typePath = args.get(1);

        Deque<String> pathLeft = new LinkedList<>(Arrays.asList(typePath.split("/", -1)));
        Deque<String> pathDone = new LinkedList<>();
        final CompoundType topLevelType = rnr.getEngine().getDecisionGraph().getTopLevelType();
        
        if ( pathLeft.peekFirst().equals(topLevelType.getName()) ) {
            pathDone.addLast( pathLeft.removeFirst());
        }
        
        topLevelType.accept(new TagType.VoidVisitor() {
            @Override
            public void visitCompoundTypeImpl(CompoundType t) {
                if (pathLeft.isEmpty()) {
                    rnr.println("%s: compound slot (consists of)", typePath);
                    printNote(t);
                    rnr.println("Sub slots:");
                    t.getFieldTypes().forEach( val -> {
                        rnr.println("* %s", val.getName());
                        if ( val.getNote() != null ) {
                            rnr.println("  \t%s", val.getNote() );
                        }
                    });
                } else {
                    String nextTypeName = pathLeft.removeFirst();
                    TagType nextType = t.getTypeNamed(nextTypeName);
                    if ( nextType == null ) {
                        rnr.printWarning("Slot %s does not exist: %s does not have a sub-slot named %s.", typePath, formatTypePath(pathDone), nextTypeName );
                    } else {
                        pathDone.addLast(nextTypeName);
                        nextType.accept(this);
                    }
                    
                }
            }

            @Override
            public void visitAtomicTypeImpl(AtomicType t) {
                if (pathLeft.isEmpty()) {
                    rnr.println("%s: atomic slot (one of)", typePath);
                    printNote(t);
                    rnr.println("Possible values:");
                    t.values().forEach( val -> {
                        rnr.println("* %s", val.getName());
                        if ( val.getNote() != null ) {
                            rnr.println("  \t%s", val.getNote() );
                        }
                    });
                } else {
                    rnr.printWarning("Slot %s does not exist: %s is an atomic slot, and has no sub-slots.", typePath, formatTypePath(pathDone));
                }
            }

            @Override
            public void visitAggregateTypeImpl(AggregateType t) {
                if (pathLeft.isEmpty()) {
                    rnr.println("%s: aggregate slot (some of)", typePath);
                    printNote(t);
                    rnr.println("Possible values:");
                    t.getItemType().values().forEach( val -> {
                        rnr.println("* %s", val.getName());
                        if ( val.getNote() != null ) {
                            rnr.println("  \t%s", val.getNote() );
                        }
                    });
                } else {
                    rnr.printWarning("Slot %s does not exist: %s is an aggregate slot, and has no sub-slots.", typePath, formatTypePath(pathDone));
                }
            }

            @Override
            public void visitTodoTypeImpl(ToDoType t) {
                if (pathLeft.isEmpty()) {
                    rnr.println("%s: TODO", typePath);
                    printNote(t);
                } else {
                    rnr.printWarning("Slot %s does not exist: %s is a placeholder slot, and has no sub-slots.", typePath, formatTypePath(pathDone));
                }
            }

            void printNote(TagType t) {
                if (t.getNote() != null && !t.getNote().isEmpty()) {
                    rnr.println(t.getNote());
                }

            }
        });

    }

}
