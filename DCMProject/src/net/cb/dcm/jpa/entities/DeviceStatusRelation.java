package net.cb.dcm.jpa.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity DeviceStatusRelation
 *
 */
@Entity
@Table(name="devices_status_relation", 
uniqueConstraints = @UniqueConstraint(columnNames={"device_id", "device_status_id"}))
public class DeviceStatusRelation implements Serializable {

	private static final long serialVersionUID = 1L;
	   
	@Id
	private long id;
	
	@ManyToOne
	@JoinColumn(name="device_id")
	private Device device;
	
	@ManyToOne
	@JoinColumn(name="device_status_id")
	private DeviceStatus deviceStatus;
	
	@Column(columnDefinition="text")
	private String value;

	public DeviceStatusRelation() {
		super();
	}   
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}   
  
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
   
}
