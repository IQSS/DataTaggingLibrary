/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.List;

/**
 *
 * @author mor_vilozni
 */
public class ParsedFile {

 
    List<? extends AstNode> astNodes;
    List<AstImport> imports;

    public ParsedFile(List<? extends AstNode> astNodes, List<AstImport> imports) {
        this.astNodes = astNodes;
        this.imports = imports;
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
}
