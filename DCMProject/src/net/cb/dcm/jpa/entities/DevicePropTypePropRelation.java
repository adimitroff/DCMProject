package net.cb.dcm.jpa.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: DevicePropTypePropRelation
 * It's used only for proper database table generation.
 * Instead use directly {@link DevicePropertyType#getProperties()}
 *
 */
@Entity
@Table(name = "dev_prop_type_properties")
public class DevicePropTypePropRelation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(name = "DEV_PROP_TYPE_ID")
	private long devicePropertyTypeId; 
	
	@Column(name = "PROPERTY_ID")
	private long propertyId; 
	

	public DevicePropTypePropRelation() {
		super();
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getDevicePropertyTypeId() {
		return devicePropertyTypeId;
	}


	public void setDevicePropertyTypeId(long devicePropertyTypeId) {
		this.devicePropertyTypeId = devicePropertyTypeId;
	}


	public long getPropertyId() {
		return propertyId;
	}


	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}
   
}
