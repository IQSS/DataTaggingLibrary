package edu.harvard.iq.policymodels.cli;

import edu.harvard.iq.policymodels.cli.commands.RestartCommand;
import edu.harvard.iq.policymodels.cli.commands.ShowCurrentValueCommand;
import edu.harvard.iq.policymodels.cli.commands.ShowNodeCommand;
import edu.harvard.iq.policymodels.cli.commands.ReloadModelCommand;
import edu.harvard.iq.policymodels.cli.commands.ShowSlotCommand;
import edu.harvard.iq.policymodels.cli.commands.CliCommand;
import edu.harvard.iq.policymodels.cli.commands.AboutCommand;
import edu.harvard.iq.policymodels.cli.commands.RunValidationsCommand;
import edu.harvard.iq.policymodels.cli.commands.VisualizeDecisionGraphCommand;
import edu.harvard.iq.policymodels.cli.commands.AskAgainCommand;
import edu.harvard.iq.policymodels.cli.commands.CreateLocalizationCommand;
import edu.harvard.iq.policymodels.cli.commands.LoadLocalizationCommand;
import edu.harvard.iq.policymodels.cli.commands.OptimizeDecisionGraphCommand;
import edu.harvard.iq.policymodels.cli.commands.StatisticsCommand;
import edu.harvard.iq.policymodels.cli.commands.QuitCommand;
import edu.harvard.iq.policymodels.cli.commands.ToggleDebugMessagesCommand;
import edu.harvard.iq.policymodels.cli.commands.MatchResultToSequenceCommand;
import edu.harvard.iq.policymodels.cli.commands.VisualizePolicySpaceCommand;
import edu.harvard.iq.policymodels.cli.commands.LoadPolicyModelCommand;
import edu.harvard.iq.policymodels.cli.commands.NewModelCommand;
import edu.harvard.iq.policymodels.cli.commands.OpenInDesktopCommand;
import edu.harvard.iq.policymodels.cli.commands.PrintRunTraceCommand;
import edu.harvard.iq.policymodels.cli.commands.PrintStackCommand;
import edu.harvard.iq.policymodels.cli.commands.TodoCommand;
import edu.harvard.iq.policymodels.cli.commands.UpdateLocalizationCommand;
import edu.harvard.iq.policymodels.io.StringMapFormat;
import edu.harvard.iq.policymodels.model.PolicyModel;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.AskNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.CallNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.EndNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.RejectNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.SetNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ToDoNode;
import edu.harvard.iq.policymodels.model.decisiongraph.Answer;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ConsiderNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ContinueNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.PartNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.SectionNode;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
import edu.harvard.iq.policymodels.runtime.RuntimeEngine;
import edu.harvard.iq.policymodels.runtime.RuntimeEngineStatus;
import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.policymodels.runtime.listeners.RuntimeEngineTracingListener;
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
import org.jparsec.Parser;
import org.jparsec.Scanners;

/**
 * A command-line application that executes decision graphs.
 *
 * @author michael
 */
public class CliRunner {

    public static final String LOGO_OLD
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

    public static final String LOGO=
            " ____         _  _               __  __             _        _      \n" +
            "|  _ \\  ___  | |(_)  ___  _   _ |  \\/  |  ___    __| |  ___ | | ___ \n" +
            "| |_) |/ _ \\ | || | / __|| | | || |\\/| | / _ \\  / _` | / _ \\| |/ __|\n" +
            "|  __/| (_) || || || (__ | |_| || |  | || (_) || (_| ||  __/| |\\__ \\\n" +
            "|_|    \\___/ |_||_| \\___| \\__, ||_|  |_| \\___/  \\__,_| \\___||_||___/\n" +
            "              datatags.org|___/                                     ";
    
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

        @Override
        public boolean requiresModel() {
            return false;
        }
        
    };

    public CliRunner() {
        // Register commands here
        Arrays.asList(new ShowCurrentValueCommand(), new AboutCommand(),
                new QuitCommand(), new ToggleDebugMessagesCommand(), new ShowNodeCommand(),
                new PrintStackCommand(), new RestartCommand(), new ReloadModelCommand(),
                new AskAgainCommand(), new ShowSlotCommand(), new VisualizeDecisionGraphCommand(),
                new VisualizePolicySpaceCommand(), new PrintRunTraceCommand(), new LoadPolicyModelCommand(), 
                new NewModelCommand(), new OpenInDesktopCommand(), new LoadLocalizationCommand(),
                new RunValidationsCommand(), new MatchResultToSequenceCommand(), new StatisticsCommand(),
                new CreateLocalizationCommand(), new OptimizeDecisionGraphCommand(), new TodoCommand(), new UpdateLocalizationCommand()
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
            
            while (true) {
                try {
                    if ( (getModel() != null) && restartFlag && ngn.start()) {
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
                    restartFlag = false;
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

    public void dumpTagValue(AbstractValue val) {
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
            
            Answer ans = Answer.withName(ansText);
            if ((ngn.getCurrentNode() instanceof AskNode)
                    && (((AskNode) ngn.getCurrentNode()).getAnswers().contains(ans))) {
                return ans;

            } else if (ansText.startsWith("?")) {
                printHelp(ansText.substring(1).trim());

            } else if (ansText.startsWith("\\")) {
                try {
                    List<String> args = cmdScanner.parse( ansText );
                    String commandString = args.get(0).substring(1);
                    commandString = shortcuts.getOrDefault(commandString, commandString).trim();
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
        if ( getModel()==null ) {
            printWarning("No model loaded! Use \\load, or \\new to create one.");
        }
        String userChoice = readLine("Command (? for help): ");
        if ( userChoice == null ) return;
        userChoice = userChoice.trim();
        if ( userChoice.isEmpty() ) return;
            
        if ( userChoice.startsWith("?")) {
            printHelp(userChoice.substring(1));

        } else {
            if ( userChoice.startsWith("\\")) {
                userChoice = userChoice.substring(1);
                userChoice = shortcuts.getOrDefault(userChoice, userChoice); // expand abbreviations
            }
            try {
                List<String> args = cmdScanner.parse(userChoice);
                CliCommand selectedCommand = commands.getOrDefault(args.get(0), COMMAND_NOT_FOUND);
                if ( selectedCommand.requiresModel() && (getModel()==null) ) {
                    printWarning("Command %s requires a model. Currently, no model is loaded.", selectedCommand.command());
                } else {
                    selectedCommand.execute(this, args);
                }
                println("");

            } catch (Exception ex) {
                printWarning("Error executing the command: " + ex.getMessage());
                if ( printDebugMessages ) {
                    Logger.getLogger(CliRunner.class.getName()).log(Level.SEVERE, "Java stack trace:", ex);
                }
            }
        } 
    }

    private void printHelp(String commandName) {
        commandName = commandName.trim();
        if ( commandName.isEmpty() ) {
            println("Please type one of the commands below. Type ? <command> for help on a specific command."
                + "");
            commands.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                    .forEach(e -> println(" %s %s", e.getKey(), findShortcut(e.getKey())));
        } else {
            CliCommand cmd = commands.get(commandName);
            if (cmd == null ) {
                printWarning("Command '%s' not found.", commandName);
            } else {
                printTitle(commandName + " " + findShortcut(commandName));
                println(cmd.description());
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
                    println("Interview terminated: " + nd.getReason());
                }

                @Override
                public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                }

                @Override
                public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                    printMsg("TODO: " + nd.getTodoText());
                }

                @Override
                public void visitImpl(EndNode nd) throws DataTagsRuntimeException {}
                
                @Override
                public void visitImpl(ContinueNode nd) throws DataTagsRuntimeException {}
                
                @Override
                public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {}
                
                @Override
                public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                    printMsg("Started section " + nd.getTitle() );
                }

                @Override
                public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
                    printMsg("Started part " + nd.getId());
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
                dumpTagValue(ngn.getCurrentValue());
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

        @Override
        public void partStarted(RuntimeEngine ngn, Node node) {
            if (printDebugMessages) {
                printMsg("Started part");
            }
        }

        @Override
        public void partEnded(RuntimeEngine ngn, Node node) {
            if (printDebugMessages) {
                printMsg("Finished part");
            }
        }
    }

}
