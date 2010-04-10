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
public class Suggestion {

	private int trackId;
	private int suggestedTrackId;
	private String comment;
	private int rating;
	
	/**
	 * 
	 */
	public Suggestion(){}
	
	
	/**
	 * @param trackId
	 * @param suggestedTrackId
	 * @param comment
	 * @param rating
	 */
	public Suggestion(int trackId, int suggestedTrackId, String comment, int rating ){
		this.trackId = trackId;
		this.suggestedTrackId = suggestedTrackId;
		this.comment = comment;
		this.rating = rating;
	}

	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @param rating
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	/**
	 * @return suggestedTrackId
	 */
	public int getSuggestedTrackId() {
		return suggestedTrackId;
	}

	/**
	 * @param suggestedTrackId
	 */
	public void setSuggestedTrackId(int suggestedTrackId) {
		this.suggestedTrackId = suggestedTrackId;
	}

	/**
	 * @return trackId
	 */
	public int getTrackId() {
		return trackId;
	}

	/**
	 * @param trackId
	 */
	public void setTrackId(int trackId) {
		this.trackId = trackId;
	}
	
}
