package net.cb.dcm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cb.dcm.enums.PlaylistScheduleType;
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.GenericDao;
import net.cb.dcm.jpa.PlaylistDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DevicePropertyType;
import net.cb.dcm.jpa.entities.Playlist;
import net.cb.dcm.jpa.entities.PlaylistSchedule;
import net.cb.dcm.jpa.entities.Property;

public class DbTest {

	private static final Logger logger = LoggerFactory.getLogger(DbTest.class);
	
	@Test
	public void insertBasicData() {
		logger.debug("insertBasicData test");
		DeviceDAO deviceDao = new DeviceDAO();
		PlaylistDao playlistDao = new PlaylistDao(deviceDao);
		GenericDao<DevicePropertyType> devPropTypeDao = new GenericDao<DevicePropertyType>(deviceDao) {
		};
		GenericDao<Property> propertyDao = new GenericDao<Property>(deviceDao) {
		};
		assertEquals(0, deviceDao.findAll().size());
		assertEquals(0, playlistDao.findAll().size());
		assertEquals(0, devPropTypeDao.findAll().size());
		assertEquals(0, propertyDao.findAll().size());
		
		// Insert basic properties
		List<Property> properties = createProperties();
		
		// Insert device property type
		DevicePropertyType devPropertyType = new DevicePropertyType();
		devPropertyType.setName("Samsung Smart Tv");
		devPropertyType.setDescription("Samsung Smart Tv device");
		devPropertyType.setProperties(properties);
		devPropTypeDao.insert(devPropertyType);
		assertTrue(devPropertyType.getId() > 0);
		assertTrue(properties.size() > 0);
		assertTrue(properties.get(0).getId() > 0);
		
		List<Device> devices = new ArrayList<>();
		for(int i = 100; i <= 250; i++) {
			Device device = testRegisterDevice(deviceDao, "127.0.0." + i, "SAMSUTV100" + i);
			devices.add(device);
		}
		
		testCreatePlaylists(playlistDao);
		
	}
	
	public Device testRegisterDevice(DeviceDAO deviceDao, String ip, String serialNumber) {
		Device device = deviceDao.registerNewDevice(ip, serialNumber);
		assertNotNull(device);
		assertTrue(device.getId() > 0);
		assertNotNull(device.getName());
		assertEquals(ip, device.getIp());
		assertEquals(serialNumber, device.getSerialNumber());
		return device;
	}
	
	public List<Playlist> testCreatePlaylists(PlaylistDao playlistDao) {
		List<Playlist> playlists = new ArrayList<>();
		
		Calendar calStartTime = Calendar.getInstance();
		calStartTime.set(Calendar.HOUR, 0);
		calStartTime.set(Calendar.MINUTE, 0);
		calStartTime.set(Calendar.SECOND, 0);
		Calendar calEndTime = Calendar.getInstance();
		calEndTime.set(Calendar.HOUR, 23);
		calEndTime.set(Calendar.MINUTE, 59);
		calEndTime.set(Calendar.SECOND, 59);
		
		List<PlaylistSchedule> schedules = new ArrayList<>();
		PlaylistSchedule playlistSchedule = new PlaylistSchedule();
		playlistSchedule.setType(PlaylistScheduleType.DAILY);
		playlistSchedule.setStartTime(calStartTime.getTime());
		playlistSchedule.setEndTime(calEndTime.getTime());
		schedules.add(playlistSchedule);
		
		Playlist playlist = new Playlist();
		playlist.setActive(true);
		playlist.setDef(true);
		playlist.setName("Default");
		playlist.setDescription("Default playlist");
		playlist.setPriority(10);
		playlist.setSchedules(schedules);
		playlistDao.insert(playlist);
		playlists.add(playlist);
		
		
		schedules = new ArrayList<>();
		calStartTime.set(Calendar.HOUR, 13);
		calEndTime.set(Calendar.HOUR, 17);
		playlistSchedule = new PlaylistSchedule();
		playlistSchedule.setType(PlaylistScheduleType.DAILY);
		playlistSchedule.setStartTime(calStartTime.getTime());
		playlistSchedule.setEndTime(calEndTime.getTime());
		schedules.add(playlistSchedule);
		
		playlist = new Playlist();
		playlist.setActive(true);
		playlist.setDef(true);
		playlist.setName("Default 2");
		playlist.setDescription("Default playlist 2");
		playlist.setPriority(9);
		playlist.setSchedules(schedules);
		playlistDao.insert(playlist);
		playlists.add(playlist);
		
		
		return playlists;
	}
	
	public List<Property> createProperties() {
		List<Property> properties = new ArrayList<>();
		Property p = new Property();
		p.setKey("REQUEST_COUNTER");
		p.setName("Request counter");
		properties.add(p);
		
		p = new Property();
		p.setKey("UPDATE_DATA_COUNTER");
		p.setName("Update data counter");
		properties.add(p);
		
		p = new Property();
		p.setKey("USED_DISK_SPACE");
		p.setName("Used disk space");
		properties.add(p);
		
		p = new Property();
		p.setKey("FREE_DISK_SPACE");
		p.setName("Free disk space");
		properties.add(p);
		
		p = new Property();
		p.setKey("PLAYING_MEDIA_NAME");
		p.setName("Current playing media file name");
		properties.add(p);
		
		p = new Property();
		p.setKey("PLAYING_MEDIA_TIME");
		p.setName("Current playing media time");
		properties.add(p);
		
		p = new Property();
		p.setKey("TEMPERATURE");
		p.setName("Device temperature");
		properties.add(p);
		
		p = new Property();
		p.setKey("ON_OFF_STATUS");
		p.setName("Display ON/OFF");
		properties.add(p);
		
		return properties;
		
	}
}
