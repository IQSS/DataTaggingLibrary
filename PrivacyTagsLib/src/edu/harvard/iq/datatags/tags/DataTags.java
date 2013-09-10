package edu.harvard.iq.datatags.tags;

import java.util.Objects;

/**
 * The reason this whole project exists: the set of privacy tags that describe 
 * how a data set should be treated. A set is incomplete, if some of the fields
 * have {@literal null} values.
 * 
 * @author michael
 */
public class DataTags  {
	
	private ApprovalType approvalType;
	private AuthenticationType authenticationType;
	private DataUseAgreement dataUseAgreement;
	private EncryptionType transitEncryptionType;
	private EncryptionType storageEncryptionType;
	
	public DataTags(ApprovalType approvalType, AuthenticationType authenticationType, DataUseAgreement dataUseAgreement, EncryptionType transitEncryptionType, EncryptionType storageEncryptionType) {
		this.approvalType = approvalType;
		this.authenticationType = authenticationType;
		this.dataUseAgreement = dataUseAgreement;
		this.transitEncryptionType = transitEncryptionType;
		this.storageEncryptionType = storageEncryptionType;
	}
	
	public DataTags() {
		
	}

	public DataTags makeCopy() {
		return new DataTags(getApprovalType(),
				getAuthenticationType(),
				getDataUseAgreement(),
				getTransitEncryptionType(),
				getStorageEncryptionType()
			);
	}
	
	/**
	 * @return {@literal true} iff all values are set (e.g non-null).
	 */
	public boolean isComplete() {
		return (approvalType != null)            
				&& (authenticationType != null )    
				&& (dataUseAgreement != null )      
				&& (transitEncryptionType != null ) 
				&& (storageEncryptionType != null );
	}
	
	/**
	 * A partial order on privacy tag sets.{@literal a} is stricter-than {@literal b}
	 * if, they have the same non-null fields, and in each of these fields the values
	 * in {@literal a} are equal or higher (in their type's natural order) than those in {@literal b}' fields. <br />
	 * A practical use case is that if a standard requires tag set of {@literal S}, than
	 * all sets {@literal T} for which {@literal T stricter-than S} fall within that standard.
	 * 
	 * @param other The tag set we compare {@literal this} to.
	 * @return whether {@literal this} is stricter than {@literal other}.
	 */
	public boolean stricterThan( DataTags other ) {
		int res;
		int total=0;
		
		if ( getApprovalType() != null ) {
			if ( other.getApprovalType() == null ) return false; // can't compare
			res = (int)Math.signum(getApprovalType().compareTo(other.getApprovalType()));
			if ( res < 0 ) return false;
			total += res;
		} else {
			if ( other.getApprovalType() != null ) return false; // can't compare
		}
		
		if ( getAuthenticationType()!= null ) {
			if ( other.getAuthenticationType() == null ) return false; // can't compare
			res = (int)Math.signum(getAuthenticationType().compareTo(other.getAuthenticationType()));
			if ( res < 0 ) return false;
			total += res;
		} else {
			if ( other.getAuthenticationType() != null ) return false; // can't compare
		}
		
		if ( getDataUseAgreement()!= null ) {
			if ( other.getDataUseAgreement() == null ) return false; // can't compare
			res = (int) Math.signum(getDataUseAgreement().compareTo(other.getDataUseAgreement()));
			if ( res < 0 ) return false;
			total += res;
		} else {
			if ( other.getDataUseAgreement() != null ) return false; // can't compare
		}
		
		if ( getTransitEncryptionType()!= null ) {
			if ( other.getTransitEncryptionType() == null ) return false; // can't compare
			res = (int) Math.signum(getTransitEncryptionType().compareTo(other.getTransitEncryptionType()));
			if ( res < 0 ) return false;
			total += res;
		} else {
			if ( other.getTransitEncryptionType() != null ) return false; // can't compare
		}
		
		if ( getStorageEncryptionType()!= null ) {
			if ( other.getStorageEncryptionType() == null ) return false; // can't compare
			res = (int) Math.signum(getStorageEncryptionType().compareTo(other.getStorageEncryptionType()));
			if ( res < 0 ) return false;
			total += res;
		} else {
			if ( other.getStorageEncryptionType() != null ) return false; // can't compare
		}
		
		return total > 0;
	}
	
	public boolean stricterOrEqualTo( DataTags other ) {
		return stricterThan(other) || equals(other);
	}
	
	public boolean stricterThan( HarmLevel hl ) { return stricterThan( hl.tags() ); }
	public boolean stricterOrEqualTo( HarmLevel hl ) { return stricterOrEqualTo( hl.tags() ); }
	
	public DataTags composeWith( DataTags other ) {
		return new DataTags(
				getApprovalType()!=null ? getApprovalType() : other.getApprovalType(), 
				getAuthenticationType()!=null ? getAuthenticationType() : other.getAuthenticationType(), 
				getDataUseAgreement()!=null ? getDataUseAgreement() : other.getDataUseAgreement(), 
				getTransitEncryptionType()!=null ? getTransitEncryptionType() : other.getTransitEncryptionType(), 
				getStorageEncryptionType()!=null ? getStorageEncryptionType() : other.getStorageEncryptionType()
			);
	}
	
	public ApprovalType getApprovalType() {
		return approvalType;
	}

	public DataTags setApprovalType(ApprovalType approvalType) {
		this.approvalType = approvalType;
		return this;
	}

	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	public DataTags setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
		return this;
	}

	public DataUseAgreement getDataUseAgreement() {
		return dataUseAgreement;
	}

	public DataTags setDataUseAgreement(DataUseAgreement dataUseAgreement) {
		this.dataUseAgreement = dataUseAgreement;
		return this;
	}

	public EncryptionType getTransitEncryptionType() {
		return transitEncryptionType;
	}

	public DataTags setTransitEncryptionType(EncryptionType transitEncryptionType) {
		this.transitEncryptionType = transitEncryptionType;
		return this;
	}

	public EncryptionType getStorageEncryptionType() {
		return storageEncryptionType;
	}

	public DataTags setStorageEncryptionType(EncryptionType storageEncryptionType) {
		this.storageEncryptionType = storageEncryptionType;
		return this;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 83 * hash + Objects.hashCode(this.approvalType);
		hash = 83 * hash + Objects.hashCode(this.dataUseAgreement);
		hash = 83 * hash + Objects.hashCode(this.storageEncryptionType);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DataTags other = (DataTags) obj;
		if (this.approvalType != other.approvalType) {
			return false;
		}
		if (this.authenticationType != other.authenticationType) {
			return false;
		}
		if (this.dataUseAgreement != other.dataUseAgreement) {
			return false;
		}
		if (this.transitEncryptionType != other.transitEncryptionType) {
			return false;
		}
		if (this.storageEncryptionType != other.storageEncryptionType) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[PrivacyTagSet approvalType:" + approvalType 
			  	            + "authenticationType:" + authenticationType 
							+ "dataUseAgreement:" + dataUseAgreement 
							+ "transitEncryptionType:" + transitEncryptionType 
						    + "storageEncryptionType:" + storageEncryptionType + ']';
	}
	
}
