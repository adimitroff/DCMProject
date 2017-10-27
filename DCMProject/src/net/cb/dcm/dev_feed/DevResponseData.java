package net.cb.dcm.dev_feed;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.cb.dcm.enums.MediaObjectType;
import net.cb.dcm.jpa.entities.MediaContent;

class DevResponseData {
	
	public enum ResponseDataType
	{
		DATA_TYPE_NONE,
		DATA_TYPE_NEW_VERSION,
		DATA_TYPE_COMMAND,
		DATA_TYPE_PLAY_LIST
	}

	private static final Logger moLogger = LoggerFactory.getLogger(DevResponseData.class);
	
	private JsonObject responseData;

	/**
	 * Url to redirect requests from device
	 */
	private static final String PROP_REDIRECT_URL = "redirectUrl";

	/**
	 * 
	 */
	private static final String PROP_DATA_ID = "dataId";
	private static final String PROP_DATA_TYPE = "dataType";

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

	private ResponseDataType responseDataType = ResponseDataType.DATA_TYPE_NONE;

	private List<MediaContent> mediaContents;
	private List<String> jsFiles;
	private String htmlContent;
	private String rssFeedUrl;
	private String rssFeedXmlContent;

	
	public DevResponseData(String deviceIP) {
		super();
		this.responseData = new JsonObject();

	}
	
	/**
	 * 
	 */
	public void generateResponse() {
		int liDataId = 1; // TODO get from database for this device
		this.responseData.addProperty(PROP_REDIRECT_URL, ""); // TODO Currently not used
		this.responseData.addProperty(PROP_DATA_ID, liDataId);
		switch (this.responseDataType) {
		case DATA_TYPE_NEW_VERSION:
			this.responseData.addProperty(PROP_DATA_TYPE, "new_version");
			this.appendNewVersion();
			break;
		case DATA_TYPE_COMMAND:
			this.responseData.addProperty(PROP_DATA_TYPE, "command");
			this.appendCommands();
			this.appendPlayList();
			this.appendRssFeed();
			break;
		case DATA_TYPE_PLAY_LIST:
			this.responseData.addProperty(PROP_DATA_TYPE, "playlist");
			this.appendPlayList();
			break;
		default:
			moLogger.warn("generateResponse() - Response data type not set!");
			break;
		}
	}

	public JsonObject getResponseData() {
		return this.responseData;
	}

	private void appendNewVersion() {
		JsonArray loJsonArray = new JsonArray();
		loJsonArray.add("app/javascript/App.js");
		loJsonArray.add("app/javascript/SSSP.js");
		// .....
		this.responseData.add(PROP_JS_FILES, loJsonArray);

		this.responseData.addProperty(PROP_HTML_CONTENT, this.htmlContent);
	}

	private void appendCommands() {
		JsonArray loJsonArray = new JsonArray();
		loJsonArray.add("");
		this.responseData.add(PROP_COMMANDS, loJsonArray);
	}

	private void appendPlayList() {

		if(this.mediaContents == null || this.mediaContents.size() == 0) {
			return;
		}
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
		for (MediaContent mediaContent : mediaContents) {
			 JsonObject loFile = new JsonObject();
			 loFile.addProperty(PROP_SRC, "" + mediaContent.getName()); // TODO url
			 loFile.addProperty(PROP_NAME, mediaContent.getName());
			 loFile.addProperty(PROP_TYPE, mediaContent.getMediaType() == MediaObjectType.JPEG ? "image" : "video" );
			 loFile.addProperty(PROP_LOCATION, "local");
			 loPlayListFiles.add(loFile);
		}

		this.responseData.add(PROP_PLAY_LIST_FILES, loPlayListFiles);
	}

	private void appendRssFeed() {
		if (this.rssFeedUrl != null || this.rssFeedXmlContent != null) {
			JsonObject loRssFeed = new JsonObject();
			loRssFeed.addProperty(PROP_URL, this.rssFeedUrl);
			loRssFeed.addProperty(PROP_XML_CONTENT, this.rssFeedXmlContent);
			this.responseData.add(PROP_RSS_FEED, loRssFeed);
		}
	}

	public List<MediaContent> getMediaContents() {
		return mediaContents;
	}

	public void setMediaContents(List<MediaContent> mediaContents) {
		this.mediaContents = mediaContents;
	}

	public List<String> getJsFiles() {
		return jsFiles;
	}

	public void setJsFiles(List<String> jsFiles) {
		this.jsFiles = jsFiles;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getRssFeedUrl() {
		return rssFeedUrl;
	}

	public void setRssFeedUrl(String rssFeedUrl) {
		this.rssFeedUrl = rssFeedUrl;
	}

	public String getRssFeedXmlContent() {
		return rssFeedXmlContent;
	}

	public void setRssFeedXmlContent(String rssFeedXmlContent) {
		this.rssFeedXmlContent = rssFeedXmlContent;
	}

	
	public ResponseDataType getResponseDataType() {
		return responseDataType;
	}

	public void setResponseDataType(ResponseDataType responseDataType) {
		this.responseDataType = responseDataType;
	}
}
