/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.brainstorm;

 
import java.util.ArrayList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.DJMimeData.Source;
import net.z0id.djbrain.gui.tracklist.AbstractTrackListWidget;
import net.z0id.djbrain.gui.tracklist.TrackItem;
import net.z0id.djbrain.objects.Suggestion;
import net.z0id.djbrain.objects.Track;

import org.apache.log4j.Logger;

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QDrag;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragLeaveEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QHeaderView;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

/**
 * @author meatz
 * 
 */
public class BrainstormTree extends AbstractTrackListWidget {

	private static Logger logger = Logger.getLogger(BrainstormTree.class);

	private int BrainstormDepth = 5;

	private BrainStormWidget parent;

	private QAction removeSuggestion;

	/**
	 * @param signalHub
	 * @param parent
	 */
	public BrainstormTree(SignalHub signalHub, BrainStormWidget parent) {
		super(signalHub);
		this.parent = parent;
		setColumnCount(3);
		setSortingEnabled(true);
		setDragEnabled(true);
		setDropIndicatorShown(true);
		setAcceptDrops(true);
		setUpdatesEnabled(true);

		QHeaderView hv = header();
		hv.setResizeMode(ResizeMode.Interactive);
		// hv.setClickable(true);
		// hv.setMovable(true);
		// hv.setCascadingSectionResizes(false );
		hv.setUpdatesEnabled(true);
		hv.setStretchLastSection(true);

		this.setHeader(hv);

		List<String> headers = new ArrayList<String>();
		headers.add("Track");
		headers.add("Label");
		headers.add("Length");

		setHeaderLabels(headers);

		setColumnWidth(0, 400);
		setColumnWidth(1, 200);
		resizeColumnToContents(2);

		setContextMenuPolicy(Qt.ContextMenuPolicy.DefaultContextMenu);

		setAlternatingRowColors(true);

		this
				.setSelectionMode(QAbstractItemView.SelectionMode.ExtendedSelection);

		this.itemSelectionChanged.connect(this, "selectionChanged()");

		// this.itemDoubleClicked.connect(this,
		// "itemDoubleClicked(QTreeWidgetItem, Integer)");

		this.doubleClicked.connect(this, "doubleClicked(QObject, String)");
	}

	@SuppressWarnings("unused")
	private void doubleClicked(QObject qobject, String string) {
		System.out.println("doubleclick:" + qobject.getClass().toString()
				+ " ++ " + string);
	}

	@SuppressWarnings("unused")
	private void selectionChanged() {

		if (selectedItems().size() > 0) {
			TrackItem selectedItem = (TrackItem) selectedItems().get(0);
			Track suggestedTrack = selectedItem.getTrack();

			if (selectedItem.parent() != null) {
				Track sourceTrack = ((TrackItem) selectedItem.parent())
						.getTrack();
				Suggestion suggestion = DBConnection.getInstance()
						.getSuggestion(sourceTrack.getId(),
								suggestedTrack.getId());
				String from = sourceTrack.toPlaylistString();
				String to = suggestedTrack.toPlaylistString();

				String identString = from + " --> " + to;
				parent.setSuggestion(identString, suggestion);
			}
		}
	}

	/**
	 * @param trackId
	 */
	public void brainstorm(int trackId) {
		this.clear();

		Track rootTrack = DBConnection.getInstance().getTrackForId(trackId);
		logger.debug("storming id:" + trackId + " ==> "
				+ rootTrack.toPlaylistString());

		TrackItem rootTrackItem = new TrackItem(rootTrack);

		rootTrackItem.setText(0, rootTrackItem.getTrack().toPlaylistString());
		rootTrackItem.setText(1, rootTrackItem.getTrack().getLabel());
		rootTrackItem.setText(2, rootTrackItem.getTrack().getLength());
		addTopLevelItem(rootTrackItem);

		depthstorm(1, rootTrackItem);
		update();
	}

	private void depthstorm(int currentDepth, TrackItem trackItem) {
		// this method may be rewritten, so that the tracks can be browsed to
		// unlimited depth, by loading them on demand
		if (currentDepth < BrainstormDepth) {

			List<Track> levelTracks = DBConnection.getInstance()
					.getSuggestedTracksForTrackId(trackItem.getTrack().getId());
			for (Track track : levelTracks) {
				TrackItem child = new TrackItem(track);
				child.setText(0, track.toPlaylistString());
				child.setText(1, child.getTrack().getLabel());
				child.setText(2, child.getTrack().getLength());
				trackItem.addChild(child);
				depthstorm(currentDepth + 1, child);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragLeaveEvent(com.trolltech.qt.gui.QDragLeaveEvent)
	 */
	@Override
	protected void dragLeaveEvent(QDragLeaveEvent event) {
		event.accept();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#startDrag(int)
	 */
	@Override
	protected void startDrag(
			com.trolltech.qt.core.Qt.DropActions supportedActions) {

		//brainstorm has single selection
		TrackItem item = (TrackItem) selectedItems().get(0);

		
		// if (item instanceof TrackItem) {
		// System.out.println("drag started");
		QDrag drag = new QDrag(this);

		DJMimeData bar = new DJMimeData();
		bar.setSource(Source.BRAINSTORM);
		bar.setText(Source.BRAINSTORM + "|"
				+ ((TrackItem) item).getTrack().getId() + "");
		bar.addTrackId(((TrackItem) item).getTrack().getId());
		drag.setMimeData(bar);

		QPixmap foo = new QPixmap("classpath://images/track_edit.png");
		drag.setPixmap(foo);

		drag.exec();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.tracklist.AbstractTrackListWidget#getUniqueIdentifier()
	 */
	@Override
	protected int getUniqueIdentifier() {
		return Source.BRAINSTORM;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
	 */
	@Override
	protected void dragEnterEvent(QDragEnterEvent event) {
		
		if ( event.mimeData() instanceof DJMimeData) {
			event.acceptProposedAction();			
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

		if (event.source() != this) {

			QMimeData data = event.mimeData();
			if (data instanceof DJMimeData) {
				DJMimeData djMimeData = (DJMimeData) data;
				if (djMimeData.hasTracks()) {
					brainstorm(djMimeData.getFirstTrackId());
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void itemDoubleClicked(QTreeWidgetItem item, Integer i) {
		System.out.println("doubleklick");
		signalHub.signalCurrentTrackChanged(((TrackItem) item).getTrack());
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

			if (selectedItem.parent() != null) {

				menu.addAction(removeSuggestion);

			}
			// else selected the root file, which is not a suggestion...

			menu.addAction(deleteTrackFromLibrary);
			if (selectedItem.getTrack().isPlayable()) {
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.tracklist.AbstractTrackListWidget#initContextMenus()
	 */
	@Override
	protected void initContextMenus() {
		super.initContextMenus();

		removeSuggestion = new QAction(new QIcon(
				"classpath://images/track_remove.png"),
				tr("&Remove track from suggestions"), this);
		removeSuggestion.triggered.connect(this, "removeSelectedSuggestion()");
	}

	@SuppressWarnings("unused")
	private void removeSelectedSuggestion() {
		int parentId = ((TrackItem) selectedItem.parent()).getTrack().getId();
		int trackId = selectedItem.getTrack().getId();

		DBConnection.getInstance().deleteSuggestedTrack(parentId, trackId);

		// this.takeTopLevelItem(indexOfTopLevelItem(selectedItem));
		selectedItem.setHidden(true);
		signalHub.signalSuggestionDeleted(parentId, trackId);
		logger.debug("deleted suggestion: " + parentId + " -> " + trackId);
	}

}
