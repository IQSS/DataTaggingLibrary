package edu.harvard.iq.datatags.model.metadata;

import java.util.Objects;

/**
 * A working group that took part in authoring the model.
 * 
 * @author michael
 */
public class GroupAuthorData extends AuthorData {
    
    private String contact;
    
    @Override
    public String displayString() {
        return name + (contact!=null?" (Contact: " + contact +")" : "");
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GroupAuthorData other = (GroupAuthorData) obj;        
        return Objects.equals(this.contact, other.contact) && equals(other);
    }
    
}
