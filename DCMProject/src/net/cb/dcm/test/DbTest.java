package net.cb.dcm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cb.dcm.enums.MediaObjectType;
import net.cb.dcm.enums.PlaylistScheduleType;
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.GenericDao;
import net.cb.dcm.jpa.MediaContentDao;
import net.cb.dcm.jpa.PlaylistDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DevicePropertyType;
import net.cb.dcm.jpa.entities.MediaContent;
import net.cb.dcm.jpa.entities.Playlist;
import net.cb.dcm.jpa.entities.PlaylistSchedule;
import net.cb.dcm.jpa.entities.Property;
import net.cb.dcm.jpa.entities.Tag;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DbTest {

	private static final Logger logger = LoggerFactory.getLogger(DbTest.class);
	
	private static long tagIdBg;
	private static long tagIdPl;
	private static long tagIdSo;
	private static long tagIdTv;

	@Test
	public void test1InsertDeviceData() {
		logger.debug("insertBasicData test");
		DeviceDAO deviceDao = new DeviceDAO();

		GenericDao<DevicePropertyType> devPropTypeDao = new GenericDao<DevicePropertyType>(deviceDao) {
		};
		GenericDao<Property> propertyDao = new GenericDao<Property>(deviceDao) {
		};
		assertEquals(0, deviceDao.findAll().size());
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

		for (int i = 100; i <= 120; i++) {
			String ip = "127.0.0." + i;
			String serialNumber = "SAMSUTV100" + i;
			Device device = deviceDao.registerNewDevice(ip, serialNumber);
			assertNotNull(device);
			assertTrue(device.getId() > 0);
			assertNotNull(device.getName());
			assertEquals(ip, device.getIp());
			assertEquals(serialNumber, device.getSerialNumber());
		}
	}
	
	@Test
	public void test2insertTags() {
		GenericDao<Tag> tagDao = new GenericDao<Tag>() {
		};
		
		assertEquals(0, tagDao.findAll().size());
		
		List<Tag> tags = new ArrayList<>();
		
		Tag tag = new Tag();
		tag.setName("Bulgaria");
		tag.setDescription("Tag Bulgaria");
		tags.add(tag);
		
		tag = new Tag();
		tag.setName("Sofia");
		tag.setDescription("Tag Sofia");
		tags.add(tag);
		
		tag = new Tag();
		tag.setName("Plovdiv");
		tag.setDescription("Tag Plovdiv");
		tags.add(tag);
		
		tag = new Tag();
		tag.setName("Burgas");
		tag.setDescription("Tag Burgas");
		tags.add(tag);
		
		tag = new Tag();
		tag.setName("Ruse");
		tag.setDescription("Tag Ruse");
		tags.add(tag);
		
		tag = new Tag();
		tag.setName("Smart TV");
		tag.setDescription("Tag Smart TV");
		tags.add(tag);
		
		tagDao.insertAll(tags);
		
		for (int i = 0; i < tags.size(); i++) {
			Tag t = tags.get(i);
			assertTrue(t.getId() > 0);
			switch (i) {
			case 0:
				tagIdBg = t.getId();
				break;
			case 1:
				tagIdSo = t.getId();
				break;
			case 2:
				tagIdPl = t.getId();
				break;
			case 5:
				tagIdTv = t.getId();
				break;
			default:
				break;
			}
		}	
	}
	
	@Test
	public void test3insertDeviceTags() {
		DeviceDAO deviceDao = new DeviceDAO();
		GenericDao<Tag> tagDao = new GenericDao<Tag>(deviceDao) {
		};
		
		List<Device> devices = deviceDao.findAll();
		
		Tag tagBg = tagDao.findById(tagIdBg);
		Tag tagSo = tagDao.findById(tagIdSo);
		Tag tagPl = tagDao.findById(tagIdPl);
		Tag tagTv = tagDao.findById(tagIdTv);
		
		List<Tag> tagsBg = new ArrayList<>();
		tagsBg.add(tagBg);
		tagsBg.add(tagTv);
		
		List<Tag> tagsSo = new ArrayList<>();
		tagsSo.add(tagSo);
		tagsSo.add(tagTv);
		
		List<Tag> tagsPl = new ArrayList<>();
		tagsPl.add(tagPl);
		tagsPl.add(tagTv);
		
		int tagType = 0;
		for (Device dev : devices) {
			assertEquals(0, dev.getTags().size());
			switch (tagType) {
			case 0:
				dev.setTags(tagsBg);
				tagType++;
				break;
			case 1:
				dev.setTags(tagsSo);
				tagType++;
				break;
			case 2:
				dev.setTags(tagsPl);
				tagType = 0;
				break;
			}
			deviceDao.update(dev);
		}
		
	}

	@Test
	public void test4insertMediaData() {
		MediaContentDao mediaContentDao = new MediaContentDao();
		GenericDao<Tag> tagDao = new GenericDao<Tag>(mediaContentDao) {
		};
		
		assertEquals(0, mediaContentDao.findAll().size());
		
		List<MediaContent> mediaContents = new ArrayList<>();
		List<Tag> tags = tagDao.findAll();
		
		MediaContent mediaContent = new MediaContent();
		mediaContent.setName("Test media 1");
		mediaContent.setFilePath("/test/test_media_1.jpg");
		mediaContent.setMediaType(MediaObjectType.JPEG);
		mediaContent.setTags(tags);
		mediaContents.add(mediaContent);
		
		mediaContent = new MediaContent();
		mediaContent.setName("Test media 2");
		mediaContent.setFilePath("/test/test_media_2.mp4");
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setTags(tags);
		mediaContents.add(mediaContent);
		
		mediaContent = new MediaContent();
		mediaContent.setName("Test media 3");
		mediaContent.setFilePath("/test/test_media_3.mp4");
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setTags(tags);
		mediaContents.add(mediaContent);
		
		mediaContent = new MediaContent();
		mediaContent.setName("Test media 4");
		mediaContent.setFilePath("/test/test_media_4.mp4");
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setTags(tags);
		mediaContents.add(mediaContent);
		
		mediaContent = new MediaContent();
		mediaContent.setName("Test media 5");
		mediaContent.setFilePath("/test/test_media_5.mp4");
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setTags(tags);
		mediaContents.add(mediaContent);
		
		mediaContentDao.insertAll(mediaContents);
		
	}

	@Test
	public void test5InsertPlaylistsData() {
		
		MediaContentDao mediaContentDao = new MediaContentDao();
		PlaylistDao playlistDao = new PlaylistDao(mediaContentDao);
		
		assertEquals(0, playlistDao.findAll().size());
		
		List<MediaContent> mediaContents = mediaContentDao.findAll();

		assertTrue(mediaContents.size() >= 5);
		
		List<Playlist> playlists = new ArrayList<>();

		Calendar calStartTime = Calendar.getInstance();
		calStartTime.set(Calendar.HOUR, 0);
		calStartTime.set(Calendar.MINUTE, 0);
		calStartTime.set(Calendar.SECOND, 0);
		Calendar calEndTime = Calendar.getInstance();
		calEndTime.set(Calendar.HOUR, 23);
		calEndTime.set(Calendar.MINUTE, 59);
		calEndTime.set(Calendar.SECOND, 59);

		// Default playlist
		Playlist playlist = new Playlist();
		playlist.setActive(true);
		playlist.setDef(true);
		playlist.setName("Default");
		playlist.setDescription("Default playlist");
		playlist.setPriority(10);
		
		List<MediaContent> plMediaContents = new ArrayList<>();
		plMediaContents.add(mediaContents.get(0));
		playlist.setMediaContents(plMediaContents);

		List<PlaylistSchedule> schedules = new ArrayList<>();
		PlaylistSchedule playlistSchedule = new PlaylistSchedule();
		playlistSchedule.setType(PlaylistScheduleType.DAILY);
		playlistSchedule.setStartTime(calStartTime.getTime());
		playlistSchedule.setEndTime(calEndTime.getTime());
		playlistSchedule.setPlaylist(playlist);
		schedules.add(playlistSchedule);

		playlist.setSchedules(schedules);
		playlistDao.insert(playlist);
		playlists.add(playlist);

		// Default playlist 2
		playlist = new Playlist();
		playlist.setActive(true);
		playlist.setDef(true);
		playlist.setName("Default 2");
		playlist.setDescription("Default playlist 2");
		playlist.setPriority(9);
		
		plMediaContents = new ArrayList<>();
		plMediaContents.add(mediaContents.get(1));
		plMediaContents.add(mediaContents.get(2));
		playlist.setMediaContents(plMediaContents);

		schedules = new ArrayList<>();
		calStartTime.set(Calendar.HOUR, 13);
		calEndTime.set(Calendar.HOUR, 17);
		playlistSchedule = new PlaylistSchedule();
		playlistSchedule.setType(PlaylistScheduleType.DAILY);
		playlistSchedule.setStartTime(calStartTime.getTime());
		playlistSchedule.setEndTime(calEndTime.getTime());
		playlistSchedule.setPlaylist(playlist);
		schedules.add(playlistSchedule);

		playlist.setSchedules(schedules);
		playlistDao.insert(playlist);
		playlists.add(playlist);

		// Playlist with 3 schedules
		playlist = new Playlist();
		playlist.setActive(true);
		playlist.setDef(false);
		playlist.setName("Playlist 1");
		playlist.setDescription("Playlist 1");
		playlist.setPriority(1);
		
		plMediaContents = new ArrayList<>();
		plMediaContents.add(mediaContents.get(3));
		plMediaContents.add(mediaContents.get(4));
		playlist.setMediaContents(plMediaContents);

		schedules = new ArrayList<>();
		calStartTime.set(Calendar.HOUR, 10);
		calEndTime.set(Calendar.HOUR, 12);
		playlistSchedule = new PlaylistSchedule();
		playlistSchedule.setType(PlaylistScheduleType.DAILY);
		playlistSchedule.setStartTime(calStartTime.getTime());
		playlistSchedule.setEndTime(calEndTime.getTime());
		playlistSchedule.setPlaylist(playlist);
		schedules.add(playlistSchedule);

		calStartTime.set(Calendar.HOUR, 13);
		calEndTime.set(Calendar.HOUR, 14);
		playlistSchedule = new PlaylistSchedule();
		playlistSchedule.setType(PlaylistScheduleType.DAILY);
		playlistSchedule.setStartTime(calStartTime.getTime());
		playlistSchedule.setEndTime(calEndTime.getTime());
		playlistSchedule.setPlaylist(playlist);
		schedules.add(playlistSchedule);

		calStartTime.set(Calendar.HOUR, 9);
		calEndTime.set(Calendar.HOUR, 19);
		playlistSchedule = new PlaylistSchedule();
		playlistSchedule.setType(PlaylistScheduleType.WEEKLY);
		playlistSchedule.setDayOfWeek(Calendar.MONDAY);
		playlistSchedule.setStartTime(calStartTime.getTime());
		playlistSchedule.setEndTime(calEndTime.getTime());
		playlistSchedule.setPlaylist(playlist);
		schedules.add(playlistSchedule);

		playlist.setSchedules(schedules);
		playlistDao.insert(playlist);
		playlists.add(playlist);
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
