/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui;

import java.util.List;

import net.z0id.djbrain.objects.Track;

import org.apache.log4j.Logger;

import com.trolltech.qt.core.QObject;

/**
 * @author meatz
 * 
 */
public class SignalHub extends QObject {

	private static Logger logger = Logger.getLogger(SignalHub.class);
	
	private boolean isFilterActive = false;
	
	private String filter = "";
	
	private QMain qmain;

	private int currentTrackId = -1;

	private int currentPlaylistId = -1;

	/**
	 * this signal is fired each time any changes to the playlist occur
	 */
	public Signal1<Integer> playlistUpdated;

	/**
	 * this signal is fired each time any changes to the genrelist occurs
	 */
	public Signal0 genresUpdated;

	/**
	 * this signal is fired each time a new track is inserted into the db
	 */
	public Signal0 trackCountChanged;

	/**
	 * 
	 */
	public Signal1<Track> currentTrackChanged;

	/**
	 * 
	 */
	public Signal1<Track> trackUpdated;

	/**
	 * is fired whenever one or more tracks are DELETED from the db
	 */
	public Signal1<List<Integer>> tracksDeleted;
	
	/**
	 * id of newly available playlist is fired
	 */
	public Signal1<Integer> newPlaylistAvailable;
	
	/**
	 * 
	 */
	public Signal2<String,Integer> openNewTab;
	
	/**
	 * 
	 */
	public Signal0 filterStateChanged;
	
	/**
	 * is fired whenever any suggestion is deleted from db
	 */
	public Signal2<Integer, Integer> suggestionDeleted;
	
	
	/**
	 * is fired when a new version of djbrain is available
	 */
	public Signal2<String, String> newVersionAvailable;

	/* ============== Constructors =============== */

	/**
	 * @param qmain
	 */
	public SignalHub(final QMain qmain) {
		this.qmain = qmain;

		playlistUpdated = new Signal1<Integer>();
		genresUpdated = new Signal0();
		trackCountChanged = new Signal0();
		currentTrackChanged = new Signal1<Track>();
		trackUpdated = new Signal1<Track>();
		tracksDeleted = new Signal1<List<Integer>>();
		newPlaylistAvailable = new Signal1<Integer>();
		filterStateChanged = new Signal0 ();
		openNewTab = new Signal2<String, Integer>();
		suggestionDeleted	= new Signal2<Integer, Integer>();
		newVersionAvailable = new Signal2<String, String>();
	}

	/* ============== Public methods =============== */

	/**
	 * @param text
	 */
	public void setStatusBarText(final String text) {
		qmain.setStatusBarText(text);
	}

	/**
	 * This signal is fired whenever any changes to the genrelist are made
	 */

	public void signalGenresChanged() {
		genresUpdated.emit();
		logger.debug("signal --> genresUpdated ");
	}

	/**
	 * this signal is fired when any changes in the playlists occur
	 */
	public void signalCurrentPlaylistUpdated() {
		logger.debug("signal --> signalCurrentPlaylistUpdated ");
		playlistUpdated.emit(this.currentPlaylistId);
	}

	/**
	 * this signal is fired, when a new track becomes available in the database
	 */
	public void signalTrackCountChanged() {
		logger.debug("signal --> signalTrackCountChanged ");
		trackCountChanged.emit();
		signalCurrentPlaylistUpdated();
	}

	/**
	 * this event is fired whenever a doubleclick on a playlist is made
	 * 
	 * @param playListId
	 *            id of the playlist that should be loaded in the tracklist
	 */
	public void selectPlaylist(final int playListId) {
		
		this.currentPlaylistId = playListId;
		qmain.playlistSelected(playListId);
		logger.debug("signal --> playlistSelected("+playListId +")");
	}

	/**
	 * this event is fired, when the current track is updated, this also updates
	 * the suggestedTracksList
	 * 
	 * @param track
	 */
	public void signalCurrentTrackChanged(final Track track) {
		if (track != null) {
			this.currentTrackId = track.getId();		
		}else {
			this.currentTrackId = -1;
		}
		currentTrackChanged.emit(track);
		logger.debug("signal --> currentTrackChanged("+this.currentTrackId +")");
	}
 

	/**
	 * returns the currently selected track, for which the suggested tracks will
	 * be shown. this track is shown in the currentTrack Widget
	 * 
	 * @return currentTrackId
	 */
	public int getCurrentTrackId() {

		return currentTrackId;
	}

	/**
	 * 
	 * @return the id of the currently selected playlist
	 */
	public int getCurrentPlaylistId() {

		return currentPlaylistId;
	}

	/**
	 * @param track
	 */
	public void signalTrackUpdated(final Track track) {
		trackUpdated.emit(track);
		logger.debug("signal --> trackUpdated("+track.getId() +")");
	}

	/**
	 * @param trackIds
	 */
	public void signalTracksDeleted(final List<Integer> trackIds) {
		tracksDeleted.emit(trackIds);
		logger.debug("signal --> signalTracksDeleted ");
		
	}

	/**
	 * @param id
	 */
	public void signalNewPlaylistAvailable(final int id) {
		newPlaylistAvailable.emit(id);
		logger.debug("signal --> newPlaylistAvailable ");
	}

	/**
	 * @return true if a filter is specified and acitve
	 */
	public boolean isFilterActive() {
		return isFilterActive;
	}

	/**
	 * @param isFilterActive
	 */
	public void setFilterActive(boolean isFilterActive) {
		this.isFilterActive = isFilterActive;
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		//do some magic here!
		this.filter = filter;
	}

	/**
	 * @param newString
	 */
	public void signalFilterStateChanged() {
		filterStateChanged.emit();
		logger.debug("signal --> filterStateChanged ");
	}

 
	/**
	 * @param name
	 * @param playlistId
	 */
	public void signalOpenNewTab( String name, int playlistId) {
		openNewTab.emit(name, playlistId);
		logger.debug("signal --> openNewTab( "+name+","+ playlistId +") ");
	}
	
	/**
	 * @param trackId
	 * @param suggestedTrackId
	 */
	public void signalSuggestionDeleted(int trackId, int suggestedTrackId){
		suggestionDeleted.emit(trackId, suggestedTrackId);
		logger.debug("signal --> suggestionDeleted( "+trackId+","+ suggestedTrackId +") ");
	}

	/**
	 * @param lastUpdated
	 * @param versionString
	 */
	public void signalNewVersionAvailable(String lastUpdated, String versionString) {
		newVersionAvailable.emit(lastUpdated, versionString);
		logger.debug("signal --> signalNewVersionAvailable( "+lastUpdated+","+ versionString +") ");
	}
}
