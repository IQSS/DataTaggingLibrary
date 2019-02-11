/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph;

import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author mor_vilozni
 */
public interface ContentReader {
    String getContent(Path path) throws IOException;
}
