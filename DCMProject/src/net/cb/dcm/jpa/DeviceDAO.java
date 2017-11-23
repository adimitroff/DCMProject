package net.cb.dcm.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import net.cb.dcm.enums.DevProcedureType;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DeviceProcedure;
import net.cb.dcm.jpa.entities.DevicePropertyType;
import net.cb.dcm.jpa.entities.DeviceStatus;
import net.cb.dcm.jpa.entities.DeviceStatusValue;
import net.cb.dcm.jpa.entities.MediaContent;
import net.cb.dcm.jpa.entities.Property;
import net.cb.dcm.jpa.entities.Tag;

public class DeviceDAO extends GenericDao<Device> {

	public static final String PROPERTY_REQUEST_COUNTER = "REQUEST_COUNTER";
	public static final String PROPERTY_UPDATE_DATA_COUNTER = "UPDATE_DATA_COUNTER";
	public static final String PROPERTY_USED_DISK_SPACE = "USED_DISK_SPACE";
	public static final String PROPERTY_FREE_DISK_SPACE = "FREE_DISK_SPACE";
	public static final String PROPERTY_PLAYING_MEDIA_NAME = "PLAYING_MEDIA_NAME";
	public static final String PROPERTY_PLAYING_MEDIA_TIME = "PLAYING_MEDIA_TIME";

	public Device findDeviceByIp(String ip) {
		TypedQuery<Device> query = entityManager.createQuery("SELECT d FROM Device d WHERE d.ip = :ip", Device.class);
		List<Device> foundDevices = query.setParameter("ip", ip).getResultList();
		if (foundDevices.size() == 1) {
			return foundDevices.get(0);
		} else {
			return null;
		}
	}

	public Device registerNewDevice(String ip) {
		Device device = new Device();
		device.setIp(ip);
		device.setName("Samsung Tv " + ip);
		DevicePropertyType devicePropertyType = entityManager.find(DevicePropertyType.class, 1l);
		device.setDevicePropertyType(devicePropertyType);
		
		// Add  procedures
		ArrayList<DeviceProcedure> deviceProcedures = new ArrayList<DeviceProcedure>();
		// Add Wake procedure
		DeviceProcedure procedure = new DeviceProcedure();
		procedure.setDevice(device);
		procedure.setProcedureType(DevProcedureType.WAKE);
		Calendar cal = Calendar.getInstance();
		cal.set(2000, 1, 1, 7, 0, 0);
		procedure.setExecutionTime(cal.getTime());
		deviceProcedures.add(procedure);
		// Add Sleep procedure
		procedure = new DeviceProcedure();
		procedure.setDevice(device);
		procedure.setProcedureType(DevProcedureType.SLEEP);
		cal.set(2000, 1, 1, 20, 0, 0);
		procedure.setExecutionTime(cal.getTime());
		deviceProcedures.add(procedure);
		
		device.setDeviceProcedures(deviceProcedures);
		
		this.insert(device);
		return device;
	}
	
	public void updateStatus(Device device, Map<String, String> propertyValueMap) {
		DeviceStatus deviceStatus = new DeviceStatus();
		deviceStatus.setDevice(device);
		deviceStatus.setDeviceStatusValues(new ArrayList<DeviceStatusValue>());
		Map<String, Property> propertiesMap = device.getDevicePropertyType().getProperties().stream()
				.collect(Collectors.toMap(Property::getKey, p -> p));

		Property property = propertiesMap.get(PROPERTY_REQUEST_COUNTER);
		appendDeviceStatusValue(deviceStatus, property, propertyValueMap);
		property = propertiesMap.get(PROPERTY_UPDATE_DATA_COUNTER);
		appendDeviceStatusValue(deviceStatus, property, propertyValueMap);
		property = propertiesMap.get(PROPERTY_USED_DISK_SPACE);
		appendDeviceStatusValue(deviceStatus, property, propertyValueMap);
		property = propertiesMap.get(PROPERTY_FREE_DISK_SPACE);
		appendDeviceStatusValue(deviceStatus, property, propertyValueMap);
		property = propertiesMap.get(PROPERTY_PLAYING_MEDIA_NAME);
		appendDeviceStatusValue(deviceStatus, property, propertyValueMap);
		property = propertiesMap.get(PROPERTY_PLAYING_MEDIA_TIME);
		appendDeviceStatusValue(deviceStatus, property, propertyValueMap);
		
		device.setCurrentDeviceStatus(deviceStatus);
		this.update(device);
	}

	private void appendDeviceStatusValue(DeviceStatus deviceStatus, Property property
			, Map<String, String> propertyValueMap) {
		if (property != null) {
			DeviceStatusValue statusValue = new DeviceStatusValue();
			statusValue.setDeviceStatus(deviceStatus);
			statusValue.setProperty(property);
			statusValue.setValue(propertyValueMap.get(property.getKey()));
			deviceStatus.getDeviceStatusValues().add(statusValue);
		}
	}

	public List<MediaContent> findMediaByDeviceTag(Device device) {
		
		if( device.getTags() == null || device.getTags().size() == 0 ){
			return new ArrayList<MediaContent>();
		}
		
		TypedQuery<MediaContent> query = entityManager.createQuery(
				"SELECT m FROM MediaContent m " + " WHERE m.tags IN :tags" + " GROUP BY m HAVING COUNT(m) = :count",
				MediaContent.class);
		List<Tag> tags = device.getTags();
		query.setParameter("tags", tags);
		query.setParameter("count", tags.size());
		return query.getResultList();
	}

}
