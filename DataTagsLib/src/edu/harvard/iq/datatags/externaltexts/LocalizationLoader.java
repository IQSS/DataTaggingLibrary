package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.io.FileUtils;
import static edu.harvard.iq.datatags.io.FileUtils.ciResolve;
import static edu.harvard.iq.datatags.io.FileUtils.readAll;
import edu.harvard.iq.datatags.model.PolicySpaceIndex;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.util.NumberedString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Loads a localization from a localization direcotry into a {@link Localization}
 * object.
 * 
 * 
 * @author michael
 */
public class LocalizationLoader {
    public static String ANSWERS_FILENAME="answers.txt";
    public static String LOCALIZED_METADATA_FILENAME="localized-model.xml";
    public static String NODE_DIRECTORY_NAME="nodes";
    public static String SPACE_DATA_FILENAME="space.md";
    
    private final List<ValidationMessage> messages = new ArrayList<>();
    
    public Localization load( CompoundSlot baseSlot, Path sourceDirectory ) throws IOException, LocalizationException {
        if ( ! Files.isDirectory(sourceDirectory) ) {
            throw new IllegalArgumentException(String.format("Cannot load localization from '%s' - it is not a directory", 
                                               sourceDirectory.toString()));
        }
        Localization retVal = new Localization(sourceDirectory.getFileName().toString());
        
        Path answersFilePath = ciResolve(sourceDirectory, ANSWERS_FILENAME);
        if ( answersFilePath != null ) {
            loadAnswers( Files.lines(answersFilePath, StandardCharsets.UTF_8), retVal );
        }
        
        Path modelLocalizationPath = ciResolve(sourceDirectory, LOCALIZED_METADATA_FILENAME);
        if ( modelLocalizationPath != null ) {
            LocalizedModelDataParser lmdp = new LocalizedModelDataParser(retVal.getLanguage());
            retVal.setLocalizedModelData(lmdp.read(modelLocalizationPath));
        }
        
        loadReadmes( retVal, sourceDirectory );
        
        Path spacePath = ciResolve(sourceDirectory, SPACE_DATA_FILENAME);
        if ( spacePath != null ) {
            loadTagspaceData( retVal, baseSlot, spacePath );
        }
                
        Path nodeDirectoryPath = ciResolve(sourceDirectory, NODE_DIRECTORY_NAME);
        if ( nodeDirectoryPath != null ) {
            loadNodeData( retVal, nodeDirectoryPath );
        }
        
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
            parser.getSpaceEnitiyTexts().forEach((p,t)->retVal.setPolicySpaceEntityText(p, t));
        } else {
            parser.getMessages().forEach( s -> messages.add(new ValidationMessage(ValidationMessage.Level.WARNING, s)));
        }
    }

    private void loadNodeData(Localization retVal, Path nodeDirectoryPath) throws IOException {
        final Set<String> fileExtensions = new TreeSet<>(Arrays.asList(".txt", ".md", ".mdown"));
        Files.find(nodeDirectoryPath, 0, (p,_opts)-> {
            String[] comps = p.getFileName().toString().toLowerCase().split("\\.");
            return fileExtensions.contains(comps[comps.length-1]);
                    
        }).forEach( path -> {
            String fileName = path.getFileName().toString();
            String[] comps = fileName.split("\\.");
            if ( comps.length > 1 ) {
                fileName = fileName.substring(0, fileName.length()-comps[comps.length-1].length()-1);
                retVal.addNodeText(fileName, readAll(path));
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
