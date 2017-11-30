package net.cb.dcm.jpa.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: DeviceGroup
 *
 */
@Entity
@Table(name="device_group")
public class DeviceGroup implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public DeviceGroup() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String name;
	
	@Column
	private String description;
	
	@ManyToMany
	@JoinTable(name = "dev_group_devices", 
	joinColumns = @JoinColumn(name = "DEVICE_GROUP_ID", referencedColumnName = "ID"), 
	inverseJoinColumns = @JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID"))
	private List<Device> devices;
	
	@ManyToMany
	@JoinTable(name = "dev_group_tags", 
	joinColumns = @JoinColumn(name = "DEVICE_GROUP_ID", referencedColumnName = "ID"), 
	inverseJoinColumns = @JoinColumn(name = "TAG_ID", referencedColumnName = "ID"))
	private List<Tag>  tags;

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


	public List<Device> getDevices() {
		return devices;
	}


	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}


	public List<Tag> getTags() {
		return tags;
	}


	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
   
}
