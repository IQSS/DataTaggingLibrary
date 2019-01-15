/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.Inference;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.slots.ToDoSlot;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jparsec.Parser;

/**
 *
 * @author mor_vilozni
 */
public class ValueInferenceParser {
    
    private final Parser<List<ValueInferrerAst>> parser = ValueInferenceTerminalParser.buildParser( ValueInferenceRuleParser.valueInferrersParser() );
    private Map<List<String>, List<String>> fullyQualifiedSlotName = new HashMap<>();
    private final CompoundSlot topLevelType;

    public ValueInferenceParser(CompoundSlot topLevelType) {
        this.topLevelType = topLevelType;
    }
    
    /**
     * Parse value inference code into a result that can be used to create actual value inferrer
     * @param valueInferrerCode
     * @return the result of the parsing
     * @throws SyntaxErrorException
     */
    public ValueInferenceParseResult parse( String valueInferrerCode ) throws SyntaxErrorException {
        fullyQualifiedSlotName = buildTypeIndex();
        try {
            return new ValueInferenceParseResult( parser.parse(valueInferrerCode), fullyQualifiedSlotName, topLevelType );
            
        } catch ( org.jparsec.error.ParserException pe ) {
			throw new SyntaxErrorException( new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
											pe.getMessage(),
											pe);
        }
    }
    
    public ValueInferenceParseResult parse( Path file ) throws IOException, SyntaxErrorException {
        String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        return parse(source);
    }
    
    Map<List<String>, List<String>> buildTypeIndex() {
        List<List<String>> fullyQualifiedNames = new LinkedList<>();
        // initial index
        topLevelType.accept(new AbstractSlot.VoidVisitor() {
            LinkedList<String> stack = new LinkedList<>();

            @Override
            public void visitAtomicSlotImpl(AtomicSlot t) {
                addType(t);
            }

            @Override
            public void visitAggregateSlotImpl(AggregateSlot t) {
                addType(t);
            }

            @Override
            public void visitTodoSlotImpl(ToDoSlot t) {
                addType(t);
            }

            @Override
            public void visitCompoundSlotImpl(CompoundSlot t) {
                stack.push(t.getName());
                t.getSubSlots().forEach(tt -> tt.accept(this));
                stack.pop();
            }

            void addType(AbstractSlot tt) {
                stack.push(tt.getName());
                fullyQualifiedNames.add(C.reverse((List) stack));
                stack.pop();
            }
        });

        fullyQualifiedNames.forEach(n -> fullyQualifiedSlotName.put(n, n));

        // add abbreviations
        Set<List<String>> ambiguous = new HashSet<>();
        Map<List<String>, List<String>> newEntries = new HashMap<>();

        fullyQualifiedNames.forEach(slot -> {
            List<String> cur = C.tail(slot);
            while (!cur.isEmpty()) {
                if (fullyQualifiedSlotName.containsKey(cur) || newEntries.containsKey(cur)) {
                    ambiguous.add(cur);
                    break;
                } else {
                    newEntries.put(cur, slot);
                }
                cur = C.tail(cur);
            }
        });

        ambiguous.forEach(newEntries::remove);
        fullyQualifiedSlotName.putAll(newEntries);
        return fullyQualifiedSlotName;
    }
    
}
