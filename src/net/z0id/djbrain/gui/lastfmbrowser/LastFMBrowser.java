/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.lastfmbrowser;

import java.util.List;

import net.z0id.djbrain.gui.SignalHub;

import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class LastFMBrowser extends QWidget {

	private static LastFMBrowser instance;


	private QLineEdit searchText;
	
	private QComboBox searchTypeSelector;
	
	private QPushButton goButton;

	private LastFMTrackList table;

//	private SignalHub signalHub;
	
	
	/**
	 * @param signalHub
	 */
	public static void init(SignalHub signalHub) {
		instance = new LastFMBrowser(signalHub);
	}

	/**
	 * @return singleton brtowser
	 */
	public static LastFMBrowser getInstance() {
	 	return instance;
	}

	private LastFMBrowser(SignalHub signalHub) {
//		this.signalHub = signalHub;
		table = new LastFMTrackList(signalHub,this);

		
		setMinimumSize(600, 400);
		init();
		initStuff();

		QGridLayout layout = new QGridLayout();

	

		layout.addWidget(searchTypeSelector,0,0);
		layout.addWidget(searchText,0,1,1,3);
		layout.addWidget(goButton, 0, 4);
		
		layout.addWidget(table, 1, 0, 1, 5);
		
		layout.setColumnMinimumWidth(0,200);
		layout.setColumnStretch(0, 20);
		
		layout.setColumnMinimumWidth(1,200);
		layout.setColumnStretch(1, 20);
		
		layout.setColumnMinimumWidth(2,50);
		layout.setColumnStretch(2, 5);
		
		layout.setColumnMinimumWidth(3,150);
		layout.setColumnStretch(3, 15);
		
		
		setLayout(layout);
		
	}

	private void init() {

		searchText = new QLineEdit();
		
		searchTypeSelector = new QComboBox();
		searchTypeSelector.addItem(LastFMConnector.TOP_TRACKS_FOR_ARTIST);
		searchTypeSelector.addItem(LastFMConnector.TOP_TRACKS_FOR_TAG);
		searchTypeSelector.addItem(LastFMConnector.RELATED_ARTISTS);

		goButton = new QPushButton(tr("&Go"));
		goButton.pressed.connect(this, "go()");


	}

	private void initStuff() {
		setWindowTitle(tr("Last.FM Browser"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}

	@SuppressWarnings("unused")
	private void go(){
	
		
		String t = searchText.text().trim();
		try {
			
			String current = searchTypeSelector.currentText();
		
			List<LastFMArtist> artists = null;
			
			if ( current.equals(LastFMConnector.RELATED_ARTISTS)){
				artists = LastFMConnector.getRelatedArtists(t);
			}else 	if ( current.equals(LastFMConnector.TOP_TRACKS_FOR_ARTIST)){
				artists = LastFMConnector.getTopTracksForArtist(t);
			}else 	if ( current.equals(LastFMConnector.TOP_TRACKS_FOR_TAG)){
				artists = LastFMConnector.getTopTracksForTag(t);
			}
			
			showArtists(artists);
		} catch (Exception e) {
			QMessageBox.critical(this, tr("Error while communicating with Last.fm"), tr("Error: " + e.getMessage()));
		} 
	}
 



	/**
	 * shows this window
	 */
	public void showWindow(){
		this.show();
		this.setVisible(true);
	}
	
	/**
	 * @param artistString
	 */
	public void browseTopTracksForArtist(String artistString) {
		try {
			List<LastFMArtist> artists = LastFMConnector.getTopTracksForArtist(artistString);
			showArtists(artists);
			searchText.setText(artistString);
		 
			
		}  catch (Exception e) {
			QMessageBox.critical(this, tr("Error while communicating with Last.fm"), tr("Error: " + e.getMessage()));
		} 
		
	
		
	}

	/**
	 * @param artistname
	 */
	public void browseRelatedArtists(String artistname) {
		try {
			List<LastFMArtist> artists = LastFMConnector.getRelatedArtists(artistname);
			showArtists(artists);
			searchText.setText(artistname);
		 
			
		}  catch (Exception e) {
			QMessageBox.critical(this, tr("Error while communicating with Last.fm"), tr("Error: " + e.getMessage()));
		} 
		
	}
	
	/**
	 * @param tag
	 */
	public void browseTopTracksForTag(String tag) {
		try {
		List<LastFMArtist> artists = LastFMConnector.getTopTracksForTag(tag);
		showArtists(artists);
		searchText.setText(tag);
	}  catch (Exception e) {
		QMessageBox.critical(this, tr("Error while communicating with Last.fm"), tr("Error: " + e.getMessage()));
	} 
		
	}

	private void showArtists(List<LastFMArtist> artists){
		table.clear();
		for(LastFMArtist artist : artists){
			table.addArtist(artist);
		}	
	}
	
	
}
