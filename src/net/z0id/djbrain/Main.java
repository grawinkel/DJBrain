/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.db.GenreCache;
import net.z0id.djbrain.gui.QMain;
import net.z0id.djbrain.gui.debug.LogHelper;
import net.z0id.djbrain.properties.DJProperties;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QPlastiqueStyle;


/**
 * @author meatz
 *
 */
public class Main {

 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
 
		
		LogHelper.initLogger();
 
 
		QApplication.initialize(args);
		QApplication.setStyle(new QPlastiqueStyle());

		DJProperties.init();
		
		//now the user preferences are read, so we can configure our logger
		LogHelper.configureLogger();
		
		DBConnection.init();
		GenreCache.init();
//		Logger.getLogger("main").debug("application started");

		QMain application = new QMain();
		application.show();

		QApplication.exec();
	}

}
