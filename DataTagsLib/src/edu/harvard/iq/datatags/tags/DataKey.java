package edu.harvard.iq.datatags.tags;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Keys for values of a tag. For example, a key
 * about storage may have values like {@literal Clear}, {@literal Encrypted}, etc.
 * While the data tags class can support any keys, 
 * this is where the "official" fields are stored - a data tag instance that
 * has all of these values is considered "complete".
 * 
 * @see DataTags#isComplete() 
 * 
 * @param <>> The type of the values 
 * @author michael
 */
public class DataKey< T extends Comparable> {
	
	public static <S extends Comparable> DataKey<S> keyFor( Class<S> klass ) {
		return new DataKey<>(klass);
	}

	public static final DataKey<DuaAgreementMethod>   DuaAgreementMethod = keyFor(DuaAgreementMethod.class);
	public static final DataKey<EncryptionType> Storage  = keyFor(EncryptionType.class);
	public static final DataKey<EncryptionType> Transit  = keyFor(EncryptionType.class);
	public static final DataKey<AuthenticationType> Authentication   = keyFor(AuthenticationType.class);
	
	private static final AtomicReference<Set<DataKey<?>>> valueSet = new AtomicReference<>(null);
	
	// Automatically get all the static fields. This is in fact a "manual" enum.
	public static Set<DataKey<?>> values() {
		if ( valueSet.get() == null ) {
			Set<DataKey<?>> set = new HashSet<>();
			for ( Field f : DataKey.class.getDeclaredFields() ) {
				if ( Modifier.isStatic(f.getModifiers()) ) {
					if ( f.getType().equals(DataKey.class) ) {
						try {
							set.add( (DataKey<?>) f.get(null));
						} catch ( IllegalArgumentException | IllegalAccessException ex) {
							Logger.getLogger(DataKey.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}
			valueSet.set( set );
		}
		return valueSet.get();
	}
	
	private final Class<T> kls;

	public DataKey(Class<T> kls) {
		this.kls = kls;
	}

	public Class<T> getValueClass() {
		return kls;
	}
	
	public String title() {
		String[] c = kls.getName().split("\\.");
		return c[c.length-1];
	}
	
	@Override
	public String toString() {
		return "(" + title() + ")";
	}
}
