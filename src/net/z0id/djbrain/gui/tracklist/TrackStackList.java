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

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * @author meatz
 * 
 */
public class TrackStackList extends AbstractTrackListWidget {

	private final String IDENTIFIER = "trackstack";

	// private ArrayList<Track> content;

	/**
	 * @param signalHub
	 */
	public TrackStackList(SignalHub signalHub) {

		super(signalHub);
		// content = new ArrayList<Track>();

		setSelectionMode(QAbstractItemView.SelectionMode.ExtendedSelection);

		setDragEnabled(true);
		setAcceptDrops(true);
		setDropIndicatorShown(true);
		setUpdatesEnabled(true);
		setRootIsDecorated(false);
		setSortingEnabled(true);

		setContextMenuPolicy(Qt.ContextMenuPolicy.DefaultContextMenu);

		setAlternatingRowColors(true);

		this.itemDoubleClicked.connect(this,
				"itemDoubleClickedAction(QTreeWidgetItem, Integer)");

		initColumns(IDENTIFIER);
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
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragMoveEvent(com.trolltech.qt.gui.QDragMoveEvent)
	 */
	@Override
	protected void dragMoveEvent(QDragMoveEvent event) {
		event.acceptProposedAction();
	}

	/*
	 * (non-Javadoc)
	 * @see com.trolltech.qt.gui.QTreeWidget#dropEvent(com.trolltech.qt.gui.QDropEvent)
	 */
	protected void dropEvent(QDropEvent event) {

	QMimeData data = event.mimeData();
		
		if (data instanceof DJMimeData) {
			DJMimeData djMimeData = (DJMimeData) data;
		
			for (Integer id : djMimeData.getTracklist()){
				Track track = DBConnection.getInstance().getTrackForId(id);
				if (track != null) {
					addTrack(track);
				}
			}
			resizeColumns();
			
		}
	}

	
	/**
	 * adds the track to this list
	 * 
	 * @param track
	 */
	public void addTrack(Track track) {

//		if (shownItems.containsKey(track.getId())) {
//			return;
//		}

		TrackItem item = new TrackItem(track);

		if (POSITION_ARTIST != -1)
			item.setText(POSITION_ARTIST, track.getArtist());

		if (POSITION_TRACKTITLE != -1)
			item.setText(POSITION_TRACKTITLE, track.getTrackname());

		if (POSITION_LABEL != -1)
			item.setText(POSITION_LABEL, track.getLabel());

		if (POSITION_LENGTH != -1)
			item.setText(POSITION_LENGTH, track.getLength());

		if (POSITION_GENRE != -1)
			item.setText(POSITION_GENRE, track.getGenreString());

		if (POSITION_RELEASED != -1)
			item.setText(POSITION_RELEASED, track.getReleased() + "");

		if (POSITION_BPM != -1)
			item.setText(POSITION_BPM, track.getBpm() + "");

		if (POSITION_CATALOGNR != -1)
			item.setText(POSITION_CATALOGNR, track.getCatalognr());

		if (POSITION_INVENTORYNR != -1)
			item.setText(POSITION_INVENTORYNR, track.getInventorynr());

		if (POSITION_COMMENT != -1)
			item.setText(POSITION_COMMENT, track.getComment());

		if (POSITION_RATING != -1)
			item.setText(POSITION_RATING, track.getRating() + "");

		if (POSITION_MEDIATYPE != -1)
			item.setText(POSITION_MEDIATYPE, track.getMediatype());

		if (POSITION_FILENAME != -1)
			item.setText(POSITION_FILENAME, track.getFilename());

		addTopLevelItem(item);
		shownItems.put(track.getId(), item);

	}
	
//	/**
//	 * SLOT
//	 */
//	public void removeSelectedItems() {
//		List<QModelIndex> itemlist = selectedIndexes();
//
//		for (QModelIndex index : itemlist) {
//			takeTopLevelItem(index.row());
//		}
//
//	}

	protected int getUniqueIdentifier() {
		return Source.TRACKSTACk;
	}

	/**
	 * @return ArrayList with trackids from all tracks in this view
	 */
	public List<Integer> getContentTrackIds() {

		List<Integer> trackIds = new ArrayList<Integer>();

		int count = topLevelItemCount();

		for (int i = 0; i < count; i++) {
			QTreeWidgetItem item = topLevelItem(i);

			if (item instanceof TrackItem) {
				trackIds.add(((TrackItem) item).getTrack().getId());
			}
		}
		return trackIds;
	}
	
	
	protected void contextMenuEvent(QContextMenuEvent event) {

		QTreeWidgetItem item = (QTreeWidgetItem) itemAt(event.pos());

		if (item != null && item instanceof TrackItem) {
			event.accept();
			selectedItem = ((TrackItem) item);

			QMenu menu = new QMenu(this);

			menu.addMenu(getRatingMenu());

			menu.addSeparator();
			menu.addAction(brainstormTrack);
			menu.addAction(deleteTrackFromLibrary);
			
			if(selectedItem.getTrack().isPlayable()){
				menu.addAction(deleteTrackFromHD);
			}
		
			
			menu.addAction(setAsCurrentTrack);
			menu.addAction(addTrackToTrackstack);
			menu.addAction(editTrack);
			
			menu.addSeparator();
			menu.addMenu(getLastFMMenu());
			
			
			menu.popup(mapToGlobal(event.pos()));
		}
	}
}
