/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;

/**
 *
 * @author mor
 */
public class BadSlotLookupException extends DataTagsParseException {
    
    public BadSlotLookupException(AstNode offending, String message) {
        super(offending, message);
    }
    
}
