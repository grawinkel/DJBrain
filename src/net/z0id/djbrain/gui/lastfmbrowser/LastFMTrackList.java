/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.lastfmbrowser;

import java.util.Arrays;
import java.util.LinkedList;

import net.z0id.djbrain.gui.CheckNewTrackWidget;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.objects.Track;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QClipboard;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * @author meatz
 * 
 */
public class LastFMTrackList extends QTreeWidget {


	private final int POSITION_ARTIST = 0;

	private final int POSITION_TRACKNAME = 1;

	private final int POSITION_RANKING = 2;

	private final int POSITION_URL = 3;

	private SignalHub signalHub;

	private LastFMBrowser lastFMBrowser;

	public LastFMTrackList(SignalHub signalHub, LastFMBrowser lastFMBrowser) {

		super();

		this.signalHub = signalHub;
		this.lastFMBrowser = lastFMBrowser;

		setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection);

		setDragEnabled(false);
		setAcceptDrops(false);
		setDropIndicatorShown(true);
		setUpdatesEnabled(true);
		setRootIsDecorated(true);
		setSortingEnabled(true);

		setContextMenuPolicy(Qt.ContextMenuPolicy.DefaultContextMenu);

		setAlternatingRowColors(true);

		init();
	}

	private void init() {
		setColumnCount(4);

		LinkedList<String> headers = new LinkedList<String>();

		headers.add(tr("LastFMArtist"));
		headers.add(tr("Trackname"));
		headers.add(tr("Ranking"));
		headers.add(tr("URL"));

		setHeaderLabels(headers);

	}

	private QTreeWidgetItem selectedItem;

	
	@SuppressWarnings("unused")
	private void contextMenuActionsearchRelatedArtists() {
		String artistname = selectedItem.text(POSITION_ARTIST);

		if (artistname.equals("")) {
			if (selectedItem.parent() != null) {
				artistname = selectedItem.parent().text(POSITION_ARTIST);
			}
		}
		lastFMBrowser.browseRelatedArtists(artistname);

	}

	@SuppressWarnings("unused")
	private void contextMenuActionsearchTopTracksOfArtist() {
		String artistname = selectedItem.text(POSITION_ARTIST);

		if (artistname.equals("")) {
			if (selectedItem.parent() != null) {
				artistname = selectedItem.parent().text(POSITION_ARTIST);
			}
		}
		lastFMBrowser.browseTopTracksForArtist(artistname);

	}

	@SuppressWarnings("unused")
	private void contextMenuActionAddAsTrackToLibrary() {
		Track track = new Track();

		String artistname = selectedItem.text(POSITION_ARTIST);

		if (artistname.equals("")) {
			if (selectedItem.parent() != null) {
				artistname = selectedItem.parent().text(POSITION_ARTIST);
			}
		}

		track.setArtist(artistname);
		track.setTrackname(selectedItem.text(POSITION_TRACKNAME));
		track.setComment(selectedItem.text(POSITION_URL));
		CheckNewTrackWidget.checkTracks(signalHub, Arrays.asList(track)).show();
	}
	
	
	@SuppressWarnings("unused")
	private void contextMenuActioncopyURLToClipboard() {
		String url = selectedItem.text(POSITION_URL);
		QApplication.clipboard().setText(url, QClipboard.Mode.Clipboard);

	}

	protected void contextMenuEvent(QContextMenuEvent event) {

		selectedItem = (QTreeWidgetItem) itemAt(event.pos());
		QMenu menu = new QMenu(this);

		String artistname = selectedItem.text(POSITION_ARTIST);

		if (artistname.equals("")) {
			if (selectedItem.parent() != null) {
				artistname = selectedItem.parent().text(POSITION_ARTIST);
			}
		}

		String trackname = selectedItem.text(POSITION_TRACKNAME);

		if (!trackname.equals("")) {

			QAction addAsTrackToLibrary = new QAction(new QIcon(
					"classpath://images/track_setcurrent.png"),
					tr("&Add as Track to your Library"), this);
			addAsTrackToLibrary.triggered.connect(this,
					"contextMenuActionAddAsTrackToLibrary()");

			menu.addAction(addAsTrackToLibrary);

			menu.addSeparator();
		}

		QAction searchRelatedArtists = new QAction(new QIcon(
				"classpath://images/track_setcurrent.png"),
				tr("browse related Artists"), this);
		searchRelatedArtists.triggered.connect(this,
				"contextMenuActionsearchRelatedArtists()");

		menu.addAction(searchRelatedArtists);

		QAction searchTopTracksOfArtist = new QAction(new QIcon(
				"classpath://images/track_setcurrent.png"),
				tr("browse top tracks of artist"), this);
		searchTopTracksOfArtist.triggered.connect(this,
				"contextMenuActionsearchTopTracksOfArtist()");

		menu.addAction(searchTopTracksOfArtist);

		QAction copyURLToClipboard = new QAction(new QIcon(
				"classpath://images/track_setcurrent.png"),
				tr("&Copy URL to Clipboard"), this);
		copyURLToClipboard.triggered.connect(this,
				"contextMenuActioncopyURLToClipboard()");
		menu.addAction(copyURLToClipboard);

		menu.popup(mapToGlobal(event.pos()));

	}

	/**
	 * adds the track to this list
	 * 
	 * @param track
	 */
	public void addArtist(LastFMArtist artist) {

		QTreeWidgetItem item = new QTreeWidgetItem();

		item.setText(POSITION_ARTIST, artist.getName());
		item.setText(POSITION_URL, artist.getUrl());

		for (LastFMTrack t : artist.getTracks()) {
			QTreeWidgetItem subitem = new QTreeWidgetItem();
			subitem.setText(POSITION_TRACKNAME, t.getTrackname());
			subitem.setText(POSITION_RANKING, t.getRanking());
			subitem.setText(POSITION_URL, t.getUrl());
			item.addChild(subitem);
		}
		addTopLevelItem(item);
	}
}
