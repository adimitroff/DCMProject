package net.cb.dcm.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.cb.dcm.enums.DevProcedureType;

/**
 * Entity implementation class for Entity: DeviceProcedures
 *
 */
@Entity
@Table(name="device_procedures")
public class DeviceProcedure implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public DeviceProcedure() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "device_id")
	private Device device;
	
	@Column(name = "EXECUTION_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date executionTime;
	
	@Column(name = "PROCEDURE_TYPE")
	private DevProcedureType procedureType;
	
	@Column(name = "LAST_EXECUTED_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastExecutedTime;
	
	@Column
	private String value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Date getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}

	public DevProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(DevProcedureType procedureType) {
		this.procedureType = procedureType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getLastExecutedTime() {
		return lastExecutedTime;
	}

	public void setLastExecutedTime(Date lastExecutedTime) {
		this.lastExecutedTime = lastExecutedTime;
	}
	
	
   
}
