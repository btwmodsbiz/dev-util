package biz.btwmods.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.print.attribute.standard.Media;

import biz.btwmods.utils.ConfigManager.MissingConfigValue;

public class DecompilerPreparer {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length == 0) {
			System.err.println("Do not call this class directly. Use the .bat or .sh versions.");
			System.exit(1);
		}

		// TODO: add extended help
		String syntax = "Syntax: " + args[0] + " <btwVersion> <mojangVersion> <type>\nUse -h for help.",
			syntaxShort = syntax + "\nUse -h for help.",
			syntaxLong = syntax + "";
		
		if (args.length < 2 || args[1].length() == 0) {
			System.err.println("Missing <btwVersion> argument.");
			System.err.println(syntaxShort);
			System.exit(1);
		}
		else if (args[1] == "-h" || args[1] == "--help" || args[1] == "-?" || args[1] == "/?") {
			System.err.println(syntaxLong);
			System.exit(1);
		}
		else if (args.length < 3 || args[2].length() == 0) {
			System.err.println("Missing <mojangVersion> argument.");
			System.err.println(syntaxShort);
			System.exit(1);
		}
		else if (args.length < 4 || args[3].length() == 0) {
			System.err.println("Missing <type> argument.");
			System.err.println(syntaxShort);
			System.exit(1);
		}
		else if (args[3].toLowerCase() != "client" && args[3].toLowerCase() != "server") {
			System.err.println("Invalid value for the <type> argument.");
			System.err.println(syntaxShort);
			System.exit(1);
		}
		
		LocationsConfigManager locations = new LocationsConfigManager(
			new File(System.getProperty("user.dir")),
			new File(System.getProperty("user.dir"), "conf/locations.properties")
		);
		
		try {
			locations.load();
		} catch (RequiredFileNotFoundException e) {
			System.err.println("Config file not found at: " + locations.getFile().getPath());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Failed to read config at: " + locations.getFile().getPath());
			System.err.println("Reason (" + e.getClass().getSimpleName() + "): " + e.getMessage());
			System.exit(1);
		}
		
		try {
			new DecompilerPreparer(locations, args[1], args[2], args[3].toLowerCase() == "server" ? Type.SERVER : Type.CLIENT).run();
			
		} catch (RequiredFileNotFoundException e) {
			System.err.println("Required file not found: " + e.getFile().getPath());
			System.exit(1);
		} catch (MissingConfigValue e) {
			System.err.println("Required config '" + e.getKey() + "' was not found in: " + e.getPropertiesFile().getPath());
			System.exit(1);
		}
	}
	
	private String btwVersion;
	private String mojangVersion;
	private Type type;
	private LocationsConfigManager locations;
	public enum Type { SERVER, CLIENT };
	
	public DecompilerPreparer(LocationsConfigManager locations, String btwVersion, String mojangVersion, Type type) {
		this.locations = locations;
		this.btwVersion = btwVersion;
		this.mojangVersion = mojangVersion;
		this.type = type;
	}
	
	private void validate() throws RequiredFileNotFoundException, MissingConfigValue {
		if (this.type == Type.SERVER) {
			validateServer();
		}
		else {
			validateClient();
		}
	}
	
	public void run() throws RequiredFileNotFoundException, MissingConfigValue {
		validate();
	}
	
	private void validateServer() throws RequiredFileNotFoundException, MissingConfigValue {
		checkFile("archives.mojang", "server_" + this.mojangVersion + ".jar");
		checkFile("archives.btw", "server_" + this.btwVersion + ".zip");
	}
	
	private void validateClient() throws RequiredFileNotFoundException, MissingConfigValue {
		checkFile("archives.mojang", "server_" + this.mojangVersion + ".jar");
		checkFile("archives.btw", "server_" + this.btwVersion + ".zip");
	}
	
	private File checkFile(String location, String path) throws RequiredFileNotFoundException, MissingConfigValue {
		File file = new File(this.locations.getLocation(location), path);
		if (!file.exists()) {
			throw new RequiredFileNotFoundException(file);
		}
		return file;
	}
	
	public class RequiredFileNotFoundException extends IOException {
		private File file;
		
		public File getFile() {
			return this.file;
		}
		
		public RequiredFileNotFoundException(File file) {
			this.file = file;
		}
	}
}
