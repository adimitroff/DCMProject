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
 * Entity implementation class for Entity: DeviceGroupDeviceRelation
 * It's used only for proper database table generation.
 * Instead use directly {@link DeviceGroup#getDevices()} or {@link Device#getGroups()}
 */
@Entity
@Table(name = "dev_group_devices", 
uniqueConstraints = { @UniqueConstraint(columnNames = {"DEVICE_GROUP_ID", "DEVICE_ID"})})
public class DeviceGroupDeviceRelation implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public DeviceGroupDeviceRelation() {
		super();
	}
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "DEVICE_GROUP_ID")
	private long deviceGroupId;
	
	@Column(name = "DEVICE_ID")
	private long deviceId;

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

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	
	
}
