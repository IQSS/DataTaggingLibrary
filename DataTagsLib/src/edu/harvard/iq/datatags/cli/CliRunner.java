package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.io.StringMapFormat;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.RuntimeEngineStatus;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.runtime.listeners.RuntimeEngineTracingListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;

/**
 * A command-line application that executes decision graphs.
 *
 * @author michael
 */
public class CliRunner {

    static final String LOGO
            = "   +-------------\n"
            + "  +|             \\\n"
            + " +||             o)\n"
            + "+|||             /\n"
            + "|||+-------------\n"
            + "||+-------------\n"
            + "|+------------- ____        _        _____\n"
            + "+------------- |  _ \\  __ _| |_  __ |_   _|_ _  __ _  ___\n"
            + "               | | | |/ _` | __|/ _` || |/ _` |/ _` |/ __|\n"
            + "               | |_| | (_| | |_ |(_| || | (_| | (_| |\\__ \\\n"
            + "               |____/ \\__,_|\\__|\\__,_||_|\\__,_|\\__, ||___/\n"
            + "                                   datatags.org|___/\n";

    RuntimeEngine ngn = new RuntimeEngine();
    ;
    BufferedReader reader;
    private final StringMapFormat dtFormat = new StringMapFormat();
    private final Map<String, CliCommand> commands = new HashMap<>();
    private boolean printDebugMessages = false;
    private Path decisionGraphPath, tagSpacePath;
    private RuntimeEngineTracingListener tracer;
    private final Parser<List<String>> cmdScanner = Scanners.many( c -> !Character.isWhitespace(c) ).source().sepBy( Scanners.WHITESPACES );

    /**
     * A default command, in case a nonexistent command has been chosen.
     */
    private static final CliCommand COMMAND_NOT_FOUND = new CliCommand() {
        @Override
        public String command() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String description() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void execute(CliRunner rnr, List<String> args) throws Exception {
            rnr.printMsg("Command not found");
        }
    };

    public CliRunner() {
        // Register commands here
        Arrays.asList(new CurrentTagsCommand(), new AboutCommand(),
                new QuitCommand(), new ToggleDebugMessagesCommand(), new ShowNodeCommand(),
                new PrintStackCommand(), new RestartCommand(), new ReloadQuestionnaireCommand(),
                new AskAgainCommand(), new ShowSlotCommand(), new VisualizeDecisionGraphCommand(),
                new VisualizeTagSpaceCommand(), new PrintRunTraceCommand(), new LoadQuestionnaireCommand())
                .forEach(c -> commands.put(c.command(), c));
    }

    public void go() throws IOException {

        try {
            if (System.console() == null) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            }

            tracer = new RuntimeEngineTracingListener(new CliEngineListener());
            ngn.setListener(tracer);

            while (true) {
                try {
                    if (ngn.getStatus() == RuntimeEngineStatus.Idle && ngn.start()) {
                        while (ngn.getStatus() == RuntimeEngineStatus.Running
                                && ngn.consume(promptUserForAnswer())) {
                            println("");
                        }
                    }
                    promptForCommand();
                } catch ( DataTagsRuntimeException dtre ) {
                    printWarning("Engine runtime error: %s", dtre.getMessage());
                    if ( printDebugMessages ) {
                        dtre.printStackTrace( System.out );
                    }
                }
            }

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void printSplashScreen() {
        print(LOGO);
        println("");
    }

    void restart() {
        ngn.restart();
    }

    void dumpTagValue(TagValue val) {
        dtFormat.format(val).entrySet().forEach((e) -> {
            println("%s = %s", e.getKey(), e.getValue());
        });
    }

    Answer promptUserForAnswer() throws IOException {

        printCurrentAskNode();

        String ansText;
        while ((ansText = readLine("answer (? for help): ")) != null) {
            ansText = ansText.trim();
            if ( ansText.isEmpty() ) continue;
            
            Answer ans = Answer.Answer(ansText);
            if ((ngn.getCurrentNode() instanceof AskNode)
                    && (((AskNode) ngn.getCurrentNode()).getAnswers().contains(ans))) {
                return ans;

            } else if (ansText.equals("?")) {
                println("Type one of the answers listed above, or one of the following commands:"
                        + "");
                commands.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                        .forEach(e -> println("\\%s:\n%s", e.getKey(), indent(e.getValue().description())));

            } else if (ansText.startsWith("\\")) {
                try {
                    List<String> args = cmdScanner.parse( ansText );
                    commands.getOrDefault(args.get(0).substring(1), COMMAND_NOT_FOUND).execute(this, args);
                    println("");

                } catch (Exception ex) {
                    Logger.getLogger(CliRunner.class.getName()).log(Level.SEVERE, "Error executing command: " + ex.getMessage(), ex);
                }

            } else {
                printMsg("Sorry, '%s' is not a valid answer. Please try again.", ansText);
            }
        }

        return null;
    }

    /**
     * Prompts the user for a command, and then executes it. If the command
     * entails restarting the engine, it is conveyed by it changing the engine
     * state to {@link RuntimeEngineStatus#Idle}
     */
    void promptForCommand() throws IOException {
        String userChoice = readLine("Command (? for help): ");
        if ( userChoice == null ) return;
        userChoice = userChoice.trim();
        if ( userChoice.isEmpty() ) return;
            
        if ( userChoice.equals("?")) {
            println("Please type one of the following commands:"
                    + "");
            commands.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                    .forEach(e -> println("\\%s:\n%s", e.getKey(), indent(e.getValue().description())));

        } else {
            if ( userChoice.startsWith("\\")) {
                userChoice = userChoice.substring(1);
            }
            try {
                List<String> args = cmdScanner.parse(userChoice);
                commands.getOrDefault(args.get(0), COMMAND_NOT_FOUND).execute(this, args);
                println("");

            } catch (Exception ex) {
                printWarning("Error executing the command: " + ex.getMessage());
                if ( printDebugMessages ) {
                    Logger.getLogger(CliRunner.class.getName()).log(Level.SEVERE, "Java stack trace:", ex);
                }
            }

        } 
    }

    public void printCurrentAskNode() {
        AskNode ask = (AskNode) ngn.getCurrentNode();
        if (printDebugMessages) {
            printMsg("Question id: " + ask.getId());
        }
        println(ask.getText());
        if (!ask.getTermNames().isEmpty()) {
            println(" Terms:");
            for (String termName : ask.getTermNames()) {
                print(" * " + termName + ":\n");
                println("\t" + ask.getTermText(termName));
            }
        }
        println("Possible Answers:");
        ask.getAnswers().forEach(ans -> println(" - " + ans.getAnswerText()));
    }

    void print(String format, Object... args) {
        if (System.console() != null) {
            System.console().printf(format, args);
        } else {
            System.out.print(String.format(format, args));
        }
    }

    void print(String format) {
        if (System.console() != null) {
            System.console().printf(format);
        } else {
            System.out.print(String.format(format));
        }
    }

    void println() {
        if (System.console() != null) {
            System.console().printf("\n");
        } else {
            System.out.println();
        }
    }

    void println(String format, Object... args) {
        if (format.endsWith("\n")) {
            print(format, args);
        } else {
            print(format + "\n", args);
        }
    }

    void println(String format) {
        if (format.endsWith("\n")) {
            print(format);
        } else {
            print(format + "\n");
        }
    }

    /**
     * Prints to the console, formatted as a message.
     *
     * @param format
     * @param args
     */
    void printMsg(String format, Object... args) {
        println("# " + format, args);
    }

    /**
     * Prints to the console, formatted as a warning.
     *
     * @param format
     * @param args
     */
    public void printWarning(String format, Object... args) {
        println("# /!\\ " + format, args);
    }

    /**
     * Prints to the console, formatted as a warning.
     *
     * @param format
     */
    public void printWarning(String format) {
        println("# /!\\ " + format);
    }

    /**
     * Prints to the console, with underline.
     *
     * @param format
     * @param args
     */
    void printTitle(String format, Object... args) {
        String msg = String.format(format, args);

        char[] deco = new char[msg.length()];
        Arrays.fill(deco, '~');

        println("");
        println(new String(deco));
        println(msg);
        println(new String(deco));
    }

    public String readLine(String format, Object... args) throws IOException {
        if (System.console() != null) {
            return System.console().readLine(format, args);
        }
        System.out.print(String.format(format, args));
        return reader.readLine();
    }

    public String readLineWithDefault(String format, String defaultValue, Object... args) throws IOException {
        String userInput = readLine(format + "[" + defaultValue + "] ", args);
        return userInput.trim().isEmpty() ? defaultValue : userInput.trim();
    }

    public String indent(String lines) {
        return Stream.of(lines.split("\\n")).map(s -> "\t" + s).collect(Collectors.joining("\n"));
    }

    public String truncateAt(String src, int width) {
        if (src.length() > width) {
            return src.substring(0, width - 3) + "...";
        } else {
            return src;
        }
    }

    public DecisionGraph getDecisionGraph() {
        return ngn.getDecisionGraph();
    }

    public void setDecisionGraph(DecisionGraph aDecisionGraph) {
        ngn.setDecisionGraph(aDecisionGraph);
    }

    public RuntimeEngine getEngine() {
        return ngn;
    }

    public void setPrintDebugMessages(boolean debugMessages) {
        printDebugMessages = debugMessages;
    }

    public boolean getPrintDebugMessages() {
        return printDebugMessages;
    }

    public Path getDecisionGraphPath() {
        return decisionGraphPath;
    }

    public void setDecisionGraphPath(Path decisionGraphPath) {
        this.decisionGraphPath = decisionGraphPath;
    }

    public Path getTagSpacePath() {
        return tagSpacePath;
    }

    public void setTagSpacePath(Path tagSpacePath) {
        this.tagSpacePath = tagSpacePath;
    }

    public List<Node> getTrace() {
        return tracer.getVisitedNodes();
    }

    private class CliEngineListener implements RuntimeEngine.Listener {

        public CliEngineListener() {
        }

        @Override
        public void runStarted(RuntimeEngine ngn) {
            printMsg("Run Started");
        }

        @Override
        public void processedNode(RuntimeEngine ngn, Node node) {
            if (printDebugMessages) {
                printMsg("Visited node " + node);
            }
            node.accept(new Node.VoidVisitor() {

                @Override
                public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                }

                @Override
                public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                    printMsg("Updating tags");
                    dtFormat.format(nd.getTags()).entrySet().forEach(e
                            -> printMsg("%s = %s", e.getKey(), e.getValue())
                    );
                }

                @Override
                public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                    println("Sorry, we can't accept the dataset: " + nd.getReason());
                }

                @Override
                public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                    printTitle("Section: " + nd.getCalleeNodeId() + "");
                }

                @Override
                public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
                    printMsg("TODO: " + nd.getTodoText());
                }

                @Override
                public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
                }
            });
        }

        @Override
        public void statusChanged(RuntimeEngine ngn) {
            if (printDebugMessages) {
                printMsg("Status changed: %s", ngn.getStatus());
            }

            if (ngn.getStatus() == RuntimeEngineStatus.Accept) {
                printTitle("Final Tags");
                dumpTagValue(ngn.getCurrentTags());
            } else if (ngn.getStatus() == RuntimeEngineStatus.Error) {
                printWarning("Runtime engine in ERROR mode");
            }
        }

        @Override
        public void runTerminated(RuntimeEngine ngn) {
            if (printDebugMessages) {
                printMsg("Run Done");
            }
        }
    }

}
