package net.cb.dcm.jpa.entities;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * 
 *
 */
@Entity
@Table(name = "device_statuses")
public class DeviceStatus implements Serializable {

	private static final long serialVersionUID = 1L;
	   
	@Id
	@GeneratedValue
	private int id;
	
	@Column(length = 100, nullable = false)
	private String name;
	
	@Column(length = 20, nullable = false, unique = true)
	private String code;

	public DeviceStatus() {
		super();
	}   
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}   
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
   
}
