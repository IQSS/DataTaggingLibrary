package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.cli.commands.RestartCommand;
import edu.harvard.iq.datatags.cli.commands.CurrentTagsCommand;
import edu.harvard.iq.datatags.cli.commands.ShowNodeCommand;
import edu.harvard.iq.datatags.cli.commands.ReloadQuestionnaireCommand;
import edu.harvard.iq.datatags.cli.commands.ShowSlotCommand;
import edu.harvard.iq.datatags.cli.commands.CliCommand;
import edu.harvard.iq.datatags.cli.commands.AboutCommand;
import edu.harvard.iq.datatags.cli.commands.RunValidationsCommand;
import edu.harvard.iq.datatags.cli.commands.VisualizeDecisionGraphCommand;
import edu.harvard.iq.datatags.cli.commands.AskAgainCommand;
import edu.harvard.iq.datatags.cli.commands.OptimizeDecisionGraphCommand;
import edu.harvard.iq.datatags.cli.commands.StatisticsCommand;
import edu.harvard.iq.datatags.cli.commands.QuitCommand;
import edu.harvard.iq.datatags.cli.commands.ToggleDebugMessagesCommand;
import edu.harvard.iq.datatags.cli.commands.MatchResultToSequenceCommand;
import edu.harvard.iq.datatags.cli.commands.VisualizeTagSpaceCommand;
import edu.harvard.iq.datatags.cli.commands.LoadPolicyModelCommand;
import edu.harvard.iq.datatags.cli.commands.NewModelCommand;
import edu.harvard.iq.datatags.cli.commands.OpenInDesktopCommand;
import edu.harvard.iq.datatags.cli.commands.PrintRunTraceCommand;
import edu.harvard.iq.datatags.cli.commands.PrintStackCommand;
import edu.harvard.iq.datatags.io.StringMapFormat;
import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.RuntimeEngineStatus;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.runtime.listeners.RuntimeEngineTracingListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static final String LOGO
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
    
    BufferedReader reader;
    private final StringMapFormat dtFormat = new StringMapFormat();
    private final Map<String, CliCommand> commands = new HashMap<>();
    private final Map<String, String> shortcuts = new HashMap<>();
    private boolean printDebugMessages = false;
    private PolicyModel model;
    private RuntimeEngineTracingListener tracer;
    private final Parser<List<String>> cmdScanner = Scanners.many( c -> !Character.isWhitespace(c) ).source().sepBy( Scanners.WHITESPACES );
    private boolean restartFlag = true;
    
    
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
                new VisualizeTagSpaceCommand(), new PrintRunTraceCommand(), new LoadPolicyModelCommand(), 
                new NewModelCommand(), new OpenInDesktopCommand(),
                new RunValidationsCommand(), new MatchResultToSequenceCommand(), new StatisticsCommand(),
                new OptimizeDecisionGraphCommand()
        ).forEach(c -> commands.put(c.command(), c));
        
        // shortcuts
        shortcuts.put("q", "quit" );
        shortcuts.put("i", "about" );
        shortcuts.put("rr","reload" );
        shortcuts.put("r", "restart" );
        shortcuts.put("a", "ask" );
        
        if (System.console() == null) {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } else {
            reader = new BufferedReader( System.console().reader() );
        }
        
    }

    public void go() throws IOException {

        try {
            tracer = new RuntimeEngineTracingListener(new CliEngineListener());
            ngn.setListener(tracer);
            ngn.setModel(model);
            
            while (true) {
                try {
                    if (restartFlag && ngn.start()) {
                        restartFlag = false;
                        boolean goFlag = true;
                        while ( goFlag && ngn.getStatus() == RuntimeEngineStatus.Running) {
                            Answer userAnswer = promptUserForAnswer();
                            if ( userAnswer != null ) {
                                goFlag = ngn.consume( userAnswer );
                            } else {
                                // this happens when, e.g. interview is restarted.
                                goFlag = false;
                            }
                            println("");
                        }
                        
                    } else {
                        promptForCommand();
                    }
                            
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

    public void restart() {
        if ( ngn.getStatus() == RuntimeEngineStatus.Running ) {
            tracer.runTerminated(ngn);
            ngn.setIdle();
        }
        if ( tracer != null ) {
            tracer.clear();
        }
        restartFlag = true;        
    }

    public void dumpTagValue(TagValue val) {
        dtFormat.format(val).entrySet().forEach((e) -> {
            println("%s = %s", e.getKey(), e.getValue());
        });
    }

    Answer promptUserForAnswer() throws IOException {

        printCurrentAskNode();

        String ansText;
        while ( (!restartFlag) && ((ansText = readLine("answer (? for help): ")) != null) ) {
            ansText = ansText.trim();
            if ( ansText.isEmpty() ) continue;
            
            Answer ans = Answer.get(ansText);
            if ((ngn.getCurrentNode() instanceof AskNode)
                    && (((AskNode) ngn.getCurrentNode()).getAnswers().contains(ans))) {
                return ans;

            } else if (ansText.equals("?")) {
                printHelp();

            } else if (ansText.startsWith("\\")) {
                try {
                    List<String> args = cmdScanner.parse( ansText );
                    String commandString = args.get(0).substring(1);
                    commandString = shortcuts.getOrDefault(commandString, commandString);
                    commands.getOrDefault(commandString, COMMAND_NOT_FOUND).execute(this, args);
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
            printHelp();

        } else {
            if ( userChoice.startsWith("\\")) {
                userChoice = userChoice.substring(1);
                userChoice = shortcuts.getOrDefault(userChoice, userChoice);
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

    private void printHelp() {
        println("Please type one of the following commands:"
                + "");
        commands.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEach(e -> println("\\%s: %s\n%s", e.getKey(), findShortcut(e.getKey()), indent(e.getValue().description())));
    }

    public void printCurrentAskNode() {
        AskNode ask = (AskNode) ngn.getCurrentNode();
        if (printDebugMessages) {
            printMsg("Question id: " + ask.getId());
        }
        println(ask.getText());
        if (!ask.getTermNames().isEmpty()) {
            println(" Terms:");
            ask.getTermOrder().forEach( termName -> {
                print(" * " + termName + ":\n");
                println("\t" + ask.getTermText(termName));
            });
        }
        println("Possible Answers:");
        ask.getAnswers().forEach(ans -> println(" - " + ans.getAnswerText()));
    }

    public void print(String format, Object... args) {
        if (System.console() != null) {
            System.console().printf(format, args);
        } else {
            System.out.print(String.format(format, args));
        }
    }

    public void print(String format) {
        if (System.console() != null) {
            System.console().printf(format);
        } else {
            System.out.print(String.format(format));
        }
    }

    public void println() {
        if (System.console() != null) {
            System.console().printf("\n");
        } else {
            System.out.println();
        }
    }

    public void println(String format, Object... args) {
        if (format.endsWith("\n")) {
            print(format, args);
        } else {
            print(format + "\n", args);
        }
    }

    public void println(String format) {
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
    public void printMsg(String format, Object... args) {
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
    public void printTitle(String format, Object... args) {
        String msg = String.format(format, args);

        char[] deco = new char[msg.length()];
        Arrays.fill(deco, '~');

        println("");
        println(new String(deco));
        println(msg);
        println(new String(deco));
    }

    public void debugPrint(String format, Object... args) {
        if ( printDebugMessages ) {
            String msg = String.format(format, args);

            char[] deco = new char[msg.length()];
            Arrays.fill(deco, '~');

            println("");
            println(new String(deco));
            println(msg);
            println(new String(deco));
        }
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

    public RuntimeEngine getEngine() {
        return ngn;
    }

    public void setPrintDebugMessages(boolean debugMessages) {
        printDebugMessages = debugMessages;
    }

    public boolean getPrintDebugMessages() {
        return printDebugMessages;
    }

    public List<Node> getTrace() {
        return tracer.getVisitedNodes();
    }

    private String findShortcut( String fullCommand ) {
        if ( shortcuts.containsValue(fullCommand) ) {
            return "(\\" + shortcuts.entrySet().stream()
                    .filter( e->e.getValue().equals(fullCommand) )
                    .map( Map.Entry::getKey )
                    .collect(Collectors.toList()).get(0) + ")";
        } else {
            return "";
        }
    } 

    public void setModel(PolicyModel aModel) {
        model = aModel;
        ngn.setModel(model);
    }

    public PolicyModel getModel() {
        return model;
    }
    
    
    private class CliEngineListener implements RuntimeEngine.Listener {

        public CliEngineListener() {}

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
                }

                @Override
                public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                    printMsg("TODO: " + nd.getTodoText());
                }

                @Override
                public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
                }
                
                @Override
                public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {}
                
                @Override
                public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                printMsg("Started section " + nd.getTitle() );
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

        @Override
        public void sectionStarted(RuntimeEngine ngn, Node node) {
            if (printDebugMessages) {
                printMsg("Started section");
            }
        }

        @Override
        public void sectionEnded(RuntimeEngine ngn, Node node) {
            if (printDebugMessages) {
                printMsg("Finished section");
            }
        }
    }

}
