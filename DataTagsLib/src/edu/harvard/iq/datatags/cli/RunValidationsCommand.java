package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.tools.NodeValidationMessage;
import edu.harvard.iq.datatags.tools.UnreachableNodeValidator;
import edu.harvard.iq.datatags.tools.UnusedTagsValidator;
import edu.harvard.iq.datatags.tools.ValidCallNodeValidator;
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
        
        List<NodeValidationMessage> nvms;

        rnr.print("Validating call nodes");
        ValidCallNodeValidator vcnv = new ValidCallNodeValidator();
        nvms = vcnv.validateIdReferences( rnr.getDecisionGraph() );
        if ( nvms.isEmpty() ) {
            rnr.println(" [ok]");
        } else {
            rnr.println(" [error]");
            rnr.print("Invalid call nodes found:");
            nvms.forEach( w -> rnr.println(" - %s", w.getEntities()) );
        }
        
        rnr.print("Checking for unreachable nodes");
        UnreachableNodeValidator unv = new UnreachableNodeValidator();
        nvms = unv.validateUnreachableNodes(rnr.getDecisionGraph());
        if ( nvms.isEmpty() ) {
            rnr.println(" [ok]");
        } else {
            rnr.println(" [warning]");
            rnr.println("Unreachable nodes found:");
            nvms.forEach( w -> rnr.println(" - %s", w.getEntities()) );
        }
        
        rnr.print("Checking for unused tags");
        UnusedTagsValidator utv = new UnusedTagsValidator();
        List<ValidationMessage> vms = utv.validateUnusedTags(rnr.getDecisionGraph());
        
        if ( vms.isEmpty() ) {
            rnr.println(" [ok]");
        } else {
            rnr.println(" [warning]");
            rnr.println("Unused tags found:");
            vms.forEach( w -> rnr.println(" - %s", w.getMessage()) );
        }
        
    }
    
}
