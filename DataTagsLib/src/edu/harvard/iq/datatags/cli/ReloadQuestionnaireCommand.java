package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.runtime.RuntimeEngineStatus;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public void execute(CliRunner rnr, List<String> args)  {
        
        Path tsPath = rnr.getTagSpacePath();
        if ( ! Files.exists(tsPath) ) {
            rnr.printWarning("Tag Space file '%s' moved.", tsPath);
            return;
        }
        Path dgPath = rnr.getDecisionGraphPath();
        if ( ! Files.exists(dgPath) ) {
            rnr.printWarning("Decision graph file '%s' moved.", dgPath);
            return;
        }
        
        CompoundType ts;
        try {
            rnr.println("reloading:\n* %s\n* %s", tsPath, dgPath);
            ts = new TagSpaceParser().parse(tsPath).buildType("DataTags").get();
            DecisionGraph dg = new DecisionGraphParser().parse(dgPath).compile(ts);
            rnr.println("");
            rnr.setDecisionGraph(dg);
            rnr.restart();
            if ( rnr.getEngine().getStatus() == RuntimeEngineStatus.Running ) {
                rnr.printCurrentAskNode();
            }
        } catch (IOException ex) {
            rnr.printWarning("Error reading files: " + ex.getMessage());
        } catch (SyntaxErrorException ex) {
            rnr.printWarning("Syntax error in tag space definitions: %s", ex.getMessage());
        } catch (SemanticsErrorException ex) {
            rnr.printWarning("Semantic error in tag space definitions: %s", ex.getMessage());
        } catch (DataTagsParseException ex) {
            rnr.printWarning("Error in tag decision graph: %s", ex.getMessage());
        }
        
    }
    
}
