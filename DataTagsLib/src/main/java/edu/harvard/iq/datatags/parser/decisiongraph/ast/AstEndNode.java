package edu.harvard.iq.datatags.parser.decisiongraph.ast;

public class AstEndNode extends AstNode {

    public AstEndNode(String id) {
        super( id );
    }
    
    public AstEndNode() {
        this( null );
    }
    
	@Override
	public <T> T accept( Visitor<T> v ) {
		return v.visit(this);
	}

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( obj == this ) return true;
        if ( !(obj instanceof AstEndNode) ) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
