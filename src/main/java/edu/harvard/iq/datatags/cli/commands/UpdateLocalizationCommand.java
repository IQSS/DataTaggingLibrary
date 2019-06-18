/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.externaltexts.FsLocalizationIO;
import edu.harvard.iq.datatags.externaltexts.LocalizationLoader;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ContinueNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.PartNode;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.ToDoSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.util.StringHelper.nonEmpty;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import java.util.stream.StreamSupport;

/**
 *
 * @author mor
 */
public class UpdateLocalizationCommand extends AbstractCliCommand {
    private String currLocName;
    private Path currLocDir;

    public UpdateLocalizationCommand() {
        super("loc-update", "Update all existing localization");
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.getModel().getLocalizations().forEach(loc-> {
                currLocName = ((String)loc).replaceAll("\\\\", "_").replaceAll("/", "_");
                currLocDir = rnr.getModel().getDirectory()
                                            .resolve(FsLocalizationIO.LOCALIZATION_DIRECTORY_NAME)
                                            .resolve(currLocName);
                if ( !Files.exists(currLocDir) ) {
                    rnr.printWarning("Try to update non-existing localization: '%s'", currLocName);
                    currLocDir = null;
                    currLocName = null;
                    return;
                }
                rnr.println("Start update localization name: %s", currLocName);
                rnr.println("-----");
                updateAnswerFile(rnr);
                updatePolicySpace(rnr);
                updateNodeFiles(rnr);
                rnr.println("-----");
                
        });
       
    }
    
    public void updateAnswerFile(CliRunner rnr) {
        rnr.println("Update answer file");
        Path answersFile = currLocDir.resolve(FsLocalizationIO.ANSWERS_FILENAME);
        Set<String> newAnswers = new TreeSet<>();
        DecisionGraph decisionGraph = rnr.getModel().getDecisionGraph();
        for ( Node nd : decisionGraph.nodes() ) {
            if ( nd instanceof AskNode ) {
                AskNode ask = (AskNode) nd;
                newAnswers.addAll( ask.getAnswers().stream()
                                   .map(a->a.getAnswerText()).collect(toSet()));
            }
        }
        Set<String> oldAnswers;
        try {
            BufferedReader br = Files.newBufferedReader(answersFile);
            oldAnswers = br.lines().map(a -> a.split(":")[0]).collect(Collectors.toSet());
            Set<String> needToAdd = newAnswers.stream().filter(n -> !oldAnswers.contains(n)).collect(Collectors.toSet());
            Set<String> needToRemove = oldAnswers.stream().filter(n -> !newAnswers.contains(n)).collect(Collectors.toSet());
            try ( BufferedWriter bwrt = new BufferedWriter(new FileWriter(answersFile.toFile(), true));
                PrintWriter prt = new PrintWriter(bwrt) ){
                List<String> orderedAnswers = new ArrayList<>(needToAdd);
                Collections.sort(orderedAnswers);
                orderedAnswers.forEach( ans -> 
                    prt.println( ans + ": " + ans )
                );
            }
            rnr.println("...Done!");
            rnr.println("Answers that were added:");
            rnr.println(StreamSupport.stream(needToAdd.spliterator(), true).collect(Collectors.joining("\n - ", " - ","")));
            rnr.println("Answers that need to remove:");
            rnr.println(StreamSupport.stream(needToRemove.spliterator(), true).collect(Collectors.joining("\n - ", " - ","")));
        } catch (IOException ex) {
            Logger.getLogger(UpdateLocalizationCommand.class.getName()).log(Level.SEVERE, "Error reading localized answers", ex);
        }
    }
    
    public void updatePolicySpace(CliRunner rnr) {
        BufferedReader br = null;
        rnr.println("Update policy space file");
        try {
            Path psFile = currLocDir.resolve(FsLocalizationIO.SPACE_DATA_FILENAME);
            br = Files.newBufferedReader(psFile);
            Set<String> oldPS = br.lines().filter(l -> l.startsWith("# ")).map(l -> l.split("# ")[1]).collect(Collectors.toSet());
            Set<String> added = new TreeSet<>();
            try ( BufferedWriter bwrt = new BufferedWriter(new FileWriter(psFile.toFile(), true));
                    PrintWriter prt = new PrintWriter(bwrt) ){
                rnr.getModel().getSpaceRoot().accept(new AbstractSlot.VoidVisitor(){
                    LinkedList<String> stack = new LinkedList<>();
                    @Override
                    public void visitAtomicSlotImpl(AtomicSlot t) {
                        stack.push(t.getName());
                        String curPath = curPath();
                        if(!oldPS.contains(curPath)){
                            prt.println("# " + curPath );
                            prt.println( t.getName() );
                            prt.println(nonEmpty(t.getNote()) ? t.getNote() : "" );
                            prt.println();
                            added.add(curPath);
                        } else {
                            oldPS.remove(curPath);
                        }

                        t.values().forEach( v -> {
                            String currVal = curPath + "/" + v.getName();
                            if(!oldPS.contains(currVal)) {
                                prt.println("# " + currVal );
                                prt.println( v.getName() );
                                prt.println(nonEmpty(v.getNote()) ? v.getNote() : "" );
                                prt.println();
                                added.add(currVal);
                            } else {
                                oldPS.remove(currVal);
                            }
                        });
                        
                        stack.pop();
                    }
                    
                    @Override
                    public void visitAggregateSlotImpl(AggregateSlot t) {
                        stack.push(t.getName());
                        String curPath = curPath();
                        if(!oldPS.contains(curPath)) {
                            prt.println("# " + curPath );
                            prt.println( t.getName() );
                            prt.println(nonEmpty(t.getNote()) ? t.getNote() : "" );
                            prt.println();
                            added.add(curPath);
                        } else {
                            oldPS.remove(curPath);
                        }
                        t.getItemType().values().forEach( v -> {
                            String currVal = curPath + "/" + v.getName();
                            if(!oldPS.contains(currVal)) {
                                prt.println("# " + currVal );
                                prt.println( v.getName() );
                                prt.println(nonEmpty(v.getNote()) ? v.getNote() : "" );
                                prt.println();
                                added.add(currVal);
                            } else {
                                oldPS.remove(currVal);
                            }
                        });
                        
                        stack.pop();
                        
                    }
                    
                    @Override
                    public void visitCompoundSlotImpl(CompoundSlot t) {
                        stack.push(t.getName());
                        if(!oldPS.contains(curPath())) {
                            prt.println("# " + curPath() );
                            prt.println( t.getName() );
                            prt.println(nonEmpty(t.getNote()) ? t.getNote() : "" );
                            prt.println();
                            added.add(curPath());
                        } else {
                            oldPS.remove(curPath());
                        }

                        t.getSubSlots().forEach( ft -> ft.accept(this) );
                        
                        stack.pop();
                    }
                    
                    @Override
                    public void visitTodoSlotImpl(ToDoSlot t) {
                        
                    }
                    
                    private String curPath() {
                        List<String> now = new ArrayList<>(stack);
                        Collections.reverse(now);
                        return now.stream().collect( joining("/") );
                    }
                } );
            } catch (IOException ex) {
                Logger.getLogger(UpdateLocalizationCommand.class.getName()).log(Level.SEVERE, "Error writing to policy space file", ex);
            }
            rnr.println("...Done");
            rnr.println("slots/values that were added:");
            rnr.println(StreamSupport.stream(added.spliterator(), true).collect(Collectors.joining("\n - ", " - ","")));
            rnr.println("slots/values that need to remove:");
            rnr.println(StreamSupport.stream(oldPS.spliterator(), true).collect(Collectors.joining("\n - ", " - ","")));
        } catch (IOException ex) {
            Logger.getLogger(UpdateLocalizationCommand.class.getName()).log(Level.SEVERE, "Error reading policy space file", ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(UpdateLocalizationCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
    
        private void updateNodeFiles(CliRunner rnr) {
        try {
            rnr.println("Update node files");
            Set<Path> oldNodes = Files.walk(currLocDir.resolve(FsLocalizationIO.NODE_DIRECTORY_NAME))
                    .filter(Files::isRegularFile).map(f -> currLocDir.resolve(FsLocalizationIO.NODE_DIRECTORY_NAME).relativize(f))
                    .collect(Collectors.toSet());
            final Path nodesDir = currLocDir.resolve(FsLocalizationIO.NODE_DIRECTORY_NAME);
            if ( ! Files.exists(nodesDir) ) {
                Files.createDirectory(nodesDir);
            }
            Set<Path> addedNodes = new TreeSet<>();
            Map<String, Path> nodesToPaths = FsLocalizationIO.getNodesPath(StreamSupport
                    .stream(rnr.getModel().getDecisionGraph().nodes().spliterator(), true).collect(Collectors.toSet()));
            
            Node.Visitor writer = new Node.VoidVisitor() {
                
            @Override
            public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                StringBuilder sb = new StringBuilder();
                sb.append(nd.getText());
                if ( ! nd.getTermNames().isEmpty() ) {
                    sb.append("\n");
                    sb.append("\n");
                    sb.append("### Terms\n");
                    nd.getTermOrder().forEach( termName -> sb.append("* *").append(termName)
                                                             .append("*: ")
                                                             .append(nd.getTermText(termName))
                                                             .append("\n")
                    );
                }
                addedNodes.add(nodesToPaths.get(nd.getId()));
                FsLocalizationIO.createNodeLocalizationFile(nodesDir, nodesToPaths.get(nd.getId()), sb.toString());
            }

            @Override
            public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                addedNodes.add(nodesToPaths.get(nd.getId()));
                FsLocalizationIO.createNodeLocalizationFile(nodesDir, nodesToPaths.get(nd.getId()), nd.getTitle());
            }

            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                    addedNodes.add(nodesToPaths.get(nd.getId()));
                    FsLocalizationIO.createNodeLocalizationFile(nodesDir, nodesToPaths.get(nd.getId()), nd.getReason());
            }

            @Override
            public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                addedNodes.add(nodesToPaths.get(nd.getId()));
                FsLocalizationIO.createNodeLocalizationFile(nodesDir, nodesToPaths.get(nd.getId()), nd.getTodoText());
            }

            @Override
            public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
                    addedNodes.add(nodesToPaths.get(nd.getId()));
                    FsLocalizationIO.createNodeLocalizationFile(nodesDir, nodesToPaths.get(nd.getId()), nd.getTitle());
            }

            @Override public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {}
            @Override public void visitImpl(SetNode nd)      throws DataTagsRuntimeException {}
            @Override public void visitImpl(CallNode nd)     throws DataTagsRuntimeException {}
            @Override public void visitImpl(EndNode nd)      throws DataTagsRuntimeException {}
            @Override public void visitImpl(ContinueNode nd) throws DataTagsRuntimeException {}
        };
            StreamSupport.stream(rnr.getModel().getDecisionGraph().nodes().spliterator(), true)
                    .filter((node) -> !AstNodeIdProvider.isAutoId(node.getId()))
                    .filter(node -> !oldNodes.contains(nodesToPaths.get(node.getId()))).forEach(n -> n.accept(writer));
        rnr.println("..Done");
        rnr.println("nodes that were added:");
            rnr.println(StreamSupport.stream(addedNodes.spliterator(), true).map(p-> p.toString()).collect(Collectors.joining("\n - ", " - ","")));
        rnr.println("nodes that need to remove:");
        rnr.println(oldNodes.stream().filter(node -> !nodesToPaths.values().contains(node)).map(n -> n.normalize().toString()).collect(Collectors.joining("\n - ", " - ","")));
        } catch (IOException ex) {
            Logger.getLogger(UpdateLocalizationCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}
