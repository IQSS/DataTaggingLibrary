package edu.harvard.iq.datatags.tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The reason this whole project exists: the set of privacy tags that describe 
 * how a data set should be treated. A set is incomplete, if some of the fields
 * have {@literal null} values.
 * 
 * @author michael
 */
public class DataTags  {
	private final Map values;
	
	public DataTags(ApprovalType approvalType, AuthenticationType authenticationType, DataUseAgreement dataUseAgreement, EncryptionType transitEncryptionType, EncryptionType storageEncryptionType) {
		this();
		put( DataKey.Approval, approvalType );
		put( DataKey.Authentication, authenticationType );
		put( DataKey.DataUseAgreement, dataUseAgreement );
		put( DataKey.Transit, transitEncryptionType );
		put( DataKey.Storage, storageEncryptionType );
	}
	
	public DataTags() {
		values = new HashMap();
	}
	
	private DataTags( Map m ) {
		values = new HashMap(m);
	}
	
	public DataTags makeCopy() {
		return new DataTags(values);
	}
	
	/**
	 * Sets a tag key to a value
	 * @param k the key
	 * @param <T> the type of data the key can refer to
	 * @param val the data
	 * @return {@code this}, for call chaining.
	 */
	public final <T extends Comparable> DataTags put( DataKey<T> k, T val ) {
		if ( val == null ) {
			values.remove(k);
		} else {
			values.put(k,val);
		}

		return this;
	}
	
	public final <T extends Comparable> T get( DataKey<T> k ) {
		return k.getValueClass().cast(values.get(k));
	}
	
	public Set<DataKey<?>> keys() {
		return values.keySet();
	}
	
	/**
	 * @return {@literal true} iff all values are set (e.g non-null).
	 */
	public boolean isComplete() {
		return keys().containsAll(DataKey.values());
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
		
		// see if we can compare
		if ( other == null ) return false;
		if ( ! keys().equals(other.keys()) ) return false;
		if ( equals(other) ) return false;
		
		for ( DataKey dk : keys() ) {
			Comparable mine = get(dk);
			Comparable hers = other.get(dk);
			if ( mine.compareTo(hers) < 0 ) return false;
		}
		
		return true;
	}
	
	public boolean stricterOrEqualTo( DataTags other ) {
		return stricterThan(other) || equals(other);
	}
	
	public boolean stricterThan( HarmLevel hl ) { return stricterThan( hl.tags() ); }
	public boolean stricterOrEqualTo( HarmLevel hl ) { return stricterOrEqualTo( hl.tags() ); }
	
	public DataTags composeWith( DataTags other ) {
		Map newMap = new HashMap();
		Set<DataKey<?>> allKeys = new HashSet<>( keys() );
		allKeys.addAll( other.keys() );
		for ( DataKey dk : allKeys ) {
			newMap.put(dk,  get(dk)!=null ? get(dk) : other.get(dk) );
		}
		return new DataTags( newMap );
	}
	
	public ApprovalType getApprovalType() {
		return get( DataKey.Approval );
	}

	public DataTags setApprovalType(ApprovalType approvalType) {
		return put( DataKey.Approval, approvalType );
	}

	public AuthenticationType getAuthenticationType() {
		return get( DataKey.Authentication );
	}

	public DataTags setAuthenticationType(AuthenticationType authenticationType) {
		return put( DataKey.Authentication, authenticationType );
	}

	public DataUseAgreement getDataUseAgreement() {
		return get( DataKey.DataUseAgreement );
	}

	public DataTags setDataUseAgreement(DataUseAgreement dataUseAgreement) {
		return put( DataKey.DataUseAgreement, dataUseAgreement );
	}

	public EncryptionType getTransitEncryptionType() {
		return get( DataKey.Transit );
	}

	public DataTags setTransitEncryptionType(EncryptionType transitEncryptionType) {
		return put( DataKey.Transit, transitEncryptionType );
	}

	public EncryptionType getStorageEncryptionType() {
		return get( DataKey.Storage );
	}

	public DataTags setStorageEncryptionType(EncryptionType storageEncryptionType) {
		return put( DataKey.Storage, storageEncryptionType );
	}
	
	/**
	 * Returns the underlying map of values.
	 * Override with caution, as no type checks are done on this.
	 * @return the internal map, holding all the values of the instance.
	 */
	protected Map getValuesMap() {
		return values;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 83 * hash + Objects.hashCode(getApprovalType());
		hash = 83 * hash + Objects.hashCode(getDataUseAgreement());
		hash = 83 * hash + Objects.hashCode(getStorageEncryptionType());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if ( ! (obj instanceof DataTags) ) return false;
		return ((DataTags)obj).getValuesMap().equals(getValuesMap());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for ( DataKey dk : keys() ) {
			sb.append( dk ).append( ": " ).append( get( dk ) );
		}
		return "[PrivacyTagSet " + sb.toString() + ']';
	}
	
}
