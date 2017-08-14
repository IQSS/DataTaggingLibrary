/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mor_vilozni
 */
public class StringContentReader implements ContentReader{

    /**
     *
     * @param path
     * @return
     */
    @Override
    public String getContent(Path path) throws IOException{
      return (new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
    }
    
}
