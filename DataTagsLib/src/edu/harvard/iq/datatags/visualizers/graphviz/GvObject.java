package edu.harvard.iq.datatags.visualizers.graphviz;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Base object for graphviz file construction.
 * @author michael
 */
public abstract class GvObject<T extends GvObject> {
    
    private static final Pattern illegalIdChars = Pattern.compile("\\s|-");
    
    private String url;
    private final Map<String,String> atts = new HashMap<>();
    
    public String gv() {
        StringBuilder sb = new StringBuilder();
        sb.append( gvTitle() );
        if ( ! atts.isEmpty() ) {
            sb.append( "[ " );
            for ( Map.Entry<String,String> e : atts.entrySet() ) {
                sb.append( e.getKey() )
                  .append( "=\"").append( e.getValue() ).append("\" ");
            }
            sb.append( "]" );
        }
        return sb.toString();
    }
    
    public T add( String att, String value ) {
        atts.put( att, value );
        return (T)this;
    }
    
    public T color( String colorOrList ) {
        return add( "color", colorOrList );
    }
    
    public T comment( String aComment ) {
        return add( "comment", aComment );
    }
    public T fontColor( String aColor ) {
        return add( "fontcolor", aColor );
    }
    public T fontSize( int points ) {
        return add( "fontsize", Integer.toString(points) );
    }
    public T label( String aLabel ) {
        return add("label", sanitizeString(aLabel) );
    }
    public T penwidth( double w ) {
        return add( "penwidth", Double.toString(w) );
    }
    
    String sanitizeId(String s) {
        String candidate = illegalIdChars.matcher(s.trim()).replaceAll("_").replaceAll("\\.", "_").trim();
        candidate = candidate.replaceAll(Pattern.quote("$"), "_DLR_");
        candidate = candidate.replaceAll(Pattern.quote("#"), "_HSH_");
        char first = candidate.charAt(0);
        return (first > '0' && first < '9') ? "_" + candidate : candidate;
    }

    String sanitizeString(String s) {
        return s.replaceAll("\"", "\\\"").replaceAll("\n", "\\n");
    }
    
    public T url( String aUrl ) {
        atts.put("url", sanitizeString(aUrl) );
        return (T)this;
    }
    
    protected void addToGv( StringBuilder sb ) {
        sb.append( " url=\"" )
          .append( sanitizeString(url) )
          .append("\" ");
    }
    
    protected abstract String gvTitle();
}
