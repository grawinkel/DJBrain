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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.db.GenreCache;
import net.z0id.djbrain.imexport.ID3TagHelper;
import net.z0id.djbrain.objects.Track;

import org.apache.log4j.Logger;

import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QCompleter;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QIntValidator;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpinBox;

/**
 * @author meatz 
 * 
 * this widget is singleton. it is only created once, and show on
 *         demand, and hidden if closed it is used for track imports into the
 *         database, where each track should be review before adding.
 */
public class CheckNewTrackWidget extends AbstractTrackEditWidget {

	private static Logger logger = Logger.getLogger(CheckNewTrackWidget.class);

	private static CheckNewTrackWidget instance;

	private List<Track> checkTracks;

	private List<Track> newTracksToAdd;
	private List<Track> newTracksToWriteID3;

	private int addToPlaylistId;
	
	private int currentTrack;

	private QPushButton skipButton;

	/**
	 * @param signalHub
	 * @return an instance
	 */
	public static CheckNewTrackWidget getInstance(SignalHub signalHub) {

		if (instance == null) {
			instance = new CheckNewTrackWidget(signalHub);
		} else {

			instance.currentTrack = 0;
		}

		instance.refreshGenreBox();

		return instance;

	}

	/**
	 * @param signalCenter
	 * @param trackId
	 */
	private CheckNewTrackWidget(SignalHub signalHub) {
		super(signalHub);
		newTracksToAdd = new ArrayList<Track>();
	 newTracksToWriteID3 = new ArrayList<Track>();
	}

	/**
	 * 
	 */
	void setTrack(Track checkTrack) {
		artistLineEdit.setText(checkTrack.getArtist());
		tracknameLineEdit.setText(checkTrack.getTrackname());
		labelLineEdit.setText(checkTrack.getLabel());
		lengthLineEdit.setText(checkTrack.getLength());

		if (GenreCache.containsId(checkTrack.getGenreId())) {
			int index = genreComboBox.findText(GenreCache
					.getGenreForId(checkTrack.getGenreId()));
			genreComboBox.setCurrentIndex(index);
		}

		releasedLineEdit.setText(checkTrack.getReleased() + "");
		bpmLineEdit.setText(checkTrack.getBpm() + "");
		catalogLineEdit.setText(checkTrack.getCatalognr());
		inventoryLineEdit.setText(checkTrack.getInventorynr());
		commentLineEdit.setText(checkTrack.getComment());
		ratingSpinBox.setValue(checkTrack.getRating());
		String mediaType = checkTrack.getMediatype();

		if (mediaType.equals("Vinyl")) {
			mediatypeCombobox.setCurrentIndex(0);
		} else if (mediaType.equals("Cd")) {
			mediatypeCombobox.setCurrentIndex(1);
		} else {
			mediatypeCombobox.setCurrentIndex(2);
			
		}

		filenameLineEdit.setText(checkTrack.getFilename());

		setWindowTitle(tr("Check Track: " + (currentTrack + 1) + " of "
				+ checkTracks.size()));
		
		File file = new File( checkTrack.getFilename() );
		if (mediaType.equals("Mp3") && file.canWrite()) {
			writeID3Checkbox.setEnabled(true);
			writeID3Label.setEnabled(true);
		}else{
			writeID3Checkbox.setEnabled(false);
			writeID3Label.setEnabled(false);
		}
		
		update();
	}

	@SuppressWarnings("unused")
	private void actionButtonClicked() {

		Track track = new Track();

		track.setArtist(artistLineEdit.text());
		track.setTrackname(tracknameLineEdit.text());
		track.setLabel(labelLineEdit.text());
		track.setLength(lengthLineEdit.text());

		String genre = genreComboBox.currentText();
		if (GenreCache.containsGenre(genre)) {
			track.setGenreId(GenreCache.getIDForGenre(genre));

		} else {
			GenreCache.addGenre(genre);
			track.setGenreId(GenreCache.getIDForGenre(genre));
		}

		track.setReleased(Integer.parseInt(releasedLineEdit.text()));
		track.setBpm(Integer.parseInt(bpmLineEdit.text()));
		track.setCatalognr(catalogLineEdit.text());
		track.setInventorynr(inventoryLineEdit.text());
		track.setComment(commentLineEdit.text());
		track.setRating(Integer.parseInt(ratingSpinBox.text()));
		track.setMediatype(mediatypeCombobox.currentText());
		track.setFilename(filenameLineEdit.text());

		if (writeID3Checkbox.isChecked()){
			newTracksToWriteID3.add(track);
		}
		newTracksToAdd.add(track);

		if (++currentTrack < checkTracks.size()) {
			setTrack(checkTracks.get(currentTrack));

		} else {
			showSurvey();
		}
	}

	@SuppressWarnings("unused")
	private void skipButtonClicked() {

		if (++currentTrack < checkTracks.size()) {
			setTrack(checkTracks.get(currentTrack));

		} else {
			showSurvey();

		}

	}

	@SuppressWarnings("unused")
	private void closeButtonClicked() {

		showSurvey();
		this.hide();
		this.clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.AbstractTrackEditWidget#initButtons()
	 */
	@Override
	void initButtons() {
		actionButton = new QPushButton(tr("&Add Track"));
		actionButton.setDefault(true);
		actionButton.clicked.connect(this, "actionButtonClicked()");

		skipButton = new QPushButton(tr("&Skip Track"));
		skipButton.clicked.connect(this, "skipButtonClicked()");

		cancelButton = new QPushButton(tr("&Close"));
		cancelButton.clicked.connect(this, "closeButtonClicked()");
	}

	@Override
	void initStuff() {
		// setWindowTitle(tr("Update Track Information"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.AbstractTrackEditWidget#initLines()
	 */
	@Override
	void initLines() {
		artistLineEdit = new QLineEdit();

		tracknameLineEdit = new QLineEdit();

		labelLineEdit = new QLineEdit();

		lengthLineEdit = new QLineEdit();
		lengthLineEdit.setInputMask("00:00");
		lengthLineEdit.setText("0000");
		lengthLineEdit.setCursorPosition(0);

		genreComboBox = new QComboBox();
		genreComboBox.setCompleter(new QCompleter());
		genreComboBox.setDuplicatesEnabled(false);
		genreComboBox.setEditable(true);

		releasedLineEdit = new QLineEdit();
		releasedLineEdit.setInputMask("0000");

		bpmLineEdit = new QLineEdit();
		bpmLineEdit.setValidator(new QIntValidator(bpmLineEdit));

		catalogLineEdit = new QLineEdit();

		inventoryLineEdit = new QLineEdit();

		commentLineEdit = new QLineEdit();

		ratingSpinBox = new QSpinBox();
		ratingSpinBox.setRange(0, 5);
		ratingSpinBox.setSingleStep(1);

		mediatypeCombobox = new QComboBox();
		mediatypeCombobox.addItem(tr("Vinyl"));
		mediatypeCombobox.addItem(tr("Cd"));
		mediatypeCombobox.addItem(tr("Mp3"));
		filenameLineEdit = new QLineEdit();
	}

	@Override
	void dolayout() {
		final QGridLayout newtrackLayout = new QGridLayout();
		newtrackLayout.addWidget(artistLabel, 0, 0);
		newtrackLayout.addWidget(artistLineEdit, 0, 1);
		newtrackLayout.addWidget(tracknameLabel, 1, 0);
		newtrackLayout.addWidget(tracknameLineEdit, 1, 1);
		newtrackLayout.addWidget(labelLabel, 2, 0);
		newtrackLayout.addWidget(labelLineEdit, 2, 1);

		newtrackLayout.addWidget(lengthLabel, 3, 0);
		newtrackLayout.addWidget(lengthLineEdit, 3, 1);

		newtrackLayout.addWidget(genreLabel, 4, 0);
		newtrackLayout.addWidget(genreComboBox, 4, 1);

		newtrackLayout.addWidget(releasedLabel, 5, 0);
		newtrackLayout.addWidget(releasedLineEdit, 5, 1);

		newtrackLayout.addWidget(bpmLabel, 6, 0);
		newtrackLayout.addWidget(bpmLineEdit, 6, 1);

		newtrackLayout.addWidget(catalognrLabel, 7, 0);
		newtrackLayout.addWidget(catalogLineEdit, 7, 1);

		newtrackLayout.addWidget(inventoryLabel, 8, 0);
		newtrackLayout.addWidget(inventoryLineEdit, 8, 1);

		newtrackLayout.addWidget(commentLabel, 9, 0);
		newtrackLayout.addWidget(commentLineEdit, 9, 1);

		newtrackLayout.addWidget(ratingLabel, 10, 0);
		newtrackLayout.addWidget(ratingSpinBox, 10, 1);

		newtrackLayout.addWidget(mediatypeLabel, 11, 0);
		newtrackLayout.addWidget(mediatypeCombobox, 11, 1);

		newtrackLayout.addWidget(filenameLabel, 12, 0);
		newtrackLayout.addWidget(filenameLineEdit, 12, 1);

		newtrackGroup.setLayout(newtrackLayout);
		final QGridLayout layout = new QGridLayout();
		layout.addWidget(newtrackGroup, 0, 0, 1, 3);
		layout.addWidget(actionButton, 1, 0);
		layout.addWidget(skipButton, 1, 1);
		layout.addWidget(cancelButton, 1, 2);

		//	      
		setLayout(layout);
	}

	

	private void showSurvey() {

		for (Track track : instance.newTracksToAdd) {

			DBConnection.getInstance().insertTrack(track);

			if (addToPlaylistId != -1) {
				
				int trackId = DBConnection.getInstance().getHighestTrackId();
				if (DBConnection.getInstance().insertTrackinPlaylist(
						addToPlaylistId, trackId )) {
					logger.debug("adding trackId:" +trackId
							+ " to playlist: " + addToPlaylistId);
				} else {
					logger.error("adding trackId:" + trackId
							+ " to playlist: " + addToPlaylistId + " FAILED!");
				}
			}
		}
		
		for (Track track : instance.newTracksToWriteID3) {
			try {
				ID3TagHelper.updateTagsForTrack(track);
			} catch (Exception e) {
				logger.error(e.getMessage());
				QMessageBox.critical(this, tr("Error!"),
				tr(e.getMessage()));
			}  
		}
		

		QMessageBox.information(this, tr("Import survey!"), tr(newTracksToAdd
				.size()
				+ " out of " + checkTracks.size() + " were added."));

		this.hide();
		this.clear();
		signalHub.signalTrackCountChanged();
	}

	/**
	 * @param signalHub
	 * @param tracklist
	 * @param playlistId 
	 * @param addToPlaylistId
	 * @return singleton instance of CheckNewTrackWidget
	 */
	public static CheckNewTrackWidget checkTracks(SignalHub signalHub,
			List<Track> tracklist, int playlistId) {

		if (instance == null) {
			instance = new CheckNewTrackWidget(signalHub);
		}
		instance.checkTracks = tracklist;
		instance.addToPlaylistId = playlistId;
		instance.currentTrack = 0;
		instance.newTracksToAdd.clear();

		instance.setTrack(instance.checkTracks.get(0));

		return instance;

	}

	
 
	
	
	/**
	 * @param signalHub
	 * @param tracklist
	 * @return singleton instance of CheckNewTrackWidget
	 */
	public static CheckNewTrackWidget checkTracks(SignalHub signalHub,
			List<Track> tracklist) {
		return checkTracks(signalHub, tracklist, -1);// add to library only
	}



}
