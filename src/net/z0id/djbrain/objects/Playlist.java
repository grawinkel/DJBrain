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

 
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a Playlist. It has a name, id and a list of integers which are the trackids contained in this playlist
 * @author meatz
 * 
 */
public class Playlist {

	private String Name;
	private int id;
	private List<Integer> tracklist;
	private int itemCount;
	private String comment = "";
	/**
	 * 
	 */
	public Playlist(){
		 tracklist = new ArrayList<Integer>();
	}
	

	/**
	 * @return The displayed name of this playlist
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return List with Trackids of Tracks in this list
	 */
	public List<Integer> getTracklist() {
		return tracklist;
	}

	/**
	 * @param tracklist
	 */
	public void setTracklist(List<Integer> tracklist) {
		this.tracklist = tracklist;
	}
	
	/**
	 * @param trackId adds trackid to this playlist
	 */
	public void addTrack(int trackId){
		tracklist.add(trackId);
	}
	
	/**
	 * @param trackId removes the specified track from this playlist
	 */
	public void removeTrack(int trackId){	
		tracklist.remove((Integer) trackId);
	}

	/**
	 * @return the internal id of this playlist
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id sets the internal id of this playlist
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param itemCount number of items in this playlist
	 */
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * @return number of tracks in this playlist
	 */
	public int getItemCount() {
	return itemCount;
		
	}


	/**
	 * @param trackId
	 * @return true if tacklist contains this id
	 */
	public boolean contains(int trackId) {
	
		return tracklist.contains(trackId);
		
	}


	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}


	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
