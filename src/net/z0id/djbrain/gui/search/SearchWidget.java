/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.search;

import net.z0id.djbrain.gui.SignalHub;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 * Artist like '%%alko%%'
 * 
 * Artist='Various'
 */
public class SearchWidget extends QWidget {

	private SearchCombobox searchCombobox;

	private QPushButton clearButton;

	// private QLabel filterActiveLabel;

	private SignalHub signalHub;

	/**
	 * @param signalHub
	 */
	public SearchWidget(SignalHub signalHub) {
		this.signalHub = signalHub;

		searchCombobox = new SearchCombobox();
		searchCombobox.setToolTip(tr("Insert filter String here"));
		searchCombobox.setAcceptDrops(false);
		// filterActiveLabel = new QLabel("unset");
		clearButton = new QPushButton();
		clearButton.setText("clear");

		QGridLayout layout = new QGridLayout();
		layout.setMargin(0);
		layout.addWidget(searchCombobox, 0, 0, Qt.AlignmentFlag.AlignRight);
		// layout.addWidget(filterActiveLabel, 0, 1, Qt.AlignRight);
		layout.addWidget(clearButton, 0, 1, Qt.AlignmentFlag.AlignRight);

		setLayout(layout);

		searchCombobox.enterPressed.connect(this, "enterPressed()");

		searchCombobox.contentChanged.connect(this,
				"searchComboboxContentChanged()");
		clearButton.clicked.connect(this, "clearButtonClicked(Boolean)");
	}

	boolean contentChanged = false;

	@SuppressWarnings("unused")
	private void searchComboboxContentChanged() {

		if (!contentChanged) {
			searchCombobox.setColored(true);
		}
	}

	/**
	 * 
	 */
	public void setSearchFocus() {
		searchCombobox.setSearchFocus();
	}

	@SuppressWarnings("unused")
	private void enterPressed() {
		contentChanged = false;
		searchCombobox.setColored(false);
		signalHub.setFilter(searchCombobox.currentText());
		signalHub.setFilterActive(true);
		signalHub.signalFilterStateChanged();
	}

	@SuppressWarnings("unused")
	private void clearButtonClicked(Boolean foo) {
		contentChanged = false;
		searchCombobox.clearEditText();
		searchCombobox.setColored(true);
		signalHub.setFilterActive(false);
		signalHub.signalFilterStateChanged();
	}

}
