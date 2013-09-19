package edu.harvard.iq.datatags.webapp.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author michael
 */
@Entity
public class Explanation implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String name;
	
	private String shortExplanation;
	
	@Column(length = 2048)
	private String longExplanation;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortExplanation() {
		return shortExplanation;
	}

	public void setShortExplanation(String shortExplanation) {
		this.shortExplanation = shortExplanation;
	}

	public String getLongExplanation() {
		return longExplanation;
	}
	
	public void setLongExplanation(String longExplanation) {
		this.longExplanation = longExplanation;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 73 * hash + Objects.hashCode(this.name);
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
		final Explanation other = (Explanation) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.shortExplanation, other.shortExplanation)) {
			return false;
		}
		return Objects.equals(this.longExplanation, other.longExplanation);
	}

	@Override
	public String toString() {
		return "[Explanation name:" + name + ']';
	}
	
}
