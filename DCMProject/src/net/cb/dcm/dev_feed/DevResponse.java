package net.cb.dcm.dev_feed;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.cb.dcm.enums.DevResponseDataType;
import net.cb.dcm.enums.DeviceCommand;
import net.cb.dcm.enums.MediaObjectType;
import net.cb.dcm.jpa.entities.MediaContent;

/**
 * Class for generation of response data to the device.
 * 
 * @author krum
 *
 */
public class DevResponse {

	private static final Logger moLogger = LoggerFactory.getLogger(DevResponse.class);

	/**
	 * Json response
	 */
	private JsonObject responseJson;

	/**
	 * Property name for url to redirect requests from device
	 */
	private static final String PROP_REDIRECT_URL = "redirectUrl";

	/**
	 * Property name for id of response data. Must increase on new data.
	 */
	private static final String PROP_DATA_ID = "dataId";

	/**
	 * Property name for data type
	 */
	private static final String PROP_DATA_TYPE = "dataType";

	/**
	 * Property name for changed js files of the app. On new version.
	 */
	private static final String PROP_JS_FILES = "jsFiles";

	/**
	 * Property name for changed html content of the app. On new version.
	 */
	private static final String PROP_HTML_CONTENT = "htmlContent";

	/**
	 * Property name for json array of commands
	 */
	private static final String PROP_COMMANDS = "commands";

	/**
	 * Property name for json object of image global settings
	 */
	private static final String PROP_IMAGE_GLOBAL_SETTINGS = "imageGlobalSettings";

	/**
	 * Property name for json object of video global settings
	 */
	private static final String PROP_VIDEO_GLOBAL_SETTINGS = "videoGlobalSettings";

	/**
	 * Property name for json array of play list file objects
	 */
	private static final String PROP_PLAY_LIST_FILES = "playListFiles";
	private static final String PROP_X = "x";
	private static final String PROP_Y = "y";
	private static final String PROP_WIDTH = "width";
	private static final String PROP_HEIGHT = "height";
	private static final String PROP_SRC = "src";
	private static final String PROP_NAME = "name";
	private static final String PROP_TYPE = "type";
	private static final String PROP_LOCATION = "location";

	/**
	 * Property name for rss feed json object
	 */
	private static final String PROP_RSS_FEED = "rssFeed";
	private static final String PROP_URL = "url";
	private static final String PROP_XML_CONTENT = "xmlContent";

	/**
	 * Media files path - WebContent/media. TODO change after decision where to
	 * store media files
	 */
	private static final String MEDIA_PATH = "/media/";

	/**
	 * Type of response data
	 */
	private DevResponseDataType responseDataType = DevResponseDataType.NONE;

	/**
	 * List of media files for the play list
	 */
	private List<MediaContent> mediaContents;

	/**
	 * List of js file urls for the device app
	 */
	private List<String> jsFiles;

	/**
	 * List of commands for the device app
	 */
	private List<DeviceCommand> commands;

	/**
	 * Html content for the device app
	 */
	private String htmlContent;

	/**
	 * Rss feed url. Use this or {@link #rssFeedXmlContent}
	 */
	private String rssFeedUrl;

	/**
	 * Rss feed xml content. Use this or {@link #rssFeedUrl}
	 */
	private String rssFeedXmlContent;

	/**
	 * Web server url including port number.
	 */
	private String serverUrl;

	/**
	 * After construction DevResponse object call
	 * {@link #setResponseDataType(DevResponseDataType)}, set new version or
	 * other data and generate response {@link #generateResponse()}
	 * 
	 * @param serverUrl
	 *            - Server url including port number. Example
	 *            http://localhost:8080
	 */
	public DevResponse(String serverUrl) {
		super();
		this.serverUrl = serverUrl;
		this.responseJson = new JsonObject();

	}

	/**
	 * Generates response json.
	 * 
	 * @see #setResponseDataType(DevResponseDataType)
	 * @see #getResponseJson()
	 */
	public void generateResponse() {
		int liDataId = 1000; // TODO get from database for this device
		this.responseJson.addProperty(PROP_REDIRECT_URL, ""); // TODO Currently not used
		this.responseJson.addProperty(PROP_DATA_ID, liDataId);
		switch (this.responseDataType) {
		case NEW_VERSION:
			this.responseJson.addProperty(PROP_DATA_TYPE, "new_version");
			this.appendNewVersion();
			break;
		case COMMAND:
			this.responseJson.addProperty(PROP_DATA_TYPE, "command");
			this.appendCommands();
			// The command can run play list or rss feed
			this.appendPlayList();
			this.appendRssFeed();
			break;
		case PLAY_LIST:
			this.responseJson.addProperty(PROP_DATA_TYPE, "playlist");
			this.appendPlayList();
			break;
		case NONE:
			moLogger.warn("generateResponse() - Response data type not set!");
			break;
		}
	}

	/**
	 * 
	 * @return Generated response json
	 * 
	 * @see #setResponseDataType(ResponseDataType)
	 * @see #generateResponse()
	 */
	public JsonObject getResponseJson() {
		return this.responseJson;
	}

	/**
	 * Appends new version json data to the response json
	 */
	private void appendNewVersion() {
		// Add js file urls
		if (this.jsFiles != null && this.jsFiles.size() > 0) {
			JsonArray loJsonArray = new JsonArray();
			for (String jsFileUrl : jsFiles) {
				loJsonArray.add(jsFileUrl);
			}
			this.responseJson.add(PROP_JS_FILES, loJsonArray);
		}

		// Add html content
		if (this.htmlContent != null && this.htmlContent.length() > 0) {
			this.responseJson.addProperty(PROP_HTML_CONTENT, this.htmlContent);
		}
	}

	/**
	 * Appends commands json array to the response json
	 */
	private void appendCommands() {
		if (commands == null || commands.size() == 0) {
			return;
		}
		JsonArray loJsonArray = new JsonArray();
		for (DeviceCommand deviceCommand : commands) {
			switch (deviceCommand) {
			case REBOOT:
				loJsonArray.add("reboot");
				break;
			case TURN_ON:
				loJsonArray.add("lfdDisplayOn");
				break;
			case TURN_OFF:
				loJsonArray.add("lfdDisplayOff");
				break;
			case START_PLAY_LIST:
				loJsonArray.add("startPlayList");
				break;
			case STOP_PLAY_LIST:
				loJsonArray.add("stopPlayList");
				break;
			case START_RSS_FEED:
				loJsonArray.add("loadRssFeed");
				break;
			case STOP_RSS_FEED:
				loJsonArray.add("stopRssFeed");
				break;
			}
		}
		this.responseJson.add(PROP_COMMANDS, loJsonArray);
	}

	/**
	 * Appends media play list data to the response json
	 */
	private void appendPlayList() {

		if (this.mediaContents == null || this.mediaContents.size() == 0) {
			return;
		}
		JsonObject loImageGlobalSettings = new JsonObject();
		loImageGlobalSettings.addProperty(PROP_X, 0);
		loImageGlobalSettings.addProperty(PROP_Y, 0);
		loImageGlobalSettings.addProperty(PROP_WIDTH, 1280);
		loImageGlobalSettings.addProperty(PROP_HEIGHT, 720);
		this.responseJson.add(PROP_IMAGE_GLOBAL_SETTINGS, loImageGlobalSettings);
		JsonObject loVideoGlobalSettings = new JsonObject();
		loVideoGlobalSettings.addProperty(PROP_X, 0);
		loVideoGlobalSettings.addProperty(PROP_Y, 0);
		loVideoGlobalSettings.addProperty(PROP_WIDTH, 1280);
		loVideoGlobalSettings.addProperty(PROP_HEIGHT, 720);
		this.responseJson.add(PROP_VIDEO_GLOBAL_SETTINGS, loVideoGlobalSettings);

		String lsMediaUrl = this.serverUrl + MEDIA_PATH;
		JsonArray loPlayListFiles = new JsonArray();
		for (MediaContent mediaContent : mediaContents) {
			JsonObject loFile = new JsonObject();
			loFile.addProperty(PROP_SRC, lsMediaUrl + mediaContent.getName());
			loFile.addProperty(PROP_NAME, mediaContent.getName());
			loFile.addProperty(PROP_TYPE, mediaContent.getMediaType() == MediaObjectType.JPEG ? "image" : "video");
			loFile.addProperty(PROP_LOCATION, "local");
			loPlayListFiles.add(loFile);
		}

		this.responseJson.add(PROP_PLAY_LIST_FILES, loPlayListFiles);
	}

	/**
	 * Appends rss feed json data to the response json
	 */
	private void appendRssFeed() {
		if (this.rssFeedUrl != null || this.rssFeedXmlContent != null) {
			JsonObject loRssFeed = new JsonObject();
			loRssFeed.addProperty(PROP_URL, this.rssFeedUrl);
			loRssFeed.addProperty(PROP_XML_CONTENT, this.rssFeedXmlContent);
			this.responseJson.add(PROP_RSS_FEED, loRssFeed);
		}
	}

	/**
	 * 
	 * @return List of {@link MediaContent} or null if not set
	 */
	public List<MediaContent> getMediaContents() {
		return mediaContents;
	}

	/**
	 * Set list of media contents for the play list
	 * 
	 * @param mediaContents
	 */
	public void setMediaContents(List<MediaContent> mediaContents) {
		this.mediaContents = mediaContents;
	}

	/**
	 * 
	 * @return List of js file urls or null if not set
	 */
	public List<String> getJsFiles() {
		return jsFiles;
	}

	/**
	 * Set list of js file urls
	 * 
	 * @param jsFiles
	 */
	public void setJsFiles(List<String> jsFiles) {
		this.jsFiles = jsFiles;
	}

	/**
	 * 
	 * @return Html text content for device app or null if not set
	 */
	public String getHtmlContent() {
		return htmlContent;
	}

	/**
	 * Set html text content for device app
	 * 
	 * @param htmlContent
	 */
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	/**
	 * 
	 * @return Rss feed url or null if not set
	 */
	public String getRssFeedUrl() {
		return rssFeedUrl;
	}

	/**
	 * Set rss feed url. Use this or {@link #setRssFeedXmlContent(String)}
	 * 
	 * @param rssFeedUrl
	 * @see #setRssFeedXmlContent(String)
	 */
	public void setRssFeedUrl(String rssFeedUrl) {
		this.rssFeedUrl = rssFeedUrl;
	}

	/**
	 * 
	 * @return Rss feed xml content or null if not set
	 */
	public String getRssFeedXmlContent() {
		return rssFeedXmlContent;
	}

	/**
	 * Set rss feed xml content. Use this or {@link #setRssFeedUrl(String)}
	 * 
	 * @param rssFeedXmlContent
	 * @see #setRssFeedUrl(String)
	 */
	public void setRssFeedXmlContent(String rssFeedXmlContent) {
		this.rssFeedXmlContent = rssFeedXmlContent;
	}

	/**
	 * 
	 * @return Response data type
	 */
	public DevResponseDataType getResponseDataType() {
		return responseDataType;
	}

	/**
	 * Set response data type
	 * 
	 * @param responseDataType
	 */
	public void setResponseDataType(DevResponseDataType responseDataType) {
		this.responseDataType = responseDataType;
	}

	/**
	 * 
	 * @return List of commands for the device app or null if not set
	 */
	public List<DeviceCommand> getCommands() {
		return commands;
	}

	/**
	 * Set list of commands for the device app
	 * 
	 * @param commands
	 */
	public void setCommands(List<DeviceCommand> commands) {
		this.commands = commands;
	}
}
