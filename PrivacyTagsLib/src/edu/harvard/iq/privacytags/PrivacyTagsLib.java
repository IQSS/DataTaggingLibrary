/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.privacytags;

import edu.harvard.iq.privacytags.model.HarmLevel;
import edu.harvard.iq.privacytags.model.PrivacyTagSet;

/**
 *
 * @author michael
 */
public class PrivacyTagsLib {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		PrivacyTagSet pts = new PrivacyTagSet();
		pts.stricterThan(HarmLevel.Shamed);
	}
	
}
