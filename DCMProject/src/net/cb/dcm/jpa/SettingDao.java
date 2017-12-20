package net.cb.dcm.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cb.dcm.jpa.entities.Setting;

public class SettingDao extends GenericDao<Setting> {
	
	public final static String SETTING_MEDIA_PATH = "MEDIA_PATH";

	public SettingDao() {
		super();
	}
	
	public SettingDao(GenericDao<?> genericDao) {
		super(genericDao);
	}
	
	public Map<String, Setting> findAllAsMap() {
		List<Setting> settings = this.findAll();
		
		Map<String, Setting> settingsMap = new HashMap<>();
		for (Setting setting : settings) {
			settingsMap.put(setting.getName(), setting);
		}
		
		return settingsMap;
	}
}
