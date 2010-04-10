/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author meatz
 * 
 */
public class DJProperties {
	
	private static Logger LOG = Logger.getLogger(DJProperties.class);

	private static DJProperties instance;

	private final static  String userdir = System.getProperty("user.dir");
	private final static  String userHome = System.getProperty("user.home");
	private final static  String djBrainFolder = ".djbrain";
	private final static  String filesep = System.getProperty("file.separator");

	private Properties userProperties;
	private Properties systemProperties;

	
	private final static String LOGDIR = "logs";
	private final static String CONFDIR = "conf";
	private final static String DBDIR = "database";
	
	
	/**
	 * initialize the properties... init() has to be called before first usage
	 */
	public static void init() {
		checkOrCreateHomeDir();
		instance = new DJProperties();
	}

	
	public  static void saveUserProperties(Properties properties) throws IOException{
		File file= instance.getOrCreateUserDJBrainProperties();
		
		FileOutputStream fos = new FileOutputStream(file);
		properties.store(fos,"Saved by DJBrain, you should not edit this by hand unless you know what you do!");
		instance.userProperties = properties; //reload userproperties
	}
	
	private DJProperties() {
		
		try {
			String sysProperties = userdir + filesep + "conf" + filesep
			+ "djbrain.properties";
			systemProperties = new Properties();
			systemProperties.load(new FileInputStream(sysProperties));

		} catch (FileNotFoundException e1) {
			System.err
					.println("file: djbrain.propoerties could not be found at specified path: "
							+ userdir
							+ filesep
							+ "conf"
							+ filesep
							+ "djbrain.properties");
			System.exit(1);
		} catch (IOException ioe) {
			System.err
					.println("Error while reading the file: djbrain.propoerties  : "
							+ ioe.getMessage());
			System.exit(1);
		}
		
		 
		try {
			userProperties = new Properties();
			userProperties.load(new FileInputStream(getDJBrainUserHome() + filesep+CONFDIR +filesep+ "djbrain.properties"));
		} catch (FileNotFoundException e) {
			userProperties = new Properties();
		LOG.debug("no userProperties file exist: " + e.getMessage() );
		} catch (IOException e) {
			userProperties = new Properties();
			LOG.debug("IOException while loading userProperties: " + e.getMessage() );
		}
		
	}

	/**
	 * @param key
	 * @return the property files value for the key. If userProperties 
	 *     are specified, they are used in favor to systemProperties
	 */
	public static String getProperty(String key) {
		String ret = instance.userProperties.getProperty(key);
		if (ret == null){
			 ret = instance.systemProperties.getProperty(key);
		}
		return ret;
	}

	/**
	 * @return path to hsqlBD
	 */
	public static String getHSQLDBPath() {
		String name = getProperty("hsqldb_name");
		return getDJBrainUserHome()+filesep+"database"+filesep+name ;
	}
	
	/**
	 * @return filesep
	 */
	public static String getFileSep(){
		return filesep;
	}
	
	public static String getDJBrainUserHome(){
		return userHome+filesep+djBrainFolder;
	}
	
	public static String getDJBrainUserLogDir(){
		return getDJBrainUserHome() + filesep+LOGDIR +filesep;
	}
	
private static void checkOrCreateHomeDir(){
	File homeDir = new File(userHome+filesep+djBrainFolder);
	if (!homeDir.exists()){
			homeDir.mkdir();
			LOG.debug("djbrain Homedir created  at: " + homeDir.toString());
	}
	checkOrCreateSubdir(LOGDIR);
	checkOrCreateSubdir(DBDIR);
	checkOrCreateSubdir(CONFDIR);
	}
	
	private static void checkOrCreateSubdir(String subdir){
	
		File homeDir = new File(getDJBrainUserHome());
	
		File[] files = homeDir.listFiles();

	boolean found = false;
		
		for (File file : files ){
			if (file.getName().equals(subdir)){
				found = true;
			}
		}
		
		if (!found){
			File newDir = new File (getDJBrainUserHome()+filesep+subdir);
			newDir.mkdir();
			LOG.debug("created dir: "+newDir.toString());
		}
	}

//	/**
//	 * 
//	 * @return String for userconfig file, or null if it does not exist
//	 */
//	private File getUserDJBrainProperties(){
//		File userDJProperties = new File(getDJBrainHome() + filesep+CONFDIR +filesep+ "djbrain.properties");
//		if ( userDJProperties.exists()){
//			return userDJProperties;
//		}
//		return null;
//	}
	
	/**
	 * 
	 * @return String for userconfig file, or null if it does not exist
	 * @throws IOException 
	 */
	private File getOrCreateUserDJBrainProperties() throws IOException{
		File userDJProperties = new File(getDJBrainUserHome() + filesep+CONFDIR +filesep+ "djbrain.properties");
		if (! userDJProperties.exists()){
			userDJProperties.createNewFile();
		}
		return userDJProperties;
	}

	
}
