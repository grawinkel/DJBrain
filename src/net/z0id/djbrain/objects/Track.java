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

import net.z0id.djbrain.db.GenreCache;

/**
 * @author meatz
 * 
 */
public class Track {

	
	public static class MediaTypes
	{
		public static String MP3 = "Mp3";
		public static String VINYL = "Vinyl";
		public static String CD = "Cd";
		public static String MP3VINYL = "Mp3+Vinyl";
		public static String MP3CD = "Mp3+Cd";
		public static String VINYLCD = "Vinyl+Cd";
		public static String MP3VINYLCD = "Mp3+Vinyl+Cd";
	 
	}
	
	private int id;

	private String artist = "";

	private String trackname = "";

	private String label = "";

	private String length = "00:00";

	private int genreId = 0;

	private int released = 0;

	private int bpm = 0;

	private String catalognr = "";

	private String inventorynr = "";

	private String comment = "";

	private int rating = 0;

	private String mediatype = "";

	private String filename = "";

	/**
	 * @return artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 */
	public void setArtist(String artist) {
		if (artist == null) {
			this.artist = "";
		} else {
			this.artist = artist.trim();
		}

	}

	/**
	 * @return bpm
	 */
	public int getBpm() {
		return bpm;
	}

	/**
	 * @param bpm
	 */
	public void setBpm(int bpm) {
		this.bpm = bpm;
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
		if (comment == null) {
			this.comment = "";
		} else {

			this.comment = comment.trim();
		}
	}

	/**
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 */
	public void setFilename(String filename) {
		if (filename == null) {
			this.filename = "";
		} else {

			this.filename = filename.trim();
		}
	}

	/**
	 * @return genre
	 */
	public int getGenreId() {
		return genreId;
	}

	/**
	 * @param genreId
	 */
	public void setGenreId(int genreId) {

		this.genreId = genreId;

	}

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return inventorynr
	 */
	public String getInventorynr() {
		return inventorynr;
	}

	/**
	 * @param inventorynr
	 */
	public void setInventorynr(String inventorynr) {
		if (inventorynr == null) {
			this.inventorynr = "";
		} else {
			this.inventorynr = inventorynr.trim();

		}

	}

	/**
	 * @return length
	 */
	public String getLength() {
		return length;
	}

	/**
	 * @param length
	 */
	public void setLength(String length) {
		if (length == null) {
			this.length = "";
		} else {
			this.length = length.trim();
		}

	}

	/**
	 * @return mediatype
	 */
	public String getMediatype() {
		return mediatype;
	}

	/**
	 * @param mediatype
	 */
	public void setMediatype(String mediatype) {

		if (mediatype == null) {
			this.mediatype = "";
		} else {

			this.mediatype = mediatype.trim();
		}
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
	 * @return trackname
	 */
	public String getTrackname() {
		return trackname;
	}

	/**
	 * @param trackname
	 */
	public void setTrackname(String trackname) {
		if (trackname == null) {
			this.trackname = "";
		} else {

			this.trackname = trackname.trim();
		}
	}

	/**
	 * @return catalognr
	 */
	public String getCatalognr() {
		return catalognr;
	}

	/**
	 * @param catalognr
	 */
	public void setCatalognr(String catalognr) {
		if (catalognr == null) {
			this.catalognr = "";
		} else {
			this.catalognr = catalognr.trim();

		}

	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 */
	public void setLabel(String label) {
		if (label == null) {
			this.label = "";
		} else {

			this.label = label.trim();
		}
	}

	/**
	 * @return released
	 */
	public int getReleased() {
		return released;
	}

	/**
	 * @param released
	 */
	public void setReleased(int released) {
		this.released = released;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append(id + " \n");
		buf.append(artist + " \n");
		buf.append(trackname + " \n");
		buf.append(label + " \n");
		buf.append(length + " \n");
		buf.append(genreId + " \n");
		buf.append(released + " \n");
		buf.append(bpm + " \n");
		buf.append(catalognr + " \n");
		buf.append(inventorynr + " \n");
		buf.append(comment + " \n");
		buf.append(rating + " \n");
		buf.append(mediatype + " \n");
		buf.append(filename + " \n");

		return buf.toString();
	}

	/**
	 * @return returns the Track as an array of its strings
	 */
	public String[] getAsArray() {
		String[] array = new String[13];

		array[0] = artist;
		array[1] = trackname;
		array[2] = label;
		array[3] = length;
		array[4] = genreId + "";
		array[5] = released + "";
		array[6] = bpm + "";
		array[7] = catalognr;
		array[8] = inventorynr;
		array[9] = comment;
		array[10] = rating + "";
		array[11] = mediatype;
		// array[0] = filename;

		return array;
	}

	/**
	 * 
	 * @return String
	 */
	public String toPlaylistString() {
		return artist + " - " + trackname;
	}

	public boolean equals(Object otherTrack) {

		if (otherTrack instanceof Track) {
			Track track = (Track) otherTrack;

			if (this.artist.equals(track.getArtist())
					&& this.trackname.equals(track.getTrackname())
					&& this.label.equals(track.getLabel())
					&& this.length.equals(track.getLength())
					&& this.genreId == track.getGenreId()
					&& this.released == track.getReleased()
					&& this.bpm == track.getBpm()
					&& this.catalognr.equals(track.getCatalognr())
					&& this.inventorynr.equals(track.getInventorynr())
					&& this.comment.equals(track.getComment())
					&& this.rating == track.getRating()
					&& this.mediatype.equals(track.getMediatype())
					&& this.filename.equals(track.getFilename()))
				return true;

		}

		return false;

	}

	/**
	 * @return String Representation of this tracks genre
	 */
	public String getGenreString() {
		String genre = GenreCache.getGenreForId(this.genreId);
		if (genre == null) {
			return "";
		}
		return genre;
	}

	/**
	 * @return true if this track has a filename specified and is of a format,
	 *         the mediaplayer is capable of
	 */
	public boolean isPlayable() {
		if (!this.filename.trim().equals("")) {
			if (this.mediatype.trim().equalsIgnoreCase("Mp3") || this.mediatype.trim().equalsIgnoreCase("ogg")) {
				return true;
			}
		}
		return false;
	}
}
