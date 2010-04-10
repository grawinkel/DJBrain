/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */


package net.z0id.djbrain.gui.playlist;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.objects.Playlist;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMessageBox;

/**
 * @author meatz
 * 
 */
public class EditPlaylistWidget extends AbstractPlaylistEditor {

	
	private Playlist playlist;
	private static EditPlaylistWidget instance = null;
	
	
	private EditPlaylistWidget(SignalHub signalHub){
		super(signalHub);
	}
	  	
 
	/**
	 * @param signalHub
	 * @param playlist 
	 * @return instance
	 */
	public static EditPlaylistWidget getInstance(SignalHub signalHub, Playlist playlist) {
		if (instance == null){
		instance = new 	EditPlaylistWidget(signalHub);
		}
		instance.playlist = playlist;
		instance.comment.setPlainText(playlist.getComment());
		instance.playlistName.setText(playlist.getName());
		
		return instance;
	}



	@SuppressWarnings("unused")
	private void actionButtonClicked() {
		
		String newname = playlistName.text();
		String commentString = comment.toPlainText();
		
				
			if ( DBConnection.getInstance().updatePlaylist(playlist.getId(),newname,commentString) ){
				signalHub.signalNewPlaylistAvailable(playlist.getId());
			}else{
				QMessageBox.critical(this, tr("Error!"),
						tr("Playlist could not be renamed. Reason:\n" + DBConnection.getInstance().getLastError()));
				}
		
		this.hide();
		this.clear();
}

	
	protected void initStuff() {
		actionButton.setText(tr("Edit"));
		setWindowTitle(tr("Edit Playlist"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}
	
}
