package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.runtime.RuntimeEngineStatus;
import edu.harvard.iq.datatags.tools.EagerSetCallsOptimizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Loads a questionnaire to the CliRunner.
 *
 * @author michael
 */
public class LoadQuestionnaireCommand implements CliCommand {

    @Override
    public String command() {
        return "load";
    }

    @Override
    public String description() {
        return "Loads a new questionnaire.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        Path tsPath = null;
        Path dgPath = null;
        String inputString;

        boolean loaded = false;
        do {
            // get the files
            do {
                final String tsFilePrompt = "Please enter path to the definitions (tagspace) file: ";
                if ( tsPath == null ) {
                    inputString = rnr.readLine(tsFilePrompt).trim();
                } else {
                    inputString = rnr.readLineWithDefault(tsFilePrompt, tsPath.toString());
                }
                tsPath = Paths.get(inputString);
                if (!Files.exists(tsPath)) {
                    rnr.printWarning(inputString + " does not exist.");
                    tsPath = null;
                }
            } while (tsPath == null);
            
            if ( dgPath != null ) {
                inputString = dgPath.toString();
            } else if (inputString.endsWith(".ts")) {
                inputString = inputString.substring(0, inputString.length() - 2) + "dg";
            }

            do {
                inputString = rnr.readLineWithDefault("Please enter path to decision graph ", inputString).trim();
                dgPath = Paths.get(inputString);
                if (!Files.exists(dgPath)) {
                    rnr.printWarning(inputString + " does not exist.");
                    dgPath = null;
                }
            } while (dgPath == null);
        
            try {
                CompoundType ts;
                rnr.println("Reading Tag Space:");
                rnr.println( tsPath.toRealPath().toString() );
                ts = new TagSpaceParser().parse(tsPath).buildType("DataTags").get();

                rnr.println("Reading Decision Graph:");
                rnr.println( dgPath.toRealPath().toString() );
                DecisionGraph dg = new DecisionGraphParser().parse(dgPath).compile(ts);
                
                rnr.setDecisionGraph(dg);
                rnr.setDecisionGraphPath(dgPath);
                rnr.setTagSpacePath(tsPath);

                if ( rnr.getEngine().getStatus() == RuntimeEngineStatus.Running ) {
                    rnr.restart();
                    rnr.printCurrentAskNode();
                } else {
                    rnr.getEngine().setIdle();
                }
                loaded = true;
                
            } catch (IOException ex) {
                rnr.printWarning("Error reading file: %s", ex.getMessage());
            } catch (SyntaxErrorException ex) {
                rnr.printWarning("Syntax Error: " + ex.getMessage() );
            } catch (SemanticsErrorException ex) {
                rnr.printWarning("Semantics Error: " + ex.getMessage() );
            } catch (DataTagsParseException ex) {
                rnr.printWarning("Error parsing decision graph: " + ex.getMessage() );
            }
        } while ( ! loaded );
    }

}
