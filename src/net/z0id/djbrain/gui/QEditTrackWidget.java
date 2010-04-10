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

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.db.GenreCache;
import net.z0id.djbrain.imexport.ID3TagHelper;
import net.z0id.djbrain.objects.Track;

import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QCompleter;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QIntValidator;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpinBox;

/**
 * @author meatz this widget is singleton. it is only created once, and show on
 *         demand, and hidden if closed
 */
public class QEditTrackWidget extends AbstractTrackEditWidget {

	private int trackid;

	private static QEditTrackWidget instance;

	/**
	 * @param signalHub
	 * @param trackId
	 * @return an instance
	 */
	public static QEditTrackWidget getInstance(SignalHub signalHub, int trackId) {
		if (instance == null) {
			instance = new QEditTrackWidget(signalHub, trackId);
		}
		instance.trackid = trackId;
		instance.refreshGenreBox();
		instance.initEditTrackLines();

		return instance;

	}

	/**
	 * @param signalCenter
	 * @param trackId
	 */
	private QEditTrackWidget(SignalHub signalHub, int trackId) {
		super(signalHub);
		this.trackid = trackId;
	}

	/**
	 * 
	 */
	void initEditTrackLines() {

		Track track = DBConnection.getInstance().getTrackForId(trackid);

		if (track == null) {
			QMessageBox.critical(this, tr("Error!"),
					tr("TheTrack could not be read from database"));
			return;
		}

		artistLineEdit.setText(track.getArtist());

		tracknameLineEdit.setText(track.getTrackname());

		labelLineEdit.setText(track.getLabel());

		lengthLineEdit.setText(track.getLength());

		if (GenreCache.containsId(track.getGenreId())) {
			int index = genreComboBox.findText(GenreCache.getGenreForId(track
					.getGenreId()));
			genreComboBox.setCurrentIndex(index);
		}

		releasedLineEdit.setText(track.getReleased() + "");

		bpmLineEdit.setText(track.getBpm() + "");

		catalogLineEdit.setText(track.getCatalognr());

		inventoryLineEdit.setText(track.getInventorynr());

		commentLineEdit.setText(track.getComment());

		ratingSpinBox.setValue(track.getRating());

		String mediaType = track.getMediatype();

		if (mediaType.equals("Vinyl")) {
			mediatypeCombobox.setCurrentIndex(0);
		} else if (mediaType.equals("Cd")) {
			mediatypeCombobox.setCurrentIndex(1);
		} else {
			mediatypeCombobox.setCurrentIndex(2);
		}
		filenameLineEdit.setText(track.getFilename());

		File file = new File(track.getFilename());
		if (mediaType.equals("Mp3") && file.canWrite()) {
			writeID3Checkbox.setEnabled(true);
			writeID3Label.setEnabled(true);
		} else {
			writeID3Checkbox.setEnabled(false);
			writeID3Label.setEnabled(false);
		}

	}

	@SuppressWarnings("unused")
	private void actionButtonClicked() {

		Track track = new Track();
		track.setId(trackid);
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

		if (!DBConnection.getInstance().updateTrack(track)) {
			QMessageBox.critical(this, tr("Error!"),
					tr("The Track could not be updated in the Database"));
		} else {

			if (writeID3Checkbox.isChecked()) {
				try {
					ID3TagHelper.updateTagsForTrack(track);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());

					QMessageBox
							.critical(this, tr("Error!"), tr(e.getMessage()));
				}
			}

			signalHub.signalTrackUpdated(track);
		}

		this.hide();
		this.clear();
	}

	@SuppressWarnings("unused")
	private void cancelButtonClicked() {
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
		actionButton = new QPushButton(tr("&Update Track"));
		actionButton.clicked.connect(this, "actionButtonClicked()");

		cancelButton = new QPushButton(tr("&Cancel"));
		cancelButton.clicked.connect(this, "cancelButtonClicked()");
	}

	void initStuff() {
		setWindowTitle(tr("Update Track Information"));
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
//		genreComboBox
//				.setAutoCompletionCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive);
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

}
