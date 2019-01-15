package edu.harvard.iq.datatags.visualizers.graphviz;

/**
 * A node in graphviz.
 * @author Michael Bar-Sinai
 */
public class GvNode extends GvObject<GvNode> {
    
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
        solid, dashed, dotted, bold,
        rounded, diagonals, filled, striped, wedged
    }
    
    private final String id;
    
    public static GvNode node( String id ) {
        return new GvNode( id );
    }
    
    public GvNode(String id) {
        this.id = id;
    }
    
    public GvNode shape( Shape aShape ) {
        return add("shape", aShape.name());
    }
    
    public GvNode fillColor( String aColor ) {
        return add( "fillcolor", aColor );
    }
    
    public GvNode style( Style aStyle ) {
        return add( "style", aStyle.name() );
    }
    
    public GvNode peripheries( int count ) {
        return add( "peripheries", Integer.toString(count) );
    }
    
    public GvNode width( double sz ) {
        return add("width", Double.toString(sz) );
    }
    
    @Override
    protected String gvTitle() {
        return sanitizeId(id);
    }

}
