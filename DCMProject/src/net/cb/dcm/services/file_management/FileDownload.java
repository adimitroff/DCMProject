package net.cb.dcm.services.file_management;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/get")
public class FileDownload extends HttpServlet {
	
	private static final String SERVER_PATH = "C:\\web_content";
	private static final long serialVersionUID = 4838640584214645172L;
	@Override
	protected void doGet(HttpServletRequest ioRequest, HttpServletResponse ioResponse) throws ServletException, IOException {
        String lsFileName = ioRequest.getParameter("filename").toLowerCase();
        if (lsFileName == null || lsFileName.equals("")){
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
        }
        ioResponse.setContentType(lsFileType);

        ioResponse.setHeader("Content-disposition","attachment; filename=" + lsFileName);

        File loFile = new File(SERVER_PATH + File.separator + lsFileName);

        // This should send the file to browser
        OutputStream loOutputStream = ioResponse.getOutputStream();
        FileInputStream loInputStream = new FileInputStream(loFile);
        byte[] laBuffer = new byte[4096];
        int liLength;
        while ((liLength = loInputStream.read(laBuffer)) > 0){
           loOutputStream.write(laBuffer, 0, liLength);
        }
        loInputStream.close();
        loOutputStream.flush();
	}
	
	private String getFileExtension(String isFileName) {
	    try {
	        return isFileName.substring(isFileName.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
}
