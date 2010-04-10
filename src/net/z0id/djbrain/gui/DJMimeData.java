/**
 * 
 */
package net.z0id.djbrain.gui;

import java.util.LinkedList;
import java.util.List;


/**
 * @author meatz
 * 
 */
public class DJMimeData extends com.trolltech.qt.core.QMimeData {

	public class Source {
		public static final int BRAINSTORM = 0;
		public static final int SUGGESTIONLIST = 1;
		public static final int CURRENTTRACK = 2;
		public static final int TRACKSTACk = 3;
		
		public static final int PLAYLIST = 4;
		public static final int TRACKLIST = 5;
		public static final int LASTFMBROWSER = 6;
	
		
		
	}

	private int source;

	boolean hasTracks = false;

	boolean hasPlaylists = false;

	List<Integer> tracklist;

	int playlistId;

	private int playlistItemCount;



	/**
	 * @param brainstorm
	 */
	public void setSource(int source) {
		this.source = source;
	}

	public boolean hasPlaylists() {
		return hasPlaylists;
	}

	public boolean hasTracks() {
		return hasTracks;
	}

	public int getPlaylistId() {
		return playlistId;
	}

	public int getSource() {
		return source;
	}

	public List<Integer> getTracklist() {
		return tracklist;
	}

	/**
	 * @param id
	 */
	public void setPlaylistId(int id) {
	this.playlistId = id;
		hasPlaylists = true;
	}
	
	/**
	 * @param id
	 */
	public void addTrackId(int id) {
		if (tracklist == null){
			tracklist = new LinkedList<Integer>();
		}
		tracklist.add(id);	
		hasTracks = true;
	}


	public void setTracklist(List<Integer> tracklist) {
		this.tracklist = tracklist;
		hasTracks = true;
	}

	/**
	 * @return
	 */
	public int getFirstTrackId() {
		return ((LinkedList<Integer>)tracklist).getFirst();
	}

	/**
	 * @param itemCount
	 */
	public void setPlaylistItemCount(int itemCount) {
	this.playlistItemCount = itemCount;
		
	}

	public int getPlaylistItemCount() {
		return playlistItemCount;
	}

}
