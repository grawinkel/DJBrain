/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.debug;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class LogWindow extends QWidget {

	// private Logger logger = Logger.getLogger(this.getClass());

	private static LogWindow instance;

	private QPushButton hideButton;

	private QTextEdit logText;
	
	
	 

	/**
	 * @param signalHub
	 * @param parent
	 * @return singleton TrackStackWidget instance that can be shown
	 */
	public static void showLogWindow( ) {
		if (instance == null) {
			instance = new LogWindow();
		}
		instance.show();
	}

	private LogWindow( ) {
		
		setMinimumSize(800, 200);
		initButtons();
		initStuff();

		logText = new QTextEdit();
		logText.setReadOnly(true);
		
		
		Logger.getRootLogger().addAppender(new LogAppender(this));
		
		QGridLayout layout = new QGridLayout();

		layout.addWidget(logText, 0, 0, 1,2);

 
		layout.addWidget(hideButton, 1, 1);
		setLayout(layout);
		
	}

	private void initButtons() {
		hideButton = new QPushButton(tr("&Hide"));
		hideButton.clicked.connect(this, "hide()");

	}

	private void initStuff() {
		setWindowTitle(tr("Log"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}

	/**
	 * @param event
	 */
	public void doAppend(LoggingEvent event) {
		this.logText.append(event.getMessage().toString());
	}

	   

}
