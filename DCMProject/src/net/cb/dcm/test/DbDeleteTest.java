package net.cb.dcm.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.GenericDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.Tag;

public class DbDeleteTest {
	
	@Test
	public void testDeleteTags() {
		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.findAll();
		Assert.assertTrue(devices.size() > 0);
		
		Device device = null;
		for(int i = 0; i < devices.size(); i++) {
			if(devices.get(i).getTags().size() > 0) {
				device = devices.get(i);
				break;
			}
		}
		Assert.assertNotNull(device);
		List<Tag> tags = device.getTags();
		Assert.assertTrue(tags.size() > 0);
		
		GenericDao<Tag> tagDao = new GenericDao<Tag>(deviceDao) {
		};
		tagDao.deleteAll(tags);
		
		
		device = deviceDao.findById(device.getId());		
		tags = device.getTags();
		Assert.assertTrue(tags.size() == 0);
	}
}
