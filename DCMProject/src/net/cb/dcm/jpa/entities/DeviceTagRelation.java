package net.cb.dcm.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

//@Entity
//@Table(name="dev_tag_rel")
public class DeviceTagRelation {
	
	
//	@Id
//	@GeneratedValue
	private long id;
	
//	@Column(name="device_id")
	private long deviceId;
	
//	@Column(name="tag_id")
	private long tagId;
	
//	@ManyToOne
//	@PrimaryKeyJoinColumn(name="DEVICEID", referencedColumnName="ID")
	/* if this JPA model doesn't create a table for the "PROJ_EMP" entity,
	 * please comment out the @PrimaryKeyJoinColumn, and use the ff:
	 *  @JoinColumn(name = "deviceId", updatable = false, insertable = false)
	 * or @JoinColumn(name = "deviceId", updatable = false, insertable = false, referencedColumnName = "id")
	*/
	private Device device;
	
//	@ManyToOne
//	@PrimaryKeyJoinColumn(name="TAGID", referencedColumnName="ID")
	/* if this JPA model doesn't create a table for the "PROJ_DeviceTagRelation" entity,
	 * please comment out the @PrimaryKeyJoinColumn, and use the ff:
	 *  @JoinColumn(name = "tagId", updatable = false, insertable = false)
	 * or @JoinColumn(name = "tagId", updatable = false, insertable = false, referencedColumnName = "id")
	*/
	private Tag tag;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public long getTagId() {
		return tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
