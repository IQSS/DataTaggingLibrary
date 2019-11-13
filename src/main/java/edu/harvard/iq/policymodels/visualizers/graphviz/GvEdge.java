package edu.harvard.iq.policymodels.visualizers.graphviz;

/**
 * An edge in GraphViz.
 * 
 * @author michael
 */
public class GvEdge extends GvObject<GvEdge> {
    
    public enum ArrowType {
        Normal, Inv, Dot,
        Invdot, Odot, Invodot, None,
        Tee, empty, Invempty, Diamond,
        Odiamond, Ediamond, Crow, Box,
        Obox, Open, Halfopen, Vee, Circle	
    }
    
    public enum Style {
        Solid, Dashed, Dotted, Bold, Invis
    }
    
    private final String from, to;
    
    public static GvEdge edge( String from, String to ) {
        return new GvEdge( from, to );
    }
    
    public GvEdge(String from, String to) {
        this.from = from;
        this.to = to;
    }
    
    public GvEdge style( Style aStyle ) {
        return add("style", aStyle.name().toLowerCase());
    }
    
    public GvEdge arrowhead( ArrowType at ) {
        return add("arrowhead", at.name().toLowerCase());
    }
    
    public GvEdge arrowtail( ArrowType at ) {
        return add("arrowtail", at.name().toLowerCase());
    }
    
    public GvEdge constraint( boolean c ) {
        return add("constraint", Boolean.toString(c));
    }
    
    public GvEdge decorate( boolean c ) {
        return add("decorate", Boolean.toString(c));
    }
    
    public GvEdge sameHead( String headId ) {
        return add("samehead", headId );
    }
    
    public GvEdge sameTail( String tailId ) {
        return add("sametail", tailId );
    }
    
    public GvEdge headLabel(String lbl) {
        return add("headlabel", lbl );
    }
    
    public GvEdge tailLabel(String lbl) {
        return add("taillabel", lbl );
    }
    
    @Override
    public String gvTitle() {
        return sanitizeId(from) + " -> " + sanitizeId(to);
    }
    
}
