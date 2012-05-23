package de.catma.repository.db.model;

// Generated 22.05.2012 21:58:37 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * UnseparableCharsequence generated by hbm2java
 */
@Entity
@Table(name = "unseparable_charsequence", catalog = "CatmaRepository")
public class DBUnseparableCharsequence implements java.io.Serializable {

	private Integer uscId;
	private String charsequence;
	private int sourceDocumentId;

	public DBUnseparableCharsequence() {
	}

	public DBUnseparableCharsequence(String charsequence, int sourceDocumentId) {
		this.charsequence = charsequence;
		this.sourceDocumentId = sourceDocumentId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "uscID", unique = true, nullable = false)
	public Integer getUscId() {
		return this.uscId;
	}

	public void setUscId(Integer uscId) {
		this.uscId = uscId;
	}

	@Column(name = "charsequence", nullable = false, length = 45)
	public String getCharsequence() {
		return this.charsequence;
	}

	public void setCharsequence(String charsequence) {
		this.charsequence = charsequence;
	}

	@Column(name = "sourceDocumentID", nullable = false)
	public int getSourceDocumentId() {
		return this.sourceDocumentId;
	}

	public void setSourceDocumentId(int sourceDocumentId) {
		this.sourceDocumentId = sourceDocumentId;
	}

}