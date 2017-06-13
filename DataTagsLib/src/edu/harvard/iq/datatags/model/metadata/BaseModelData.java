package edu.harvard.iq.datatags.model.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base class for model metadata classes. 
 * @author michael
 */
public abstract class BaseModelData {

    protected final List<AuthorData> authors = new ArrayList<>();
    protected final Set<String> keywords = new TreeSet<>();
    protected final List<ModelReference> references = new ArrayList<>();
    protected String subTitle;
    protected String title;

    public AuthorData add(AuthorData ad) {
        authors.add(ad);
        return ad;
    }

    public ModelReference add(ModelReference r) {
        references.add(r);
        return r;
    }

    public String addKeyword(String aKeyword) {
        keywords.add(aKeyword);
        return aKeyword;
    }

    public List<AuthorData> getAuthors() {
        return authors;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public List<ModelReference> getReferences() {
        return references;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
