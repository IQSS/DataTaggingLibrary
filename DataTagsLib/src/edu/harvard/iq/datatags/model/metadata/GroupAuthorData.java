package edu.harvard.iq.datatags.model.metadata;

/**
 * A working group that took part in authoring the model.
 * 
 * @author michael
 */
public class GroupAuthorData implements AuthorData {
    
    private String name;
    private String contact;
    
    @Override
    public String displayString() {
        return name + (contact!=null?" (Contact: " + contact +")" : "");
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
    
}
