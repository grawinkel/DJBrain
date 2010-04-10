/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.help;

import java.util.LinkedList;

import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QTextBrowser;
import com.trolltech.qt.gui.QToolButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class HelpBrowserWidget extends QWidget {

	public class Pages{
		public static final int HELP = 0;
	}
	
	
//	private static Logger logger = Logger.getLogger(HelpBrowserWidget.class);

	private QTextBrowser textBrowser;
	
	private QToolButton homeButton;
	private QToolButton forwardButton;
	private QToolButton backButton;
	
	private static HelpBrowserWidget instance;
	 

	private HelpBrowserWidget() {
		
		textBrowser = new QTextBrowser();
		
		 LinkedList<String> searchPaths = new LinkedList<String>();
		 searchPaths.add("docs/help/");
		textBrowser.setSearchPaths(searchPaths );
		
		
		textBrowser.forwardAvailable.connect(this,"textBrowserForwardAvailable(Boolean)");
		textBrowser.backwardAvailable.connect(this,"textBrowserBackwardAvailable(Boolean)");
		
		homeButton = new QToolButton();
		homeButton.setIcon(new QIcon(
		"classpath://images/browser/go-home.png"));
		homeButton.setAutoRaise(true);
		homeButton.setIconSize(new QSize(25, 25));
		
		homeButton.clicked.connect(textBrowser,"home()");
		
		backButton = new QToolButton();
		backButton.setIcon(new QIcon(
		"classpath://images/browser/go-previous.png"));
		backButton.setAutoRaise(true);
		backButton.setIconSize(new QSize(25, 25));
		backButton.clicked.connect(textBrowser,"backward()");
		
		forwardButton = new QToolButton();
		forwardButton.setIcon(new QIcon(
		"classpath://images/browser/go-next.png"));
		forwardButton.setAutoRaise(true);
		forwardButton.setIconSize(new QSize(25, 25));
	    forwardButton.clicked.connect(textBrowser,"forward()");

		QHBoxLayout upperButtonbarlayout = new QHBoxLayout();
		upperButtonbarlayout.addWidget(homeButton); 	
		upperButtonbarlayout.addWidget(backButton);
		upperButtonbarlayout.addWidget(forwardButton);
		upperButtonbarlayout.addStretch(100);
		
		QVBoxLayout layout = new QVBoxLayout();
		layout.addLayout(upperButtonbarlayout);
	
		layout.addWidget(textBrowser);
	
		setLayout(layout);

		setWindowTitle(tr("Helpbrowser"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
 
		resize(700, 500);
	}
	
	
	@SuppressWarnings("unused")
	private void textBrowserForwardAvailable(Boolean available){
		forwardButton.setEnabled(available);
	}
	
	@SuppressWarnings("unused")
	private void textBrowserBackwardAvailable(Boolean available){
		backButton.setEnabled(available);
	}
	
	
	private void setHelpId(int helpId){
		
		if (helpId == Pages.HELP){
			textBrowser.setSource(new QUrl("index.html"));	
		}
	}
	

	public static void showHelp(int helpId){
		if (instance == null){
			instance = new HelpBrowserWidget();
		}
		
		instance.setHelpId(helpId);
		instance.show();
		instance.setFocus();
	}

 
	
}
