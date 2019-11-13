package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.tools.processors.EagerSetCallsOptimizer;
import edu.harvard.iq.policymodels.tools.processors.EndNodeOptimizer;
import java.util.List;
import edu.harvard.iq.policymodels.tools.processors.DecisionGraphProcessor;

/**
 * Runs an optimizer on the decision graph.
 * @author michael
 */
public class OptimizeDecisionGraphCommand implements CliCommand {

    @Override
    public String command() {
        return "optimize";
    }

    @Override
    public String description() {
        return "Runs various optimizers on the decision graph.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.printTitle("Available Optimizers");
        rnr.println("1. End node unification");
        rnr.println("    Unify all [end] nodes with no id. Saves memory.");
        rnr.println("2. Early [set]");
        rnr.println("    Pulls [set] calls as early in the decision graph");
        rnr.println("    as possible.");
        
        String res = rnr.readLine("Please select a number: ");
        res = res.trim();
        if ( res.isEmpty() ) {
            return;
        }
        
        DecisionGraphProcessor sel = null;
        
        if ( res.equals("1") ) {
            sel = new EndNodeOptimizer();
        } else if ( res.equals("2") ) {
            sel = new EagerSetCallsOptimizer();
        }
        if ( sel != null ) {
            // optimize using the selected optimizer.
            rnr.getModel().setDecisionGraph(sel.process(rnr.getModel().getDecisionGraph()));
        } else {
            rnr.printWarning("%s is not available", res);
        }
    }
    
}
