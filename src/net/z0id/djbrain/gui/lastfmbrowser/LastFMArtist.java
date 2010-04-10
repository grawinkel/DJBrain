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

import java.util.LinkedList;
import java.util.List;
 

/**
 * @author meatz
 *
 */
public class LastFMArtist {

	String name = "";
	String url = "";
	
	List<LastFMTrack> tracks;
	
	public LastFMArtist(){
		tracks = new LinkedList<LastFMTrack>();
	}
	
	public List<LastFMTrack> getTracks(){
		return tracks;
	}
	
	public void setTracks(List<LastFMTrack> tracks) {
		this.tracks = tracks;
	}

	public void addTrack(LastFMTrack track){
		tracks.add(track);
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

}
