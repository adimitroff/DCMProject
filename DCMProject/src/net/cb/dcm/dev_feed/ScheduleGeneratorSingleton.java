package net.cb.dcm.dev_feed;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.PlaylistDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DeviceGroup;
import net.cb.dcm.jpa.entities.DeviceSchedule;
import net.cb.dcm.jpa.entities.Loop;
import net.cb.dcm.jpa.entities.MediaContent;
import net.cb.dcm.jpa.entities.Playlist;
import net.cb.dcm.jpa.entities.PlaylistSchedule;
import net.cb.dcm.jpa.entities.Tag;

public class ScheduleGeneratorSingleton {

	private static ScheduleGeneratorSingleton instance = null;
	
	private DeviceDAO deviceDao;

	protected ScheduleGeneratorSingleton() {
		deviceDao = new DeviceDAO();
	}

	public synchronized static ScheduleGeneratorSingleton getInstance() {
		if (instance == null) {
			instance = new ScheduleGeneratorSingleton();
		}
		return instance;
	}
	
	public List<Loop> getDailySchedule(Calendar calDay) {
		PlaylistDao playlistDao = new PlaylistDao(deviceDao);
		List<Playlist> dailyPlaists = playlistDao.findDailyPlaylists(calDay);
		List<Loop> loops = new ArrayList<Loop>();
		// Start from playlist with lowest priority(max priority value) and increment to highest priority
		for(int pIdx = dailyPlaists.size() - 1; pIdx >= 0; pIdx--) {
			List<PlaylistSchedule> shcedules = dailyPlaists.get(pIdx).getSchedules();
			for (PlaylistSchedule schedule : shcedules) {
				// First remove previous loops added from playlists with lower priority
				// Or change loops start and end time
				List<Loop> loopsForRemove = new ArrayList<>();
				List<Loop> loopsToAdd = new ArrayList<>();
				for (Loop loop : loops) {
					boolean loopStartsInSchedule = (loop.getValidFrom().compareTo(schedule.getStartTime()) >= 0 
							&& loop.getValidFrom().compareTo(schedule.getEndTime()) <= 0) ;
					boolean loopEndsInSchedule = (loop.getValidTo().compareTo(schedule.getStartTime()) >= 0
							&& loop.getValidTo().compareTo(schedule.getEndTime()) <= 0);
					if(loopStartsInSchedule && loopEndsInSchedule) {
						// Loop timeplan is inside schedule timeplan so remove it 
						// because it's from playlist with lower (maybe equal) priority
						loopsForRemove.add(loop);
					} else if (loopStartsInSchedule && !loopEndsInSchedule) {
						loop.setValidFrom(schedule.getEndTime());
					} else if (!loopStartsInSchedule && loopEndsInSchedule) {
						loop.setValidTo(schedule.getStartTime());
					} else if (loop.getValidFrom().compareTo(schedule.getStartTime()) < 0 
							&& loop.getValidTo().compareTo(schedule.getEndTime()) > 0) {
						// Loop timeplan contains schedule timpelan
						// Cut loop to 2 parts - before and after schedule timeplan
						Date validTo = loop.getValidTo();
						loop.setValidTo(schedule.getStartTime());
						Loop secondLoop = new Loop();
						secondLoop.setMediaContents(loop.getMediaContents());
						secondLoop.setSourcePlaylist(loop.getSourcePlaylist());
						secondLoop.setValidFrom(schedule.getEndTime());
						secondLoop.setValidTo(validTo);
						loopsToAdd.add(secondLoop);
					}
				}
				loops.removeAll(loopsForRemove);
				loops.addAll(loopsToAdd);
				// Create and add new loop with current schedule times and playlist contents 
				Loop loop = new Loop();
				loop.setMediaContents(dailyPlaists.get(pIdx).getMediaContents());
				loop.setSourcePlaylist(dailyPlaists.get(pIdx));
				loop.setValidFrom(schedule.getStartTime());
				loop.setValidTo(schedule.getEndTime());
				loops.add(loop);
			}
		}
		
		return loops;
	}
	
	public DeviceSchedule getDeviceSchedule(Device device) {
		List<Loop> deviceLoops = generateDeviceLoops(device, Calendar.getInstance());
		DeviceSchedule currentDeviceSchedule = device.getCurrentDeviceSchedule();
		// Check equality of current schedule loops and generated device loops
		if(currentDeviceSchedule != null  && currentDeviceSchedule.getLoops().size() > 0 
				&& currentDeviceSchedule.getLoops().size() == deviceLoops.size()) {

			List<Loop> orderedDeviceLoops  = deviceLoops.stream()
			.sorted((l1, l2) -> l1.getValidFrom().compareTo(l2.getValidFrom()))
			.collect(Collectors.toList());
			
			List<Loop> orderedCurrentLoops = currentDeviceSchedule.getLoops().stream()
					.sorted((l1, l2) -> l1.getValidFrom().compareTo(l2.getValidFrom()))
					.collect(Collectors.toList());
			
			boolean isScheduleEqual = true;
			loops:
			for(int i = 0; i < orderedDeviceLoops.size(); i++) {
				// Check time interval equality
				if(!orderedDeviceLoops.get(i).getValidFrom().equals( orderedCurrentLoops.get(i).getValidFrom())
						|| !orderedDeviceLoops.get(i).getValidTo().equals(orderedCurrentLoops.get(i).getValidTo())) {
					isScheduleEqual = false;
					break;
				}
				// Check media content equality
				List<MediaContent> mediaContents = orderedDeviceLoops.get(i).getMediaContents().stream()
						.sorted((m1, m2) -> (int) (m1.getId() - m2.getId()))
						.collect(Collectors.toList());
				List<MediaContent> scheduledMediaContents = orderedCurrentLoops.get(i).getMediaContents().stream()
						.sorted((m1, m2) -> (int) (m1.getId() - m2.getId()))
						.collect(Collectors.toList());
				if (mediaContents.size() != scheduledMediaContents.size()) {
					isScheduleEqual = false;
					break;
				}
				for (int mediaIdx = 0; mediaIdx < mediaContents.size(); mediaIdx++) {
					if (!mediaContents.get(mediaIdx).equals(scheduledMediaContents.get(mediaIdx))) {
						isScheduleEqual = false;
						break loops;
					}
				}
			}
			if(isScheduleEqual) {
				return currentDeviceSchedule;
			}
		}
		
		DeviceSchedule deviceSchedule = new DeviceSchedule();
		deviceSchedule.setDevice(device);
		deviceSchedule.setLoops(deviceLoops);
		for (Loop loop : deviceLoops) {
			loop.setDeviceSchedule(deviceSchedule);
		}
		device.setCurrentDeviceSchedule(deviceSchedule);
		deviceDao.update(device);
		
		return deviceSchedule;
	}
	
	public List<Loop> generateDeviceLoops(Device device, Calendar calDay) {
		
		// Collect device tags from device and device groups
		Set<Tag> deviceTags = new HashSet<>();
		if(device.getTags() != null) {
			deviceTags.addAll(device.getTags());
		}
		if(device.getGroups() != null) {
			for (DeviceGroup group : device.getGroups()) {
				deviceTags.addAll(group.getTags());
			}
		}
		

		Set<MediaContent> allMediaContents = new HashSet<>();
		List<Loop> loops = getDailySchedule(calDay);
		for (Loop loop : loops) {
			allMediaContents.addAll(loop.getMediaContents());
		}

		List<MediaContent> mediaContentsToRemove = new ArrayList<>();
		for (MediaContent mediaContent : allMediaContents) {
			if (mediaContent.getTags() != null && !mediaContent.getTags().isEmpty()
					&& !deviceTags.containsAll(mediaContent.getTags())) {
				mediaContentsToRemove.add(mediaContent);
			}
		}

		List<Loop> filteredLoops = new ArrayList<>();
		for (Loop loop : loops) {
			Loop filtredLoop = new Loop();
			filtredLoop.setMediaContents(new ArrayList<>());
			filtredLoop.getMediaContents().addAll(loop.getMediaContents());
			filtredLoop.getMediaContents().removeAll(mediaContentsToRemove);
			filtredLoop.setSourcePlaylist(loop.getSourcePlaylist());
			filtredLoop.setValidFrom(loop.getValidFrom());
			filtredLoop.setValidTo(loop.getValidTo());
			filteredLoops.add(filtredLoop);
		}
		
		return filteredLoops;
	}
}
