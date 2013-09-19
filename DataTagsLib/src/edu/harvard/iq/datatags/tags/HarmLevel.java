package edu.harvard.iq.datatags.tags;

/**
 * A utility class that maps from the level of harm a dataset may cause
 to the minimal DataTags required to handle it.
 * @author michael
 */
public enum HarmLevel {
	NoRisk( 
		1,   new DataTags(ApprovalType.None,   AuthenticationType.None,           DataUseAgreement.None,      EncryptionType.Clear,     EncryptionType.Clear) ),
	Forsaken( 
		2,   new DataTags(ApprovalType.None,   AuthenticationType.Email_or_OAuth, DataUseAgreement.None,      EncryptionType.Clear,     EncryptionType.Clear) ),
	Shamed( 
		3,   new DataTags(ApprovalType.Online, AuthenticationType.Password,       DataUseAgreement.ClickThrough,  EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CivilPenalties( 
		4,   new DataTags(ApprovalType.Signed, AuthenticationType.Password,       DataUseAgreement.Sign,  EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CriminalPenalties(
		4,   new DataTags(ApprovalType.Signed, AuthenticationType.TwoFactor,      DataUseAgreement.Sign,  EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	MaxControl( 
		4.5, new DataTags(ApprovalType.Signed, AuthenticationType.TwoFactor,      DataUseAgreement.Sign,  EncryptionType.DoubleEncryption, EncryptionType.DoubleEncryption) );
	
	private final DataTags tags; 
	private final double harvardSecurityLevel;
	
	HarmLevel( double aSecurityLevel, DataTags aTagSet ) {
		harvardSecurityLevel = aSecurityLevel;
		tags = aTagSet;
	}
	
	public DataTags tags() {
		return tags;
	}
	
	public double harvardSecurityLevel() {
		return harvardSecurityLevel;
	}
}
