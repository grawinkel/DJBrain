/**
 * 
 */
package net.z0id.djbrain.gui.debug;

import java.io.IOException;

import net.z0id.djbrain.properties.DJProperties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author meatz
 *
 */
public class LogHelper {

	/**
	 * 
	 */
	public static void initLogger() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	
	public static void configureLogger() {
		String loglevel = DJProperties.getProperty("LogLevel");
		
		if (loglevel != null){
			if (loglevel.equalsIgnoreCase("info")){
				Logger.getRootLogger().setLevel(Level.INFO);
			}else if (loglevel.equalsIgnoreCase("error")){
				Logger.getRootLogger().setLevel(Level.ERROR);
			}else if (loglevel.equalsIgnoreCase("debug")){
				Logger.getRootLogger().setLevel(Level.DEBUG);
			}
		}
		
		String LogToFile =  DJProperties.getProperty("LogToFile");
		if (LogToFile != null){
		 String path = DJProperties.getDJBrainUserLogDir()+"djbrain.log";
				 
		 PatternLayout pl = new org.apache.log4j.PatternLayout();
		 DailyRollingFileAppender darofi;
		try {
			darofi = new DailyRollingFileAppender(pl,path,"'.'MMdd");
			 Logger.getRootLogger().addAppender(darofi);
		} catch (IOException e) {
			// ignore
			e.printStackTrace();
		}
		 
		 
	
		}
		
	}

}
