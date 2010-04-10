/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.tracklist;

import net.z0id.djbrain.objects.Track;

import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * @author meatz
 *
 */
public class TrackItem extends QTreeWidgetItem {

	private Track track;
	 

	/**
	 * @param track 
	 */
	public TrackItem( Track track){
		
		this.track = track;
		
	}
	
	/**
	 * @return the playlist item
	 */
	public Track getTrack(){
		return this.track;
	}
	

}
