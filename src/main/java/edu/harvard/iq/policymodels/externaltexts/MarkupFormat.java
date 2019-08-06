package edu.harvard.iq.policymodels.externaltexts;

import java.util.Arrays;
import java.util.Optional;

/**
 *  
 * Enumerates the markup formats for localized data, in precedence order.
 * The system would first try the first formats, and if no file is available
 * will move to the next ones.
 * 
 * @author michael
 */
public enum MarkupFormat {
    
    
    HTML("html"), Markdown("md"), Text("txt");
    
    public final String extension;
    
    public static Optional<MarkupFormat> forExtension( String suffix ) {
        String lcSuffix = suffix.toLowerCase();
        return Arrays.stream(MarkupFormat.values())
                .filter( fmt -> lcSuffix.equals(fmt.extension) )
                .findFirst();
    }
    
    private MarkupFormat(String extension) {
        this.extension = extension;
    }
    
}
