package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.tools.NodeValidationMessage;
import edu.harvard.iq.datatags.tools.UnreachableNodeValidator;
import edu.harvard.iq.datatags.tools.UnusedTagsValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import java.util.List;

/**
 * Runs validation on the current questionnaire.
 * @author michael
 */
public class RunValidationsCommand implements CliCommand {

    @Override
    public String command() {
        return "validate";
    }

    @Override
    public String description() {
        return "Runs validations on the loaded questionnaire";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.printTitle("Running Validations");
        
        rnr.print("Checking for unreachable nodes");
        UnreachableNodeValidator unv = new UnreachableNodeValidator();
        List<ValidationMessage> unm = unv.validate(rnr.getModel().getDecisionGraph());
        if ( unm.isEmpty() ) {
            rnr.println(" [ok]");
        } else {
            rnr.println(" [warning]");
            rnr.println("Unreachable nodes found:");
            unm.forEach( w -> rnr.println(" - %s", ((NodeValidationMessage)w).getEntities()) );
        }
        
        rnr.print("Checking for unused tags");
        UnusedTagsValidator utv = new UnusedTagsValidator();
        List<ValidationMessage> vms = utv.validateUnusedTags(rnr.getModel());
        
        if ( vms.isEmpty() ) {
            rnr.println(" [ok]");
        } else {
            rnr.println(" [warning]");
            rnr.println("Unused tags found:");
            vms.sort( (vm1, vm2) -> vm1.getMessage().compareTo(vm2.getMessage()) );
            vms.forEach( w -> rnr.println(" - %s", w.getMessage()) );
        }
        
    }
    
}
