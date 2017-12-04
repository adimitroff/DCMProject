package net.cb.dcm.dev_feed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cb.dcm.enums.DevResponseDataType;
import net.cb.dcm.enums.DeviceCommand;
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.PlaylistDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DeviceProcedure;
import net.cb.dcm.jpa.entities.DeviceSchedule;
import net.cb.dcm.jpa.entities.Loop;
import net.cb.dcm.jpa.entities.MediaContent;
import net.cb.dcm.jpa.entities.Playlist;
import net.cb.dcm.jpa.entities.PlaylistSchedule;

/**
 * Servlet for processing http request for new content from the samsung tv app
 * and other smart devices.
 */
public class DevFeeder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger moLogger = LoggerFactory.getLogger(DevFeeder.class);

	private DeviceDAO deviceDao;

	/** {@inheritDoc} */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		deviceDao = new DeviceDAO();
		String loIp = request.getParameter("ip");
		String lsServerUrl =  new StringBuilder()
				.append("http://")
				.append(request.getServerName())
				.append(":")
				.append(request.getServerPort())
				.append(request.getContextPath())
				.toString();
		Gson loGson = new GsonBuilder().setPrettyPrinting().create();
		DevResponse loDevResponse = new DevResponse(lsServerUrl);
		
		if (loIp == null || loIp.isEmpty()) {
			moLogger.debug("No device IP. Generating default response.");
			this.getPlayList(null, loDevResponse);
		} else {
			Device loDevice = deviceDao.findDeviceByIp(loIp);
			if (loDevice == null || loDevice.getId() <= 0) {
				moLogger.debug("Register new device with IP " + loIp);
				//// Register device ID in the database
				//// Maybe registering only the IP, then the system
				//// opearator should see it in the
				//// list as a device without data, and maintain it.
				loDevice = deviceDao.registerNewDevice(loIp, request.getParameter("serialN"));
			}
			
			// Update device status
			Map<String, String> propertyValueMap = new HashMap<>();
			propertyValueMap.put(DeviceDAO.PROPERTY_REQUEST_COUNTER, request.getParameter("request"));
			propertyValueMap.put(DeviceDAO.PROPERTY_UPDATE_DATA_COUNTER, request.getParameter("dataId"));
			propertyValueMap.put(DeviceDAO.PROPERTY_USED_DISK_SPACE, request.getParameter("usedSize"));
			propertyValueMap.put(DeviceDAO.PROPERTY_FREE_DISK_SPACE, request.getParameter("freeSize"));
			propertyValueMap.put(DeviceDAO.PROPERTY_PLAYING_MEDIA_NAME, request.getParameter("mediaName"));
			propertyValueMap.put(DeviceDAO.PROPERTY_PLAYING_MEDIA_TIME, request.getParameter("mediaTime"));
			propertyValueMap.put(DeviceDAO.PROPERTY_TEMPERATURE, request.getParameter("temperature"));
			propertyValueMap.put(DeviceDAO.PROPERTY_ON_OFF_STATUS, request.getParameter("display"));
			deviceDao.updateStatus(loDevice, propertyValueMap);
			
			// Get saved status data id			
			String sDataId = propertyValueMap.get(DeviceDAO.PROPERTY_UPDATE_DATA_COUNTER);
			if(sDataId != null){
				// Set same data id for new response
				int dataId = Integer.valueOf(sDataId);
				if( dataId == 1)
					dataId++;
				
				loDevResponse.setDataId(dataId);
			}
			
			// Check and generate response with commands
			boolean responseGenerated = this.getProcedure(loDevice, loDevResponse);
			if(!responseGenerated) {
				// Load device playlist or default playlist
				this.getPlayList(loDevice, loDevResponse);
			}
		}
		
		loDevResponse.generateResponse();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(loGson.toJson(loDevResponse.getResponseJson()));
	}

	/** {@inheritDoc} */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Should not be called
		doGet(request, response);
	}
	
	private boolean getProcedure(Device device, DevResponse devResponse)
	{
		// TODO check device status wake up/sleep
		Calendar currCalendar = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		List<DeviceProcedure> deviceProcedures = device.getDeviceProcedures();
		for (DeviceProcedure deviceProcedure : deviceProcedures) {
			if(deviceProcedure.getLastExecutedTime() != null) {
				calendar.setTime(deviceProcedure.getLastExecutedTime());
				if (calendar.get(Calendar.YEAR) == currCalendar.get(Calendar.YEAR)
						&& calendar.get(Calendar.DAY_OF_YEAR) == currCalendar.get(Calendar.DAY_OF_YEAR)) {
					// already executed
					continue;
				}
			}
			calendar.setTime(deviceProcedure.getExecutionTime());
			calendar.set(currCalendar.get(Calendar.YEAR), currCalendar.get(Calendar.MONTH)
					, currCalendar.get(Calendar.DAY_OF_MONTH));
			long timeDiff = currCalendar.getTimeInMillis() - calendar.getTimeInMillis();
			if(timeDiff >= 0 && timeDiff < (30 * 60 * 1000)) {
				// Execute procedure
				devResponse.setResponseDataType(DevResponseDataType.COMMAND);
				devResponse.setDataId(devResponse.getDataId() + 1);
				ArrayList<DeviceCommand> deviceCommands = new ArrayList<>();
				switch (deviceProcedure.getProcedureType()) {
				case WAKE:
					deviceCommands.add(DeviceCommand.TURN_ON);
					break;
				case SLEEP:
					deviceCommands.add(DeviceCommand.TURN_OFF);
					break;
				}
				devResponse.setCommands(deviceCommands);
				deviceProcedure.setLastExecutedTime(currCalendar.getTime());
				deviceDao.update(device);
				
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Generates default json data with play list files for the device
	 * 
	 * @param serverUrl
	 *            Server url including port number
	 * @param response
	 *            - Response to write json data
	 * @throws IOException
	 */
	private void getPlayList(Device device, DevResponse devResponse) throws IOException {

		devResponse.setResponseDataType(DevResponseDataType.PLAY_LIST);

		List<MediaContent> mediaContents = new ArrayList<MediaContent>();
		PlaylistDao playlistDao = new PlaylistDao(deviceDao);
		if (device != null) {			
			List<Playlist> playlists = playlistDao.findAll();
			List<Playlist> filteredPlaylists = new ArrayList<Playlist>();
			final List<MediaContent> deviceMediaContents = deviceDao.findMediaByDeviceTag(device);
			
			if( !deviceMediaContents.isEmpty() ){			
				// Check playst contents is included in device tags
				for (Playlist playlist : playlists) {
					List<MediaContent> deviceMedia = playlist.getMediaContents().stream()
							.filter(media -> deviceMediaContents.contains(media)).collect(Collectors.toList());
					if (!deviceMedia.isEmpty()) {
						playlist.setMediaContents(deviceMedia);
						filteredPlaylists.add(playlist);
					}
				}
	
				// Order playlists by priority
				filteredPlaylists = filteredPlaylists.stream()
						.sorted((p1, p2) -> Integer.compare(p1.getPriority(), p2.getPriority()))
						.collect(Collectors.toList());
				
				// Get actual media contents on device
				DeviceSchedule deviceSchedule = device.getCurrentDeviceSchedule();
				if(deviceSchedule != null  && deviceSchedule.getLoops() != null && deviceSchedule.getLoops().size() > 0
						&& deviceSchedule.getLoops().get(0).getMediaContents() != null) {
					mediaContents = deviceSchedule.getLoops().get(0).getMediaContents();
				}
	
				// Find playlist with highest priority for current time
				for (int i = 0; i < filteredPlaylists.size(); i++) {
					List<MediaContent> playlistMediaContent = filteredPlaylists.get(i).getMediaContents();
					if(playlistMediaContent.isEmpty()) {
						continue;
					}
					Date validFrom = filteredPlaylists.get(i).getValidFrom();
					Date validTo = filteredPlaylists.get(i).getValidTo();
					Calendar cal = Calendar.getInstance();
					cal.setTime(validFrom);
					Calendar currCal = Calendar.getInstance();
					currCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
					Date currentTime = currCal.getTime();
					
					// Check for current time playlist
					if(currentTime.compareTo(validFrom) >= 0 && currentTime.compareTo(validTo) <= 0 ) {
						// Check current playlist media for difference from actual device playlist media
						int mediaIndex = -1;
						if(playlistMediaContent.size() == mediaContents.size()) {
							for(mediaIndex = 0; mediaIndex < playlistMediaContent.size(); mediaIndex++) {
								if(!playlistMediaContent.get(mediaIndex).equals(mediaContents.get(mediaIndex))) {
									mediaIndex = -1;
									break;
								}
							}
						}
						// If media content differs, create new loop with the new media content
						if(mediaIndex == -1) {
							devResponse.setDataId(devResponse.getDataId() + 1);
							// New playlist
							mediaContents = playlistMediaContent;
							Loop deviceLoop = new Loop();
							deviceLoop.setMediaContents(playlistMediaContent);
							deviceLoop.setSourcePlaylist(filteredPlaylists.get(i));
							List<Loop> deviceLoops = new ArrayList<Loop>();
							deviceLoops.add(deviceLoop);
							deviceSchedule = new DeviceSchedule();
							deviceSchedule.setDevice(device);
							deviceSchedule.setLoops(deviceLoops);
							deviceLoop.setDeviceSchedule(deviceSchedule);
							device.setCurrentDeviceSchedule(deviceSchedule);
							deviceDao.update(device);
						}
	
						break;
					}
				}
			}
		}

		if( mediaContents == null || mediaContents.isEmpty() ) {
			Playlist defaultPlaylist = playlistDao.findDefaultPlaylist();
			if (defaultPlaylist != null && defaultPlaylist.getMediaContents() != null) {
				mediaContents = defaultPlaylist.getMediaContents();
			}
		}
		devResponse.setMediaContents(mediaContents);

		
	}
	
	
	public List<Loop> getDailySchedule() {
		PlaylistDao playlistDao = new PlaylistDao(deviceDao);
		List<Playlist> dailyPlaists = playlistDao.findDailyPlayists();
		List<Loop> loops = new ArrayList<Loop>();
		// Start from playlist with lowest priority(max priority value) and increment to highest priority
		for(int pIdx = dailyPlaists.size() - 1; pIdx >= 0; pIdx--) {
			List<PlaylistSchedule> shcedules = dailyPlaists.get(pIdx).getSchedules();
			for (PlaylistSchedule schedule : shcedules) {
				// First remove previous loops added from playlists with lower priority
				// Or change loops start and end time
				List<Loop> loopsForRemove = new ArrayList<>();
				for (Loop loop : loops) {
					boolean loopStartsInSchedule = (loop.getValidFrom().compareTo(schedule.getStartTime()) >= 0 
							&& loop.getValidFrom().compareTo(schedule.getEndTime()) <= 0) ;
					boolean loopEndsInSchedule = (loop.getValidTo().compareTo(schedule.getStartTime()) >= 0
							&& loop.getValidTo().compareTo(schedule.getEndTime()) <= 0);
					// Check loop timeplan is inside schedule timeplan to remove it 
					// because it's from playlist with lower (maybe equal) priority
					if(loopStartsInSchedule && loopEndsInSchedule) {
						loopsForRemove.add(loop);
					} else if (loopStartsInSchedule && !loopEndsInSchedule) {
						loop.setValidFrom(schedule.getEndTime());
					} else if (!loopStartsInSchedule && loopEndsInSchedule) {
						loop.setValidTo(schedule.getStartTime());
					}
				}
				loops.removeAll(loopsForRemove);
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
}
