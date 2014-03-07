package edu.harvard.iq.datatags.parser.flowcharts.references;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class TodoNodeRef extends StringBodyNodeRef {

    public TodoNodeRef(String id, String body) {
        super(new TypedNodeHeadRef(id, NodeType.Todo), body);
    }

}
