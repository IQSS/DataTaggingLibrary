package edu.harvard.iq.policymodels.externaltexts;

import edu.harvard.iq.policymodels.util.NumberedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;

/**
 * Parse a key-value localization file, where the values are {@link LocalizationTexts}.
 * The tag space format is based on Markdown, and works as follows:
 * 
 * <code>
 * # KEY NAME (1)
 * localized name
 * tooltip name (2)
 * ---          (3)
 * markdown text
 * multiline
 * 
 * </code>
 * where: 
 * <ul>
 * <li>(1) this is the {@code #} sign followed by a key, e.g. section id / policy space object path</li>
 * <li>(2) Tooltip. Leave a blank line if empty.</li>
 * <li>(3) Multiline markdown text. This is started by {@code ---}, markdown's {@code <hr>} notation.</li>
 * </ul>
 *
 * @author michael
 */
public class LocalizationTextsParser {
    
    /**
     * Object that finds the canonical key for a given entry.
     */
    public interface KeyCanonizer {
        Set<List<String>> cannonize( String key );
    }
    
    private final Pattern typeStart = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*(/[a-zA-Z_][a-zA-Z0-9_]*)*:.*$");
    private final Pattern notesSeparator = Pattern.compile("^(\\h*-){3,}(\\h*)$", Pattern.MULTILINE);
    
    private final KeyCanonizer canonizer;
    
    private final List<String> messages = new LinkedList<>();
    
    private boolean hasError = false;
    
    private final Map<List<String>, LocalizationTexts> localizedEntries = new HashMap<>();
    
    public LocalizationTextsParser(KeyCanonizer aCanonizer) {
        this.canonizer = aCanonizer;
    }
    
    /**
     * Parses a file of policy space localization.
     * @param lines
     * @return {@code true} iff there are no errors reading the input.
     */
    public boolean parse( Stream<String> lines ) {
        final AtomicInteger idxer = new AtomicInteger(0);
        final List<String> lineBuffer = new ArrayList<>();
        final AtomicInteger lastStart = new AtomicInteger(0);
        
        Stream.concat(lines, Stream.of("#"))
             .map( l -> new NumberedString(l, idxer.incrementAndGet()))
             .map( l -> l.copy(l.string.split("<--")[0].trim()) )
             .forEachOrdered(nl -> {   // format now cannonized to be "# slot/or/value/path\n lines of text"
                 String curLine = nl.string;
                 if ( curLine.startsWith("#") ) {
                     // new entry started. Process the current one (if any
                     if ( ! lineBuffer.isEmpty() ) {
                        String keyReference = cleanEntityReference(lineBuffer.get(0));
                        List<String> cannonizedKey = validateKey(keyReference, lastStart.get());
                        if ( cannonizedKey != null ) {
                            addLocalizedEntry(cannonizedKey, lineBuffer);
                        }                         
                     }
                     lineBuffer.clear();
                     lastStart.set(nl.number);
                 }
                 lineBuffer.add( curLine );
             });  
             
        return !hasError;
    }
    
    boolean isInlineSlotStart( String line ) {
        return typeStart.matcher(line).matches();
    }

    private void addLocalizedEntry(List<String> key, List<String> rawRecord) {
        if ( rawRecord.isEmpty() ) return;
        Iterator<String> rows = rawRecord.iterator();
        rows.next(); // skip the first line, which contains the key
        
        String name = null;
        if ( rows.hasNext() ) {
            name = rows.next().trim();
            if (name.trim().isEmpty() ) {
                name = null;
            }
        }
        
        String toolTipText = null;
        String bigNoteText = null;
        
        if ( rows.hasNext() ) toolTipText = rows.next().trim();
        if ( toolTipText!=null && (toolTipText.equals("---") || toolTipText.isEmpty()) ) {
            toolTipText = null;
        }
        
        if ( rows.hasNext() ) {
            StringBuilder sb = new StringBuilder();
            
            while ( rows.hasNext() ) {
                sb.append( rows.next() ).append("\n");
            }
            bigNoteText = sb.toString().trim();

            if (  bigNoteText.startsWith("---") && 
                  bigNoteText.contains("\n") ) {
                bigNoteText = bigNoteText.substring(bigNoteText.indexOf("\n")).trim();
            }
            if ( bigNoteText.trim().isEmpty() ) {
                bigNoteText = null;
            }
        }
        
        localizedEntries.put(key, new LocalizationTexts(name, toolTipText, bigNoteText));
        
    }

    private List<String> validateKey(String keyReference, int lineNum) {
        keyReference = cleanEntityReference(keyReference);
        Set<List<String>> keys = canonizer.cannonize(keyReference);
        switch ( keys.size() ) {
            case 0:
                messages.add( String.format("Line %d: Reference '%s' does not refer to any valid entity", lineNum, keyReference) );
                break;
                
            case 1: 
                return keys.iterator().next();

            default:
                messages.add( String.format("Line %d: Reference '%s' is amibguous. Could Refer to: [%s]", 
                                            lineNum,
                                            keyReference,
                                            keys.stream().map(Object::toString).collect(joining(", "))
                ));
                hasError = true;
                break;
        }
        return null;
    }
    
    private String cleanEntityReference( String entityReference ) {
        entityReference = entityReference.trim();
        if ( entityReference.startsWith("#") ) {
            entityReference = entityReference.substring(1).trim();
        }
        if ( entityReference.endsWith(":") ) {
            entityReference = entityReference.substring(0, entityReference.length()-1).trim();
        }
                
        return entityReference;
    }
    
    public boolean isHasError() {
        return hasError;
    }

    public List<String> getMessages() {
        return messages;
    }

    public Map<List<String>, LocalizationTexts> getTextsMap() {
        return localizedEntries;
    }
    
}
