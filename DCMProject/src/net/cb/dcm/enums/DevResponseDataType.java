package net.cb.dcm.enums;

/**
 * Types of json response data
 * @author krum
 * @see net.cb.dcm.dev_feed.DevResponse
 */
public enum DevResponseDataType {
	/** Not used in json response **/
	NONE,
	/**
	 * Response type for new app version. Changes in js files or in the html
	 * content
	 **/
	NEW_VERSION,
	/** Response type for commands to device. **/
	COMMAND,
	/** Response type for media play list. **/
	PLAY_LIST
}
