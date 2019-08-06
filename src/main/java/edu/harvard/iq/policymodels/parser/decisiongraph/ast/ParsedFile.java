package edu.harvard.iq.policymodels.parser.decisiongraph.ast;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Contains a single decision graph file, in parsed form.
 * 
 * @author mor_vilozni
 */
public class ParsedFile {
 
    private final List<? extends AstNode> astNodes;
    private final List<AstImport> imports;  // for validation
    private final Map<String, Path> importsById = new HashMap<>();
    
    public ParsedFile(List<AstImport> imports, List<? extends AstNode> astNodes) {
        this.astNodes = astNodes;
        this.imports = imports;
        imports.forEach(imp->importsById.put(imp.getName(), imp.getPath()));
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
 
    public List<AstImport> getImports() {
        return imports;
    }
    
    public Set<String> getimportNames() {
        return importsById.keySet();
    }
    
    public Path getImportPath(String importName ) {
        return importsById.get(importName);
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
