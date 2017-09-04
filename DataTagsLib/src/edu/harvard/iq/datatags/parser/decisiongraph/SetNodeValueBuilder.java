package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.slots.ToDoSlot;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.exceptions.BadLookupException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author mor_vilozni
 */
public class SetNodeValueBuilder implements AstSetNode.Assignment.Visitor {

    private final CompoundValue topValue;
    private final Map<List<String>, List<String>> fullyQualifiedSlotName;

    public SetNodeValueBuilder(CompoundValue aTopValue, Map<List<String>, List<String>> typeIndex) {
        topValue = aTopValue;
        fullyQualifiedSlotName = typeIndex;
    }

    @Override
    public void visit(AstSetNode.AtomicAssignment aa) {
        final CompoundValue additionPoint = descend(C.tail(fullyQualifiedSlotName.get(aa.getSlot())), topValue);
        AbstractSlot valueType = additionPoint.getSlot().getSubSlot(C.last(aa.getSlot()));
        if (valueType == null) {
            throw new RuntimeException("Type '" + additionPoint.getSlot().getName()
                    + "' does not have a field of type '" + C.last(aa.getSlot()) + "'");
        }
        valueType.accept(new AbstractSlot.VoidVisitor() {
            @Override
            public void visitAtomicSlotImpl(AtomicSlot t) {
                try {
                    additionPoint.put(t.valueOf(aa.getValue())); // if there's no such value, an IllegalArgumentException will be thrown.
                } catch (IllegalArgumentException iae) {
                    throw new RuntimeException(new BadLookupException(t, aa.getValue(), null));
                }
            }

            @Override
            public void visitAggregateSlotImpl(AggregateSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is aggregate, not atomic. Use ``+='' to assign a value to it.");
            }

            @Override
            public void visitCompoundSlotImpl(CompoundSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is compound, not atomic. Can't assign values here.");
            }

            @Override
            public void visitTodoSlotImpl(ToDoSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is a placeholder. Can't assign values here.");
            }
        });
    }

    @Override
    public void visit(AstSetNode.AggregateAssignment aa) {
        final List<String> fullyQualifiedName = fullyQualifiedSlotName.get(aa.getSlot());
        if (fullyQualifiedName == null) {
            throw new RuntimeException(new DataTagsParseException((AstNode) null, "slot '"
                    + aa.getSlot().stream().collect(Collectors.joining("/")) + "' not found"));
        }
        final CompoundValue additionPoint = descend(C.tail(fullyQualifiedName), topValue);
        AbstractSlot valueType = additionPoint.getSlot().getSubSlot(C.last(aa.getSlot()));
        if (valueType == null) {
            throw new RuntimeException(new BadLookupException(additionPoint.getSlot(), C.last(aa.getSlot()), null));
        }
        valueType.accept(new AbstractSlot.VoidVisitor() {
            @Override
            public void visitAtomicSlotImpl(AtomicSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is compound, not aggregate. Can't assign values here.");
            }

            @Override
            public void visitAggregateSlotImpl(AggregateSlot t) {
                AggregateValue value = (AggregateValue) additionPoint.get(t);
                if (value == null) {
                    value = t.createInstance();
                    additionPoint.put(value);
                }
                for (String val : aa.getValue()) {
                    try {
                        value.add(t.getItemType().valueOf(val));
                    } catch (IllegalArgumentException iae) {
                        throw new RuntimeException(new BadLookupException(t, val, null));
                    }
                }
            }

            @Override
            public void visitCompoundSlotImpl(CompoundSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is compound, not atomic. Can't assign values here.");
            }

            @Override
            public void visitTodoSlotImpl(ToDoSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is a placeholder. Can't assign values here.");
            }
        });
    }

    /**
     * Descends the compound value tree, adding values as needed.
     *
     * @param pathRemainder the names of the fields along which we descend.
     * @param cVal the value we start the descend from
     * @return the compound value of the type pointed by the penultimate item in
     * {@code path}
     * @throws RuntimeException if the path is not descendable (i.e fields don't
     * exist or of the wrong type).
     */
    CompoundValue descend(List<String> pathRemainder, CompoundValue cVal) {
        if (pathRemainder.size() == 1) {
            return cVal;
        }
        CompoundSlot cType = cVal.getSlot();
        AbstractSlot nextTagType = cType.getSubSlot(C.head(pathRemainder));
        if (nextTagType == null) {
            throw new RuntimeException("Type '" + cType.getName()
                    + "' does not have a field of type '" + C.head(pathRemainder));
        }

        return descend(C.tail(pathRemainder), nextTagType.accept(new AbstractSlot.Visitor<CompoundValue>() {
            @Override
            public CompoundValue visitSimpleSlot(AtomicSlot t) {
                throw new RuntimeException("Type '" + t.getName()
                        + "' is not a compound type");
            }

            @Override
            public CompoundValue visitAggregateSlot(AggregateSlot t) {
                throw new RuntimeException("Type '" + t.getName()
                        + "' is not a compound type");
            }

            @Override
            public CompoundValue visitTodoSlot(ToDoSlot t) {
                throw new RuntimeException("Type '" + t.getName() + "' is not a compound type");
            }

            @Override
            public CompoundValue visitCompoundSlot(CompoundSlot t) {
                if (cVal.get(t) == null) {
                    final CompoundValue newInstance = t.createInstance();
                    cVal.put(newInstance);
                }
                return (CompoundValue) cVal.get(t);
            }
        }));
    }

}
