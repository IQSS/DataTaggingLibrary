package edu.harvard.iq.datatags.model.metadata;

import java.util.Objects;

/**
 * A person who (co-)authored the model.
 * @author michael
 */
public class PersonAuthorData extends AuthorData {
    
    private String orcid;
    private String affiliation;
    
    @Override
    public String displayString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if ( affiliation != null ) {
            sb.append(", ").append(affiliation);
        }
        if ( orcid != null ) {
            sb.append(", ORCiD:").append(orcid);
        }
        sb.append(".");
        return sb.toString();
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }


    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.orcid);
        hash = 67 * hash + Objects.hashCode(this.name);
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
        final PersonAuthorData other = (PersonAuthorData) obj;
        if (!equals(other)) 
            return false;
        if (!Objects.equals(this.orcid, other.orcid))
            return false;
       
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.affiliation, other.affiliation)) {
            return false;
        }
        return Objects.equals(this.email, other.email);
    }

    @Override
    public String toString() {
        return "[PersonAuthorData name:" + name + ']';
    } 
    

}
