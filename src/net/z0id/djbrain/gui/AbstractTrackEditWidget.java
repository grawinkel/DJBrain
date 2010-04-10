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

import java.util.Enumeration;

import net.z0id.djbrain.db.GenreCache;

import org.apache.log4j.Logger;

import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public abstract class AbstractTrackEditWidget extends QWidget {

	protected static Logger logger = Logger.getLogger(AbstractTrackEditWidget.class);
	
	protected QLineEdit artistLineEdit;

	protected QLineEdit tracknameLineEdit;

	protected QLineEdit labelLineEdit;

	protected QLineEdit lengthLineEdit;

	protected QComboBox genreComboBox;

	protected QLineEdit releasedLineEdit;

	protected QLineEdit bpmLineEdit;

	protected QLineEdit catalogLineEdit;

	protected QLineEdit inventoryLineEdit;

	protected QLineEdit commentLineEdit;

	protected QSpinBox ratingSpinBox;

	protected QComboBox mediatypeCombobox;

	protected QLineEdit filenameLineEdit;

	protected QGroupBox newtrackGroup;

	protected QLabel artistLabel;

	protected QLabel tracknameLabel;

	protected QLabel labelLabel;

	protected QLabel lengthLabel;

	protected QLabel genreLabel;

	protected QLabel releasedLabel;

	protected QLabel bpmLabel;

	protected QLabel catalognrLabel;

	protected QLabel inventoryLabel;

	protected QLabel commentLabel;

	protected QLabel ratingLabel;

	protected QLabel mediatypeLabel;

	protected QLabel filenameLabel;

	protected QLabel writeID3Label;
	
	protected QCheckBox writeID3Checkbox;
	
	QPushButton actionButton;

	QPushButton cancelButton;

	SignalHub signalHub;

	/**
	 * @param signalHub
	 */
	public AbstractTrackEditWidget(SignalHub signalHub) {
		this.signalHub = signalHub;
		newtrackGroup = new QGroupBox(tr("New Track"));
		artistLabel = new QLabel(tr("Artist:"));
		tracknameLabel = new QLabel(tr("Trackname:"));
		labelLabel = new QLabel(tr("Label:"));
		lengthLabel = new QLabel(tr("Length:"));

		genreLabel = new QLabel(tr("Genre:"));
		releasedLabel = new QLabel(tr("Released:"));
		bpmLabel = new QLabel(tr("Bpm:"));
		catalognrLabel = new QLabel(tr("CatalogNr:"));
		inventoryLabel = new QLabel(tr("InventoryNR:"));
		commentLabel = new QLabel(tr("Comment:"));
		ratingLabel = new QLabel(tr("Rating:"));
		mediatypeLabel = new QLabel(tr("MediaType:"));
		filenameLabel = new QLabel(tr("Filename:"));
		writeID3Label =  new QLabel(tr("Write ID3Tag:"));
		writeID3Checkbox = new QCheckBox();
		writeID3Checkbox.setChecked(false);
		writeID3Checkbox.setEnabled(false);
		writeID3Label.setEnabled(false);
		
		initStuff();
		initLines();
		initButtons();
		dolayout();

		setMinimumSize(300, 450);
		

		artistLineEdit.setFocus();
	}

	void dolayout() {
		QGridLayout newtrackLayout = new QGridLayout();
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
		
		newtrackLayout.addWidget(writeID3Label, 13, 0);
		newtrackLayout.addWidget(writeID3Checkbox, 13, 1);
		

		newtrackGroup.setLayout(newtrackLayout);
		QGridLayout layout = new QGridLayout();
		layout.addWidget(newtrackGroup, 0, 0, 1, 2);
		layout.addWidget(actionButton, 1, 0);
		layout.addWidget(cancelButton, 1, 1);
		//	      
		setLayout(layout);
	}

	void clear() {
		artistLineEdit.clear();
		tracknameLineEdit.clear();
		labelLineEdit.clear();
		lengthLineEdit.setText("0000");
		genreComboBox.clear();
		releasedLineEdit.clear();
		bpmLineEdit.clear();
		catalogLineEdit.clear();
		inventoryLineEdit.clear();
		commentLineEdit.clear();
		ratingSpinBox.setValue(0);
		mediatypeCombobox.setCurrentIndex(0);
		filenameLineEdit.clear();
		writeID3Checkbox.setChecked(false);
		writeID3Checkbox.setEnabled(false);
		writeID3Label.setEnabled(false);
	}

	abstract void initStuff();

	abstract void initLines();

	abstract void initButtons();

	protected void refreshGenreBox() {
		genreComboBox.clear();
		Enumeration<String> foo = GenreCache.getAllGenres();
		genreComboBox.addItem("");
		while (foo.hasMoreElements()) {
			String genre = (String) foo.nextElement();
			genreComboBox.addItem(genre);
		}

	}

}
