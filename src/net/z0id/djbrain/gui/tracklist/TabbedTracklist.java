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

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.playlist.PlaylistListWidget;
import net.z0id.djbrain.objects.Playlist;

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTabBar;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class TabbedTracklist extends QTabWidget {

	// /**
	// * contains all currenty shown playlists, identified by their ids, mapped
	// to their names
	// * is needed for handling in trashwidget to determine the source for drag
	// events
	// */
	// private Hashtable<Integer, String> shownPlaylists;

	private SignalHub signalHub;

	private QAction closeTabAction;

	/**
	 * @param signalHub
	 */
	public TabbedTracklist(SignalHub signalHub) {
		this.signalHub = signalHub;

		setAcceptDrops(true);

		setContextMenuPolicy(Qt.ContextMenuPolicy.DefaultContextMenu);

		initContextMenuActions();

		signalHub.openNewTab.connect(this, "addPlaylist(String, Integer)");
	}

	/**
	 * 
	 */
	private void initContextMenuActions() {
		closeTabAction = new QAction(new QIcon(
				"classpath://images/track_remove.png"), tr("&Close Playlist"),
				this);
		closeTabAction.triggered.connect(this,
				"contextMenuCloseSelectedTabAction()");

	}

	/**
	 * @param tabname
	 * @param playlistId
	 */
	public void addPlaylist(String tabname, Integer playlistId) {
		TrackList foo = new TrackList(signalHub, this);
		foo.showTracksForPlaylistID(playlistId);
		setCurrentIndex(addTab(foo, tabname));
	}

	/**
	 * @param playlist
	 */
	public void addPlaylist(Playlist playlist) {
		TrackList foo = new TrackList(signalHub, this);
		foo.showTracksForPlaylistID(playlist.getId());
		setCurrentIndex(addTab(foo, playlist.getName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QWidget#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
	 */
	@Override
	protected void dragEnterEvent(QDragEnterEvent event) {
		if (event.source() != this) {

			QMimeData d = event.mimeData();
			if (d instanceof DJMimeData) {
				DJMimeData djMimeData = (DJMimeData) d;

				if (djMimeData.hasPlaylists()) {
					event.accept();
					return;
				}
			}
		}
		event.ignore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QWidget#dragMoveEvent(com.trolltech.qt.gui.QDragMoveEvent)
	 */
	@Override
	protected void dragMoveEvent(QDragMoveEvent event) {
		event.accept();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QWidget#dropEvent(com.trolltech.qt.gui.QDropEvent)
	 */
	@Override
	protected void dropEvent(QDropEvent event) {

		DJMimeData djMimeData = (DJMimeData) event.mimeData();

		if (djMimeData.getPlaylistId() == PlaylistListWidget.LIBRARYID) {
			addPlaylist(tr("Library"), djMimeData.getPlaylistId());
		} else {
			addPlaylist(DBConnection.getInstance().getPlaylistForId(
					djMimeData.getPlaylistId()));
		}

	}

	/**
	 * @param playListId
	 */
	public void showTracksForPlaylistID(int playListId) {
		((TrackList) currentWidget()).showTracksForPlaylistID(playListId);

		if (playListId == PlaylistListWidget.LIBRARYID) {
			setTabText(tr("Library"));
		} else {
			setTabText(DBConnection.getInstance().getPlaylistNameForId(
					playListId));
		}

	}

	/**
	 * @return the currently shown TrackList
	 */
	public TrackList getCurrentTrackList() {
		return (TrackList) currentWidget();
	}

	private int selectedTabIndex;

	protected void contextMenuEvent(QContextMenuEvent event) {

		QWidget foo = childAt(event.pos());
		if (foo instanceof QTabBar) {
			QTabBar bar = (QTabBar) foo;

			selectedTabIndex = bar.currentIndex();

			QMenu menu = new QMenu(this);

			menu.addAction(closeTabAction);

			menu.popup(mapToGlobal(event.pos()));
		}

		super.contextMenuEvent(event);
	}

	@SuppressWarnings("unused")
	private void contextMenuCloseSelectedTabAction() {
		removeTab(selectedTabIndex);
	}

	/**
	 * @param text
	 */
	public void setTabText(String text) {

		setTabText(currentIndex(), text);
	}
}
