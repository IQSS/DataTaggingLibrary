package edu.harvard.iq.datatags.webapp.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Contains extra data for a {@link DecisionTreeNode}, e.g better
 * explanatory texts.
 * @author michael
 */
@Entity
public class DecisionTreeNodeExtraData implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String treeNodeId;
	private String explanation;
	private String helpText;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTreeNodeId() {
		return treeNodeId;
	}

	public void setTreeNodeId(String treeNodeId) {
		this.treeNodeId = treeNodeId;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DecisionTreeNodeExtraData)) {
			return false;
		}
		DecisionTreeNodeExtraData other = (DecisionTreeNodeExtraData) object;
		return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
	}

	@Override
	public String toString() {
		return "[DecisionTreeNodeExtraData id:" + id + " ]";
	}
	
}
