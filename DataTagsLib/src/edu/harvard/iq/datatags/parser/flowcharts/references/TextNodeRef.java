package edu.harvard.iq.datatags.parser.flowcharts.references;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class TextNodeRef extends StringBodyNodeRef {
    
    public TextNodeRef( String id, String text ) {
        super( new TypedNodeHeadRef(id, NodeType.Text), text );
    }
    
    public TextNodeRef( StringBodyNodeRef src ) {
        this( src.getHead().getId(), src.getBody() );
    }
}
