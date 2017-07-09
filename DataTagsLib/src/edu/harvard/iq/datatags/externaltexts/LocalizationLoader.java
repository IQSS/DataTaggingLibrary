package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.io.FileUtils;
import static edu.harvard.iq.datatags.io.FileUtils.ciResolve;
import static edu.harvard.iq.datatags.io.FileUtils.readAll;
import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.PolicySpaceIndex;
import edu.harvard.iq.datatags.model.PolicySpacePathQuery;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import edu.harvard.iq.datatags.util.NumberedString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Loads a localization from a localization directory into a 
 * {@link Localization} object.
 * 
 * @author michael
 */
public class LocalizationLoader {
    
    public static final String LOCALIZATION_DIRECTORY_NAME = "languages";
    public static final String ANSWERS_FILENAME = "answers.txt";
    public static final String LOCALIZED_METADATA_FILENAME = "localized-model.xml";
    public static final String NODE_DIRECTORY_NAME = "nodes";
    public static final String SPACE_DATA_FILENAME = "space.md";
    
    private final List<ValidationMessage> messages = new ArrayList<>();
    
    public Localization load( PolicyModel model, String localizationName ) throws IOException, LocalizationException {
        messages.clear();
        Path baseLocalizationPath = model.getMetadata().getModelDirectoryPath();
        Path localizationPath = baseLocalizationPath.resolve(LOCALIZATION_DIRECTORY_NAME)
                                                    .resolve(localizationName);
        
        if ( ! Files.exists(localizationPath) ) {
            throw new LocalizationException(localizationName,
                    String.format("Localization directory '%s' does not exist.", localizationPath.toAbsolutePath()));
        }
        if ( ! Files.isDirectory(localizationPath) ) {
            throw new LocalizationException(localizationName,
                    String.format("Expecting a directory at '%s'.", localizationPath.toAbsolutePath()));
        }
                
        Localization retVal = new Localization(localizationName);
        
        Path answersFilePath = ciResolve(localizationPath, ANSWERS_FILENAME);
        if ( answersFilePath != null ) {
            loadAnswers( Files.lines(answersFilePath, StandardCharsets.UTF_8), retVal );
        }
        
        Path modelLocalizationPath = ciResolve(localizationPath, LOCALIZED_METADATA_FILENAME);
        if ( modelLocalizationPath != null ) {
            LocalizedModelDataParser lmdp = new LocalizedModelDataParser(retVal.getLanguage());
            retVal.setLocalizedModelData(lmdp.read(modelLocalizationPath));
        }
        
        loadReadmes( retVal, localizationPath );
        
        Path spacePath = ciResolve(localizationPath, SPACE_DATA_FILENAME);
        if ( spacePath != null ) {
            loadTagspaceData( retVal, model.getSpaceRoot(), spacePath );
        }
                
        loadNodeData( retVal, model );
        
        return retVal;
    }

    void loadAnswers(Stream<String> lines, Localization retVal) throws LocalizationException {
        final AtomicInteger idxer = new AtomicInteger();
        try {
        lines.map( l -> new NumberedString(l, idxer.incrementAndGet()))
             .map( l -> l.copy(l.string.split("<--")[0].trim()) )
             .filter( l -> !l.string.isEmpty() ) // OK, no empty lines and no line comments.
             .forEach( nl -> {
                 String[] arr = nl.string.split(":",2);
                 if ( arr.length < 2 ) {
                     throw new RuntimeException("Error at line " + nl.number + ": missing ':' separating the answer from the localized text");
                 }
                String localizedValue = arr[1].trim();
                if ( ! localizedValue.isEmpty() ) {
                    retVal.addAnswer(arr[0].trim(), localizedValue);
                }
               });
        } catch (RuntimeException rte) {
            messages.add(new ValidationMessage(ValidationMessage.Level.ERROR,
                                "Error reading answer localization file: " + rte.getMessage()));
        }
    }

    private void loadReadmes(Localization retVal, Path sourceDirectory) throws IOException {
        try {
            Files.find(sourceDirectory, 1,
                   (path, atts) -> path.getFileName().toString().toLowerCase().startsWith("readme.") )
                .forEach( path -> {
                    String[] comps = path.getFileName().toString().split("\\.");
                    MarkupFormat.forExtension(comps[comps.length-1])
                                .ifPresent( fmt -> retVal.addReadme(fmt, FileUtils.readAll(path)));
                });
        } catch ( RuntimeException rte ) {
            if ( rte.getCause()!=null && rte.getCause() instanceof IOException ) {
                messages.add(new ValidationMessage(ValidationMessage.Level.ERROR,
                                "Error reading readme files: " + rte.getCause().getMessage()));
            } else {
                throw rte;
            }
        }
    }

    private void loadTagspaceData(Localization retVal, CompoundSlot baseSlot, Path spacePath) throws IOException {
        PolicySpaceIndex spaceIndex = new PolicySpaceIndex(baseSlot);
        SpaceLocalizationParser parser = new SpaceLocalizationParser(spaceIndex);
        
        if ( parser.parse(Files.lines(spacePath, StandardCharsets.UTF_8)) ) {
            PolicySpacePathQuery qry = new PolicySpacePathQuery(baseSlot);
                    
            parser.getSpaceEnitiyTexts().forEach(
                    (path,text) -> qry.get(path).accept(new PolicySpacePathQuery.Result.Visitor<Void>() {
                           @Override
                           public Void visit(PolicySpacePathQuery.TagValueResult tvr) {
                               retVal.setSlotValueText(tvr.value, text);
                               return null;
                           }

                           @Override
                           public Void visit(PolicySpacePathQuery.SlotTypeResult str) {
                               retVal.setSlotText(str.value, text);
                               return null;
                           }

                           @Override
                           public Void visit(PolicySpacePathQuery.NotFoundResult nfr) {
                               messages.add( new ValidationMessage(Level.WARNING, "Localization refers to nonexistent slot/value '" + nfr.path + "'"));
                               return null;
                           }
                    }));
        } 
        
        parser.getMessages().forEach( s -> messages.add(new ValidationMessage(Level.WARNING, s)));
        
    }
    
    
    private void loadNodeData(Localization loc, PolicyModel model) throws IOException {
        
        // collect node direcotry paths for all compilation units
        Map<String, CompilationUnit> compilationUnitPaths = model.getMetadata().getCompilationUnits();
        Map<String, Path> compilationUnitNodeDirectories = new HashMap<>();
        compilationUnitPaths.forEach( (id, cu)->{
            // find the node directory path
            Path cuNodesPath = cu.getSourcePath();
            
            while ( cuNodesPath != null && !Files.exists(cuNodesPath.resolveSibling(LOCALIZATION_DIRECTORY_NAME)) ) {
                cuNodesPath = cuNodesPath.getParent();
            }
            if ( cuNodesPath != null ) {
                cuNodesPath = cuNodesPath.resolveSibling(LOCALIZATION_DIRECTORY_NAME);
                cuNodesPath = cuNodesPath.resolve(loc.getLanguage()).resolve(NODE_DIRECTORY_NAME);
                if ( Files.exists(cuNodesPath) ) {
                    compilationUnitNodeDirectories.put(id, cuNodesPath);
                } else {
                    messages.add( new ValidationMessage(ValidationMessage.Level.WARNING, "Could not find '" + loc.getLanguage() + "/nodes' directory for compilation unit '" + cu.getSourcePath() + "'"));
                }
            } else {
                messages.add( new ValidationMessage(ValidationMessage.Level.WARNING, "Could not find 'languages' directory for compilation unit '" + cu.getSourcePath() + "'"));
            }
        });
        
        // load nodes that have localization ids.
        model.getDecisionGraph().nodeIds().forEach( id -> {
           String[] comps = id.split(">");
           String cuID = (comps.length==1) ? DecisionGraphCompiler.MAIN_CU_ID : comps[0]; 
           String nodeName = (comps.length==1) ? comps[0] : comps[1];
           Path cuNodesPath = compilationUnitNodeDirectories.get(cuID);
           if ( cuNodesPath != null ) {
               for ( String ext : new String[]{".md", ".mdown", ".txt"}) {
                   Path attempt = (comps.length==1) ? cuNodesPath.resolve(nodeName + ext) 
                                                    : cuNodesPath.resolve(comps[0]).resolve(comps[1]  + ext);
                   if ( Files.exists(attempt) ) {
                       loc.addNodeText(id, readAll(attempt));
                       break;
                   }
               }
           }
        });
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public boolean isHasErrors() {
        return messages.stream().anyMatch(vm->vm.getLevel()==ValidationMessage.Level.ERROR);
    }
    
}
