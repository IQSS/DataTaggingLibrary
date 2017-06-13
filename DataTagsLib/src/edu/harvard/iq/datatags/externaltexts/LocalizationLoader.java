package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.util.NumberedString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    
    
    public Localization load( Path sourceDirectory ) throws IOException, LocalizationException {
        if ( ! Files.isDirectory(sourceDirectory) ) {
            throw new IllegalArgumentException(String.format("Cannot load localization from '%s' - it is not a directory", 
                                               sourceDirectory.toString()));
        }
        Localization retVal = new Localization(sourceDirectory.getFileName().toString());
        
        Path answersFilePath = sourceDirectory.resolve(ANSWERS_FILENAME);
        if ( Files.exists(answersFilePath) ) {
            loadAnswers( Files.lines(answersFilePath, StandardCharsets.UTF_8), retVal );
        }
        
        Path modelLocalizationPath = sourceDirectory.resolve(LOCALIZED_METADATA_FILENAME);
        if ( Files.exists(modelLocalizationPath) ) {
            LocalizedModelDataParser lmdp = new LocalizedModelDataParser(retVal.getLanguage());
            retVal.setLocalizedModelData(lmdp.read(modelLocalizationPath));
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
            throw new LocalizationException(retVal.getLanguage(), rte.getMessage());
        }
    }

    private LocalizedModelData loadModelLocalization(Path modelLocalizationPath) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement loadModelLocalization
    }
}
