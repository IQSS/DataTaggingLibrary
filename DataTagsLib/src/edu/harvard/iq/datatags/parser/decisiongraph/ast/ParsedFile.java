package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author mor_vilozni
 */
public class ParsedFile {

 
    List<? extends AstNode> astNodes;
    List<AstImport> imports;

    public ParsedFile(List<AstImport> imports, List<? extends AstNode> astNodes) {
        this.astNodes = astNodes;
        this.imports = imports;
    }
    
    public ParsedFile(AstImport imports, List<? extends AstNode> astNodes) {
        this.astNodes = astNodes;
        this.imports = Arrays.asList(imports);
    }

    public boolean add(AstImport e) {
        return imports.add(e);
    }

    public List<? extends AstNode> getAstNodes() {
        return astNodes;
    }

    public void setAstNodes(List<? extends AstNode> astNodes) {
        this.astNodes = astNodes;
    }

    public List<AstImport> getImports() {
        return imports;
    }

    public void setImports(List<AstImport> imports) {
        this.imports = imports;
    }
    
    @Override
    public String toString() {
        return "[ParsedFile imports:" + imports.toString() + " ast:" + astNodes.toString() + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.imports);
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
        final ParsedFile other = (ParsedFile) obj;
        if (!Objects.equals(this.astNodes, other.astNodes)) {
            return false;
        }
        return Objects.equals(this.imports, other.imports);
    }

    
    
}
