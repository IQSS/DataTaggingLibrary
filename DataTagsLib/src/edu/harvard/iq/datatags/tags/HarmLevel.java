package edu.harvard.iq.datatags.tags;

/**
 * A utility class that maps from the level of harm a dataset may cause
 to the minimal DataTags required to handle it.
 * @author michael
 */
public enum HarmLevel {
	NoRisk( 
		1,   new DataTags( DuaAgreementMethod.None, AuthenticationType.None,           EncryptionType.Clear,     EncryptionType.Clear) ),
	Minimal( 
		2,   new DataTags( DuaAgreementMethod.None, AuthenticationType.Email_or_OAuth, EncryptionType.Clear,     EncryptionType.Clear) ),
	Shame( 
		3,   new DataTags( DuaAgreementMethod.ClickThrough,  AuthenticationType.Password,      EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CivilPenalties( 
		4,   new DataTags(DuaAgreementMethod.Sign,  AuthenticationType.Password,       EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	CriminalPenalties(
		4,   new DataTags(DuaAgreementMethod.Sign,  AuthenticationType.TwoFactor,      EncryptionType.Encrypted, EncryptionType.Encrypted) ),
	MaxControl( 
		4.5, new DataTags(DuaAgreementMethod.Sign,  AuthenticationType.TwoFactor,      EncryptionType.DoubleEncryption, EncryptionType.DoubleEncryption) );
	
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
	
	/**
	 * Finds the least strict level that still complies with the tags.
	 * @param tags
	 * @return lowest level that complies with the tags.
	 */
	public static HarmLevel levelFor( DataTags tags ) {
		for ( HarmLevel l : values() ) {
			if ( l.tags().stricterOrEqualTo(tags) ) {
				return l;
			}
		}
		return null;
	}
}
