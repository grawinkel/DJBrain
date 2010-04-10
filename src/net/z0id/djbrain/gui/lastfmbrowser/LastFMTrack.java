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

/**
 * @author meatz
 *
 */
public class LastFMTrack {

	private String trackname;
	private String url;
	private String ranking;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTrackname() {
		return trackname;
	}
	
	public void setTrackname(String trackname) {
		this.trackname = trackname;
	}

	public void setRanking(String ranking) {
		this.ranking = ranking;
	}
	
	public String getRanking() {
		return ranking;
	}

}
