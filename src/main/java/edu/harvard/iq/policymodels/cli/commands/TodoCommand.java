/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ToDoNode;
import edu.harvard.iq.policymodels.tools.NodeValidationMessage;
import edu.harvard.iq.policymodels.tools.UnreachableNodeValidator;
import edu.harvard.iq.policymodels.tools.UnusedTagsValidator;
import edu.harvard.iq.policymodels.tools.ValidationMessage;
import java.util.List;
import static edu.harvard.iq.policymodels.util.CollectionHelper.C;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * @author mor
 */
public class TodoCommand extends AbstractCliCommand {

    public TodoCommand() {
        super("todo", "Generates a report that lists:\n"
                + "- All values/slots not used\n"
                + "- Unreachable nodes\n"
                + "- All [todo] nodes\n"
                + "todo [-f filename] prints the report to file");
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("## Unreachable nodes\n");
        UnreachableNodeValidator unv = new UnreachableNodeValidator();
        List<ValidationMessage> unm = unv.validate(rnr.getModel().getDecisionGraph());
        if ( unm.isEmpty() ) {
            stringBuilder.append(" No unreachable nodes found\n");
        } else {
            stringBuilder.append(" Warning\n");
            stringBuilder.append("Unreachable nodes found:\n");
            unm.forEach( w -> stringBuilder.append(" - ").append(((NodeValidationMessage)w).getEntities()).append("\n"));
        }
        
        stringBuilder.append("\n## Unused tags\n");
        UnusedTagsValidator utv = new UnusedTagsValidator();
        List<ValidationMessage> vms = utv.validateUnusedTags(rnr.getModel());
        
        if ( vms.isEmpty() ) {
            stringBuilder.append(" No unreachable tags found\n");
        } else {
            stringBuilder.append(" Warning\n");
            stringBuilder.append("Unused tags found:\n");
            vms.sort( (vm1, vm2) -> vm1.getMessage().compareTo(vm2.getMessage()) );
            vms.forEach( w -> stringBuilder.append(" - ").append(w.getMessage()).append("\n") );
        }
        
        stringBuilder.append("\n## TODO nodes\n");
        stringBuilder.append(StreamSupport.stream(rnr.getModel().getDecisionGraph().nodes().spliterator(), true).filter(node -> (node instanceof ToDoNode))
                .map(n -> (ToDoNode)n).map(n->n.getId() + ": " + n.getTodoText()).collect(Collectors.joining("\n - ", " - ","")));
        
        if(hasFlag("f", args)) {
            List<String> noFlags = noFlags(args);
            if(noFlags.size() > 0){
                String filename = C.last(noFlags).split("\\.")[0] + ".md";
                Path outputPath = rnr.getModel().getMetadata().getModelDirectoryPath();
                Files.write(outputPath.resolve(filename), stringBuilder.toString().getBytes());
            } else {
                rnr.print("Please enter filename");
            }
        } else {
            rnr.print(stringBuilder.toString());
        }
    }
    
}
