package net.cb.dcm.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.cb.dcm.dev_feed.ScheduleGeneratorSingleton;
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.entities.Device;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScheduleGenerationTest {

	private static long deviceId;
	
	@Test
	public void test1GenerateAll() {
		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.findAll();
		
		Assert.assertTrue(devices.size() > 0);
		
		// Generate for all devices
		for (Device device : devices) {
			ScheduleGeneratorSingleton.getInstance().generateDeviceLoops(device, Calendar.getInstance());
		}
	}
	
	@Test
	public void test2GenerateAll() {
		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.findAll();
		
		Assert.assertTrue(devices.size() > 0);
		
		// Generate second time for all devices
		for (Device device : devices) {
			ScheduleGeneratorSingleton.getInstance().generateDeviceLoops(device, Calendar.getInstance());
		}
	}
	
	@Test
	public void test2GenerateAndPersistAll() {
		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.findAll();
		
		Assert.assertTrue(devices.size() > 0);
		
		// Generate second time for all devices
		for (Device device : devices) {
			ScheduleGeneratorSingleton.getInstance().getDeviceSchedule(device);
		}
	}
	
	@Test
	public void test3GenerateAllFor10Days() {
		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.findAll();
		
		Assert.assertTrue(devices.size() > 0);
		
		Calendar cal = Calendar.getInstance();
		// Generate for all devices for next 10 days
		for(int i = 0; i < 10; i++) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			for (Device device : devices) {
				ScheduleGeneratorSingleton.getInstance().generateDeviceLoops(device, cal);
			}
		}
	}
	
	@Test
	public void test4GenerateFirst() {
		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.findAll();
		
		Assert.assertTrue(devices.size() > 0);
		// Generate for first device
		ScheduleGeneratorSingleton.getInstance().generateDeviceLoops(devices.get(0), Calendar.getInstance());
	}
	
	@Test
	public void test5RegisterNewDevice() {
		DeviceDAO deviceDao = new DeviceDAO();
		
		String serialNumber = String.valueOf(new Date().getTime());
		Device device =  deviceDao.registerNewDevice(serialNumber.substring(2), serialNumber);
		
		Assert.assertTrue(device.getId() > 0);
		deviceId = device.getId();
	}
	
	@Test
	public void test6GenerateAndPersistNewDevice() {
		Assert.assertTrue(deviceId > 0);
		DeviceDAO deviceDao = new DeviceDAO();
		Device dev = deviceDao.findById(deviceId);
		Assert.assertNotNull(dev);
		// Generate for first device
		ScheduleGeneratorSingleton.getInstance().getDeviceSchedule(dev);
	}
	
	@Test
	public void test7GenerateAndPersistNewDevice() {
		Assert.assertTrue(deviceId > 0);
		DeviceDAO deviceDao = new DeviceDAO();
		Device dev = deviceDao.findById(deviceId);
		Assert.assertNotNull(dev);
		// Generate for first device
		ScheduleGeneratorSingleton.getInstance().getDeviceSchedule(dev);
	}
}
