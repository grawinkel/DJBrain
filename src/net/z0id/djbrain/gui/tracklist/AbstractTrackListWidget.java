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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.QEditTrackWidget;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.TrackStackWidget;
import net.z0id.djbrain.gui.brainstorm.BrainStormWidget;
import net.z0id.djbrain.gui.lastfmbrowser.LastFMBrowser;
import net.z0id.djbrain.imexport.MediaImporter;
import net.z0id.djbrain.objects.Track;
import net.z0id.djbrain.properties.DJProperties;

import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt.DropActions;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QDrag;
import com.trolltech.qt.gui.QHeaderView;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

/**
 * @author meatz
 * 
 */
public abstract class AbstractTrackListWidget extends QTreeWidget {

	protected int POSITION_ARTIST = -1;

	protected int POSITION_TRACKTITLE = -1;

	protected int POSITION_LABEL = -1;

	protected int POSITION_LENGTH = -1;

	protected int POSITION_GENRE = -1;

	protected int POSITION_RELEASED = -1;

	protected int POSITION_BPM = -1;

	protected int POSITION_CATALOGNR = -1;

	protected int POSITION_INVENTORYNR = -1;

	protected int POSITION_COMMENT = -1;

	protected int POSITION_RATING = -1;

	protected int POSITION_MEDIATYPE = -1;

	protected int POSITION_FILENAME = -1;

	protected int columns = 0;

	// trackid, trackitem
	protected Hashtable<Integer, TrackItem> shownItems;

	// this is the item on which the contextmenu was called
	protected TrackItem selectedItem = new TrackItem(null);

	protected SignalHub signalHub;

	protected QAction editTrack;

	protected QAction addTrackToTrackstack;

	protected QAction deleteTrackFromLibrary;

	protected QAction deleteTrackFromHD;

	protected QAction brainstormTrack;

	protected QAction setAsCurrentTrack;
	
	private QMenu myRatingMenu;
	
	private QMenu myLastFMMenu;
	/**
	 * @param signalHub
	 */
	public AbstractTrackListWidget(SignalHub signalHub) {

		this.signalHub = signalHub;
		shownItems = new Hashtable<Integer, TrackItem>();

		signalHub.trackUpdated.connect(this, "updateTrack(Track)");
		signalHub.tracksDeleted.connect(this, "removeItemsFromView(List)");
		initContextMenus();

		// QHeaderView hv = new QHeaderView(Qt.Orientation.Horizontal);
		QHeaderView hv = header();
		hv.setResizeMode(ResizeMode.Interactive);
		// hv.setClickable(true);
		// hv.setMovable(true);
		// hv.setCascadingSectionResizes(false );
		hv.setUpdatesEnabled(true);
		hv.setStretchLastSection(true);

		this.setHeader(hv);
	}

	/**
	 * 
	 */
	protected void initContextMenus() {
		deleteTrackFromLibrary = new QAction(new QIcon(
				"classpath://images/track_delete.png"), tr("&Remove track from Library"),
				this);
		deleteTrackFromLibrary.triggered.connect(this,
				"contextMenuDeleteTrackFromLibraryAction()");
		/* ======================================= */

		deleteTrackFromHD = new QAction(new QIcon(
				"classpath://images/track_delete.png"), tr("&Delete track from harddisk"),
				this);
		deleteTrackFromHD.triggered.connect(this,
				"contextMenuDeleteTrackFromHDAction()");
		/* ======================================= */
		setAsCurrentTrack = new QAction(new QIcon(
				"classpath://images/track_setcurrent.png"),
				tr("&Set as Currenttrack"), this);
		setAsCurrentTrack.triggered.connect(this,
				"contextMenuSetAsCurrentTrackAction()");

		/* ======================================= */
		brainstormTrack = new QAction(new QIcon(
				"classpath://images/brainstorm.png"),
				tr("&Brainstorm this Track"), this);
		brainstormTrack.triggered.connect(this,
				"contextMenuBrainstormTrackAction()");

		/* ======================================= */

		editTrack = new QAction(new QIcon("classpath://images/track_edit.png"),
				tr("&Edit Track"), this);
		editTrack.triggered.connect(this, "contextMenuEditTrackAction()");
		/* ======================================= */
		addTrackToTrackstack = new QAction(new QIcon(
				"classpath://images/trackstack.png"),
				tr("&Add track to Trackstack"), this);
		addTrackToTrackstack.triggered.connect(this,
				"contextMenuAddTrackToTrackstackAction()");
		/* ======================================= */
	}

	protected abstract int getUniqueIdentifier();

	protected void initColumns(String identifier) {

		int i;

		i = check(identifier + "_show_artist");
		if (i != -1) {
			this.POSITION_ARTIST = i;
		}

		i = check(identifier + "_show_tracktitle");
		if (i != -1) {
			this.POSITION_TRACKTITLE = i;
		}

		i = check(identifier + "_show_label");
		if (i != -1) {
			this.POSITION_LABEL = i;
		}

		i = check(identifier + "_show_length");
		if (i != -1) {
			this.POSITION_LENGTH = i;
		}

		i = check(identifier + "_show_genre");
		if (i != -1) {
			this.POSITION_GENRE = i;
		}

		i = check(identifier + "_show_released");
		if (i != -1) {
			this.POSITION_RELEASED = i;
		}

		i = check(identifier + "_show_bpm");
		if (i != -1) {
			this.POSITION_BPM = i;
		}

		i = check(identifier + "_show_catalognr");
		if (i != -1) {
			this.POSITION_CATALOGNR = i;
		}

		i = check(identifier + "_show_inventorynr");
		if (i != -1) {
			this.POSITION_INVENTORYNR = i;
		}

		i = check(identifier + "_show_comment");
		if (i != -1) {
			this.POSITION_COMMENT = i;
		}

		i = check(identifier + "_show_rating");
		if (i != -1) {
			this.POSITION_RATING = i;
		}

		i = check(identifier + "_show_mediatype");
		if (i != -1) {
			this.POSITION_MEDIATYPE = i;
		}

		i = check(identifier + "_show_filename");
		if (i != -1) {
			this.POSITION_FILENAME = i;
		}

		setColumnCount(columns);

		// eek, something is wrong in here :O, please don't look at the next 30
		// lines ;)

		String[] headers = new String[columns + 1];
		if (POSITION_ARTIST != -1) {
			headers[POSITION_ARTIST] = tr("Artist");
			// headerLablels.add(POSITION_ARTIST, tr("Artist"));
		}

		if (POSITION_TRACKTITLE != -1) {
			headers[POSITION_TRACKTITLE] = tr("Trackname");
			// headerLablels.add(POSITION_TRACKTITLE, tr("Trackname"));
		}
		if (POSITION_LABEL != -1) {
			headers[POSITION_LABEL] = tr("Label");
			// headerLablels.add(POSITION_LABEL, tr("Label"));
		}

		if (POSITION_LENGTH != -1) {
			headers[POSITION_LENGTH] = tr("Length");
			// headerLablels.add(POSITION_LENGTH, tr("Length"));
		}

		if (POSITION_GENRE != -1) {
			headers[POSITION_GENRE] = tr("Genre");
			// headerLablels.add(POSITION_LENGTH, tr("Genre"));
		}

		if (POSITION_RELEASED != -1) {
			headers[POSITION_RELEASED] = tr("Released");
			// headerLablels.add(POSITION_LENGTH, tr("Released"));
		}

		if (POSITION_BPM != -1) {
			headers[POSITION_BPM] = tr("BPM");
			// headerLablels.add(POSITION_LENGTH, tr("BPM"));
		}

		if (POSITION_CATALOGNR != -1) {
			headers[POSITION_CATALOGNR] = tr("CatalogNr");
			// headerLablels.add(POSITION_LENGTH, tr("CatalogNr"));
		}

		if (POSITION_INVENTORYNR != -1) {
			headers[POSITION_INVENTORYNR] = tr("InventoryNr");
			// headerLablels.add(POSITION_LENGTH, tr("InventoryNr"));
		}

		if (POSITION_COMMENT != -1) {
			headers[POSITION_COMMENT] = tr("Comment");
			// headerLablels.add(POSITION_LENGTH, tr("Comment"));
		}

		if (POSITION_RATING != -1) {
			headers[POSITION_RATING] = tr("Rating");
			// headerLablels.add(POSITION_LENGTH, tr("Rating"));
		}

		if (POSITION_MEDIATYPE != -1) {
			headers[POSITION_MEDIATYPE] = tr("Media");
			// headerLablels.add(POSITION_LENGTH, tr("Media"));
		}

		if (POSITION_FILENAME != -1) {
			headers[POSITION_FILENAME] = tr("Filename");
			// headerLablels.add(POSITION_LENGTH, tr("Filename"));
		}

		List<String> headerLablels = new ArrayList<String>();
		for (int j = 0; j < headers.length; j++) {
			String string = headers[j];

			headerLablels.add(string);

		}

		setHeaderLabels(headerLablels);

	}

	private int check(String foo) {
		String tmp = DJProperties.getProperty(foo);
		if (tmp != null) {
			try {
				int id = Integer.parseInt(tmp);
				if (id > 0) {
					columns++;
				}
				return id;
			} catch (NumberFormatException nfe) {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * @param tracklist
	 *            displays this tracklist
	 */
	public void showTrackList(List<Track> tracklist) {
		this.clear();

		if (tracklist == null) {
			return;
		}
		shownItems.clear();
		for (Track track : tracklist) {
			addTrack(track);
		}

		resizeColumns();

	}

	/**
	 * resize columns of this table to appropriate sizes
	 */
	public void resizeColumns() {

		if (POSITION_ARTIST != -1)
			this.setColumnWidth(POSITION_ARTIST, 160);

		if (POSITION_TRACKTITLE != -1)
			this.setColumnWidth(POSITION_TRACKTITLE, 160);

		if (POSITION_LABEL != -1)
			this.resizeColumnToContents(POSITION_LABEL);

		if (POSITION_LENGTH != -1)
			this.resizeColumnToContents(POSITION_LENGTH);

		if (POSITION_GENRE != -1)
			this.resizeColumnToContents(POSITION_GENRE);

		if (POSITION_RELEASED != -1)
			this.resizeColumnToContents(POSITION_RELEASED);

		if (POSITION_BPM != -1)
			this.resizeColumnToContents(POSITION_BPM);

		if (POSITION_CATALOGNR != -1)
			this.resizeColumnToContents(POSITION_CATALOGNR);

		if (POSITION_INVENTORYNR != -1)
			this.resizeColumnToContents(POSITION_INVENTORYNR);

		if (POSITION_COMMENT != -1)
			this.resizeColumnToContents(POSITION_COMMENT);

		if (POSITION_RATING != -1)
			this.resizeColumnToContents(POSITION_RATING);

		if (POSITION_MEDIATYPE != -1)
			this.resizeColumnToContents(POSITION_MEDIATYPE);

		if (POSITION_FILENAME != -1)
			this.resizeColumnToContents(POSITION_FILENAME);
	}

	/**
	 * adds the track to this list
	 * 
	 * @param track
	 */
	public void addTrack(Track track) {

		if (shownItems.containsKey(track.getId())) {
			return;
		}

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

	/**
	 * updates the track in this list
	 * 
	 * @param track
	 */
	public void updateTrack(Track track) {

		TrackItem item = shownItems.get(track.getId());

		if (item == null)
			return;
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

	}

	/**
	 * only remove this track from the view
	 * 
	 * @param track
	 */
	public void removeTrackFromView(Track track) {
		TrackItem item = shownItems.get(track.getId());
		if (item != null) {
			takeTopLevelItem(indexOfTopLevelItem(item));
			shownItems.remove(item);
		}
	}

	/**
	 * only remove this track from the view
	 * 
	 * @param trackId
	 */
	public void removeTrackFromView(int trackId) {
		TrackItem item = shownItems.get(trackId);
		if (item != null) {
			takeTopLevelItem(indexOfTopLevelItem(item));
			shownItems.remove(item);
		}
	}

	/**
	 * remove tracks with trackIds from this view
	 * 
	 * @param trackIds
	 */
	public void removeItemsFromView(List<Integer> trackIds) {
		for (Integer id : trackIds) {
			removeTrackFromView(id);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#startDrag(int)
	 */
	@Override
	protected void startDrag(DropActions arg0) {
		QDrag drag = new QDrag(this);

		DJMimeData mimedata = new DJMimeData();
		
		List<QUrl> urls = new LinkedList<QUrl>();
	
		List<QTreeWidgetItem> selectedItems = this.selectedItems();

		List<Integer> trackIdList = new LinkedList<Integer>();
		for (QTreeWidgetItem selectedItem : selectedItems) {
			if (selectedItem instanceof TrackItem) {
				TrackItem trackItem = (TrackItem) selectedItem;
				trackIdList.add (trackItem.getTrack().getId());
				
				if (trackItem.getTrack().isPlayable()){
					urls.add( QUrl.fromLocalFile(trackItem.getTrack().getFilename()));
				}
				
			}
		}
		
		mimedata.setTracklist(trackIdList);
		mimedata.setUrls(urls);
		mimedata.setSource(getUniqueIdentifier());
		
		drag.setMimeData(mimedata);
		QPixmap foo = new QPixmap("classpath://images/dragndrop-media-icon.png");
		drag.setPixmap(foo);

		
		drag.exec();
	}

	@SuppressWarnings("unused")
	protected void itemDoubleClickedAction(QTreeWidgetItem item, Integer column) {
		if (item instanceof TrackItem) {
			signalHub.signalCurrentTrackChanged(((TrackItem) item).getTrack());

		}
	}

	/* ========= Context Menu Actions ================ */
	@SuppressWarnings("unused")
	private void contextMenuSetAsCurrentTrackAction() {
		signalHub.signalCurrentTrackChanged(selectedItem.getTrack());
	}

	@SuppressWarnings("unused")
	private void contextMenuDeleteTrackFromHDAction() {
		QMessageBox.StandardButton ret = QMessageBox
				.warning(
						this,
						tr("Delete Track from HD"),
						tr("This track will be removed from the Library, all playlists and will be deleted from your harddrive.\n"
								+ "Continue?"),
						new QMessageBox.StandardButtons(
								QMessageBox.StandardButton.Yes,
								QMessageBox.StandardButton.No),
						QMessageBox.StandardButton.Yes);

		
		
		if (ret == QMessageBox.StandardButton.Yes) {
		
			if (MediaImporter.deleteTrackFromHD(selectedItem.getTrack())){
				DBConnection.getInstance().deleteTrack(
						selectedItem.getTrack().getId());

				
				ArrayList<Integer> trackIds = new ArrayList<Integer>(1);
				trackIds.add(selectedItem.getTrack().getId());
				signalHub.signalTracksDeleted(trackIds);
	
			}else{
				QMessageBox
				.critical(
						this,
						tr("Delete Track from HD"),
						tr("This Track was not deleted. An error occured..."));
			}
			
					}
	}

	@SuppressWarnings("unused")
	protected void contextMenuDeleteTrackFromLibraryAction() {
		QMessageBox.StandardButton ret = QMessageBox
				.warning(
						this,
						tr("Delete Track from Library"),
						tr("This track will be removed from the Library and all playlists.\n"
								+ "Continue?"),
						new QMessageBox.StandardButtons(
								QMessageBox.StandardButton.Yes,
								QMessageBox.StandardButton.No),
						QMessageBox.StandardButton.Yes);

		if (ret == QMessageBox.StandardButton.Yes) {
			DBConnection.getInstance().deleteTrack(
					selectedItem.getTrack().getId());

			ArrayList<Integer> trackIds = new ArrayList<Integer>(1);
			trackIds.add(selectedItem.getTrack().getId());
			signalHub.signalTracksDeleted(trackIds);
		}
	}

	@SuppressWarnings("unused")
	private void contextMenuAddTrackToTrackstackAction() {
		TrackStackWidget.getInstance(signalHub).addTrack(
				selectedItem.getTrack());
		TrackStackWidget.getInstance(signalHub).resizeColumns();
	}

	@SuppressWarnings("unused")
	private void contextMenuBrainstormTrackAction() {
		BrainStormWidget.showBrainstorm(signalHub, selectedItem.getTrack()
				.getId());
	}

	@SuppressWarnings("unused")
	private void contextMenuEditTrackAction() {
		QEditTrackWidget
				.getInstance(signalHub, selectedItem.getTrack().getId()).show();
	}

	
	protected QMenu getRatingMenu() {
		if (myRatingMenu != null) {
			return myRatingMenu;
		}
		myRatingMenu = new QMenu(tr("My Rating"));

		for (int i = 0; i <= 5; i++) {
			QAction action = new QAction("" + i, this);
			action.triggered.connect(this, "setTrackRating" + i + "()");
			myRatingMenu.addAction(action);
		}
 
		return myRatingMenu;
	}
	
	

	protected QMenu getLastFMMenu() {
		if (myLastFMMenu != null) {
			return myLastFMMenu;
		}
		myLastFMMenu = new QMenu(tr("Last.fm"));
	 
		QAction showBrowserAction = new QAction(tr("show Last.fm browser"),this);
		showBrowserAction.triggered.connect(LastFMBrowser.getInstance(), "showWindow()");
		myLastFMMenu.addAction(showBrowserAction);
		
		myLastFMMenu.addSeparator();
		
		QAction browseRelatedArtistAction = new QAction(tr("browse related artists"),this);
		browseRelatedArtistAction.triggered.connect(this, "browseRelatedArtists()");
		myLastFMMenu.addAction(browseRelatedArtistAction);
	 
		QAction browseTopTracksForArtistAction = new QAction(tr("browse top tracks of artist"),this);
		browseTopTracksForArtistAction.triggered.connect(this, "browseTopTracksForArtist()");
		myLastFMMenu.addAction(browseTopTracksForArtistAction);
		

		QAction browseTopTracksForGenreAction = new QAction(tr("browse top tracks for genre"),this);
		browseTopTracksForGenreAction.triggered.connect(this, "browseTopTracksForTag()");
		myLastFMMenu.addAction(browseTopTracksForGenreAction);
		
		return myLastFMMenu;
	}
	
	

	@SuppressWarnings("unused")
	private void browseRelatedArtists() {
		LastFMBrowser.getInstance().showWindow();
		LastFMBrowser.getInstance().browseRelatedArtists(selectedItem.getTrack().getArtist());
}
	
	@SuppressWarnings("unused")
	private void browseTopTracksForArtist() {
		LastFMBrowser.getInstance().showWindow();
		LastFMBrowser.getInstance().browseTopTracksForArtist(selectedItem.getTrack().getArtist());
	}
	
	@SuppressWarnings("unused")
	private void browseTopTracksForTag() {
		LastFMBrowser.getInstance().showWindow();
		Track selected = selectedItem.getTrack();
		String genre = selected.getGenreString();
		if (genre != null && !genre.equals("")) {
			LastFMBrowser.getInstance().browseTopTracksForTag(genre);	
		}
		
	}

	private void setTrackRating(int i) {
		try {

			Track updatedTrack = selectedItem.getTrack();

			DBConnection.getInstance().update(
					"update tracks set rating=' " + i + "' where id='"
							+ updatedTrack.getId() + "'");
			updatedTrack.setRating(i);

			signalHub.signalTrackUpdated(updatedTrack);
		} catch (SQLException e) {
			DBConnection.getInstance().DBError(e);
		}
	}

	@SuppressWarnings("unused")
	private void setTrackRating0() {
		setTrackRating(0);
	}

	@SuppressWarnings("unused")
	private void setTrackRating1() {
		setTrackRating(1);
	}

	@SuppressWarnings("unused")
	private void setTrackRating2() {
		setTrackRating(2);
	}

	@SuppressWarnings("unused")
	private void setTrackRating3() {
		setTrackRating(3);
	}

	@SuppressWarnings("unused")
	private void setTrackRating4() {
		setTrackRating(4);
	}

	@SuppressWarnings("unused")
	private void setTrackRating5() {
		setTrackRating(5);
	}

}
