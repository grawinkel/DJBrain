/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui;

import java.util.List;

import net.z0id.djbrain.gui.playlist.QNewPreparedPlaylistWidget;
import net.z0id.djbrain.gui.tracklist.TrackStackList;
import net.z0id.djbrain.objects.Track;

import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class TrackStackWidget extends QWidget {

	// private Logger logger = Logger.getLogger(this.getClass());

	private static TrackStackWidget instance;

	private QPushButton hideButton;

	private QPushButton clearButton;

//	private QPushButton removeSelectedButton;

	private QPushButton saveAsPlaylistButton;

	private TrackStackList table;

	private SignalHub signalHub;

	/**
	 * @param signalHub
	 * @param parent
	 * @return singleton TrackStackWidget instance that can be shown
	 */
	public static TrackStackWidget getInstance(SignalHub signalHub) {
		if (instance == null) {
			instance = new TrackStackWidget(signalHub);
		}
		return instance;
	}

	private TrackStackWidget(SignalHub signalHub) {
		this.signalHub = signalHub;
		table = new TrackStackList(this.signalHub);
		setMinimumSize(450, 200);
		initButtons();
		initStuff();

		QGridLayout layout = new QGridLayout();

		layout.addWidget(table, 0, 0, 1, 3);

//		layout.addWidget(removeSelectedButton, 1, 0);
		layout.addWidget(clearButton, 1, 0);
		layout.addWidget(saveAsPlaylistButton, 1, 1);
		layout.addWidget(hideButton, 1, 2);
		setLayout(layout);
		
	}

	private void initButtons() {
		hideButton = new QPushButton(tr("&Hide"));
		hideButton.clicked.connect(this, "hide()");

		clearButton = new QPushButton(tr("&Clear"));
		clearButton.clicked.connect(table, "clear()");

		saveAsPlaylistButton = new QPushButton(tr("&Save as Playlist"));
		saveAsPlaylistButton.clicked.connect(this, "saveAsPlaylist()");

//		removeSelectedButton = new QPushButton(tr("&Remove Selected"));
//		removeSelectedButton.clicked.connect(table, "removeSelectedItems()");

	}

	private void initStuff() {
		setWindowTitle(tr("TrackStack"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}

	@SuppressWarnings("unused")
	private void saveAsPlaylist() {

		QNewPreparedPlaylistWidget.getInstance(signalHub,
				table.getContentTrackIds()).show();
	}


	/**
	 * @param track
	 */
	public void addTrack(Track track) {
		table.addTrack(track);
	}

	/**
	 * @param items
	 */
	public void removeItemsFromView(List<Integer> items) {
		table.removeItemsFromView(items);
	}

	/**
	 * resize Columns to their contents
	 *
	 */
	public void resizeColumns() {
		table.resizeColumns();
	}

	public void showWindow(){
		this.show();
		this.setFocus();
	}
}
