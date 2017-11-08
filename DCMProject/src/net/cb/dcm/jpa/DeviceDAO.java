package net.cb.dcm.jpa;

import java.util.List;

import javax.persistence.TypedQuery;

import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.MediaContent;

public class DeviceDAO extends GenericDao<Device> {
	
	public Device findDeviceByIp(String ip) {
		TypedQuery<Device> query = entityManager.createQuery("SELECT d FROM Device d WHERE d.ip = :ip", Device.class);
		List<Device> foundDevices = query.setParameter("ip", ip).getResultList();
		if(foundDevices.size() == 1) {
			return foundDevices.get(0);
		} else {
			return null;
		}
	}
	
	public Device registerNewDevice(String ip) {
		Device device = new Device();
		device.setIp(ip);
		device.setName("Samsung Tv " + ip);
		this.insert(device);
		return device;
	}
	
	public List<MediaContent> findMediaByDeviceTag(Device device) {
		TypedQuery<MediaContent> query = entityManager.createQuery("SELECT m FROM MediaContent m "
													+ " WHERE m.tags = :tags", MediaContent.class);
		return query.setParameter("tags", device.getTags()).getResultList();
	}
}
