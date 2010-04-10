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

import net.z0id.djbrain.objects.Playlist;

import com.trolltech.qt.gui.QListWidgetItem;

/**
 * @author meatz
 *
 */
public class PlaylistItem extends QListWidgetItem {

	private Playlist playlist;
	
	/**
	 * @param playlist
	 */
	public PlaylistItem( Playlist playlist){
		
		this.playlist = playlist;
		
	}
	
	/**
	 * @return the playlist item
	 */
	public Playlist getPlaylist(){
		return this.playlist;
	}
	

}
