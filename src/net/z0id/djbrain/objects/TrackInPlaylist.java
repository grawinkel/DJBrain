/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.objects;

/**
 * @author meatz
 *
 */
public class TrackInPlaylist {

	int playlistId;
	int trackId;
	
	/**
	 * 
	 */
	public TrackInPlaylist(){}
	/**
	 * @param playlistId
	 * @param trackId
	 */
	public TrackInPlaylist( int playlistId, int trackId){
		this.playlistId = playlistId;
		this.trackId = trackId;
	}

	/**
	 * @return the playlistId
	 */
	public int getPlaylistId() {
		return playlistId;
	}

	/**
	 * @param playlistId the playlistId to set
	 */
	public void setPlaylistId(int playlistId) {
		this.playlistId = playlistId;
	}

	/**
	 * @return the trackId
	 */
	public int getTrackId() {
		return trackId;
	}

	/**
	 * @param trackId the trackId to set
	 */
	public void setTrackId(int trackId) {
		this.trackId = trackId;
	}
	
}
