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
    private final Pattern notesSeparator = Pattern.compile("^(\\h*-){3,}(\\h*)$", Pattern.MULTILINE);
    
    private final PolicySpaceIndex spaceIndex;
    
    private final List<String> messages = new LinkedList<>();
    
    private boolean hasError = false;
    
    private final Map<List<String>, LocalizationTexts> spaceEntityText = new HashMap<>();
    
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
             .forEach(nl -> {   // format now cannonized to be "# slot/or/value/path\n lines of text"
                 String curLine = nl.string;
                 if ( curLine.startsWith("#") ) {
                     validateTypeExists( cleanEntityReference(curLine), nl.number );
                     addTypeData( sb.toString(), spaceEntityText );
                     sb.setLength(0);
                 }
                 sb.append(curLine).append("\n");
             });
        addTypeData(sb.toString(), spaceEntityText);   
             
        return !hasError;
    }
    
    boolean isInlineSlotStart( String line ) {
        return typeStart.matcher(line).matches();
    }

    private void addTypeData(String typeText, Map<List<String>, LocalizationTexts> retVal) {
        if ( typeText.trim().isEmpty() ) return;
        String[] comps = typeText.split("\n",3); //comps[0]-slot/value, cpmps[1]-name, comps[2]-notes
        comps[0] = cleanEntityReference(comps[0]);
        
        Set<List<String>> fullTypeRefs = spaceIndex.get(comps[0]);
        if ( fullTypeRefs.size() == 1 ) {
            String[] notes = new String[]{"", ""};;
            if(comps.length > 2 && !comps[2].trim().isEmpty()){
                if(notesSeparator.split(comps[2]).length == 2){
                    notes = notesSeparator.split(comps[2], 2);    
                } else notes[0] = comps[2].trim();
                
            }
            retVal.put(fullTypeRefs.iterator().next(), new LocalizationTexts(("".equals(comps[1])) ? null : comps[1].trim(), ("".equals(notes[0])) ? null : notes[0].trim(), ("".equals(notes[1])) ? null : notes[1].trim()));
        }
    }

    private void validateTypeExists(String policySpaceReference, int lineNum) {
        policySpaceReference = cleanEntityReference(policySpaceReference);
        List<String> policyPath = Arrays.stream(policySpaceReference.split("/")).collect(toList());
        Set<List<String>> fullPaths = spaceIndex.get(policyPath);
        switch ( fullPaths.size() ) {
            case 0:
                messages.add( String.format("Line %d: Type reference '%s' does not refer to any type", lineNum, policySpaceReference) );
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

    public Map<List<String>, LocalizationTexts> getSpaceEnitiyTexts() {
        return spaceEntityText;
    }
    
}
