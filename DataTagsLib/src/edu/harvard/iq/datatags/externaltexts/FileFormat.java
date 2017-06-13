package edu.harvard.iq.datatags.externaltexts;

/**
 *  
 * Enumerates the file formats for localized data, in precedence order.
 * The system would first try the first formats, and if no file is available
 * will move to the next ones.
 * 
 * @author michael
 */
public enum FileFormat {
    
    
    HTML("html"), Markdown("md"), Text("txt");
    
    public final String extension;

    private FileFormat(String extension) {
        this.extension = extension;
    }
    
}
