package edu.harvard.iq.policymodels.externaltexts;

import static edu.harvard.iq.policymodels.io.FileUtils.ciResolve;
import static edu.harvard.iq.policymodels.io.FileUtils.readAll;
import edu.harvard.iq.policymodels.model.PolicyModel;
import edu.harvard.iq.policymodels.model.PolicySpaceIndex;
import edu.harvard.iq.policymodels.model.PolicySpacePathQuery;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.SectionNode;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.parser.BaseModelLoader;
import edu.harvard.iq.policymodels.tools.ValidationMessage;
import edu.harvard.iq.policymodels.tools.ValidationMessage.Level;
import edu.harvard.iq.policymodels.util.NumberedString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Loads a localization from a localization directory into a 
 * {@link Localization} object.
 * 
 * @author michael
 */
public class LocalizationLoader extends BaseModelLoader {
    
    
    private final List<ValidationMessage> messages = new ArrayList<>();
    
    public Localization load( PolicyModel model, String localizationName ) throws IOException, LocalizationException {
        messages.clear();
        Path baseLocalizationPath = model.getMetadata().getModelDirectoryPath();
        Path localizationPath = baseLocalizationPath.resolve(FsLocalizationIO.LOCALIZATION_DIRECTORY_NAME)
                                                    .resolve(localizationName);
        
        if ( ! Files.exists(localizationPath) ) {
            throw new LocalizationException(localizationName,
                    String.format("Localization directory '%s' does not exist.", localizationPath.toAbsolutePath()));
        }
        if ( ! Files.isDirectory(localizationPath) ) {
            throw new LocalizationException(localizationName,
                    String.format("Expecting a directory at '%s'.", localizationPath.toAbsolutePath()));
        }
                
        Localization retVal = new Localization();
        
        
        Path answersFilePath = ciResolve(localizationPath, FsLocalizationIO.ANSWERS_FILENAME);
        if ( answersFilePath != null ) {
            loadAnswers( Files.lines(answersFilePath, StandardCharsets.UTF_8), retVal );
        }
        
        Path modelLocalizationPath = ciResolve(localizationPath, FsLocalizationIO.LOCALIZED_METADATA_FILENAME);
        if ( modelLocalizationPath != null ) {
            LocalizedModelDataParser lmdp = new LocalizedModelDataParser(localizationName);
            retVal.setLocalizedModelData(lmdp.read(modelLocalizationPath));
        } else {
            LocalizedModelData locMd = new LocalizedModelData();
            locMd.setLanguage("DEFAULT");
            locMd.setUiLanguage("en");
            locMd.setTitle( model.getMetadata().getTitle() );
            locMd.setSubTitle( model.getMetadata().getSubTitle() );
            retVal.setLocalizedModelData(locMd);
        }
        
        messages.addAll( loadReadmes(retVal.getLocalizedModelData(), localizationPath) );
        
        Path spacePath = ciResolve(localizationPath, FsLocalizationIO.SPACE_DATA_FILENAME);
        if ( spacePath != null ) {
            loadTagspaceData( retVal, model.getSpaceRoot(), spacePath );
        }
        
        Path sectionPath = ciResolve(localizationPath, FsLocalizationIO.SECTION_TEXTS_FILENAME);
        if ( sectionPath!=null && Files.exists(sectionPath) ) {
            loadSectionLocalizations(retVal, model, sectionPath);
        }
                
        loadNodeData( retVal, model, localizationPath );
        
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

    private void loadTagspaceData(Localization retVal, CompoundSlot baseSlot, Path spacePath) throws IOException {
        PolicySpaceIndex spaceIndex = new PolicySpaceIndex(baseSlot);
        
        LocalizationTextsParser parser = new LocalizationTextsParser(spaceIndex::get);
        
        if ( parser.parse(Files.lines(spacePath, StandardCharsets.UTF_8)) ) {
            PolicySpacePathQuery qry = new PolicySpacePathQuery(baseSlot);
                    
            parser.getTextsMap().forEach(
                    (path,text) -> qry.get(path).accept(new PolicySpacePathQuery.Result.Visitor<Void>() {
                           @Override
                           public Void visit(PolicySpacePathQuery.TagValueResult tvr) {
                               retVal.setSlotValueTexts(tvr.value, text);
                               return null;
                           }

                           @Override
                           public Void visit(PolicySpacePathQuery.SlotTypeResult str) {
                               retVal.setSlotTexts(str.value, text);
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
    
    private void loadSectionLocalizations(Localization retVal, PolicyModel model, Path sectionsPath) throws IOException {
        final Set<String> sectionNodeIds = StreamSupport.stream(model.getDecisionGraph().nodes().spliterator(), true)
                                                   .filter( n -> n instanceof SectionNode )
                                                   .map( Node::getId )
                                                   .collect( toSet() );
        
        LocalizationTextsParser parser = new LocalizationTextsParser(key -> {
            if ( sectionNodeIds.contains(key) ) {
                return Collections.singleton(Collections.singletonList(key));
            } else {
                return Collections.emptySet();
            }
        });
        
        if ( parser.parse(Files.lines(sectionsPath, StandardCharsets.UTF_8)) ) {
            parser.getTextsMap().forEach((k,v)->{
                retVal.setSectionTexts(k.get(0), v);
            });
        } 
        
        parser.getMessages().forEach( s -> messages.add(new ValidationMessage(Level.WARNING, s)));
        
    }
    
    private void loadNodeData(Localization loc, PolicyModel model, Path localizationPath) throws IOException {
        
        // load nodes that have localization ids.
        model.getDecisionGraph().nodes().forEach(node -> {
            String nodeName = node.getId().substring(node.getId().indexOf("]")+1, node.getId().length());
           
            String effNodeId = node.getId();
            if ( effNodeId.startsWith("[") ) {
                effNodeId = effNodeId.substring(1, node.getId().indexOf("]"));
            }
            Path relativePath = Paths.get(effNodeId);
           
            //delete the .dg from file name
            String fileName = relativePath.toString();
            fileName = fileName.endsWith(".dg") ? fileName.substring(0, fileName.length() - 3) : fileName;
            relativePath = Paths.get(fileName);

            if ( relativePath != null ) {
               for ( String ext : new String[]{".md", ".mdown", ".txt"}) {
                   Path attempt = localizationPath.resolve(FsLocalizationIO.NODE_DIRECTORY_NAME).resolve(relativePath.resolve(nodeName + ext)) ;
                                                    
                   if ( Files.exists(attempt) ) {
                       loc.addNodeText(node.getId(), readAll(attempt));
                       break;   
                   }
               }
            }
        });
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public boolean hasErrors() {
        return messages.stream().anyMatch(vm->vm.getLevel()==ValidationMessage.Level.ERROR);
    }
    
}
