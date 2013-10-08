package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.TagType;
import java.util.Objects;

/**
 *
 * @author michael
 */
public abstract class TagValue<T extends TagType> {
	private String name;
	private T type;
	private String info;

	public TagValue() {
	}

	public TagValue(String name, T type, String info) {
		this.name = name;
		this.type = type;
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getType() {
		return type;
	}

	public void setType(T type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.name);
		hash = 37 * hash + Objects.hashCode(this.type);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( !(obj instanceof TagValue) ) {
			return false;
		}
		final TagValue<?> other = (TagValue<?>) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.type, other.type)) {
			return false;
		}
		return Objects.equals(this.info, other.info);
	}

	@Override
	public String toString() {
		return "[TagValue name:" + name + " type:" + type + ']';
	}
	
}
