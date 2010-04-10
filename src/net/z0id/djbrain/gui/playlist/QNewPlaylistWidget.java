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

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMessageBox;

/**
 * @author meatz
 * 
 */
public class QNewPlaylistWidget extends AbstractPlaylistEditor {

	private static QNewPlaylistWidget instance = null;
	
	
	private QNewPlaylistWidget(SignalHub signalHub){
		super(signalHub);
	}
	  	
 
	/**
	 * @param signalHub
	 * @return instance
	 */
	public static QNewPlaylistWidget getInstance(SignalHub signalHub) {
		if (instance == null){
		instance = new 	QNewPlaylistWidget(signalHub);
		}
		return instance;
	}



	@SuppressWarnings("unused")
	private void actionButtonClicked() {
		
		String commentString = comment.toPlainText();
		String name = playlistName.text();
		
		if(name.trim().equals("")){
			QMessageBox.critical(this, tr("Error!"),
					tr("The playlistname may not be empty"));
			return;
		}
			if (DBConnection.getInstance().insertPlaylist(name, commentString) ){
			
				int id=	DBConnection.getInstance().getPlaylistIdForName(name);
				
				signalHub.signalNewPlaylistAvailable(id);	
			
			}else{
				QMessageBox.critical(this, tr("Error!"),
					tr("Playlist could not be added. Reason: \n" + DBConnection.getInstance().getLastError()));
			}
			super.clear();
			this.hide();	
	}

	
	
	


	/* (non-Javadoc)
	 * @see net.z0id.djbrain.gui.playlist.AbstractPlaylistEditor#initStuff()
	 */
	@Override
	protected void initStuff() {
		actionButton.setText(tr("&Add"));
		setWindowTitle(tr("Add new Playlist"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}
}
