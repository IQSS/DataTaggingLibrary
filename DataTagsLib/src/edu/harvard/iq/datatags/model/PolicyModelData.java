package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.metadata.AuthorData;
import edu.harvard.iq.datatags.model.metadata.ModelReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class contains data about a policy model. This includes paths to the 
 * model's file, and metadata.
 * 
 * @author michael
 */
public class PolicyModelData {
   
    public enum AnswerTransformationMode {
        /** Do not change answers */
        Verbatim,
        /** Yes answers appear first (Yes/No nodes only)*/
        YesFirst,
        /** Yes answers appear last (Yes/No nodes only)*/
        YesLast
    }
    private String title;
    private String version;
    private String doi;
    private Calendar releaseDate;
    private Path baseFolder, policySpacePath, decisionGraphPath;
    private final List<AuthorData> authors = new ArrayList<>();
    private final Set<ModelReference> references = new HashSet<>();
    private final Set<String> keywords = new TreeSet<>();
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Calendar getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Calendar releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Path getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(Path baseFolder) {
        this.baseFolder = baseFolder;
    }

    public Path getPolicySpacePath() {
        return policySpacePath;
    }

    public void setPolicySpacePath(Path policySpacePath) {
        this.policySpacePath = policySpacePath;
    }

    public Path getDecisionGraphPath() {
        return decisionGraphPath;
    }

    public void setDecisionGraphPath(Path decisionGraphPath) {
        this.decisionGraphPath = decisionGraphPath;
    }

    public Set<ModelReference> getReferences() {
        return references;
    }
    
    public ModelReference add(ModelReference r) {
        references.add(r);
        return r;
    }
    
    public List<AuthorData> getAuthors() {
        return authors;
    }
    
    public AuthorData add( AuthorData ad ) {
        authors.add(ad);
        return ad;
    }

    public Set<String> getKeywords() {
        return keywords;
    }
    
    public String addKeyword( String aKeyword ) {
        keywords.add(aKeyword);
        return aKeyword;
    }
    
}
