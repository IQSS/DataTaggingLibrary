package edu.harvard.iq.datatags.visualizers.graphviz;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A node in graphviz.
 * @author Michael Bar-Sinai
 */
public class GvNode {
    
    private static final Pattern whitespace = Pattern.compile("\\s|-");
    
    
    public enum Shape {
        box, polygon, ellipse, oval,
        circle, point, egg, triangle,
        plaintext, diamond, trapezium, parallelogram,
        house, pentagon, hexagon, septagon,
        octagon, doublecircle, doubleoctagon, tripleoctagon,
        invtriangle, invtrapezium, invhouse, Mdiamond,
        Msquare, Mcircle, rect, rectangle,
        square, star, none, underline,
        note, tab, folder, box3d,
        component, promoter, cds, terminator,
        utr, primersite, restrictionsite, fivepoverhang,
        threepoverhang, noverhang, assembly, signature,
        insulator, ribosite, rnastab, proteasesite,
        proteinstab, rpromoter, rarrow, larrow,
        lpromoter
    }
    
    public enum Style {
        solid, dashed, dotted,bold,
        rounded, diagonals, filled, striped, wedged
    }
    
    private final String id;
    private final Map<String,String> atts = new HashMap<>();
    
    public static GvNode node( String id ) {
        return new GvNode( id );
    }
    
    public GvNode(String id) {
        this.id = id;
    }
    
    public GvNode shape( Shape aShape ) {
        atts.put("shape", aShape.name());
        return this;
    }
    public GvNode label( String aLabel ) {
        atts.put("label", aLabel);
        return this;
    }
    public GvNode fillColor( String aColor ) {
        atts.put( "fillcolor", aColor );
        return this;
    }
    public GvNode fontColor( String aColor ) {
        atts.put( "fontcolor", aColor );
        return this;
    }
    public GvNode style( Style aStyle ) {
        atts.put( "style", aStyle.name() );
        return this;
    }
    public GvNode peripheries( int count ) {
        atts.put( "peripheries", Integer.toString(count) );
        return this;
    }
    
    public GvNode fontSize( int points ) {
        atts.put( "fontsize", Integer.toString(points) );
        return this;
    }
    
   
    
    public String gv() {
        StringBuilder sb = new StringBuilder();
        sb.append( id );
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
}
