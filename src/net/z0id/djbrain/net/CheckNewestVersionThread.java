/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.properties.DJProperties;

import org.apache.log4j.Logger;

/**
 * @author meatz
 * 
 */
public class CheckNewestVersionThread extends Thread {

	private static Logger logger = Logger
			.getLogger(CheckNewestVersionThread.class);

	private SignalHub signalHub;

	/**
	 * @param signalHub
	 */
	public CheckNewestVersionThread(SignalHub signalHub) {
		this.signalHub = signalHub;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		URL url;
		try {
			url = new URL("http://www.djbrain.net/latest.txt");

			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream()));

			String lastUpdated = in.readLine();
			String versionString = in.readLine();
			logger.debug("read VersionString from Server: " + lastUpdated
					+ " - " + versionString);

			in.close();

			if (lastUpdated != null && versionString != null) {
				int latest = getVersion(versionString);
				int current = getVersion(DJProperties.getProperty("version"));

				if (latest > current) {
					signalHub.signalNewVersionAvailable(lastUpdated,
							versionString);
				}
			}

		} catch (MalformedURLException e) {
		logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * @param versionString
	 * @return comparable Integer Value that handles 1.xx > 0.9.9
	 */
	private int getVersion(String versionString) {
		versionString = versionString.trim();

		versionString = versionString.replace(".", "");

		try {
			int version = Integer.parseInt(versionString);

			if (versionString.startsWith("1")) {
				version = version * 1000;
			}
			return version;
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

//	public static void main(String[] args) {
//		new CheckNewestVersionThread().start();
//	}
}
