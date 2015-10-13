package edu.harvard.iq.datatags.parser.decisiongraph.references;

/**
 * A reference to an {@code (end)} node in a chart.
 * @author Michael Bar-Sinai
 */
public class EndNodeRef extends InstructionNodeRef {

    public EndNodeRef(String id) {
        super( id );
    }
    
    public EndNodeRef() {
        this( null );
    }
    
	@Override
	public <T> T accept( Visitor<T> v ) {
		return v.visit(this);
	}
    
}
