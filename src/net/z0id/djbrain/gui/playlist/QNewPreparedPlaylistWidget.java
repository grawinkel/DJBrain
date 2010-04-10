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

import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.objects.Playlist;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 *
 */
public class QNewPreparedPlaylistWidget  extends AbstractPlaylistEditor  {

	private List<Integer> trackIds;
	
 

	private  static QNewPreparedPlaylistWidget instance;
	
	
	/**
	 * @param signalHub
	 * @param trackIds
	 * @return instance
	 */
	public static QWidget getInstance(SignalHub signalHub, List<Integer> trackIds) {
		
		if (instance == null){
			instance  = new QNewPreparedPlaylistWidget(signalHub);
		}

		instance.trackIds  = trackIds;
		instance.setWindowTitle(instance.tr("Save "+trackIds.size()+" tracks to new Playlist"));
		return instance;
	}
	
	
  
	private QNewPreparedPlaylistWidget(SignalHub signalHub){
	super(signalHub);
	}


	@SuppressWarnings("unused")
	private void actionButtonClicked() {
		
		Playlist playlist = new Playlist();
		playlist.setName(playlistName.text());
		playlist.setComment(comment.toPlainText());
		for (int trackId : trackIds) {
			playlist.addTrack(trackId);
		}
		
		if(	DBConnection.getInstance().insertPlaylist(playlist) ){
			int id = DBConnection.getInstance().getPlaylistIdForName(playlist.getName());
			
			signalHub.signalNewPlaylistAvailable(id);	
				
			}else{
				QMessageBox.critical(this, tr("Error!"),
					tr("Playlist could not be added,\n see logfile for details"));
			}
			this.clear();
			this.hide();	
			
	}

	
   

	 protected void initStuff() {
		 actionButton.setText(tr("&Add Playlist"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}
	

	  
	
}
