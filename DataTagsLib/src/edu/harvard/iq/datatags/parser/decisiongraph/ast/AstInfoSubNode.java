/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.Objects;

/**
 *
 * @author mor_vilozni
 */
public class AstInfoSubNode {
    private final String bodyText;
	
    public AstInfoSubNode( String text ) {
		bodyText = text;
    }
    
    public String getText() { 
		return bodyText;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.bodyText);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof AstInfoSubNode) ) {
			return false;
		}
		final AstInfoSubNode other = (AstInfoSubNode) obj;
		
		return Objects.equals(this.bodyText, other.bodyText);
	}
	
	
}
