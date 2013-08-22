package edu.harvard.iq.privacytags.model;

/**
 * A utility class that maps from the level of harm a dataset may cause
 * to the minimal PrivacyTagSet required to handle it.
 * @author michael
 */
public enum HarmLevel {
	NoRisk( 
		1,   new PrivacyTagSet(ApprovalType.None,   AuthenticationType.None,           DataUseAgreement.None,      EncryptionType.Clear,     EncryptionType.Clear) ),
	Forsaken( 
		2,   new PrivacyTagSet(ApprovalType.None,   AuthenticationType.Email_or_OAuth, DataUseAgreement.None,      EncryptionType.Clear,     EncryptionType.Clear) ),
	Shamed( 
		3,   new PrivacyTagSet(ApprovalType.Online, AuthenticationType.Password,       DataUseAgreement.Required,  EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CivilPenalties( 
		4,   new PrivacyTagSet(ApprovalType.Signed, AuthenticationType.Password,       DataUseAgreement.Required,  EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CriminalPenalties(
		4,   new PrivacyTagSet(ApprovalType.Signed, AuthenticationType.TwoFactor,      DataUseAgreement.Required,  EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	MaxControl( 
		4.5, new PrivacyTagSet(ApprovalType.Signed, AuthenticationType.TwoFactor,      DataUseAgreement.Required,  EncryptionType.DoubleEncryption, EncryptionType.DoubleEncryption) );
	
	private final PrivacyTagSet tags; 
	private final double harvardSecurityLevel;
	
	HarmLevel( double aSecurityLevel, PrivacyTagSet aTagSet ) {
		harvardSecurityLevel = aSecurityLevel;
		tags = aTagSet;
	}
	
	public PrivacyTagSet tags() {
		return tags;
	}
	
	public double harvardSecurityLevel() {
		return harvardSecurityLevel;
	}
}
