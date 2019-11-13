/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.policymodels.parser.decisiongraph;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mor_vilozni
 */
public class MemoryContentReader implements ContentReader{
    Map<Path, String> pathToString = new HashMap<>();
    
    public MemoryContentReader(Map<Path, String> aPathToString){
        pathToString = aPathToString;
    }

    @Override
    public String getContent(Path path) {
        return pathToString.get(path);
    }
}
