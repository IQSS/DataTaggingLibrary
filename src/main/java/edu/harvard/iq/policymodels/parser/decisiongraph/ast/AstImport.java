package edu.harvard.iq.policymodels.parser.decisiongraph.ast;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 *
 * @author mor_vilozni
 */
public class AstImport {

    private Path initialPath;
    private Path path;
    private String name;

    public AstImport(String path, String name) {
        this.path = Paths.get(path);
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setInitalPath(Path initialPath) {
        this.initialPath = initialPath;
    }
    
    public Path getInitialPath() {
        return initialPath;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.name);
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
        final AstImport other = (AstImport) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "[AstImport path:" + path + " name:" + name + ']';
    }

}
