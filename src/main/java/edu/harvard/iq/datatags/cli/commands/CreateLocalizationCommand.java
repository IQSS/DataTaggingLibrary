package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.externaltexts.LocalizationLoader;
import edu.harvard.iq.datatags.model.PolicyModel;
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
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.slots.ToDoSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.util.StringHelper.nonEmpty;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;

/**
 * A command that creates a localization
 * @author michael
 */
public class CreateLocalizationCommand extends AbstractCliCommand {
    
    private final Pattern fieldDetector = Pattern.compile("\\$\\{[_A-Z]*\\}");
    
    private String localizationName = null;
    private Path localizationPath = null;
    
    public CreateLocalizationCommand() {
        super("loc-create", "Creates a new localization for the current model.");
    }
    
    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        
        if ( args.size()==1 ) {
            localizationName = rnr.readLine("Localization name:");
        } else {
            localizationName = args.get(1);
        }
        localizationName = localizationName.trim();
        
        // start new
        localizationPath = createLocalizationFolder(rnr);
        
        if ( localizationPath == null ) {
            rnr.println(" (localization creation canceled)");
            return;
        }
        
        createLocalizedModel(rnr);
        createAnswersFile(rnr);
        createReadmeFile(rnr);
        createPolicySpace(rnr);
        createNodeFiles(rnr);
        
        // clean up
        localizationName=null;
        localizationPath=null;
        
    }
    
    /**
     * Prompts the user for the localization name, and creates the folder for it.
     * @param rnr
     * @return The path to the new localization folder, or {@code null} if the user canceled.
     * @throws IOException 
     */
    private Path createLocalizationFolder( CliRunner rnr ) throws IOException {
        
        if ( localizationName.isEmpty() ) return null;
        localizationName = localizationName.replaceAll("\\\\", "_").replaceAll("/", "_");
        Path localizationsDir = rnr.getModel().getDirectory()
                                    .resolve(LocalizationLoader.LOCALIZATION_DIRECTORY_NAME)
                                    .resolve(localizationName);
        
        if ( Files.exists(localizationsDir) ) {
            rnr.printWarning("A localization named '%s' already exists.", localizationName);
            return null;
        }
        Files.createDirectories(localizationsDir);
        
        return localizationsDir;
    }

    private void createAnswersFile(CliRunner rnr) throws IOException {
        rnr.print(" - Creating answers file");
        
        Set<String> answers = new TreeSet<>();
        DecisionGraph decisionGraph = rnr.getModel().getDecisionGraph();
        for ( Node nd : decisionGraph.nodes() ) {
            if ( nd instanceof AskNode ) {
                AskNode ask = (AskNode) nd;
                answers.addAll( ask.getAnswers().stream()
                                   .map(a->a.getAnswerText()).collect(toSet()));
            }
        }
        rnr.print(".");
        try ( BufferedWriter bwrt = Files.newBufferedWriter(localizationPath.resolve(LocalizationLoader.ANSWERS_FILENAME));
              PrintWriter prt = new PrintWriter(bwrt) ){
            List<String> orderedAnswers = new ArrayList<>(answers);
            Collections.sort(orderedAnswers);
            orderedAnswers.forEach( ans -> 
                prt.println( ans + ": " + ans )
            );
        }
        rnr.println("..Done");
    }

    private void createReadmeFile(CliRunner rnr) throws IOException {
        rnr.print(" - Creating readme.md file");
        PolicyModel pm = rnr.getModel();
        try ( BufferedWriter bwrt = Files.newBufferedWriter(localizationPath.resolve("readme.md"));
              PrintWriter prt = new PrintWriter(bwrt) ){
            prt.println("# " + pm.getMetadata().getTitle() );
            String subTitle = pm.getMetadata().getSubTitle();
            if ( subTitle!=null && ! subTitle.trim().isEmpty() ) {
                prt.println("### " + subTitle );
            }
            prt.println();
            prt.println("__Version " + pm.getMetadata().getVersion() + "__");
            prt.println();
            prt.println("(add about text here)");
        }
        rnr.println("...Done");
    }

    private void createPolicySpace(CliRunner rnr) throws IOException {
        rnr.print(" - Creating " + LocalizationLoader.SPACE_DATA_FILENAME + " file");
        try ( BufferedWriter bwrt = Files.newBufferedWriter(localizationPath.resolve(LocalizationLoader.SPACE_DATA_FILENAME));
              PrintWriter prt = new PrintWriter(bwrt) ){
            rnr.getModel().getSpaceRoot().accept(new AbstractSlot.VoidVisitor(){
                
                LinkedList<String> stack = new LinkedList<>();
                
                @Override
                public void visitAtomicSlotImpl(AtomicSlot t) {
                    stack.push(t.getName());
                    String curPath = curPath();
                    
                    prt.println("# " + curPath );
                    prt.println(nonEmpty(t.getNote()) ? t.getNote() : ("<-- TODO: describe " + curPath) );
                    prt.println();
                    
                    t.values().forEach( v -> {
                        prt.println("# " + curPath + "/" + v.getName() );
                        prt.println(nonEmpty(v.getNote()) ? v.getNote() : ("<-- TODO: describe " + curPath + "/" + v.getName()) );
                        prt.println();
                    });
                    
                    stack.pop();
                }

                @Override
                public void visitAggregateSlotImpl(AggregateSlot t) {
                    stack.push(t.getName());
                    String curPath = curPath();
                    
                    prt.println("# " + curPath );
                    prt.println(nonEmpty(t.getNote()) ? t.getNote() : ("<-- TODO: describe " + curPath) );
                    prt.println();
                    
                    t.getItemType().values().forEach( v -> {
                        prt.println("# " + curPath + "/" + v.getName() );
                        prt.println(nonEmpty(v.getNote()) ? v.getNote() : ("<-- TODO: describe " + curPath + "/" + v.getName()) );
                        prt.println();
                    });
                    
                    stack.pop();

                }

                @Override
                public void visitCompoundSlotImpl(CompoundSlot t) {
                    stack.push(t.getName());
                    
                    prt.println("# " + curPath() );
                    prt.println(nonEmpty(t.getNote()) ? t.getNote() : ("<-- TODO: describe " + curPath() ) );
                    prt.println();
                    
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
        }
        rnr.println("...Done");
    }
    
    private void createNodeFiles(CliRunner rnr) throws IOException {
        rnr.print(" - Creating node files");
        
        final Path nodesDir = localizationPath.resolve(LocalizationLoader.NODE_DIRECTORY_NAME);
        if ( ! Files.exists(nodesDir) ) {
            Files.createDirectory(nodesDir);
        }
        
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
                createNodeLocalizationFile(nodesDir, nd, sb.toString());
            }

            @Override
            public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                createNodeLocalizationFile(nodesDir, nd, nd.getTitle());
            }

            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                createNodeLocalizationFile(nodesDir, nd, nd.getReason());
            }

            @Override
            public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                createNodeLocalizationFile(nodesDir, nd, nd.getTodoText());
            }

            @Override
            public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
                createNodeLocalizationFile(nodesDir, nd, nd.getTitle());
            }

            @Override public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {}
            @Override public void visitImpl(SetNode nd)      throws DataTagsRuntimeException {}
            @Override public void visitImpl(CallNode nd)     throws DataTagsRuntimeException {}
            @Override public void visitImpl(EndNode nd)      throws DataTagsRuntimeException {}
            @Override public void visitImpl(ContinueNode nd) throws DataTagsRuntimeException {}
        };
        
        
        rnr.getModel().getDecisionGraph().nodes().forEach( nd -> {
            if ( AstNodeIdProvider.isAutoId(nd.getId()) ) return;
            nd.accept(writer);
        });
        
        rnr.println("..Done");
    }
    
    private void createNodeLocalizationFile( Path nodesDir, Node node, String content) {
        Path relativePath = Paths.get(node.getId().substring(1, node.getId().indexOf("]")));
        //delete the .dg from file name
        String fileName = relativePath.toString();
        fileName = fileName.endsWith(".dg") ? fileName.substring(0, fileName.length() - 3) : fileName;
        relativePath = Paths.get(fileName);
        
        Path nodeDir;
        Path secFol = nodesDir.resolve(relativePath);
        if ( ! Files.exists(secFol) ) {
            try {
                Files.createDirectories(secFol);
            } catch (IOException ex) {
                Logger.getLogger(CreateLocalizationCommand.class.getName()).log(Level.SEVERE, "Error creating directory for node localizatoin", ex);
            }
        }
        
        String nodeName = node.getId().substring(node.getId().indexOf("]")+1, node.getId().length());
        nodeDir = secFol.resolve(nodeName + ".md");
        if ( nodeDir != null ) {
            createFileWithContent(nodeDir, content);
        }
    }
    
    private void createFileWithContent(Path p, String c) {
        try {
            Files.write(p, c.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(CreateLocalizationCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createLocalizedModel(CliRunner rnr) throws IOException {
        rnr.print(" - Creating "+LocalizationLoader.LOCALIZED_METADATA_FILENAME+" file");
        
        try ( InputStream rIn=getClass().getClassLoader().getResourceAsStream("localized-model-template.xml");
              BufferedReader rdr = new BufferedReader(new InputStreamReader(rIn)) 
        ) {
            List<String> xmlTemplate = new ArrayList<>(50);  
            String line;
            while ( (line=rdr.readLine()) != null ) {
                xmlTemplate.add(line);
            }
            PolicyModelData data = rnr.getModel().getMetadata();
            
            rnr.print(".");
            Stream<String> processedLines = xmlTemplate.stream().flatMap(s->processSingleLine(s, data).stream());
            
            rnr.print(".");
            Files.write(localizationPath.resolve(LocalizationLoader.LOCALIZED_METADATA_FILENAME),
                        processedLines.collect(toList()));
        }
        
        rnr.println("..Done");
    }
    
    List<String> processSingleLine(String in, PolicyModelData data) {
        String out = in;
        boolean go = true;
        
        while ( go ) {
            Matcher matcher = fieldDetector.matcher(out);
            if ( matcher.find() ) {
                String group = matcher.group();
                String fieldName = group.substring(2,group.length()-1);
                
                switch ( fieldName ) {
                    case "TITLE": 
                        out = out.replace(group, data.getTitle() );
                        break;
                    case "SUBTITLE":
                        out = out.replace(group, data.getSubTitle() != null ? data.getSubTitle() : "");
                        break;
                    case "KEYWORDS":
                        out = out.replace(group, data.getKeywords().stream().collect(joining(", ")));
                        break;
                    case "AUTHORS":
                        AuthorToXml a2x = new AuthorToXml(matcher.start());
                        List<String> outList = new ArrayList<>();
                        data.getAuthors().forEach( ad -> 
                            outList.addAll(ad.accept(a2x)) );
                        return outList;
                }
                
            } else {
                go = false;
            }
        }
        return Collections.singletonList(out);
    }

}
