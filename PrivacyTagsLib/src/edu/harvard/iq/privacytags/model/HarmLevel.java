package edu.harvard.iq.privacytags.model;

/**
 * A utility class that maps from the level of harm a dataset may cause
 * to the minimal PrivacyTagSet required to handle it.
 * @author michael
 */
public enum HarmLevel {
	NoRisk( new PrivacyTagSet(ApprovalType.None, AuthenticationType.None, DataUseAgreement.None, EncryptionType.Clear, EncryptionType.Clear) ),
	Forsaken( new PrivacyTagSet(ApprovalType.None, AuthenticationType.Email_or_OAuth, DataUseAgreement.None, EncryptionType.Clear, EncryptionType.Clear) ),
	Shamed( new PrivacyTagSet(ApprovalType.Online, AuthenticationType.Password, DataUseAgreement.Required, EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CivilPenalties( new PrivacyTagSet(ApprovalType.Signed, AuthenticationType.Password, DataUseAgreement.Required, EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CriminalPenalties( new PrivacyTagSet(ApprovalType.Signed, AuthenticationType.TwoFactor, DataUseAgreement.Required, EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	MaxControl( new PrivacyTagSet(ApprovalType.Signed, AuthenticationType.TwoFactor, DataUseAgreement.Required, EncryptionType.DoubleEncryption, EncryptionType.DoubleEncryption) );
	
	private final PrivacyTagSet tags; 

	HarmLevel( PrivacyTagSet aTagSet ) {
		tags = aTagSet;
	}
	
	public PrivacyTagSet tags() {
		return tags;
	}
}
