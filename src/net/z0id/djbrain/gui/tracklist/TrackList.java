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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.CheckNewTrackWidget;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.QNewTrackWidget;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.DJMimeData.Source;
import net.z0id.djbrain.gui.playlist.PlaylistListWidget;
import net.z0id.djbrain.imexport.MediaImporter;
import net.z0id.djbrain.objects.Track;

import org.apache.log4j.Logger;

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * @author meatz
 * 
 */
public class TrackList extends AbstractTrackListWidget {

	private static Logger logger = Logger.getLogger(TrackList.class);

	private final String IDENTIFIER = "tracklist";

	private int currentPlaylist = -1;

	private QAction addTrackToPlaylist;

	private QAction removeTrackFromPlaylist;

	private TabbedTracklist parent;

	/**
	 * @param signalHub
	 * @param parent
	 *            to set the tabtext
	 */
	public TrackList(SignalHub signalHub, TabbedTracklist parent) {
		super(signalHub);

		this.parent = parent;
		this.setSelectionMode(QAbstractItemView.SelectionMode.ExtendedSelection);

		setDragEnabled(true);
		setDropIndicatorShown(true);
		setAcceptDrops(true);
		setUpdatesEnabled(true);
		setRootIsDecorated(false);
		setSortingEnabled(true);

		setContextMenuPolicy(Qt.ContextMenuPolicy.DefaultContextMenu);

		setAlternatingRowColors(true);

		this.itemDoubleClicked.connect(this,
				"itemDoubleClickedAction(QTreeWidgetItem, Integer)");

		signalHub.playlistUpdated.connect(this, "playlistUpdated(Integer)");

		signalHub.filterStateChanged.connect(this, "filterStateChanged()");
		initColumns(IDENTIFIER);

		showTracksForPlaylistID(-1); // initially show the library
	}

	@SuppressWarnings("unused")
	private void filterStateChanged() {
		showTracksForPlaylistID(currentPlaylist);
	}

	

	private String getSQLForFilter(String filter) {
		filter = filter.toUpperCase();
		// SELECT foobar from TRACKS WHERE
		String sql = "( UCASE(ARTIST) LIKE '%%" + filter + "%%'"
				+ " OR UCASE(TRACKNAME) LIKE '%%" + filter + "%%'"
				+ " OR UCASE(LABEL) LIKE '%%" + filter + "%%'"
				+ " OR UCASE(COMMENT) LIKE '%%" + filter + "%%' );";
		return sql;
	}

	/**
	 * @param playlistid
	 * 
	 */
	public void showTracksForPlaylistID(int playlistid) {

		this.currentPlaylist = playlistid;

		List<Track> tracklist = null;

		if (signalHub.isFilterActive()
				&& !signalHub.getFilter().trim().equals("")) {
			if (playlistid == -1) {
				// show the library
				tracklist = DBConnection.getInstance().getAllTracks(
						getSQLForFilter(signalHub.getFilter()));

			} else {
				tracklist = DBConnection.getInstance()
						.getAllTracksForPlaylistId(playlistid,
								getSQLForFilter(signalHub.getFilter()));

			}
		} else {
			if (playlistid == -1) {
				// show the library
				tracklist = DBConnection.getInstance().getAllTracks();

			} else {
				tracklist = DBConnection.getInstance()
						.getAllTracksForPlaylistId(playlistid);

			}

		}
		if (tracklist == null) {
			QMessageBox
					.critical(
							this,
							tr("Error!"),
							tr("The Database could not be read,\n see log for detailed information"));
			return;
		}

		showTrackList(tracklist);

	}

	@Override
	protected void contextMenuEvent(QContextMenuEvent event) {

		QTreeWidgetItem item = (QTreeWidgetItem) itemAt(event.pos());

		if (item != null && item instanceof TrackItem) {
			event.accept();
			selectedItem = ((TrackItem) item);

			QMenu menu = new QMenu(this);

			menu.addMenu(getRatingMenu());

			menu.addSeparator();
			menu.addAction(deleteTrackFromLibrary);
			
			if (selectedItem.getTrack().isPlayable()){
				//if its playable, its on the HD!
				menu.addAction(deleteTrackFromHD);
			}
			
			if (currentPlaylist != -1) {
				// tracks cannot be removed from library, only deleted
				menu.addAction(removeTrackFromPlaylist);
			}
			menu.addSeparator();
			
			menu.addAction(setAsCurrentTrack);
			menu.addAction(brainstormTrack);
			menu.addAction(addTrackToTrackstack);
			menu.addAction(editTrack);
			menu.addSeparator();
			menu.addMenu(getLastFMMenu());
			
			
			menu.popup(mapToGlobal(event.pos()));
		} else {

			QMenu menu = new QMenu(this);

			menu.addAction(addTrackToPlaylist);
			
					
			menu.popup(mapToGlobal(event.pos()));
		}

		super.contextMenuEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.tracklist.AbstractTrackListWidget#initContextMenus()
	 */
	@Override
	protected void initContextMenus() {
		super.initContextMenus();

		addTrackToPlaylist = new QAction(new QIcon(
				"classpath://images/track_new.png"),
				tr("&Add new Track to this Playlist"), this);
		addTrackToPlaylist.triggered.connect(this,
				"contextMenuAddTrackToPlaylistAction()");

		removeTrackFromPlaylist = new QAction(new QIcon(
				"classpath://images/track_remove.png"),
				tr("&Remove Track from Playlist"), this);
		removeTrackFromPlaylist.triggered.connect(this,
				"contextMenuRemoveTrackFromPlaylistAction()");
	}

	@SuppressWarnings("unused")
	private void contextMenuAddTrackToPlaylistAction() {
		QNewTrackWidget.getInstance(signalHub, currentPlaylist).show();
	}

	@SuppressWarnings("unused")
	private void contextMenuRemoveTrackFromPlaylistAction() {
		DBConnection.getInstance().deleteTrackfromPlaylist(currentPlaylist,
				selectedItem.getTrack().getId());
		removeTrackFromView(selectedItem.getTrack());
		signalHub.signalCurrentPlaylistUpdated();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.tracklist.AbstractTrackListWidget#getUniqueIdentifier()
	 */
	@Override
	protected int getUniqueIdentifier() {
		return Source.TRACKLIST;
	}

	@SuppressWarnings("unused")
	private void playlistUpdated(Integer playlistid) {
		if (this.currentPlaylist == playlistid) {
			showTracksForPlaylistID(playlistid); // reload this view
		}
	}

	/* ============== Drag n Drop methods =============== */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
	 */
	@Override
	protected void dragEnterEvent(QDragEnterEvent event) {
		
		if (event.source() != this) {
			QMimeData d = event.mimeData();
			if (d == null){
				event.ignore();
				return;
			}
	
			if (d instanceof DJMimeData) {
				DJMimeData djMimeData = (DJMimeData) d;
				
				if (djMimeData.hasTracks() || djMimeData.hasPlaylists()){
					event.accept();
					return;
				}
			}else if (event.mimeData().hasFormat("text/uri-list")){
				event.accept();
				return;
			}
		}
		event.ignore();
		
		}
 

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragMoveEvent(com.trolltech.qt.gui.QDragMoveEvent)
	 */
	@Override
	protected void dragMoveEvent(QDragMoveEvent event) {
		event.accept();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dropEvent(com.trolltech.qt.gui.QDropEvent)
	 */
	@Override
	protected void dropEvent(QDropEvent event) {
		
	
		if (event.mimeData().hasFormat("text/uri-list")) {
//drop from external application...
			List<Track> tracklist = new ArrayList<Track>();
			try {
				for (QUrl url : event.mimeData().urls()) {
					File file = new File(url.toLocalFile());

					if (file.isDirectory() && file.canRead()) {
						logger.debug("adding dir: " + url.toLocalFile());
						tracklist.addAll(MediaImporter
								.getTracksForDirectory(file));
					} else if (file.isFile() && file.canRead()) {
						Track foo = MediaImporter.getTrackForMp3(file);
						logger.debug("adding file: " + url.toLocalFile());
						if (foo != null) {
							tracklist.add(foo);
						}
					}
				}

				QMessageBox.StandardButton ret = QMessageBox
						.question(
								this,
								tr("Import Summary"),
								tr("This will add "
										+ tracklist.size()
										+ " tracks to the Library\n"
										+ "Do you want to check each file? By clicking \"No All\" all tracks will\n be inserted directly into the database?"),
							new QMessageBox.StandardButtons(			QMessageBox.StandardButton.YesToAll ,
										QMessageBox.StandardButton.NoToAll, QMessageBox.StandardButton.Cancel
),QMessageBox.StandardButton.YesToAll	);
				if (ret == QMessageBox.StandardButton.YesToAll) {

					CheckNewTrackWidget.checkTracks(signalHub, tracklist,
							currentPlaylist).show();

				} else if (ret == QMessageBox.StandardButton.NoToAll) {
					for (Track track : tracklist) {
						DBConnection.getInstance().insertTrack(track);
						if (currentPlaylist != -1) {

							int trackId = DBConnection.getInstance()
									.getHighestTrackId();
							if (DBConnection.getInstance()
									.insertTrackinPlaylist(currentPlaylist,
											trackId)) {
								logger.debug("adding trackId:" + trackId
										+ " to playlist: " + currentPlaylist);
							} else {
								logger.error("adding trackId:" + trackId
										+ " to playlist: " + currentPlaylist
										+ " FAILED!");
							}
						}
					}
					signalHub.signalTrackCountChanged();
				}

			} catch (Exception ioe) {
				ioe.printStackTrace();
				QMessageBox.critical(this, tr("Error!"), tr("Error occured:"
						+ ioe.getMessage()));
			}

			return;
		}
		
		QMimeData d = event.mimeData();
		
		
		if (d instanceof DJMimeData) {
			DJMimeData djMimeData = (DJMimeData) d;
		
		if (djMimeData.hasTracks()){
			logger.debug("not implemented...");
			

		}else if (djMimeData.hasPlaylists()){
			this.showTracksForPlaylistID(djMimeData.getPlaylistId());
			if (djMimeData.getPlaylistId() == PlaylistListWidget.LIBRARYID) {
				parent.setTabText(tr("Library"));
			} else {
				parent.setTabText(DBConnection.getInstance()
						.getPlaylistNameForId(djMimeData.getPlaylistId()));
			}
		}
			
		}


		
	}
}
