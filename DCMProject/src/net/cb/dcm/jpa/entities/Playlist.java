package net.cb.dcm.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class Playlist {
	@Id
	@GeneratedValue
	private long id;
	
	@Column(length=50)
	private String name;
	
	@Column(length=250)
	private String description;
	
	private int priority;
	
	private boolean active;
	
	private boolean _default;
	
	@Column(name="valid_from")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;
	
	@Column(name="valid_to")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDefault() {
		return _default;
	}

	public void setDefault(boolean _default) {
		this._default = _default;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	} 
}
