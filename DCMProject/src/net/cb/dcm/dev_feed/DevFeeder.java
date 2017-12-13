package net.cb.dcm.dev_feed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cb.dcm.enums.DevProcedureScheduleType;
import net.cb.dcm.enums.DevResponseDataType;
import net.cb.dcm.enums.DeviceCommand;
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.GenericDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DeviceProcedure;
import net.cb.dcm.jpa.entities.DeviceSchedule;
import net.cb.dcm.jpa.entities.Loop;
import net.cb.dcm.jpa.entities.MediaContent;

/**
 * Servlet for processing http request for new content from the samsung tv app
 * and other smart devices.
 */
public class DevFeeder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger moLogger = LoggerFactory.getLogger(DevFeeder.class);

	/** {@inheritDoc} */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DeviceDAO deviceDao = new DeviceDAO();
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
			this.getPlayList(null, loDevResponse, deviceDao);
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
			
			// First check and generate response with commands
			if(!this.getProcedure(loDevice, loDevResponse, deviceDao)) {
				// Check for new device schedule for playlist response
				this.getPlayList(loDevice, loDevResponse, deviceDao);
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
	
	private boolean getProcedure(Device device, DevResponse devResponse, DeviceDAO deviceDao)
	{
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
			if(deviceProcedure.getScheduleType() == DevProcedureScheduleType.DAILY) {
				calendar.set(currCalendar.get(Calendar.YEAR), currCalendar.get(Calendar.MONTH),
						currCalendar.get(Calendar.DAY_OF_MONTH));
			}
			long timeDiff = currCalendar.getTimeInMillis() - calendar.getTimeInMillis();
			// Execute procedure up to 30 min after scheduled execution time
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
	private boolean getPlayList(Device device, DevResponse devResponse,DeviceDAO deviceDao) throws IOException {
		if(device == null) {
			return false;
		}
		// Check for new device schedule once every 30 seconds or more but not more often.
		if(device.getCurrentDeviceSchedule() != null 
				&& (new Date().getTime() - device.getCurrentDeviceSchedule().getDeviceCheckTime().getTime()) < 30000) {
			return false;
		}

		List<MediaContent> mediaContents = new ArrayList<MediaContent>();
		
		if (device != null) {			
			DeviceSchedule schedule = ScheduleGeneratorSingleton.getInstance().getDeviceSchedule(device);
			schedule.setDeviceCheckTime(new Date());
			if (schedule.getDeviceDataId() == 0 || schedule.getDeviceDataId() > devResponse.getDataId()) {
				// Find loop for current time
				List<Loop> loops = schedule.getLoops();
				Calendar cal = Calendar.getInstance();
				for (Loop loop : loops) {
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(loop.getValidFrom());
					// Compare times of same year, month and day
					cal.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH));
					Date timeOfDay = cal.getTime();
					if (loop.getValidFrom().compareTo(timeOfDay) <= 0 && loop.getValidTo().compareTo(timeOfDay) >= 0) {
						if(loop.getMediaContents() != null) {
							mediaContents = loop.getMediaContents();
							schedule.setDeviceDataId(devResponse.getDataId() + 1);
							
						}
						break;
					}
				}
			}
			GenericDao<DeviceSchedule> scheduleDao = new GenericDao<DeviceSchedule>(deviceDao) {
			};
			scheduleDao.update(schedule);
		}

//		if (mediaContents.isEmpty()) {
//			PlaylistDao playlistDao = new PlaylistDao(deviceDao);
//			// Get default playlist
//			Playlist defaultPlaylist = playlistDao.findDefaultPlaylist();
//			if (defaultPlaylist != null && defaultPlaylist.getMediaContents() != null) {
//				mediaContents = defaultPlaylist.getMediaContents();
//			}
//		}
		// Generate playlist response
		if (!mediaContents.isEmpty()) {
			devResponse.setDataId(devResponse.getDataId() + 1);
			devResponse.setResponseDataType(DevResponseDataType.PLAY_LIST);
			devResponse.setMediaContents(mediaContents);
			return true;
		}

		return false;
	}
	
	

}
