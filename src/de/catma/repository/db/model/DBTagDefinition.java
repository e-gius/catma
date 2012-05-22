package de.catma.repository.db.model;

// Generated 22.05.2012 21:58:37 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 * Tagdefinition generated by hbm2java
 */
@Entity
@Table(name = "tagdefinition", catalog = "CatmaRepository", uniqueConstraints = @UniqueConstraint(columnNames = {
		"uuid", "version" }))
public class DBTagDefinition implements java.io.Serializable {

	private Integer tagDefinitionId;
	private Date version;
	private byte[] uuid;
	private String name;
	private int tagsetDefinitionId;
	private Integer parentId;

	public DBTagDefinition() {
	}

	public DBTagDefinition(byte[] uuid, String name, int tagsetDefinitionId) {
		this.uuid = uuid;
		this.name = name;
		this.tagsetDefinitionId = tagsetDefinitionId;
	}

	public DBTagDefinition(byte[] uuid, String name, int tagsetDefinitionId,
			Integer parentId) {
		this.uuid = uuid;
		this.name = name;
		this.tagsetDefinitionId = tagsetDefinitionId;
		this.parentId = parentId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tagDefinitionID", unique = true, nullable = false)
	public Integer getTagDefinitionId() {
		return this.tagDefinitionId;
	}

	public void setTagDefinitionId(Integer tagDefinitionId) {
		this.tagDefinitionId = tagDefinitionId;
	}

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "version", nullable = false, length = 19)
	public Date getVersion() {
		return this.version;
	}

	public void setVersion(Date version) {
		this.version = version;
	}

	@Column(name = "uuid", nullable = false)
	public byte[] getUuid() {
		return this.uuid;
	}

	public void setUuid(byte[] uuid) {
		this.uuid = uuid;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "tagsetDefinitionID", nullable = false)
	public int getTagsetDefinitionId() {
		return this.tagsetDefinitionId;
	}

	public void setTagsetDefinitionId(int tagsetDefinitionId) {
		this.tagsetDefinitionId = tagsetDefinitionId;
	}

	@Column(name = "parentID")
	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

}
