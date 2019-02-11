package edu.harvard.iq.datatags.parser;

import edu.harvard.iq.datatags.externaltexts.MarkupFormat;
import edu.harvard.iq.datatags.io.FileUtils;
import edu.harvard.iq.datatags.model.metadata.BaseModelData;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for model loaders. Mainly used for code-reuse.
 * 
 * @author michael
 */
public abstract class BaseModelLoader {

    protected List<ValidationMessage> loadReadmes(BaseModelData modelData, Path sourceDirectory) throws IOException {
        final List<ValidationMessage> messages = new LinkedList<>();
        try {
            Files.find(sourceDirectory, 1,
                   (path, atts) -> path.getFileName().toString().toLowerCase().startsWith("readme.") )
                .forEach( path -> {
                    String[] comps = path.getFileName().toString().split("\\.");
                    MarkupFormat.forExtension(comps[comps.length-1])
                                .ifPresent( fmt -> modelData.addReadme(fmt, FileUtils.readAll(path)));
                });
        } catch ( RuntimeException rte ) {
            if ( rte.getCause()!=null && rte.getCause() instanceof IOException ) {
                messages.add(new ValidationMessage(ValidationMessage.Level.ERROR,
                                "Error reading readme files: " + rte.getCause().getMessage()));
            } else {
                throw rte;
            }
        }
        return messages;
    }
    
}
