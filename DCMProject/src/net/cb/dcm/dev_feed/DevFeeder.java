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
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.PlaylistDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DeviceSchedule;
import net.cb.dcm.jpa.entities.DeviceStatusValue;
import net.cb.dcm.jpa.entities.Loop;
import net.cb.dcm.jpa.entities.MediaContent;
import net.cb.dcm.jpa.entities.Playlist;

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
		
		if (loIp == null || loIp.isEmpty()) {
			moLogger.debug("No device IP. Generating default response.");
			this.getPlayList(null, lsServerUrl, response);
		} else {
			Device loDevice = deviceDao.findDeviceByIp(loIp);
			if (loDevice == null || loDevice.getId() <= 0) {
				moLogger.debug("Register new device with IP " + loIp);
				//// Register device ID in the database
				//// Maybe registering only the IP, then the system
				//// opearator should see it in the
				//// list as a device without data, and maintain it.
				loDevice = deviceDao.registerNewDevice(loIp);
			}
			
			// Update device status
			Map<String, String> propertyValueMap = new HashMap<>();
			propertyValueMap.put(DeviceDAO.PROPERTY_REQUEST_COUNTER, request.getParameter("request"));
			propertyValueMap.put(DeviceDAO.PROPERTY_UPDATE_DATA_COUNTER, request.getParameter("dataId"));
			propertyValueMap.put(DeviceDAO.PROPERTY_USED_DISK_SPACE, request.getParameter("usedSize"));
			propertyValueMap.put(DeviceDAO.PROPERTY_FREE_DISK_SPACE, request.getParameter("freeSize"));
			propertyValueMap.put(DeviceDAO.PROPERTY_PLAYING_MEDIA_NAME, request.getParameter("mediaName"));
			propertyValueMap.put(DeviceDAO.PROPERTY_PLAYING_MEDIA_TIME, request.getParameter("mediaTime"));
			deviceDao.updateStatus(loDevice, propertyValueMap);

			// Load device default playlist
			this.getPlayList(loDevice, lsServerUrl, response);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Should not be called
		doGet(request, response);
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
	private void getPlayList(Device device, String serverUrl, HttpServletResponse response) throws IOException {
		Gson loGson = new GsonBuilder().setPrettyPrinting().create();
		DevResponse loDevResponse = new DevResponse(serverUrl);
		loDevResponse.setResponseDataType(DevResponseDataType.PLAY_LIST);

		List<MediaContent> mediaContents = new ArrayList<MediaContent>();
		PlaylistDao playlistDao = new PlaylistDao(deviceDao);
		if (device != null) {
			// Get saved status data id
			Map<String, DeviceStatusValue> propertyValueMap = device.getCurrentDeviceStatus().getDeviceStatusValues()
					.stream()
					.collect(Collectors.toMap(d -> d.getProperty().getKey(), d -> d));
			
			DeviceStatusValue sDataId = propertyValueMap.get(DeviceDAO.PROPERTY_UPDATE_DATA_COUNTER);
			if(sDataId != null && sDataId.getValue() != null){
				// Set same data id for new response
				int dataId = Integer.valueOf(sDataId.getValue());
				if( dataId == 1)
					dataId++;
				
				loDevResponse.setDataId( dataId );
			}
			
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
							loDevResponse.setDataId(loDevResponse.getDataId() + 1);
							// New playlist
							mediaContents = playlistMediaContent;
							Loop deviceLoop = new Loop();
							deviceLoop.setMediaContents(playlistMediaContent);
							List<Loop> deviceLoops = new ArrayList<Loop>();
							deviceLoops.add(deviceLoop);
							deviceSchedule = new DeviceSchedule();
							deviceSchedule.setDevice(device);
							deviceSchedule.setLoops(deviceLoops);
							deviceSchedule.setTime(currentTime);
							deviceLoop.setDeviceSchedule(deviceSchedule);
							device.setCurrentDeviceSchedule(deviceSchedule);
							deviceDao.update(device);
						}
	
						break;
					}
				}
			}
		}

		if( mediaContents == null || mediaContents.isEmpty() ){
			Playlist defaultPlaylist = playlistDao.findDefaultPlaylist();
			if (defaultPlaylist != null && defaultPlaylist.getMediaContents() != null) {
				mediaContents = defaultPlaylist.getMediaContents();
			}
		}
		loDevResponse.setMediaContents(mediaContents);

		loDevResponse.generateResponse();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(loGson.toJson(loDevResponse.getResponseJson()));
	}
}
