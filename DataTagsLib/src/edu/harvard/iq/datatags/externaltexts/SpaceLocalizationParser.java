package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.model.PolicySpaceIndex;
import edu.harvard.iq.datatags.util.NumberedString;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 * Parse space localization file.
 * The tag space format is 
 * @author michael
 */
public class SpaceLocalizationParser {
    
    private final Pattern typeStart = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*(/[a-zA-Z_][a-zA-Z0-9_]*)*:.*$");
    
    private final PolicySpaceIndex spaceIndex;
    
    private final List<String> messages = new LinkedList<>();
    
    private boolean hasError = false;
    
    private final Map<List<String>, String> spaceEnitiyText = new HashMap<>();
    
    public SpaceLocalizationParser(PolicySpaceIndex spaceIndex) {
        this.spaceIndex = spaceIndex;
    }
    
    /**
     * Parses a file of policy space localization.
     * @param lines
     * @return {@code true} iff there are no errors reading the input.
     */
    public boolean parse( Stream<String> lines ) {
        final AtomicInteger idxer = new AtomicInteger(0);
        final StringBuilder sb = new StringBuilder();
        
        lines.map( l -> new NumberedString(l, idxer.incrementAndGet()))
             .map( l -> l.copy(l.string.split("<--")[0].trim()) )
             .flatMap( l -> {
                 if ( isInlineSlotStart(l.string) ) {
                     String[] comps = l.string.split(":",2);
                     return Stream.of(l.copy("# " + comps[0]), l.copy(comps[1]));
                 } else {
                     return Stream.of(l);
                 }
             }).forEach(nl -> {   // format now cannonized to be "# slot/or/value/path\n lines of text"
                 String curLine = nl.string;
                 if ( curLine.startsWith("#") ) {
                     validateTypeExists( cleanEntityReference(curLine), nl.number );
                     addTypeData( sb.toString(), spaceEnitiyText );
                     sb.setLength(0);
                 }
                 sb.append(curLine).append("\n");
             });
        addTypeData(sb.toString(), spaceEnitiyText);   
             
        return !hasError;
    }
    
    boolean isInlineSlotStart( String line ) {
        return typeStart.matcher(line).matches();
    }

    private void addTypeData(String typeText, Map<List<String>, String> retVal) {
        if ( typeText.trim().isEmpty() ) return;
        
        String[] comps = typeText.split("\n",2);
        comps[0] = cleanEntityReference(comps[0]);
        
        Set<List<String>> fullTypeRefs = spaceIndex.get(comps[0]);
        if ( fullTypeRefs.size() == 1 ) {
            retVal.put(fullTypeRefs.iterator().next(), comps[1].trim());
        }
    }

    private void validateTypeExists(String policySpaceReference, int lineNum) {
        policySpaceReference = cleanEntityReference(policySpaceReference);
        List<String> policyPath = Arrays.stream(policySpaceReference.split("/")).collect(toList());
        Set<List<String>> fullPaths = spaceIndex.get(policyPath);
        switch ( fullPaths.size() ) {
            case 0:
                messages.add( String.format("Line %d: Type reference '%s' does not refere to any type", lineNum, policySpaceReference) );
                hasError = true;
                break;
            case 1: 
                break;
            default:
                messages.add( String.format("Line %d: Type reference '%s' is amibguous. Could Refer to: [%s]", 
                                            lineNum,
                                            policySpaceReference,
                                            fullPaths.stream().map(Object::toString).collect(joining(", "))
                ) );
                hasError = true;
                break;
                
        }
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

    public Map<List<String>, String> getSpaceEnitiyTexts() {
        return spaceEnitiyText;
    }
    
}
