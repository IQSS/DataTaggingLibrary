package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.externaltexts.Localization;
import edu.harvard.iq.policymodels.externaltexts.LocalizationLoader;
import edu.harvard.iq.policymodels.model.decisiongraph.DecisionGraph;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.AskNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.RejectNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.SectionNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ToDoNode;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AggregateSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.ToDoSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
import static edu.harvard.iq.policymodels.util.CollectionHelper.C;
import edu.harvard.iq.policymodels.util.CollectionHelper.DoubleSetDiff;
import edu.harvard.iq.policymodels.util.PolicySpaceHelper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import java.util.stream.StreamSupport;

/**
 * Shows the differences between a localization and the current model.
 * 
 * @author michael
 */
public class LocalizationDiffCommand implements CliCommand {

    @Override
    public String command() {
        return "loc-diff";
    }

    @Override
    public String description() {
        return "Prints the differences between a localization and the model (e.g. non-localized nodes).";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        if ( args.size() < 2 ) {
            rnr.printWarning("Localization name required. Try \\loc-diff LOCALIZATION_NAME");
            return;
        }
        String localizationName = args.get(1);
        try {
            LocalizationLoader ldr = new LocalizationLoader();
            Localization loc = ldr.load(rnr.getModel(), localizationName);
            
            if ( ! ldr.getMessages().isEmpty() ) {
                rnr.println("Localization Messages:");
                ldr.getMessages().forEach( vm -> rnr.println(" - "+vm.getLevel()+": "+vm.getMessage()) );
                rnr.println();
            }
            
            DecisionGraph dg = rnr.getModel().getDecisionGraph();
            
            // answers
            Set<String> answersInGraph = StreamSupport.stream(dg.nodes().spliterator(),true)
                .filter(n -> n instanceof AskNode)
                .map( n -> (AskNode)n)
                .flatMap( a -> a.getAnswers().stream().map( ans -> ans.getAnswerText() ) )
                .collect( toSet() );
            DoubleSetDiff<String> ansDiff = C.diff( answersInGraph, loc.getLocalizedAnswers() );
            if ( ansDiff.inAOnly.isEmpty() ) {
                rnr.println("All answers are localized");
            } else {
                rnr.println("Non-localized answers:");
                ansDiff.inAOnly.forEach( a -> rnr.println(" - " + a));
            }
            if ( ! ansDiff.inBOnly.isEmpty() ) {
                rnr.println("Unused answer localizations:");
                ansDiff.inBOnly.forEach( a -> rnr.println(" - " + a));
            }
            rnr.println();
            
            // sections
            Set<String> sectionIdsInGraph = StreamSupport.stream(dg.nodes().spliterator(),true)
                .filter(n -> n instanceof SectionNode)
                .map( Node::getId )
                .collect( toSet() );
            DoubleSetDiff<String> sectionDiff = C.diff( sectionIdsInGraph, loc.getLocalizedSectionIds());
            if ( sectionDiff.inAOnly.isEmpty() ) {
                rnr.println("All sections are localized");
            } else {
                rnr.println("Non-localized sections:");
                sectionDiff.inAOnly.forEach( s -> rnr.println(" - " + s) );
            }
            if ( ! sectionDiff.inBOnly.isEmpty() ) {
                rnr.println("Section localization that can be removed:");
                sectionDiff.inBOnly.forEach( s -> rnr.println(" - " + s) );
            }
            rnr.println();
            
            // nodes
            Set<String> localizableNodeIdsInGraph =  StreamSupport.stream(dg.nodes().spliterator(),true)
                .filter(n -> (n instanceof AskNode) || (n instanceof RejectNode) || (n instanceof ToDoNode) )
                .map( Node::getId )
                .collect( toSet() );
            DoubleSetDiff<String> nodesDiff = C.diff( localizableNodeIdsInGraph, loc.getLocalizedNodeIds());
            if ( sectionDiff.inAOnly.isEmpty() ) {
                rnr.println("All nodes are localized");
            } else {
                rnr.println("Non-localized nodes:");
                nodesDiff.inAOnly.forEach( n -> rnr.println(" - " + n) );
            }
            if ( ! sectionDiff.inBOnly.isEmpty() ) {
                rnr.println("Node localization that can be removed:");
                nodesDiff.inBOnly.forEach( n -> rnr.println(" - " + n) );
            }
            rnr.println();
            
            // policy space
            diffPolicySpace( rnr, loc );
            
        } catch ( Exception xe ) {
            rnr.printWarning("Error diffing localization '%s': '%s'", localizationName, xe.getMessage());
            if ( rnr.getPrintDebugMessages() ) {
                xe.printStackTrace(System.out);
            }
        }
    }
    
    private void diffPolicySpace( CliRunner rnr, Localization loc ) {
        final Set<AbstractSlot> slotsInGraph = new HashSet<>();
        final Set<AbstractValue> valuesInGraph = new HashSet<>();
        
        rnr.getModel().getSpaceRoot().accept( new AbstractSlot.VoidVisitor(){
            @Override
            public void visitAtomicSlotImpl(AtomicSlot t) {
                slotsInGraph.add(t);
                valuesInGraph.addAll(t.values());
            }

            @Override
            public void visitAggregateSlotImpl(AggregateSlot t) {
                slotsInGraph.add(t);
                valuesInGraph.addAll(t.getItemType().values());
            }

            @Override
            public void visitCompoundSlotImpl(CompoundSlot t) {
                slotsInGraph.add(t);
                t.getSubSlots().forEach(subs->subs.accept(this));
            }

            @Override
            public void visitTodoSlotImpl(ToDoSlot t) {
                slotsInGraph.add(t);
            }
        });
        
        DoubleSetDiff<AbstractSlot> slotDiff = C.diff(slotsInGraph, loc.getLocalizedSlots());
        if ( ! slotDiff.inAOnly.isEmpty() ) {
            rnr.println("Non-localized slots:");
            slotDiff.inAOnly.forEach( s -> rnr.println(" - " + s.getName()) );
        }
        
        DoubleSetDiff<AbstractValue> valueDiff = C.diff(valuesInGraph, loc.getLocalizedValues());
        if ( ! valueDiff.inAOnly.isEmpty() ) {
            rnr.println("Non-localized values:");
            valueDiff.inAOnly.forEach( s -> rnr.println(" - " + PolicySpaceHelper.name(s)) );
        }
        
    }
    
}

