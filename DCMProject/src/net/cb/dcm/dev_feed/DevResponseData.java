package net.cb.dcm.dev_feed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class DevResponseData {
	
	private JsonObject responseData;
	
	
	/**
	 * Url to redirect requests from device
	 */
	private static final String PROP_REDIRECT_URL = "redirectUrl";
	
	/**
	 * 
	 */
	private static final String PROP_DATA_ID = "dataId";
	
	private static final String PROP_JS_FILES = "jsFiles";
	private static final String PROP_HTML_CONTENT = "htmlContent";
	private static final String PROP_COMMANDS = "commands";
	private static final String PROP_IMAGE_GLOBAL_SETTINGS = "imageGlobalSettings";
	private static final String PROP_VIDEO_GLOBAL_SETTINGS = "videoGlobalSettings";
	private static final String PROP_PLAY_LIST_FILES = "playListFiles";
	private static final String PROP_X = "x";
	private static final String PROP_Y = "y";
	private static final String PROP_WIDTH = "width";
	private static final String PROP_HEIGHT = "height";
	private static final String PROP_SRC = "src";
	private static final String PROP_NAME = "name";
	private static final String PROP_TYPE = "type";
	private static final String PROP_LOCATION = "location";
	private static final String PROP_RSS_FEED = "rssFeed";
	private static final String PROP_URL = "url";
	private static final String PROP_XML_CONTENT = "xmlContent";
	
	
	public DevResponseData(String deviceIP) {
		super();
		this.responseData = new JsonObject();
		
	}

	/**
	 * 
	 */
	public void generateResponse()
	{
		this.initResponseData();
		this.appendNewVersion();
		this.appendCommands();
		this.appendPlayList();
		this.appendRssFeed();
	}
	
	public JsonObject getResponseData()
	{
		return this.responseData;
	}
	
	
	/**
	 * 
	 */
	private void initResponseData()
	{
		int dataId = 1; // TODO get from database for this device
		this.responseData.addProperty(PROP_REDIRECT_URL , "");
		this.responseData.addProperty(PROP_DATA_ID, dataId);
	}
	
	private void appendNewVersion()
	{
		JsonArray loJsonArray = new JsonArray();
		loJsonArray.add("app/javascript/App.js");
		loJsonArray.add("app/javascript/SSSP.js");
//		.....
		this.responseData.add(PROP_JS_FILES, loJsonArray);
		
		this.responseData.addProperty(PROP_HTML_CONTENT, "");
	}

	private void appendCommands()
	{
		JsonArray loJsonArray = new JsonArray();
		loJsonArray.add("");
		this.responseData.add(PROP_COMMANDS, loJsonArray);
	}
	
	private void appendPlayList()
	{
		
		JsonObject loImageGlobalSettings = new JsonObject();
		loImageGlobalSettings.addProperty(PROP_X, 0);
		loImageGlobalSettings.addProperty(PROP_Y, 0);
		loImageGlobalSettings.addProperty(PROP_WIDTH, 1280);
		loImageGlobalSettings.addProperty(PROP_HEIGHT, 720);
		this.responseData.add(PROP_IMAGE_GLOBAL_SETTINGS, loImageGlobalSettings);
		JsonObject loVideoGlobalSettings = new JsonObject();
		loVideoGlobalSettings.addProperty(PROP_X, 0);
		loVideoGlobalSettings.addProperty(PROP_Y, 0);
		loVideoGlobalSettings.addProperty(PROP_WIDTH, 1280);
		loVideoGlobalSettings.addProperty(PROP_HEIGHT, 720);
		this.responseData.add(PROP_VIDEO_GLOBAL_SETTINGS, loVideoGlobalSettings);
		
		JsonArray loPlayListFiles = new JsonArray();
//		JsonObject loFile = new JsonObject();
//		loFile.addProperty(PROP_SRC, "");
//		loFile.addProperty(PROP_NAME, "");
//		loFile.addProperty(PROP_TYPE, "");
//		loFile.addProperty(PROP_LOCATION, "");
//		loPlayListFiles.add(loFile);
		this.responseData.add(PROP_PLAY_LIST_FILES, loPlayListFiles);
	}
	
	private void appendRssFeed()
	{
		JsonObject loRssFeed = new JsonObject();
		loRssFeed.addProperty(PROP_URL, "");
		loRssFeed.addProperty(PROP_XML_CONTENT, "");
		this.responseData.add(PROP_RSS_FEED, loRssFeed);
	}
}
