package biz.btwmods.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LocationsConfigManager extends ConfigManager {

	private File devRoot;
	
	public LocationsConfigManager(File devRoot, File propertiesFile) {
		super(propertiesFile);
		this.devRoot = devRoot;
	}
	
	public File getLocation(String key) throws MissingConfigValue {
		return new File(this.devRoot, this.getValue(key));
	}

	@Override
	public String getKeyBase() {
		return "location";
	}
}
