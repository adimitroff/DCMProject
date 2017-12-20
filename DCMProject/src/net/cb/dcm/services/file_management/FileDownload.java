package net.cb.dcm.services.file_management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cb.dcm.jpa.SettingDao;
import net.cb.dcm.jpa.entities.Setting;
import net.cb.dcm.tools.ExpressionValidator;
import net.cb.dcm.tools.ValidationPatterns;

@WebServlet("/get")
public class FileDownload extends HttpServlet {
	
	private static final long serialVersionUID = 4838640584214645172L;
	@Override
	protected void doGet(HttpServletRequest ioRequest, HttpServletResponse ioResponse) throws ServletException, IOException {
        String lsFileName = ioRequest.getParameter("filename");//.toLowerCase();
        if ( lsFileName == null || lsFileName.equals("") || !ExpressionValidator.isStringValid( lsFileName, ValidationPatterns.FILE_NAME_EXPRESSION ) ){
        	return;
        }
        String lsFileExtension = getFileExtension(lsFileName);

        String lsFileType = "";
        switch(lsFileExtension){
        case "txt":
        	lsFileType = "text/plain";
        	break;
        case "jpg":
        	lsFileType = "image/jpg";
        	break;
        case "png":
        	lsFileType = "image/png";
        	break;
        case "m4v":
        	lsFileType = "video/mp4";
        	break;
        case "mp4":
        	lsFileType = "video/mp4";
        	break;
        }
        ioResponse.setContentType(lsFileType);

        ioResponse.setHeader("Content-disposition","attachment; filename=" + lsFileName);

    	SettingDao settingDao = new SettingDao();
    	Setting setingMediaPath = settingDao.findAllAsMap().get(SettingDao.SETTING_MEDIA_PATH);
    	if(setingMediaPath == null) {
    		throw new FileNotFoundException("Media path setting not specified!");
    	}
        File loFile = new File(setingMediaPath.getValue() + File.separator + lsFileName);

        // This should send the file to browser
        OutputStream loOutputStream = ioResponse.getOutputStream();
        FileInputStream loInputStream = new FileInputStream(loFile);
        byte[] laBuffer = new byte[4096];
        int liLength;
        while ((liLength = loInputStream.read(laBuffer)) > 0){
           loOutputStream.write(laBuffer, 0, liLength);
           loOutputStream.flush();
        }
        loInputStream.close();
	}
	
	private String getFileExtension(String isFileName) {
	    try {
	        return isFileName.substring(isFileName.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
}
