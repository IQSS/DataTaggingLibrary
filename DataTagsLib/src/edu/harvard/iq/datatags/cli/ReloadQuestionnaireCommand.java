package edu.harvard.iq.datatags.cli;

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
        loadCmd.execute(rnr, Arrays.asList(rnr.getModel().getMetadata().getMetadataFile().toString()));
    }
    
}
