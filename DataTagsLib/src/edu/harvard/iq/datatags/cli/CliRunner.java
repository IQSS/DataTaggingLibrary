package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.io.StringMapFormat;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public class CliRunner {
    
    private FlowChartSet fcs;
    RuntimeEngine ngn;
    BufferedReader reader;
    private final StringMapFormat dtFormat = new StringMapFormat();
    
    public void go() throws IOException {
        println( "Running questionnaire %s, (version %s)", fcs.getSource(), fcs.getVersion() );
        ngn = new RuntimeEngine();
        ngn.setChartSet(fcs);
        
        try {
            if ( System.console() == null ) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            }

            printTitle("Available Charts");
            for ( FlowChart fc : fcs.charts() ) {
                print( "%s (%s)\n", fc.getTitle(), fc.getId() );
            }

            String chartId = readLine("Select chart id [%s]", fcs.getDefaultChartId() );
            if ( chartId.isEmpty() ) {
                chartId = fcs.getDefaultChartId();
            }
            println("Selected chart is %s", chartId);

            ngn.setListener( new RuntimeEngine.Listener() {

                @Override
                public void runStarted(RuntimeEngine ngn) {
                    printMsg("Run Started");
                }

                @Override
                public void processedNode(RuntimeEngine ngn, Node node) {
                    printMsg("Visited node " + node );
                    node.accept( new Node.NullVisitor() {

                        @Override
                        public void visitImpl(AskNode nd) throws DataTagsRuntimeException {}

                        @Override
                        public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                            printMsg("Updating tags");
                            for ( Map.Entry<String, String> e : dtFormat.format(nd.getTags()).entrySet() ) {
                                printMsg("%s = %s", e.getKey(), e.getValue() );
                            }
                            
                        }

                        @Override
                        public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                            printMsg("Sorry, we can't accept the dataset: " + nd.getReason());
                        }

                        @Override
                        public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                            try {
                                printTitle("Section: " + nd.getCalleeNodeId() + "");
                            } catch (IOException ex) {
                                Logger.getLogger(CliRunner.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        @Override
                        public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
                            printMsg("TODO: " + nd.getTodoText());
                        }

                        @Override
                        public void visitImpl(EndNode nd) throws DataTagsRuntimeException {}
                    });
                }

                @Override
                public void runTerminated(RuntimeEngine ngn) {
                    printMsg("Run Done");
                }

                @Override
                public void runError(RuntimeEngine ngn, DataTagsRuntimeException e) {
                    printMsg("Run Error: " + e.getMessage());
                    e.printStackTrace( System.out );
                }
            });

            if ( ngn.start(chartId) ) {
                while ( ngn.consume( getAnswer() ) ) {}
            }
            
            printTitle("Final Tags:");
            dumpTagValue( ngn.getCurrentTags() );
            
        } finally {
            if ( reader != null ) {
                reader.close();
            }
        }
    }
    
    void dumpTagValue( TagValue val ) {
        for ( Map.Entry<String, String> e : dtFormat.format(val).entrySet() ) {
            printMsg("%s = %s", e.getKey(), e.getValue() );
        }
    }
    
    Answer getAnswer() throws IOException {
        AskNode ask = (AskNode) ngn.getCurrentNode();
        println( ask.getText() );
        if ( ! ask.getTermNames().isEmpty() ) {
            println(" Terms:");
            for ( String termName : ask.getTermNames() ) {
                print( " * " + termName + ":  " );
                println( ask.getTermText(termName) );
            }
        }
        println("Possible Answers:");
        for ( Answer ans: ask.getAnswers() ) {
            println( " - " + ans.getAnswerText() );
        }

        String ansText;
        while ( (ansText = readLine("answer: ")) != null ) {
            Answer ans = Answer.Answer(ansText);
            if ( ask.getAnswers().contains(ans) ) {
                return ans;
            } else {
                printMsg("Sorry, '%s' is not a valid answer. Please try again.", ansText);
            }
        }
        
        return null;
    }
    
    void print( String format, Object... args )  throws IOException {
        if ( System.console() != null ) {
            System.console().printf(format, args);
        } else {
            System.out.print(String.format(format, args));
        }
    }
    
    void printMsg( String format, Object... args ) {
        try {
            println("# " + format, args );
        } catch (IOException ex) {
            Logger.getLogger(CliRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void println( String format, Object... args ) throws IOException {
        if ( format.endsWith("\n") ) {
            print(format, args);
        } else {
            print( format + "\n", args );
        }
    }
    
    void printTitle( String format, Object... args ) throws IOException {
        String msg = String.format(format, args);
        println( msg );
        char[] deco = new char[msg.length()];
        Arrays.fill(deco, '-');
        println( new String(deco) );
    }
    
    private String readLine(String format, Object... args) throws IOException {
        if (System.console() != null) {
            return System.console().readLine(format, args);
        }
        System.out.print(String.format(format, args));
        return reader.readLine();
    }

    public FlowChartSet getFcs() {
        return fcs;
    }

    public void setFcs(FlowChartSet fcs) {
        this.fcs = fcs;
    }
    
}
