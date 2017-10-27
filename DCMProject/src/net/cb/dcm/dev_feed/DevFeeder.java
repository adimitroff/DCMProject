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

import net.cb.dcm.dev_feed.DevResponseData.ResponseDataType;
import net.cb.dcm.enums.MediaObjectType;
import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.MediaContent;

/**
 * Servlet for processing http request for new content from the samsung tv app and other smart devices.
 */
public class DevFeeder extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger moLogger = LoggerFactory.getLogger(DevFeeder.class);

    /** {@inheritDoc} */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String loIp = request.getParameter("ip");
		
    	if (loIp == null || loIp.isEmpty()) {
    		this.getDefaultPlayList(response);
    	} else {
    		try {
    			response.getWriter().println("<p>Find the device with IP: " + loIp + " and generate the page</p>");
    			DeviceDAO loDeviceDao = new DeviceDAO();
    			Device loDevice = loDeviceDao.findDeviceByIp(loIp);
    			response.getWriter().println("<p> loDevice: " + loDevice + " </p>" );
    			if (loDevice == null || loDevice.getId() <= 0){
    				response.getWriter().println("<p> Register new device</p>");
    				//// Register device ID in the database
    				//// Maybe registering only the IP, then the system opearator should see it in the 
    				//// list as a device without data, and maintain it. 
    				loDevice = loDeviceDao.registerNewDevice(loIp);
    				moLogger.debug("loDevice id: " + loDevice.getId());
    				////    				Load device default playlist
    			} else {
    				////    				Get device playlist and load it  
    			}
    		} catch (Exception e){
    			moLogger.error("Error rendering the page: " + e.getMessage());
    			throw new ServletException(e);
    		}
    		
    	}
    }

    /** {@inheritDoc} */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
//    	Should not be called
    	doGet(request, response);
    }
    
    private void getDefaultPlayList(HttpServletResponse response) throws IOException
    {
		Gson loGson = new GsonBuilder().setPrettyPrinting().create();
		DevResponseData devResponseData = new DevResponseData(null);
		devResponseData.setResponseDataType(ResponseDataType.DATA_TYPE_PLAY_LIST);
		ArrayList<MediaContent> mediaContents = new ArrayList<>();
		
		//////////////////////////////////
		// Start adding some testing contents for the playlist
		MediaContent mediaContent = new MediaContent();
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setName("MtelDTH.mp4");
		mediaContents.add(mediaContent);
		
		mediaContent = new MediaContent();
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setName("MtelUniveralPrima2.m4v");
		mediaContents.add(mediaContent);
		
		mediaContent = new MediaContent();
		mediaContent.setMediaType(MediaObjectType.MPEG);
		mediaContent.setName("MTELHomePhoneTVC.mp4");
		mediaContents.add(mediaContent);
		
		devResponseData.setMediaContents(mediaContents);
		// End adding of testing content
		///////////////////////////////
		
		devResponseData.generateResponse();
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(loGson.toJson(devResponseData));
    }
}
