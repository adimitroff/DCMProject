package net.cb.dcm.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.PlaylistDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DeviceSchedule;
import net.cb.dcm.jpa.entities.Loop;
import net.cb.dcm.jpa.entities.MediaContent;
import net.cb.dcm.jpa.entities.Playlist;
import net.cb.dcm.jpa.entities.PlaylistSchedule;
import net.cb.dcm.jpa.entities.Tag;

public class DbLazyFetchTest {

	@Test
	public void testDevices() {
		List<Device> devices = getDevices();
		Assert.assertTrue(devices.size() > 0);
		
		Device dev = devices.get(0);
		
		DeviceSchedule devSchedule = dev.getCurrentDeviceSchedule();
		List<Tag> devTags = dev.getTags();
		
		Assert.assertTrue(devTags.size() > 0);
		
		Assert.assertNotNull(devSchedule);
		Assert.assertTrue(devSchedule.getId() > 0);
		List<Loop> loops = devSchedule.getLoops();
		Assert.assertNotNull(loops);
		Assert.assertTrue(loops.size() > 0);
		for (Loop loop : loops) {
			Assert.assertTrue(loop.getId() > 0);
			Assert.assertTrue(loop.getMediaContents().size() > 0);
			List<MediaContent> loopMedias = loop.getMediaContents();
			for (MediaContent mediaContent : loopMedias) {
				Assert.assertTrue(mediaContent.getId() > 0);
				Assert.assertTrue(mediaContent.getTags().size() > 0);
			}
		}
	}
	
	@Test
	public void testPlaylists() {
		List<Playlist> playlists = getPlaylists();
		
		Assert.assertTrue(playlists.size() > 0);
		
		for (Playlist playlist : playlists) {
			Assert.assertTrue(playlist.getId() > 0);
			List<MediaContent> mediaContents = playlist.getMediaContents();
			Assert.assertTrue(mediaContents.size() > 0); 
			for (MediaContent mediaContent : mediaContents) {
				Assert.assertTrue(mediaContent.getTags().size() > 0);
			}
			List<PlaylistSchedule> schedules = playlist.getSchedules();
			Assert.assertTrue(schedules.size() > 0);
		}
		
	}
	
	public List<Device> getDevices() {
		return new DeviceDAO().findAll();
	}
	
	public List<Playlist> getPlaylists() {
		return new PlaylistDao().findAll();
	}
}
