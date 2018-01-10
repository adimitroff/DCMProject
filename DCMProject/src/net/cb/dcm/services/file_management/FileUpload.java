package net.cb.dcm.services.file_management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;

import net.cb.dcm.jpa.SettingDao;
import net.cb.dcm.jpa.entities.Setting;
import net.cb.dcm.tools.ImageProcessor;

/**
 * Servlet for processing http request for new content from the samsung tv app
 * and other smart devices.
 */
@WebServlet("/upload")
@MultipartConfig
public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger moLogger = Logger.getLogger(FileUpload.class.getCanonicalName());

	/** {@inheritDoc} */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/** {@inheritDoc} */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		// Create path components to save the file
		final Part filePart = request.getPart("fileUploader");
		final String fileName = getFileName(filePart);

		OutputStream out = null;
		InputStream filecontent = null;
		InputStream localFileInputStream = null;
		final PrintWriter writer = response.getWriter();
		SettingDao settingDao = new SettingDao();
		try {

			Map<String, Setting> settings = settingDao.findAllAsMap();
			Setting setingMediaPath = settings.get(SettingDao.SETTING_MEDIA_PATH);
			if (setingMediaPath == null) {
				throw new FileNotFoundException("Media path setting not specified.");
			}
			String filePath = setingMediaPath.getValue() + File.separator + fileName;
			out = new FileOutputStream(new File(filePath));
			filecontent = filePart.getInputStream();

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			moLogger.log(Level.INFO, "File{0}being uploaded to {1}",
					new Object[] { fileName, setingMediaPath.getValue() });

			ImageProcessor.generateAndSaveThumbnails(filePath);
			// Upload file to the ftp server
			Setting settingFtpUrl = settings.get(SettingDao.SETTING_FTP_SERVER);
			if (settingFtpUrl != null && settingFtpUrl.getValue() != null
					&& settingFtpUrl.getValue().trim().length() > 0) {

				FTPClient ftpClient = new FTPClient();
				ftpClient.connect(settingFtpUrl.getValue());
				ftpClient.login(settings.get(SettingDao.SETTING_FTP_USER).getValue(),
						settings.get(SettingDao.SETTING_FTP_PASS).getValue());
				String remoteDir = settings.get(SettingDao.SETTING_FTP_SERVER_MEDIA_PATH).getValue();
				if (remoteDir != null && remoteDir.trim().length() > 0) {
					Assert.assertTrue(ftpClient.changeWorkingDirectory(remoteDir));
				}
				localFileInputStream = new FileInputStream(filePath);
				ftpClient.storeFile(fileName, localFileInputStream);
				ftpClient.logout();
			}
		} catch (FileNotFoundException fne) {
			writer.println("You either did not specify a file to upload or are "
					+ "trying to upload a file to a protected or nonexistent " + "location.");
			writer.println("<br/> ERROR: " + fne.getMessage());

			moLogger.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[] { fne.getMessage() });
		} finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (localFileInputStream != null) {
				localFileInputStream.close();
			}
		}
	}

	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		moLogger.log(Level.INFO, "Part Header = {0}", partHeader);
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}