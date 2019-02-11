package edu.harvard.iq.datatags.model.metadata;

import edu.harvard.iq.datatags.externaltexts.MarkupFormat;
import edu.harvard.iq.datatags.externaltexts.MarkupString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    /** 
     * A map of the readme files this localization can 
     */
    protected final Map<MarkupFormat,MarkupString> readmes = new EnumMap<>(MarkupFormat.class);

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
    
     public Optional<MarkupFormat> getBestReadmeFormat() {
        return Arrays.stream(MarkupFormat.values())
                .filter( fmt -> readmes.keySet().contains(fmt) )
                .findFirst();
    }
    
    public MarkupString getReadme(MarkupFormat fmt) {
        return readmes.get(fmt);
    }
    
    public void addReadme( MarkupFormat fmt, String content ) {
        readmes.put(fmt, new MarkupString(fmt,content) );
    }
    
    /**
     * Removes versions of localization data from {@code this}, based on the 
     * passed format. Useful for server situations, where it makes sense to 
     * remove all versions that will not be used.
     * 
     * @param keepFormat The readme format to keep.
     */
    public void keepReadme(MarkupFormat keepFormat) {
        Arrays.stream(MarkupFormat.values())
               .filter( f->! f.equals(keepFormat) )
                .forEach( f -> readmes.remove(f) );
    }
    
}
