/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.currenttrack;

import java.util.LinkedList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.QEditTrackWidget;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.brainstorm.BrainStormWidget;
import net.z0id.djbrain.gui.tracklist.SuggestedTrackList;
import net.z0id.djbrain.gui.tracklist.SuggestionBox;
import net.z0id.djbrain.objects.Track;

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragLeaveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QToolButton;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class CurrentTrackWidget extends QWidget {

	private TrackHistory trackHistory;

	private SignalHub signalHub;

	private Track currentTrack = null;;

	private DisplayedTrackWidget display;

	private QToolButton brainstormQToolButton;

	private QToolButton editTrackQToolButton;

	private QToolButton trackhistoryBackQToolButton;

	private QToolButton trackhistoryForwardQToolButton;

	private SuggestedTrackList suggestedTrackList;

	private SuggestionBox suggestionBox;

	/**
	 * @param signalHub
	 */
	public CurrentTrackWidget(SignalHub signalHub) {
		this.signalHub = signalHub;

		init();

	}

	private void init() {

		suggestionBox = new SuggestionBox(signalHub);
		suggestedTrackList = new SuggestedTrackList(signalHub, suggestionBox);

		signalHub.currentTrackChanged.connect(this, "setCurrentTrack(Track)");
		signalHub.trackUpdated.connect(this, "updateTrack(Track)");
		signalHub.tracksDeleted.connect(this, "tracksDeleted(List)");
		setAcceptDrops(true);
		setUpdatesEnabled(true);

		display = new DisplayedTrackWidget();

		resetView();

		createToolButtons();

		QGridLayout layout = new QGridLayout();
		int row = 0;
		layout.addWidget(brainstormQToolButton, row, 0);
		layout.addWidget(editTrackQToolButton, row, 1);

		layout.addWidget(trackhistoryBackQToolButton, row, 2);

		layout.addWidget(trackhistoryForwardQToolButton, row, 3);

		layout.addWidget(display, row, 4);

//		layout.addWidget(new MediaPlayer(signalHub), row, 5);

		row++;

		layout.addWidget(suggestedTrackList, row, 0, 1, 5);
		layout.addWidget(suggestionBox, row, 5);

		// set some stretch

		layout.setColumnMinimumWidth(0, 10);
		layout.setColumnStretch(0, 0);

		layout.setColumnMinimumWidth(1, 10);
		layout.setColumnStretch(1, 0);

		layout.setColumnMinimumWidth(2, 10);
		layout.setColumnStretch(2, 0);

		layout.setColumnMinimumWidth(3, 10);
		layout.setColumnStretch(3, 0);

		layout.setColumnMinimumWidth(4, 200);
		layout.setColumnStretch(4, 10);

		layout.setColumnMinimumWidth(5, 200);
		layout.setColumnStretch(5, 0);

		layout.setMargin(1);

		setLayout(layout);

		trackHistory = new TrackHistory();
	}

	private void resetView() {
		display.resetView();
	}

	private void createToolButtons() {

		brainstormQToolButton = new QToolButton();
		brainstormQToolButton.setIcon(new QIcon(
				"classpath://images/brainstorm.png"));
		brainstormQToolButton.setAutoRaise(true);
		brainstormQToolButton.setIconSize(new QSize(25, 25));
		brainstormQToolButton.setToolTip("Do Brainstorm (Hotkey Ctrl+B");
		brainstormQToolButton.clicked.connect(this, "doBrainstorm()");

		editTrackQToolButton = new QToolButton();
		editTrackQToolButton.setIcon(new QIcon(
				"classpath://images/track_edit.png"));
		editTrackQToolButton.setIconSize(new QSize(25, 25));
		editTrackQToolButton.setAutoRaise(true);
		editTrackQToolButton.setToolTip("Edit current Track");
		editTrackQToolButton.clicked.connect(this, "editTrack()");

		trackhistoryBackQToolButton = new QToolButton();
		trackhistoryBackQToolButton.setIcon(new QIcon(
				"classpath://images/browser/go-previous.png"));
		trackhistoryBackQToolButton.setIconSize(new QSize(15, 15));
		trackhistoryBackQToolButton.setAutoRaise(true);
		trackhistoryBackQToolButton
				.setToolTip(tr("back to last selected Track"));
		trackhistoryBackQToolButton.clicked.connect(this, "trackHistoryBack()");
		trackhistoryBackQToolButton.setEnabled(false);

		trackhistoryForwardQToolButton = new QToolButton();
		trackhistoryForwardQToolButton.setIcon(new QIcon(
				"classpath://images/browser/go-next.png"));
		trackhistoryForwardQToolButton.setIconSize(new QSize(15, 15));
		trackhistoryForwardQToolButton.setAutoRaise(true);
		trackhistoryForwardQToolButton
				.setToolTip(tr("forward to last selected Track"));
		trackhistoryForwardQToolButton.clicked.connect(this,
				"trackHistoryForward()");
		trackhistoryForwardQToolButton.setEnabled(false);

	}

	@SuppressWarnings("unused")
	private void trackHistoryForward() {
		if (trackHistory.isForwardAvailable()) {
			this.setCurrentTrack(trackHistory.getForwardTrack());

		} else {
			trackhistoryForwardQToolButton.setEnabled(false);
		}

	}

	@SuppressWarnings("unused")
	private void trackHistoryBack() {
		if (trackHistory.isBackAvailable()) {
			this.setCurrentTrack(trackHistory.getBackTrack());
		} else {
			trackhistoryBackQToolButton.setEnabled(false);
		}
	}

	@SuppressWarnings("unused")
	private void editTrack() {
		if (this.currentTrack != null) {
			QEditTrackWidget.getInstance(signalHub, this.currentTrack.getId())
					.show();
		} else {
			signalHub.setStatusBarText(tr("Error: No current track selected"));
		}
	}

	@SuppressWarnings("unused")
	private void doBrainstorm() {
		if (this.currentTrack != null) {
			BrainStormWidget.showBrainstorm(signalHub, currentTrack.getId());

		} else {
			signalHub.setStatusBarText(tr("Error: No current track selected"));
		}
	}

	@SuppressWarnings("unused")
	private void setCurrentTrack(Track track) {
		if (track == null) {
			resetView();
		} else {

			trackHistory.addLastTrack(this.currentTrack);

			this.currentTrack = track;
			display.setCurrentTrack(track);

			if (trackHistory.isBackAvailable()) {
				trackhistoryBackQToolButton.setEnabled(true);
			} else {
				trackhistoryBackQToolButton.setEnabled(false);
			}

			if (trackHistory.isForwardAvailable()) {
				trackhistoryForwardQToolButton.setEnabled(true);
			} else {
				trackhistoryForwardQToolButton.setEnabled(false);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QAbstractItemView#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
	 */
	@Override
	protected void dragEnterEvent(QDragEnterEvent event) {

		QMimeData d = event.mimeData();
		if (d instanceof DJMimeData) {
			DJMimeData djMimeData = (DJMimeData) d;

			if (djMimeData.hasTracks()) {

				event.acceptProposedAction();
				signalHub.setStatusBarText(tr("set current Track"));
			} else {
				event.ignore();
			}
		}

		// if (d != null && d.hasFormat("text/plain")) {
		//
		// if (d.text().startsWith(DJBrain.DDTYPE_TRACK + "|")) {
		//
		// event.acceptProposedAction();
		// signalHub.setStatusBarText(tr("set current Track"));
		// } else {
		// event.ignore();
		// }
		// }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QWidget#dragLeaveEvent(com.trolltech.qt.gui.QDragLeaveEvent)
	 */
	@Override
	protected void dragLeaveEvent(QDragLeaveEvent arg0) {
		signalHub.setStatusBarText(tr("Ready"));
		super.dragLeaveEvent(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QListView#dropEvent(com.trolltech.qt.gui.QDropEvent)
	 */
	@Override
	protected void dropEvent(QDropEvent event) {
		event.acceptProposedAction();

		QMimeData data = event.mimeData();
		if (data instanceof DJMimeData) {
			DJMimeData djMimeData = (DJMimeData) data;
			if (djMimeData.hasTracks()) {
				signalHub.signalCurrentTrackChanged(DBConnection.getInstance()
						.getTrackForId(djMimeData.getFirstTrackId()));
			}
		}

	}

	@SuppressWarnings("unused")
	private void tracksDeleted(List<Integer> trackIds) {
		if (currentTrack != null) {
			if (trackIds.contains(currentTrack.getId())) {
				// also resets this view
				signalHub.signalCurrentTrackChanged(null);
			}
		}
	}

	@SuppressWarnings("unused")
	private void updateTrack(Track track) {
		if (currentTrack != null) {
			if (track.getId() == this.currentTrack.getId()) {
				display.setCurrentTrack(track);
			}
		}
	}

	private class TrackHistory {
		private LinkedList<Track> trackHistory;

		private int trackHistoryPointer = -1;

		// private final int MAXHISTORY = 25;
		private final int MAXHISTORY = 6;

		private boolean backRequested = false;

		private boolean forwardRequested = false;

		/**
		 * 
		 */
		public TrackHistory() {
			trackHistory = new LinkedList<Track>();
		}

		/**
		 * works not exactly as supposed, but does it job without errors now...
		 * 
		 * @param track
		 */
		public void addLastTrack(Track track) {
			if (track == null) {
				return;
			}
			if (backRequested) {
				backRequested = false;

			} else if (forwardRequested) {
				forwardRequested = false;

			} else {
				// preventing double adding of same track (cause by
				// doublesetting a current track with the same track

				if (trackHistoryPointer >= 0) {
					if (trackHistory.get(trackHistoryPointer).equals(track)) {
						return;
					}
				}

				trackHistory.add(++trackHistoryPointer, track);

				if (trackHistory.size() > MAXHISTORY) {
					trackHistory.removeFirst();

					trackHistoryPointer--;
				}

			}

			//			 
			// for (int i = 0; i < trackHistory.size(); i++) {
			// System.out.println(i + " - " + trackHistory.get(i).getId()
			// + " - " + trackHistory.get(i).getTrackname());
			//
			// }
			// System.out.println("-----");
			// System.out.println("pointer:" + trackHistoryPointer);

		}

		/**
		 * @return if a next track is available and the button should be enabled
		 */
		public boolean isForwardAvailable() {

			if (trackHistory.size() > trackHistoryPointer + 1) {
				return true;
			}
			return false;
		}

		/**
		 * @return the next track
		 */
		public Track getForwardTrack() {
			forwardRequested = true;
			return trackHistory.get(++trackHistoryPointer);
		}

		/**
		 * @return if a back track is available and the button should be enabled
		 */
		public boolean isBackAvailable() {
			if (trackHistory.size() > 0 && trackHistoryPointer > 0) {
				return true;
			}
			return false;
		}

		/**
		 * @return the last track
		 */
		public Track getBackTrack() {
			backRequested = true;
			return trackHistory.get(--trackHistoryPointer);
		}

	}

	public SuggestedTrackList getSuggestedTrackList() {

		return suggestedTrackList;
	}

}
