package net.cb.dcm.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: DeviceGroupTagRelation
 * It's used only for proper database table generation.
 * Instead use directly {@link DeviceGroup#getTags()}
 *
 */
@Entity
@Table(name = "dev_group_tags",
uniqueConstraints = { @UniqueConstraint(columnNames = {"DEVICE_GROUP_ID", "TAG_ID"})} )
public class DeviceGroupTagRelation implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public DeviceGroupTagRelation() {
		super();
	}
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "DEVICE_GROUP_ID")
	private long deviceGroupId;
	
	@Column(name = "TAG_ID")
	private long tagId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDeviceGroupId() {
		return deviceGroupId;
	}

	public void setDeviceGroupId(long deviceGroupId) {
		this.deviceGroupId = deviceGroupId;
	}

	public long getTagId() {
		return tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}
	
	
}
