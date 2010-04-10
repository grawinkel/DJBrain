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

 

import java.util.ArrayList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData.Source;
import net.z0id.djbrain.gui.playlist.PlaylistListWidget;

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMessageBox;

/**
 * @author meatz
 * 
 */
public class TrashWidget extends QLabel {

	private QMain qmain;

	private SignalHub signalHub;

	/**
	 * @param signalHub
	 * @param qmain
	 */
	public TrashWidget(QMain qmain, SignalHub signalHub) {
		this.signalHub = signalHub;
		this.qmain = qmain;
		this.setAcceptDrops(true);

		setBaseSize(100, 100);

		this.setMargin(8);
		QFont f = new QFont();
		f.setPointSize(12);
		f.setWeight(QFont.Weight.Bold.value());
		setFont(f);

		setText("Drop item\nto remove");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QWidget#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
	 */
	@Override
	protected void dragEnterEvent(QDragEnterEvent event) {

		QMimeData d = event.mimeData();
		
		if (d == null){
			event.ignore();
			return;
		}
		//////////
		
//		for (String s : d.formats()){
//			System.out.println("FORMAT: " + s);
//		}
//		
//		if (d.hasUrls()){
//			for (QUrl qurl : d.urls()){
//				System.out.println(qurl);
//			}
//		}
//		
//		System.out.println("finish");
		///////////
		
		
		if (d instanceof DJMimeData) {
			DJMimeData djMimeData = (DJMimeData) d;

			if (djMimeData.hasTracks()) {
				signalHub
						.setStatusBarText(tr("Dropped track(s) will be removed from their context"));
				event.acceptProposedAction();
				return;
			} else if (djMimeData.hasPlaylists()) {
				signalHub
						.setStatusBarText(tr("Dropped playlist(s) will be removed"));
				event.acceptProposedAction();
				return;
			}
			event.ignore();
		}
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

		QMimeData d = event.mimeData();
		if (d instanceof DJMimeData) {
			DJMimeData djMimeData = (DJMimeData) d;
			int source = djMimeData.getSource();

			if (source == Source.TRACKSTACk) {
				if (djMimeData.hasTracks) {
					TrackStackWidget.getInstance(signalHub)
							.removeItemsFromView(djMimeData.getTracklist());
				}
			} else if (source == Source.TRACKLIST) {
				if (djMimeData.hasTracks) {
					if (signalHub.getCurrentPlaylistId() == -1) {
						// this will delete tracks from db, so warn the user

						QMessageBox.StandardButton ret = QMessageBox.warning(
								this, tr("Delete tracks from Library"),
								tr("This will delete "
										+ djMimeData.getTracklist().size()
										+ " tracks from Library .\n"
										+ "Do you want to continue?"),
								new QMessageBox.StandardButtons(
										QMessageBox.StandardButton.Yes,
										QMessageBox.StandardButton.Cancel),
								QMessageBox.StandardButton.Yes);
						if (ret == QMessageBox.StandardButton.Yes) {

							List<Integer> deletedItems = new ArrayList<Integer>();
							for (int trackId : djMimeData.getTracklist()) {
								DBConnection.getInstance().deleteTrack(trackId);
								deletedItems.add(trackId);
							}
							signalHub.signalTracksDeleted(deletedItems);
						}
					}

				 else {
					// only remove tracks from the playlist
					for (int trackId : djMimeData.getTracklist()) {
						DBConnection.getInstance().deleteTrackfromPlaylist(
								signalHub.getCurrentPlaylistId(), trackId);
						qmain.getTrackList().removeTrackFromView(trackId);
					}
				}
				}
				signalHub.signalCurrentPlaylistUpdated();

			} else if (source == Source.SUGGESTIONLIST) {
				
				//TODO refactor me!
				qmain.getSuggestedTrackList().removeSuggestionsFromDB(
						djMimeData.getTracklist());
			} else if (source == Source.PLAYLIST) {
				if (djMimeData.hasPlaylists()) {
					int playlistId = djMimeData.getPlaylistId();
					int itemcount = djMimeData.getPlaylistItemCount();

					if (playlistId == PlaylistListWidget.LIBRARYID) {

						QMessageBox.critical(this, tr("Error!"),
								tr("The Library can not be removed"));
						event.ignore();
						return;
					}

					if (itemcount > 0) {
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
							qmain.getPlaylist()
									.removePlaylistFromDB(playlistId);
						}
					} else {
						qmain.getPlaylist().removePlaylistFromDB(playlistId);
					}
				}

				// int itemcount = items.get(1);

			}
		}

	}

}
