/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.preferences;

import java.io.IOException;
import java.util.Properties;

import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.properties.DJProperties;

import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 *
 */
public class PreferencesWindow extends QWidget{
	
private QGroupBox preferencesGroupBox;
	private QCheckBox checkLatestVersionCheckbox;
	private QCheckBox debugEnabledCheckbox;
	private QCheckBox logToFileCheckbox;
	private QPushButton okButton;
	private QPushButton saveButton;
	private QPushButton cancelButton;
	
	private static PreferencesWindow instance;
	private SignalHub signalHub;
	
	public static void showPreferences(SignalHub signalHub){
		if (instance == null){
			instance = new PreferencesWindow(signalHub);
		}
		instance.loadProperties();
		instance.reset();
		instance.show();
		instance.setFocus();
	}


	private void reset() {
		this.okButton.setEnabled(false);
		this.saveButton.setEnabled(false);
	}
		/**
		 * 
		 */
		private PreferencesWindow(SignalHub signalHub){
			this.signalHub = signalHub;
			init();
		}
		
				
		private void loadProperties(){
			String debug = DJProperties.getProperty("Debug");
			if (debug != null){
				if (debug.equalsIgnoreCase("yes") || debug.equalsIgnoreCase("true") || debug.equals("1")){
					debugEnabledCheckbox.setChecked(true);
				}
			}
			
			String logToFile = DJProperties.getProperty("LogToFile");
			if (logToFile != null){
				if (logToFile.equalsIgnoreCase("yes") || logToFile.equalsIgnoreCase("true") || logToFile.equals("1")){
					logToFileCheckbox.setChecked(true);
				}
			}
			
			String checknewversion = DJProperties.getProperty("checknewversion");
			if (checknewversion != null){
				if (checknewversion.equalsIgnoreCase("yes") || checknewversion.equalsIgnoreCase("true") || checknewversion.equals("1")){
					checkLatestVersionCheckbox.setChecked(true);
				}
			}			
		}
		
	private void init(){
		preferencesGroupBox = new QGroupBox(tr("Preferences"),this);
		checkLatestVersionCheckbox = new QCheckBox(tr("Check www.djbrain.net for latest updates on programstart"),this);
		debugEnabledCheckbox = new QCheckBox(tr("Enable debug tools"),this);
		logToFileCheckbox= new QCheckBox(tr("Write Logfile"),this);
		saveButton = new QPushButton(tr("&Save"));
		okButton = new QPushButton(tr("&Ok"));
		cancelButton = new QPushButton(tr("&Cancel"));
		
		
		
		/* ===================== SIGNALS ================= */
		
		checkLatestVersionCheckbox.stateChanged.connect(this,"preferencesChanged()");
		debugEnabledCheckbox.stateChanged.connect(this,"preferencesChanged()");
		logToFileCheckbox.stateChanged.connect(this,"preferencesChanged()");
		
		okButton.clicked.connect(this,"okButtonPressed()");
		cancelButton.clicked.connect(this,"cancelButtonPressed()");
		saveButton.clicked.connect(this,"saveButtonPressed()");
		/* ===================== ///SIGNALS ================= */
		QHBoxLayout buttonLayout = new QHBoxLayout();
		buttonLayout.addStretch(10);
		
		buttonLayout.addWidget(okButton);
		buttonLayout.addWidget(saveButton);
		buttonLayout.addWidget(cancelButton);
		buttonLayout.addStretch(10);
		
		QVBoxLayout preferencesGroupBoxLayout = new QVBoxLayout();
		preferencesGroupBox.setLayout(preferencesGroupBoxLayout);
		
		preferencesGroupBoxLayout.addWidget(checkLatestVersionCheckbox);
		preferencesGroupBoxLayout.addWidget(debugEnabledCheckbox);
		preferencesGroupBoxLayout.addWidget(logToFileCheckbox);
		
		preferencesGroupBoxLayout.addStretch(100);
		
		QVBoxLayout layout = new QVBoxLayout();
		this.setLayout(layout);
		
		preferencesGroupBox.setLayout(preferencesGroupBoxLayout);
		
		layout.addWidget(preferencesGroupBox);
		layout.addLayout(buttonLayout);
		
		setWindowTitle(tr("Preferences"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
		setLayout(layout);
		resize(300,200);
		}
	
	
	@SuppressWarnings("unused")
	private void preferencesChanged(){
		this.saveButton.setEnabled(true);
		this.okButton.setEnabled(true);
	}
	


	@SuppressWarnings("unused")
	private void okButtonPressed(){
		savePreferences();
		this.close();
	}
	
	@SuppressWarnings("unused")
	private void saveButtonPressed(){
		savePreferences();
		saveButton.setEnabled(false);
	}
	
	@SuppressWarnings("unused")
	private void cancelButtonPressed(){
		this.close();
	}
	
	
	private void savePreferences(){

		Properties newProperties= new Properties();
		if (checkLatestVersionCheckbox.isChecked()){
			newProperties.setProperty("checknewversion","true");
		}else{
			newProperties.setProperty("checknewversion","false");
		}
		
		if (debugEnabledCheckbox.isChecked()){
			newProperties.setProperty("Debug","true");
		}else{
			newProperties.setProperty("Debug","false");
		}
		
		if (logToFileCheckbox.isChecked()){
			newProperties.setProperty("LogToFile","true");
		}else{
			newProperties.setProperty("LogToFile","false");
		}
		
		try {
			DJProperties.saveUserProperties(newProperties);
			signalHub.setStatusBarText(tr("Preferences saved"));
		} catch (IOException e) {	 
			QMessageBox.critical(this, tr("Preferences could not be saved"),tr("Error is: " + e.getLocalizedMessage())) ;
		}
	
	}
}
