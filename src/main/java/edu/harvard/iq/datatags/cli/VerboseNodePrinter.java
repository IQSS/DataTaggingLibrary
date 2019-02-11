package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.PartNode;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * Prints nodes to the console, with all the details.
 * @author michael
 */
public class VerboseNodePrinter extends Node.VoidVisitor {
    
    private final CliRunner rnr;

    public VerboseNodePrinter(CliRunner rnr) {
        this.rnr = rnr;
    }
    @Override
    public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [consider:]", nd.getId());
       
       
        rnr.println("ans: ");
        nd.getAnswers().forEach((a) -> {
            rnr.print(a.getValue().toString());
            rnr.println(" -> >%s<", nd.getNodeFor(a).getId());
        });
        rnr.println();
    }
    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [ask]", nd.getId());
        rnr.println(nd.getText());
        if (!nd.getTermNames().isEmpty()) {
            rnr.println("Terms:");
            nd.getTermNames().forEach((term) -> {
                rnr.println(term + ":");
                rnr.println("\t" + nd.getTermText(term));
            });
        }
        rnr.println("Answers: ");
        nd.getAnswers().forEach((a) -> {
            rnr.print(a.getAnswerText());
            rnr.println(" -> >%s<", nd.getNodeFor(a).getId());
        });
        rnr.println();
    }

    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [set]", nd.getId());
        rnr.dumpTagValue(nd.getTags());
        rnr.println();
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [reject]", nd.getId());
        rnr.print(nd.getReason());
        rnr.println();
    }

    @Override
    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [call]", nd.getId());
        rnr.print("Calls node >%s<.", nd.getCalleeNode());
        rnr.println();
    }

    @Override
    public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [todo]", nd.getId());
        rnr.print(nd.getTodoText());
        rnr.println();
    }

    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [end]", nd.getId());
        rnr.println();
    }
    
    @Override
    public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [Section]", nd.getId());
        rnr.print(nd.getTitle());
        rnr.print("Starting at node: %s", nd.getStartNode().getId());
        rnr.println();
    }

    @Override
    public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
        rnr.printTitle("Node >%s<: [Part]", nd.getId());
        rnr.print(nd.getTitle());
        rnr.print("Starting at node: %s", nd.getStartNode().getId());
        rnr.println();
    }
    
}
