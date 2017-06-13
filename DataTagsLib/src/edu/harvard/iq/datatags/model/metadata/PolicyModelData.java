package edu.harvard.iq.datatags.model.metadata;

import java.nio.file.Path;
import java.time.LocalDate;

/**
 * This class contains data about a policy model. This includes paths to the 
 * model's file, and metadata.
 * 
 * @author michael
 */
public class PolicyModelData extends BaseModelData {
   
    public enum AnswerTransformationMode {
        /** Do not change answers */
        Verbatim,
        /** Yes answers appear first (Yes/No nodes only)*/
        YesFirst,
        /** Yes answers appear last (Yes/No nodes only)*/
        YesLast
    }
    private String version;
    private String doi;
    private String rootTypeName;
    private LocalDate releaseDate;
    private Path metadataFile, policySpacePath, decisionGraphPath;
    private AnswerTransformationMode answerTransformationMode = AnswerTransformationMode.YesFirst;
        

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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Path getMetadataFile() {
        return metadataFile;
    }

    public void setMetadataFile(Path metadataFile) {
        this.metadataFile = metadataFile;
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


    public String getRootTypeName() {
        return rootTypeName;
    }

    public void setRootTypeName(String rootTypeName) {
        this.rootTypeName = rootTypeName;
    }

    public void setAnswerTransformationMode(AnswerTransformationMode answerTransformationMode) {
        this.answerTransformationMode = answerTransformationMode;
    }

    public AnswerTransformationMode getAnswerTransformationMode() {
        return answerTransformationMode;
    }

    
}
