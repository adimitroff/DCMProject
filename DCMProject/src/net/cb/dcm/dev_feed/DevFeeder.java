package net.cb.dcm.dev_feed;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cb.dcm.enums.DevResponseDataType;
import net.cb.dcm.enums.MediaObjectType;
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.entities.Device;
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
		String loIp = request.getParameter("ip");
		String lsServerUrl = "http://" + request.getServerName() + ":" + request.getServerPort();
		if (loIp == null || loIp.isEmpty()) {
			moLogger.debug("No device IP. Generating default response.");
			this.getDefaultPlayList(lsServerUrl, response);
		} else {
			DeviceDAO loDeviceDao = new DeviceDAO();
			Device loDevice = loDeviceDao.findDeviceByIp(loIp);
			if (loDevice == null || loDevice.getId() <= 0) {
				moLogger.debug("Register new device with IP " + loIp);
				//// Register device ID in the database
				//// Maybe registering only the IP, then the system
				//// opearator should see it in the
				//// list as a device without data, and maintain it.
				loDevice = loDeviceDao.registerNewDevice(loIp);
				// Load device default playlist
				this.getDefaultPlayList(lsServerUrl, response);
			} else {
				// TODO Get device playlist and load it
				this.getDefaultPlayList(lsServerUrl, response);
			}
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
	private void getDefaultPlayList(String serverUrl, HttpServletResponse response) throws IOException {
		Gson loGson = new GsonBuilder().setPrettyPrinting().create();
		DevResponse loDevResponse = new DevResponse(serverUrl);
		loDevResponse.setResponseDataType(DevResponseDataType.PLAY_LIST);
		ArrayList<MediaContent> mediaContents = new ArrayList<>();

		//////////////////////////////////
		// Start adding some testing contents for the playlist
		MediaContent mediaContent = new MediaContent();
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setName("MtelDTH.mp4");
		mediaContents.add(mediaContent);

		mediaContent = new MediaContent();
		mediaContent.setMediaType(MediaObjectType.JPEG);
		mediaContent.setName("map4g.jpg");
		mediaContents.add(mediaContent);

		mediaContent = new MediaContent();
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setName("MtelUniveralPrima2.m4v");
		mediaContents.add(mediaContent);

		mediaContent = new MediaContent();
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setName("MTELHomePhoneTVC.mp4");
		mediaContents.add(mediaContent);

		loDevResponse.setMediaContents(mediaContents);
		// End adding of testing content
		///////////////////////////////

		loDevResponse.generateResponse();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(loGson.toJson(loDevResponse.getResponseJson()));
	}
}
