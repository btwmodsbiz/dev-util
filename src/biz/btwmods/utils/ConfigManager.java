package biz.btwmods.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public abstract class ConfigManager {
	private File propertiesFile;
	private Properties properties = new Properties();
	
	protected ConfigManager(File propertiesFile) {
		this.propertiesFile = propertiesFile;
	}
	
	public abstract String getKeyBase();
	
	public void load() throws java.io.FileNotFoundException, IOException {
		this.properties.load(new FileReader(propertiesFile));
	}
	
	public File getFile() {
		return this.propertiesFile;
	}
	
	public String getValue(String key) throws MissingConfigValue {
		if (!this.properties.containsKey(key))
			throw new MissingConfigValue(this.propertiesFile, key);
		
		return this.properties.getProperty(getKeyBase() + key);
	}
	
	public class MissingConfigValue extends Exception {
		private File propertiesFile;
		private String key;
		
		public File getPropertiesFile() {
			return this.propertiesFile;
		}
		
		public String getKey() {
			return this.key;
		}
		
		public MissingConfigValue(File propertiesFile, String key) {
			this.propertiesFile = propertiesFile;
			this.key = key;
		}
	}
}
