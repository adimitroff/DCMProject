package net.cb.dcm.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.cb.dcm.jpa.SettingDao;
import net.cb.dcm.jpa.entities.Setting;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FtpTest {

	private final static String LOCAL_MEDIA_PATH = 
			"/Users/krum/Documents/Work/Web/Java/DCMProject/DCMProject/WebContent/TvApp/media/" ;
	
	@Test
	public void test1Connection() throws SocketException, IOException {
		SettingDao settingDao = new SettingDao();
		
		Map<String, Setting> settings = settingDao.findAllAsMap();
        Setting settingFtpUrl = settings.get(SettingDao.SETTING_FTP_SERVER);
        Assert.assertNotNull(settingFtpUrl);
        Assert.assertNotNull(settingFtpUrl.getValue());
        Assert.assertTrue(settingFtpUrl.getValue().trim().length() > 0);

		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(settingFtpUrl.getValue());
		Assert.assertTrue(ftpClient.login(settings.get(SettingDao.SETTING_FTP_USER).getValue(),
				settings.get(SettingDao.SETTING_FTP_PASS).getValue()));
		Assert.assertTrue(ftpClient.logout());
	}
	
	@Test
	public void test2FileUpload() throws SocketException, IOException {
		SettingDao settingDao = new SettingDao();
		
		Map<String, Setting> settings = settingDao.findAllAsMap();
        Setting settingFtpUrl = settings.get(SettingDao.SETTING_FTP_SERVER);
        Assert.assertNotNull(settingFtpUrl);
        Assert.assertNotNull(settingFtpUrl.getValue());
        Assert.assertTrue(settingFtpUrl.getValue().trim().length() > 0);

        String fileName = "MTELHomePhoneTVC.mp4";
        String filePath = LOCAL_MEDIA_PATH + fileName;
        	
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(settingFtpUrl.getValue());
		ftpClient.login(settings.get(SettingDao.SETTING_FTP_USER).getValue(),
				settings.get(SettingDao.SETTING_FTP_PASS).getValue());
		String remoteDir = settings.get(SettingDao.SETTING_FTP_SERVER_MEDIA_PATH).getValue();
		if(remoteDir != null && remoteDir.trim().length() > 0) {
			Assert.assertTrue(ftpClient.changeWorkingDirectory(remoteDir));
		}
		FileInputStream inputStream = new FileInputStream(filePath);
		Assert.assertTrue(ftpClient.storeFile(fileName, inputStream));
		ftpClient.logout();
		inputStream.close();
	}
}
