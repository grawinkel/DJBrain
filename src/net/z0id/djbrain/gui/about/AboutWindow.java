/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.about;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.z0id.djbrain.properties.DJProperties;

import org.apache.log4j.Logger;

import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 *
 */
public class AboutWindow extends QWidget{
	
	private static AboutWindow instance;
	private static Logger LOG = Logger.getLogger(AboutWindow.class);
	
	/**
	 * 
	 */
	public static void showWindow( ){
		if (instance == null){
			instance = new AboutWindow();
		}
		instance.show();
		instance.setFocus();
	}


		/**
		 * 
		 */
		private AboutWindow( ){
			init();
		}
		
				
	
		
	private void init(){
		QTabWidget 	tabWidget = new QTabWidget();
		tabWidget.addTab(getAboutTab(),tr("&About"));
		tabWidget.addTab(getAuthorsTab(),tr("Au&thors"));
		tabWidget.addTab(getLicenseTab(),tr("&License Agreement"));
		tabWidget.addTab(getTodoTab(),tr("T&odos / Help DJBrain ;)"));
		
//		QIcon foo = new QIcon("classpath://images/djbrain_logo.png");
//		QImage image = new QImage("classpath://images/djbrain_logo.png");
		QLabel label = new QLabel("DJBrain - " + DJProperties.getProperty("version"));
		
		QFont f = new QFont();
		f.setPointSize(12);
		f.setWeight(QFont.Weight.Bold.value());
		label.setFont(f);
		
		QGridLayout layout = new QGridLayout();
		this.setLayout(layout);
		
		QLabel foo = new QLabel();
		QPixmap pixmap = new QPixmap("classpath://images/djbrain_logo.png");
 		foo.setPixmap(pixmap);
 		
 		layout.setColumnMinimumWidth(0, 30);
 		layout.setColumnMinimumWidth(1, 370);
 		
 		layout.addWidget(foo,0,0);
		layout.addWidget(label,0,1);
		layout.addWidget(tabWidget,1,0,1,2);
		
		setWindowTitle(tr("About DJBrain"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
		setLayout(layout);
		resize(400,300);
		}
	

	

	private QWidget getTodoTab() {
		QWidget todoWidget = new QWidget();
		QVBoxLayout layout = new QVBoxLayout();
		todoWidget.setLayout(layout);
		
		QTextEdit t = new QTextEdit();
		t.setReadOnly(true);
		layout.addWidget(t);
		
		String text = "<center><p>If you like the idea behind DJBrain, you can help to improve it. Any help is good!</p><br>"+
		"If you would like to:"+
		"<ul><li>write some documentation</li><li>test the application</li><li>design a cool logo</li><li>add $yourImprovement</li><ul>"+
		"feel free to join the development team at sourceforge.net"+
				"</center>";
		
		t.setHtml(text);
	return todoWidget;
	}


	/**
	 * 
	 */
	private QWidget getAboutTab() {
		QWidget aboutWidget = new QWidget();
		QVBoxLayout layout = new QVBoxLayout();
		aboutWidget.setLayout(layout);
		
		QTextEdit t = new QTextEdit();
		t.setReadOnly(true);
		layout.addWidget(t);
		
		String text = "<center><p> see http://www.djbrain.net for feedback, bugs, news, ...</p>" +
			"<br> <p> DJBrain is still under heavy development, so feel free to send your suggestions, problems, bugs etc to the author!"+
				"</center>";
		
		t.setHtml(text);
	return aboutWidget;
	}
	
	/**
	 * 
	 */
	private QWidget getAuthorsTab() {
		QWidget authorsWidget = new QWidget();
		QVBoxLayout layout = new QVBoxLayout();
		authorsWidget.setLayout(layout);
		
		
		QTextEdit t = new QTextEdit();
		t.setReadOnly(true);
		layout.addWidget(t);
		
		String text = "Matthias Grawinkel <ul><li>matthias@grawinkel.com</li><li>Author</li></ul>";
		
		t.setHtml(text);
		
		
		return authorsWidget;
	}
	
	/**
	 * 
	 */
	private QWidget getLicenseTab() {
		QWidget licenseWidget = new QWidget();
		
		QVBoxLayout layout = new QVBoxLayout();
		licenseWidget.setLayout(layout);
		
		String license= "(c) 2006-2007 Matthias Grawinkel \n\n"; // = "DJBrain is released under GNU GENERAL PUBLIC LICENSE Version 2";
		
		
		 
//		
		try {
			BufferedReader in
			   = new BufferedReader(new FileReader("LICENSE"));
			
			String line;
			while ( ( line = in.readLine()) != null){
				license += line+"\n";
			}
		} catch (FileNotFoundException e) {
			LOG.error(tr("the license file could not be found"));
		} catch (IOException e) {
			LOG.error(tr("the license file could not be read"));
		}
		
		QTextEdit t = new QTextEdit();
		t.setReadOnly(true);
		t.setAcceptDrops(false);
		t.setPlainText(license);
		layout.addWidget(t);
		return licenseWidget;
	}

}
