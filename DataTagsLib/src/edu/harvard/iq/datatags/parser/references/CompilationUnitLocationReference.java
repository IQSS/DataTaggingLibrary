package edu.harvard.iq.datatags.parser.references;

import java.util.Objects;

/**
 * A reference to a place that has code. Normally that would
 * be a file, but we allow more flexibility here.
 * @author michael
 */
public class CompilationUnitLocationReference {
	private int line;
	private int column;
	private String location;

	public CompilationUnitLocationReference(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public CompilationUnitLocationReference(int line, int column, String location) {
		this.line = line;
		this.column = column;
		this.location = location;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public String getLocation() {
		return location;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + this.column;
		hash = 29 * hash + this.line;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( obj instanceof CompilationUnitLocationReference) {
			final CompilationUnitLocationReference other = (CompilationUnitLocationReference) obj;
			if (this.line != other.line) {
				return false;
			}
			if (this.column != other.column) {
				return false;
			}
			return Objects.equals(this.location, other.location);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[CompilationUnitLocationReference line:" + line + " column:" + column + " location:" + location + ']';
	}
	
	
}
