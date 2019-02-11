package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import java.util.Arrays;
import java.util.List;

/**
 * Reloads the questionnaire. Current interview is terminated.
 * @author michael
 */
public class ReloadQuestionnaireCommand implements CliCommand {

    @Override
    public String command() {
        return "reload";
    }

    @Override
    public String description() {
        return "Reloads the questionnaire. Current interview is terminated.";    
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception  {
        LoadPolicyModelCommand loadCmd = new LoadPolicyModelCommand();
        String modelPath = rnr.getModel().getMetadata().getMetadataFile().toString();
        rnr.println("Reloading %s", modelPath);
        loadCmd.execute(rnr, Arrays.asList("/dummy", modelPath));
    }
    
}
