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

 
 
import java.util.ArrayList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.DJMimeData.Source;
import net.z0id.djbrain.objects.Track;

import org.apache.log4j.Logger;

import com.trolltech.qt.core.QMimeData;
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
public class SuggestedTrackList extends AbstractTrackListWidget {

	private final String IDENTIFIER = "suggestionlist";

	private SuggestionBox suggestionBox;

	private int currentTrackId = -1;

	private QAction removeTrackFromSuggestions;
	
	private static Logger logger = Logger.getLogger(SuggestedTrackList.class);

	/**
	 * suggestTrackIDsList holds an Integer List of the ids of currently shown
	 * tracks
	 */
	private List<Integer> suggestTrackIDsList = new ArrayList<Integer>();

	/**
	 * @param signalHub
	 * @param suggestionBox
	 */
	public SuggestedTrackList(SignalHub signalHub, SuggestionBox suggestionBox) {
		super(signalHub);

		this.suggestionBox = suggestionBox;
		this
				.setSelectionMode(QAbstractItemView.SelectionMode.ExtendedSelection);

		setAcceptDrops(true);
		setUpdatesEnabled(true);
		setDragEnabled(true);
		setRootIsDecorated(false);
		setSortingEnabled(true);

		setContextMenuPolicy(Qt.ContextMenuPolicy.DefaultContextMenu);
		setAlternatingRowColors(true);

		this.itemDoubleClicked.connect(this,
				"itemDoubleClickedAction(QTreeWidgetItem, Integer)");

		signalHub.currentTrackChanged.connect(this,
				"showSuggestedTracks(Track)");
		
		signalHub.suggestionDeleted.connect(this, "suggestionRemoved(Integer,Integer)");
		initColumns(IDENTIFIER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.tracklist.AbstractTrackListWidget#initContextMenus()
	 */
	@Override
	protected void initContextMenus() {
		super.initContextMenus();

		removeTrackFromSuggestions = new QAction(new QIcon(
				"classpath://images/track_remove.png"),
				tr("&Remove track from suggestions"), this);
		removeTrackFromSuggestions.triggered.connect(this,
				"removeSelectedItem()");
	}

	@SuppressWarnings("unused")
	private void showSuggestedTracks(Track track) {
		this.currentTrackId = track.getId();

		this.clear();

		if (currentTrackId == -1) {
			return;
		}

		List<Track> tracklist;

		tracklist = DBConnection.getInstance().getSuggestedTracksForTrackId(
				currentTrackId);

		if (tracklist == null) {
			QMessageBox
					.critical(
							this,
							tr("Error!"),
							tr("The Database could not be read,\n see log for detailed information"));
			return;
		}

		suggestTrackIDsList.clear();
		// feed list with trackids in this view, so that dups can be found
		// easier
		for (Track t : tracklist) {
			suggestTrackIDsList.add(t.getId());
		}

		showTrackList(tracklist);

		this.update();
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
			menu.addAction(removeTrackFromSuggestions);
			menu.addAction(deleteTrackFromLibrary);
			if(selectedItem.getTrack().isPlayable()){
				menu.addAction(deleteTrackFromHD);
			}
			menu.addSeparator();
			menu.addAction(setAsCurrentTrack);
			menu.addAction(brainstormTrack);
			menu.addAction(addTrackToTrackstack);
			menu.addAction(editTrack);
			
			menu.addSeparator();
			menu.addMenu(getLastFMMenu());
			
			
			menu.popup(mapToGlobal(event.pos()));
		}

		super.contextMenuEvent(event);
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
				if (djMimeData.hasTracks()){
					event.acceptProposedAction();
					signalHub
							.setStatusBarText(tr("Add track(s) as suggestions for  current track"));
				}else{
					event.ignore();
				}
			}
//			if (d != null && d.hasFormat("text/plain")) {
//
//				if (d.text().startsWith(DJBrain.DDTYPE_TRACK + "|")) {
//
//			
//				} else {
//					event.ignore();
//				}
			}
		}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragMoveEvent(com.trolltech.qt.gui.QDragMoveEvent)
	 */
	@Override
	protected void dragMoveEvent(QDragMoveEvent event) {
		event.acceptProposedAction();
	}

	@Override
	protected void dropEvent(QDropEvent event) {

		if (currentTrackId == -1) {
			// track not set
			event.ignore();
			return;
		}
		
		QMimeData data = event.mimeData();
		if (data instanceof DJMimeData) {
			DJMimeData djMimeData = (DJMimeData) data;
	
		if (djMimeData.hasTracks()){
			for  (Integer suggestedTrackId : djMimeData.getTracklist()) {
			
				if (suggestedTrackId != currentTrackId
						&& !suggestTrackIDsList.contains(suggestedTrackId)) {
					// its not the current track and the suggestion is
					// not already in the suggestionslist
					if (DBConnection.getInstance().insertSuggestedTrack(
							currentTrackId, suggestedTrackId, "no Comment",
							0)) {
						suggestTrackIDsList.add(suggestedTrackId);
					}
					// add track to view
					addTrack(DBConnection.getInstance().getTrackForId(
							suggestedTrackId));

				}
			}
			
			event.accept();
		}
		
		}
			
			
				signalHub.setStatusBarText(tr("Done"));
				// showSuggestedTracks();
	 
		 
	}

	@Override
	protected int getUniqueIdentifier() {
		return Source.SUGGESTIONLIST;
	}

	/**
	 * @param items
	 */
	public void removeSuggestionsFromDB(List<Integer> items) {
		for (Integer suggestedTrack : items) {
			DBConnection.getInstance().deleteSuggestedTrack(currentTrackId,
					suggestedTrack);
			removeTrackFromView(suggestedTrack);
			suggestionBox.checkDisable(suggestedTrack);
		}
		signalHub.setStatusBarText(tr("Removed Suggestions"));
	}

	@SuppressWarnings("unused")
	private void removeSelectedItem() {

		DBConnection.getInstance().deleteSuggestedTrack(currentTrackId,
				selectedItem.getTrack().getId());
		removeTrackFromView(selectedItem.getTrack().getId());

		suggestionBox.checkDisable(selectedItem.getTrack().getId());
		signalHub.setStatusBarText(tr("Removed Suggestions"));
	}

	@Override
	@SuppressWarnings("unused")
	protected void itemDoubleClickedAction(QTreeWidgetItem item, Integer column) {
		if (item instanceof TrackItem) {
			suggestionBox.setSuggestion(DBConnection.getInstance()
					.getSuggestion(currentTrackId,
							((TrackItem) item).getTrack().getId()));
		}
	}
	
	@SuppressWarnings("unused")
	private void suggestionRemoved(Integer trackId, Integer suggestionId){
		
		if(signalHub.getCurrentTrackId() == trackId){
			if (this.shownItems.containsKey(suggestionId)){
				this.removeTrackFromView(suggestionId);
				logger.debug("removeTrackFromView( " +suggestionId+ ") ");
			}	
		}
	}
}
