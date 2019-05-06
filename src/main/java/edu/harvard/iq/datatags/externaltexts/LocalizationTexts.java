/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.externaltexts;

import java.util.Objects;

/**
 *
 * @author mor
 */
public class LocalizationTexts {

    public final String name;
    public final String bigNote;
    public final String smallNote;
    
    public LocalizationTexts(String name, String smallNote, String bigNote) {
        this.name = name;
        this.smallNote = smallNote;
        this.bigNote = bigNote;
    } 

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.bigNote);
        hash = 67 * hash + Objects.hashCode(this.smallNote);
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
        final LocalizationTexts other = (LocalizationTexts) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.bigNote, other.bigNote)) {
            return false;
        }
        if (!Objects.equals(this.smallNote, other.smallNote)) {
            return false;
        }
        return true;
    }
    
    
}
