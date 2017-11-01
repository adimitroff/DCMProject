package net.cb.dcm.enums;

/**
 * Device commands.
 * @author krum 
 * @see net.cb.dcm.dev_feed.DevResponse
 */
public enum DeviceCommand {
	/**
	 * Reboot device
	 */
	REBOOT,
	
	/**
	 * Turn device display on
	 */
	TURN_ON,
	
	/**
	 * Turn device display off
	 */
	TURN_OFF,
	
	/**
	 * Start media play list
	 */
	START_PLAY_LIST,
	
	/**
	 * Stop media play list
	 */
	STOP_PLAY_LIST,
	
	/**
	 * Start rss feed
	 */
	START_RSS_FEED,
	
	/**
	 * Stop rss feed
	 */
	STOP_RSS_FEED
}
