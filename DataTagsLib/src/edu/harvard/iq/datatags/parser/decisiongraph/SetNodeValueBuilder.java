package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.exceptions.BadLookupException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.net.URI;
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
        SlotType valueType = additionPoint.getType().getTypeNamed(C.last(aa.getSlot()));
        if (valueType == null) {
            throw new RuntimeException("Type '" + additionPoint.getType().getName()
                    + "' does not have a field of type '" + C.last(aa.getSlot()) + "'");
        }
        valueType.accept(new SlotType.VoidVisitor() {
            @Override
            public void visitAtomicSlotImpl(AtomicSlot t) {
                try {
                    additionPoint.set(t.valueOf(aa.getValue())); // if there's no such value, an IllegalArgumentException will be thrown.
                } catch (IllegalArgumentException iae) {
                    throw new RuntimeException(new BadLookupException(t, aa.getValue(), null));
                }
            }

            @Override
            public void visitAggregateSlotImpl(AggregateSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is aggregate, not atomic. Use ``+='' .");
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
        SlotType valueType = additionPoint.getType().getTypeNamed(C.last(aa.getSlot()));
        if (valueType == null) {
            throw new RuntimeException(new BadLookupException(additionPoint.getType(), C.last(aa.getSlot()), null));
        }
        valueType.accept(new SlotType.VoidVisitor() {
            @Override
            public void visitAtomicSlotImpl(AtomicSlot t) {
                throw new RuntimeException("Slot " + aa.getSlot() + " is aggregate, not atomic. Use ``+='' .");
            }

            @Override
            public void visitAggregateSlotImpl(AggregateSlot t) {
                AggregateValue value = (AggregateValue) additionPoint.get(t);
                if (value == null) {
                    value = t.createInstance();
                    additionPoint.set(value);
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
        CompoundSlot cType = cVal.getType();
        SlotType nextTagType = cType.getTypeNamed(C.head(pathRemainder));
        if (nextTagType == null) {
            throw new RuntimeException("Type '" + cType.getName()
                    + "' does not have a field of type '" + C.head(pathRemainder));
        }

        return descend(C.tail(pathRemainder), nextTagType.accept(new SlotType.Visitor<CompoundValue>() {
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
                    cVal.set(newInstance);
                }
                return (CompoundValue) cVal.get(t);
            }
        }));
    }

}
