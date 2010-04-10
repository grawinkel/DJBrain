/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.playlist;

 
import java.util.Hashtable;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.QNewTrackWidget;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.DJMimeData.Source;
import net.z0id.djbrain.objects.Playlist;

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QDrag;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QListWidgetItem;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;

/**
 * @author meatz
 * 
 */
public class PlaylistListWidget extends QListWidget {

	private SignalHub signalHub;

	private Hashtable<Integer, PlaylistItem> playListItemTable;

	private PlaylistItem contextMenuSelectedPlaylistitem;

//	private PlaylistItem selectedPlaylist;

	private QAction addTrackToPlaylist;

	private QAction removelist;

	private QAction renameList;

	private QAction openAsTab;

	/**
	 * Globally accessible playlistId for the Library
	 */
	public static final int LIBRARYID = -1;

	// private final String IDENTIFIER = "playlist";

	/**
	 * @param signalHub
	 */
	public PlaylistListWidget(SignalHub signalHub) {

		this.signalHub = signalHub;
		this.playListItemTable = new Hashtable<Integer, PlaylistItem>();
		setDragEnabled(true);
		setAcceptDrops(true);
		setDropIndicatorShown(true);
		setUpdatesEnabled(true);

		initContextMenuActions();
		this.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection);

		// set spacing between playlistitems
		setSpacing(1);

		setContextMenuPolicy(Qt.ContextMenuPolicy.DefaultContextMenu);

		this.itemDoubleClicked.connect(this,
				"signalItemDoubleClicked(QListWidgetItem)");

		signalHub.trackCountChanged.connect(this, "trackCountChanged()");
		signalHub.tracksDeleted.connect(this, "tracksDeleted()");
		signalHub.playlistUpdated.connect(this, "playlistUpdated(Integer)");
		signalHub.newPlaylistAvailable.connect(this,
				"newPlaylistAvailable(Integer)");
		getItemsFromDb();

		// initially select the library
//		selectedPlaylist = playListItemTable.get(LIBRARYID);
		// highlightItem(playListItemTable.get(LIBRARYID));
	}

	/**
	 * 
	 */
	private void initContextMenuActions() {
		addTrackToPlaylist = new QAction(new QIcon(
				"classpath://images/track_new.png"),
				tr("&Add new track to this playlist"), this);
		addTrackToPlaylist.triggered.connect(this,
				"contextMenuAddTrackToPlaylistAction()");

		removelist = new QAction(new QIcon(
				"classpath://images/playlist_remove.png"),
				tr("&Remove playlist"), this);
		removelist.triggered.connect(this, "contextMenuRemovePlaylistAction()");

		renameList = new QAction(new QIcon(
				"classpath://images/playlist_rename.png"),
				tr("&Edit playlist"), this);
		renameList.triggered.connect(this, "contextMenuRenamePlaylistAction()");

		openAsTab = new QAction(new QIcon(
				"classpath://images/playlist_opentab.png"),
				tr("&Open Playlist in tab"), this);
		openAsTab.triggered.connect(this,
				"contextMenuOpenPlaylistInTabAction()");
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractScrollArea#contextMenuEvent(com.trolltech.qt.gui.QContextMenuEvent)
	 */
	@Override
	protected void contextMenuEvent(QContextMenuEvent event) {

		// TODO: it is not needed, that the qactions are declared as local
		// variables each time the contextmenu is called.. move them to fields
		QListWidgetItem item = (QListWidgetItem) itemAt(event.pos());

		if (item != null && item instanceof PlaylistItem) {
			event.accept();
			contextMenuSelectedPlaylistitem = ((PlaylistItem) item);

			QMenu menu = new QMenu(this);

			menu.addAction(openAsTab);
			menu.addAction(addTrackToPlaylist);
			menu.addAction(removelist);
			menu.addAction(renameList);

			menu.popup(mapToGlobal(event.pos()));
		} else {
			// contextmenu not above item, so show addplaylist dialogue
			event.accept();
			QMenu menu = new QMenu(this);
			QAction addPlaylist = new QAction(new QIcon(
					"classpath://images/playlist_new.png"),
					tr("&Add Playlist"), this);

			addPlaylist.triggered.connect(this,
					"contextMenuNewPlaylistAction()");

			menu.addAction(addPlaylist);
			menu.popup(mapToGlobal(event.pos()));
		}

		super.contextMenuEvent(event);
	}

	@SuppressWarnings("unused")
	// is called by a signal
	private void contextMenuRemovePlaylistAction() {
		int id = contextMenuSelectedPlaylistitem.getPlaylist().getId();
		if (id != -1) {

			if (contextMenuSelectedPlaylistitem.getPlaylist().getItemCount() > 0) {

				QMessageBox.StandardButton ret = QMessageBox
						.question(
								this,
								tr("Playlist is not empty!"),
								tr("This Playlist is not empty\n"
										+ "Are you sure you want to delete it? \n(The tracks will remain in the Library)"),
								new QMessageBox.StandardButtons(
										QMessageBox.StandardButton.Yes,
										QMessageBox.StandardButton.Cancel),
								QMessageBox.StandardButton.Cancel);
				if (ret == QMessageBox.StandardButton.Yes) {
					removePlaylistFromDB(id);
				}
			} else {
				// playlist is empty, so just delete it
				removePlaylistFromDB(id);
			}
		} else {
			QMessageBox.information(this, tr("Error!"),
					tr("The Library can not be removed"));
		}
	}

	/**
	 * @param playlistID
	 */
	public void removePlaylistFromDB(int playlistID) {
		DBConnection.getInstance().deletePlaylist(playlistID);

		if (signalHub.getCurrentPlaylistId() == playlistID) {
			signalHub.selectPlaylist(LIBRARYID);
			// highlightItem(playListItemTable.get(LIBRARYID));
		}
		this.takeItem(indexFromItem(playListItemTable.get(playlistID)).row());
	}

	@SuppressWarnings("unused")
	// is called by a signal
	private void contextMenuRenamePlaylistAction() {
		if (contextMenuSelectedPlaylistitem.getPlaylist().getId() != -1) {

			EditPlaylistWidget.getInstance(signalHub,
					contextMenuSelectedPlaylistitem.getPlaylist()).show();
		} else {
			QMessageBox.information(this, tr("Error!"),
					tr("The Library can not be renamed"));
		}
	}

	@SuppressWarnings("unused")
	private void contextMenuOpenPlaylistInTabAction() {
		signalHub.signalOpenNewTab(contextMenuSelectedPlaylistitem
				.getPlaylist().getName(), contextMenuSelectedPlaylistitem
				.getPlaylist().getId());
	}

	@SuppressWarnings("unused")
	private void contextMenuAddTrackToPlaylistAction() {
		QNewTrackWidget.getInstance(signalHub,
				contextMenuSelectedPlaylistitem.getPlaylist().getId()).show();
	}

	@SuppressWarnings("unused")
	// is called by a signal
	private void contextMenuNewPlaylistAction() {
		QNewPlaylistWidget.getInstance(signalHub).show();
	}

	/**
	 * 
	 */
	private void getItemsFromDb() {

		this.clear();
		playListItemTable.clear();
		Playlist library = new Playlist();
		PlaylistItem item = new PlaylistItem(library);

		library.setName(tr("Library"));
		library.setId(LIBRARYID);

		library.setItemCount(DBConnection.getInstance().getTrackCount());

		item.setText(library.getName() + "(" + library.getItemCount() + ")");
		item.setIcon(new QIcon("classpath://images/playlist_library.png"));
		item.setToolTip(tr("The library contains all your Tracks"));
		addItem(item);
		playListItemTable.put(LIBRARYID, item);
		List<Playlist> playlists = DBConnection.getInstance()
				.getAllPlaylists();

		for (Playlist playlist : playlists) {
			item = new PlaylistItem(playlist);
			// item.setFlags(Qt.ItemIsDropEnabled);
			// item.setFlags(Qt.ItemIsDragEnabled);
			// item.setFlags(Qt.ItemIsSelectable);
			int itemcount = playlist.getItemCount();
			item.setText(playlist.getName() + "(" + itemcount + ")");
			item.setIcon(new QIcon("classpath://images/playlist_normal.png"));
			addItem(item);
			item.setToolTip(playlist.getComment());
			playListItemTable.put(playlist.getId(), item);
		}
	}

	@SuppressWarnings("unused")
	private void playlistUpdated(Integer playlistId) {

		if (playlistId == LIBRARYID) {
			return; // this case is handled in currentTrackCount changed
		}
		Playlist playlist = DBConnection.getInstance().getPlaylistForId(
				playlistId);

		PlaylistItem item = playListItemTable.get(playlist.getId());

		if (item != null) {
			int itemcount = playlist.getItemCount();
			item.setText(playlist.getName() + "(" + itemcount + ")");
			item.setToolTip(playlist.getComment());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
	 */
	@Override
	protected void dragEnterEvent(QDragEnterEvent event) {
		if (event.source() != this) {
			QMimeData d = event.mimeData();
			if (d instanceof DJMimeData) {
				DJMimeData djMimeData = (DJMimeData) d;

				if (djMimeData.hasTracks()) {
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
	 * @see com.trolltech.qt.gui.QListView#dropEvent(com.trolltech.qt.gui.QDropEvent)
	 */
	@Override
	protected void dropEvent(QDropEvent event) {

		QPoint p = event.pos();
		QListWidgetItem item = itemAt(p);

		if (item instanceof PlaylistItem) {
			PlaylistItem playlistitem = (PlaylistItem) item;

			QMimeData d = event.mimeData();
			if (d instanceof DJMimeData) {
				DJMimeData djMimeData = (DJMimeData) d;

				if (djMimeData.hasTracks()) {

					int playlistId = playlistitem.getPlaylist().getId();

					for (Integer trackId : djMimeData.getTracklist()) {

						if (!playlistitem.getPlaylist().contains(trackId)) {
							DBConnection.getInstance().insertTrackinPlaylist(
									playlistId, trackId);
							playlistitem.getPlaylist().addTrack(trackId);
							playlistUpdated(playlistId);
						}
					}
					signalHub.setStatusBarText(tr("Done"));
				}
			}
		}
	}

	// private QListWidgetItem olditem = new QListWidgetItem();

	protected void dragMoveEvent(QDragMoveEvent event) {

		QPoint p = event.pos();
		QListWidgetItem item = itemAt(p);
		if (item != null) {
			if (((PlaylistItem) item).getPlaylist().getId() == -1) {
				event.ignore();
			} else {
				event.accept();
				signalHub.setStatusBarText(tr("Add track(s) to playlist: ")
						+ ((PlaylistItem) item).getPlaylist().getName());

			}
		} else {
			event.ignore();
		}
	}

	@SuppressWarnings("unused")
	private void signalItemDoubleClicked(QListWidgetItem item) {

		if (item instanceof PlaylistItem) {
			PlaylistItem playlist = (PlaylistItem) item;
			// highlightItem(playlist);
			signalHub.selectPlaylist(playlist.getPlaylist().getId());
		}
	}

	@SuppressWarnings("unused")
	private void trackCountChanged() {
		// so refresh the Library item count
		PlaylistItem item = playListItemTable.get(LIBRARYID);

		item.getPlaylist().setItemCount(
				DBConnection.getInstance().getTrackCount());

		item.setText(item.getPlaylist().getName() + "("
				+ item.getPlaylist().getItemCount() + ")");

	}

	@SuppressWarnings("unused")
	private void tracksDeleted( ) {
		this.getItemsFromDb();
	}

	@SuppressWarnings("unused")
	private void newPlaylistAvailable(Integer playlistId) {
		Playlist playlist = DBConnection.getInstance().getPlaylistForId(
				playlistId);

		if (playListItemTable.containsKey(playlistId)) {
			// playlistitem should only be updated;

			PlaylistItem item = playListItemTable.get(playlistId);

			int itemcount = playlist.getItemCount();
			item.setText(playlist.getName() + "(" + itemcount + ")");
			item.setIcon(new QIcon("classpath://images/playlist_normal.png"));
			item.setToolTip(playlist.getComment());
		} else {
			PlaylistItem item = new PlaylistItem(playlist);

			int itemcount = playlist.getItemCount();
			item.setText(playlist.getName() + "(" + itemcount + ")");
			item.setIcon(new QIcon("classpath://images/playlist_normal.png"));
			addItem(item);
			item.setToolTip(playlist.getComment());
			playListItemTable.put(playlist.getId(), item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#startDrag(int)
	 */
	@Override
	protected void startDrag(
			com.trolltech.qt.core.Qt.DropActions supportedActions) {

		QDrag drag = new QDrag(this);

		PlaylistItem item = (PlaylistItem) this.selectedItems().get(0);

		DJMimeData mimedata = new DJMimeData();
		
		mimedata.setSource(Source.PLAYLIST);
		mimedata.setPlaylistId(item.getPlaylist().getId());
		mimedata.setPlaylistItemCount(item.getPlaylist().getItemCount());
		drag.setMimeData(mimedata);

		QPixmap foo = new QPixmap("classpath://images/dragndrop-media-icon.png");
		drag.setPixmap(foo);

		drag.exec();
		}
}
