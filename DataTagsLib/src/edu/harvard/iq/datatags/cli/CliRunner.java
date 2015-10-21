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
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A command-line application that executes decision graphs.
 * @author michael
 */
public class CliRunner {

    private DecisionGraph decisionGraph;
    RuntimeEngine ngn;
    BufferedReader reader;
    private final StringMapFormat dtFormat = new StringMapFormat();
    private final Map<String, CliCommand> commands = new HashMap<>();
    private boolean printDebugMessages = false;
    
    private static final CliCommand COMMAND_NOT_FOUND = new CliCommand(){
        @Override
        public String command() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String description() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void execute(CliRunner rnr) throws Exception {
            rnr.printMsg("Command not found");
        }
    };

    public CliRunner() {
        Arrays.asList( new CurrentTagsCommand(), new AboutCommand(), new QuitCommand(), new ToggleDebugMessagesCommand() )
                .forEach( c -> commands.put(c.command(), c) );
    }
    
    public void go() throws IOException {
        println("Running questionnaire %s", decisionGraph.getSource());
        ngn = new RuntimeEngine();
        ngn.setDecisionGraph(decisionGraph);

        try {
            if (System.console() == null) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            }

            ngn.setListener(new RuntimeEngine.Listener() {

                @Override
                public void runStarted(RuntimeEngine ngn) {
                    printMsg("Run Started");
                }

                @Override
                public void processedNode(RuntimeEngine ngn, Node node) {
                    if ( printDebugMessages ) {
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
                public void runTerminated(RuntimeEngine ngn) {
                    if ( printDebugMessages ) {
                        printMsg("Run Done");
                    }
                }

            });

            if (ngn.start()) {
                while (ngn.consume(getAnswer())) {
                }
            }

            printTitle("Final Tags:");
            dumpTagValue(ngn.getCurrentTags());

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    void dumpTagValue(TagValue val) {
        dtFormat.format(val).entrySet().forEach((e) -> {
            print("%s = %s", e.getKey(), e.getValue());
        });
    }

    Answer getAnswer() throws IOException {
        AskNode ask = (AskNode) ngn.getCurrentNode();
        if ( printDebugMessages ) {
            printMsg( "Question id: " + ask.getId() );
        }
        println( ask.getText());
        if (!ask.getTermNames().isEmpty()) {
            println(" Terms:");
            for (String termName : ask.getTermNames()) {
                print(" * " + termName + ":\n");
                println("\t" + ask.getTermText(termName));
            }
        }
        println("Possible Answers:");
        ask.getAnswers().forEach( ans -> println(" - " + ans.getAnswerText()) );

        String ansText;
        while ((ansText = readLine("answer (? for help): ")) != null) {
            Answer ans = Answer.Answer(ansText);
            if (ask.getAnswers().contains(ans)) {
                return ans;
            } else if (ansText.equals("?")) {
                println("Type one of the answers listed above, or one of the following commands:?"
                        + "");
                commands.entrySet().stream().sorted( (e1, e2) -> e1.getKey().compareTo(e2.getKey()) )
                        .forEach( e -> println("\\%s:\n\t%s", e.getKey(), e.getValue().description()));
            } else if ( ansText.startsWith("\\") ) {
                try {
                    commands.getOrDefault(ansText.substring(1), COMMAND_NOT_FOUND).execute(this);
                } catch (Exception ex) {
                    Logger.getLogger(CliRunner.class.getName()).log(Level.SEVERE, "Error executing command: " + ex.getMessage(), ex);
                }
            } else {
                printMsg("Sorry, '%s' is not a valid answer. Please try again.", ansText);
            }
        }

        return null;
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

    void printMsg(String format, Object... args) {
        println("# " + format, args);
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

    void printTitle(String format, Object... args) {
        String msg = String.format(format, args);
        println(msg);
        char[] deco = new char[msg.length()];
        Arrays.fill(deco, '-');
        println(new String(deco));
    }

    private String readLine(String format, Object... args) throws IOException {
        if (System.console() != null) {
            return System.console().readLine(format, args);
        }
        System.out.print(String.format(format, args));
        return reader.readLine();
    }

    public DecisionGraph getDecisionGraph() {
        return decisionGraph;
    }

    public void setDecisionGraph(DecisionGraph fcs) {
        this.decisionGraph = fcs;
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
    
}
