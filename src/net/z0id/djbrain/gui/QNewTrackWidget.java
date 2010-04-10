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

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.db.GenreCache;
import net.z0id.djbrain.objects.Track;

import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QCompleter;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QIntValidator;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz this widget is singleton. it is only created once, and show on
 *         demand, and hidden if closed
 */
public class QNewTrackWidget extends AbstractTrackEditWidget {

	private static QNewTrackWidget instance;

	/**
	 * if its -1 then the track is only inserted into the library, else its also
	 * added to this playlist
	 */
	private int playlistId = -1;

	/**
	 * opens the dialouge, and adds this new track not only to the library, but
	 * also to the specified playlist
	 * 
	 * @param signalHub
	 * @param playlistId
	 * @return instance of QWidget
	 */
	public static QWidget getInstance(SignalHub signalHub, int playlistId) {
		if (instance == null) {
			instance = new QNewTrackWidget(signalHub);
		}
		instance.playlistId = playlistId;
		instance.refreshGenreBox();
		return instance;

	}

	/**
	 * @param signalHub
	 * @return instance of QWidget
	 */
	public static QNewTrackWidget getInstance(SignalHub signalHub) {
		if (instance == null) {
			instance = new QNewTrackWidget(signalHub);
		}
		instance.refreshGenreBox();
		return instance;
	}

	private QNewTrackWidget(SignalHub signalHub) {
		super(signalHub);
	}

	/**
	 * 
	 */
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
//		genreComboBox.setAutoCompletionCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive);
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
		ratingSpinBox.setValue(0);

		mediatypeCombobox = new QComboBox();
		mediatypeCombobox.addItem(tr("Vinyl"));
		mediatypeCombobox.addItem(tr("Cd"));
		mediatypeCombobox.addItem(tr("Mp3"));

		filenameLineEdit = new QLineEdit();
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

		try {
			track.setReleased(Integer.parseInt(releasedLineEdit.text()));
		} catch (NumberFormatException e) {
			track.setReleased(0);
		}

		try {
			track.setBpm(Integer.parseInt(bpmLineEdit.text()));
		} catch (NumberFormatException e) {
			track.setBpm(0);
		}

		track.setCatalognr(catalogLineEdit.text());
		track.setInventorynr(inventoryLineEdit.text());
		track.setComment(commentLineEdit.text());
		track.setRating(Integer.parseInt(ratingSpinBox.text()));
		track.setMediatype(mediatypeCombobox.currentText());
		track.setFilename(filenameLineEdit.text());


		// DBConnection.getInstance().insertTrackinPlaylist(playlistId,,
		// trackId);

		boolean allOk = DBConnection.getInstance().insertTrack(track);

		if (allOk) {
			if (playlistId != -1) {
				int trackId = DBConnection.getInstance().getHighestTrackId();
				if (trackId != -1) {
					allOk = DBConnection.getInstance().insertTrackinPlaylist(
							playlistId, trackId);
				} else {
					allOk = false;
				}
			}

			if (allOk) {
				signalHub.signalTrackCountChanged();

			} else {
				QMessageBox.critical(this, tr("Error!"),
						tr("The Track could not be added, see log for help"));
			}
		}

		playlistId = -1;
		this.hide();
		this.clear();

	}

	@SuppressWarnings("unused")
	private void cancelButtonClicked() {

		this.hide();
		playlistId = -1;
		this.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.z0id.djbrain.gui.AbstractTrackEditWidget#initButtons()
	 */
	@Override
	void initButtons() {
		actionButton = new QPushButton(tr("&Insert Track"));
		actionButton.clicked.connect(this, "actionButtonClicked()");

		cancelButton = new QPushButton(tr("&Cancel"));
		cancelButton.clicked.connect(this, "cancelButtonClicked()");
	}

	void initStuff() {
		setWindowTitle(tr("Add new Track"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}

}
