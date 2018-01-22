package net.cb.dcm.services.file_management;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.fusesource.hawtbuf.ByteArrayOutputStream;

import net.cb.dcm.jpa.DeviceDAO;
import net.cb.dcm.jpa.MediaContentDao;
import net.cb.dcm.jpa.SettingDao;
import net.cb.dcm.jpa.entities.Device;
import net.cb.dcm.jpa.entities.DeviceStatusValue;
import net.cb.dcm.jpa.entities.MediaContent;
import net.cb.dcm.jpa.entities.Setting;
import net.cb.dcm.tools.ExpressionValidator;
import net.cb.dcm.tools.ImageProcessor;
import net.cb.dcm.tools.ValidationPatterns;

@WebServlet("/get")
public class FileDownload extends HttpServlet {
	
	private static final long serialVersionUID = 4838640584214645172L;
	@Override
	protected void doGet(HttpServletRequest ioRequest, HttpServletResponse ioResponse) throws ServletException, IOException {
        String lsFileName = ioRequest.getParameter("filename");//.toLowerCase();
        boolean isThumb = ioRequest.getParameter("thumb") != null;
        int mediaTimeSec = 0;
        DeviceDAO deviceDao = new DeviceDAO();
		
        if(lsFileName == null) {
        	if(ioRequest.getParameter("media") != null) {
	        	long mediaId = Integer.parseInt(ioRequest.getParameter("media"));
	        	MediaContentDao mediaContentDao = new MediaContentDao(deviceDao);
	        	MediaContent mediaContent = mediaContentDao.findById(mediaId);
	        	if(mediaContent != null) {
	        		lsFileName = mediaContent.getFilePath();
	        	}
	        	
        	} else if(ioRequest.getParameter("media") != null) {
	        	long deviceId = Integer.parseInt(ioRequest.getParameter("device"));
	        	Device device = deviceDao.findById(deviceId);
	        	if(device != null && device.getCurrentDeviceStatus() != null) {
	        		List<DeviceStatusValue> statusValues = device.getCurrentDeviceStatus().getDeviceStatusValues();
	        		for (DeviceStatusValue deviceStatusValue : statusValues) {
						if(deviceStatusValue.getProperty().getKey().equals(DeviceDAO.PROPERTY_PLAYING_MEDIA_NAME)) {
							lsFileName = deviceStatusValue.getValue();
						}
						if(deviceStatusValue.getProperty().getKey().equals(DeviceDAO.PROPERTY_PLAYING_MEDIA_TIME)) {
							mediaTimeSec = Integer.parseInt(deviceStatusValue.getValue());
						}
					}
	        	}
        	}
        }
        if ( lsFileName == null || lsFileName.isEmpty() 
        		|| !ExpressionValidator.isStringValid( lsFileName, ValidationPatterns.FILE_NAME_EXPRESSION ) ){
        	ioResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        	return;
        }
    	SettingDao settingDao = new SettingDao(deviceDao);
    	Setting setingMediaPath = settingDao.findAllAsMap().get(SettingDao.SETTING_MEDIA_PATH);
    	if(setingMediaPath == null) {
    		throw new FileNotFoundException("Media path setting not specified!");
    	}
        File loFile = new File(setingMediaPath.getValue() + File.separator + lsFileName);
        if(!loFile.exists()) {
        	ioResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        	return;
        }
    	
        // Check file type
        String lsFileType = "";
		String lsFileExtension = getFileExtension(lsFileName);
		boolean isVideoFile = false;
		switch (lsFileExtension) {
		case "txt":
			lsFileType = "text/plain";
			break;
		case "jpg":
		case "jpeg":
			lsFileType = "image/jpg";
			break;
		case "png":
			lsFileType = "image/png";
			break;
		case "avi":
		case "m4v":
		case "mp4":
			lsFileType = "video/mp4";
			isVideoFile = true;
			break;
		default:
        	ioResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        	return;
		}

        BufferedImage image = null;
        if(isThumb) {
        	lsFileType = "image/jpg";
        	if(isVideoFile && mediaTimeSec > 0) {
        		// Generate thumbnail image from video file in specified media time.
        		Object[] imageData = ImageProcessor.genImageFromVideo(loFile.getAbsolutePath(), mediaTimeSec);
        		if(imageData == null) {
	            	ioResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
	            	return;
        		} else {
        			BufferedImage original = (BufferedImage) imageData[0];
        			image = ImageProcessor.generateThumbnails(original)[0];
        		}
        	} else {
	        	String thumbFilePath = ImageProcessor.getThumbnailPath(loFile, ImageProcessor.DEFAULT_THUMB_SIZE);
	        	loFile = new File(thumbFilePath);
	            if(!loFile.exists()) {
	            	ioResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
	            	return;
	            }
        	}
        }

        ioResponse.setContentType(lsFileType);
        ioResponse.setHeader("Content-disposition","attachment; filename=" + lsFileName);
        
        // This should send the file to browser
		InputStream loInputStream = null;
		try {
			OutputStream loOutputStream = ioResponse.getOutputStream();
			if(image != null) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "jpg", os);
				loInputStream = new ByteArrayInputStream(os.toByteArray());
			} else {
				loInputStream = new FileInputStream(loFile);
			}
			byte[] laBuffer = new byte[4096];
			int liLength;
			while ((liLength = loInputStream.read(laBuffer)) > 0) {
				loOutputStream.write(laBuffer, 0, liLength);
				loOutputStream.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (loInputStream != null) {
				loInputStream.close();
			}
		}
	}
	
	private String getFileExtension(String isFileName) {
	    try {
	        return isFileName.substring(isFileName.lastIndexOf(".") + 1).toLowerCase();
	    } catch (Exception e) {
	        return "";
	    }
	}
}
