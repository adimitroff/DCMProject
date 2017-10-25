package net.cb.dcm.dev_feed;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementing a simple JPA based persistence sample application for SAP Cloud Platform.
 */
public class Dev_feeder extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger moLogger = LoggerFactory.getLogger(Dev_feeder.class);

    /** {@inheritDoc} */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String loIp = request.getParameter("IP");
    	if (loIp == null || loIp.isEmpty()) {
//    		response.getWriter().println("<p>Here you need to build the initial page, which will be used by all devices</p>");
    		Gson loGson = new GsonBuilder().setPrettyPrinting().create();
    		JsonObject loJsonObject = new JsonObject();
    		loJsonObject.addProperty("redirectUrl", "");
    		
    		JsonArray loJsonArray = new JsonArray();
    		loJsonArray.add("app/javascript/App.js");
    		loJsonArray.add("app/javascript/SSSP.js");
//    		.....
    		loJsonObject.add("jsFiles", loJsonArray);
    		
// Finally    		
    	    response.setContentType("application/json");
    	    response.setCharacterEncoding("UTF-8");
    	    response.getWriter().write(loGson.toJson(loJsonObject));
    	} else {
    		try {
    			response.getWriter().println("<p>Find the device with IP: " + loIp + " and generate the page</p>");
//    			Device loDevice = DeviceDAO.findDeviceByIp(loIp);
//    			if (loDevice == null){
//// Register device ID in the database
//// Maybe registering only the IP, then the system opearator should see it in the 
//// list as a device without data, and maintain it. 
////    				Load device default playlist
//    			} else {
////    				Get device playlist and load it  
//    			}
    		} catch (Exception e){
    			moLogger.error("Error rendering the page: " + e.getMessage());
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
}
