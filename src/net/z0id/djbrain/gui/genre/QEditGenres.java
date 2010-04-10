/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.genre;

 

import java.util.ArrayList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.db.GenreCache;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.objects.Genre;

import org.apache.log4j.Logger;

import com.trolltech.qt.gui.QAbstractItemView;
import com.trolltech.qt.gui.QBoxLayout;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QAbstractItemView.EditTrigger;

/**
 * @author meatz
 * 
 */
public class QEditGenres extends QWidget {

	private Logger logger = Logger.getLogger(this.getClass());

	private QPushButton addButton;

	private QPushButton removeButton;

	private QPushButton saveButton;

	private QPushButton cancelButton;

	private QListWidget genreList;

	private static QEditGenres instance;

	private List<Genre> removeOnSaveList;

	private SignalHub signalHub;

	/**
	 * @param signalHub
	 * @return singleton instance of QNewPlaylistWidget
	 */
	public static QEditGenres getInstance(SignalHub signalHub) {
		if (instance == null) {
			instance = new QEditGenres(signalHub);
		}
//		else {
//			// instance.reloadGenres();
//		}

		instance.loadGenres();
		instance.removeOnSaveList.clear();
		return instance;
	}

	private void loadGenres() {
		List<Genre> g = DBConnection.getInstance().getAllGenres();
		genreList.clear();

		for (Genre genre : g) {
			if (genre.getId() > 0) {
				// dont show the init - blank - genre
				new QEditGenresItem(genre, genreList);
			}
		}
	}

	private QEditGenres(SignalHub signalHub) {
		this.signalHub = signalHub;
		genreList = new QListWidget();
		removeOnSaveList = new ArrayList<Genre>();

		genreList.setDragEnabled(false);
		genreList.setAutoScroll(true);
		genreList.setFocus();
		genreList.setUpdatesEnabled(true);
		genreList
				.setSelectionBehavior(QAbstractItemView.SelectionBehavior.SelectRows);
		genreList.setEditTriggers(EditTrigger.DoubleClicked);
		
//		genreList.itemDoubleClicked.connect(this,
//				"renameListItem(QListWidgetItem)");
		setFixedSize(250, 200);
		initButtons();
		initStuff();

		QBoxLayout bl = new QBoxLayout(QBoxLayout.Direction.TopToBottom);

		bl.addWidget(addButton);
		bl.addWidget(removeButton);
		bl.insertStretch(10);

		bl.addWidget(saveButton);
		bl.addWidget(cancelButton);

		QGridLayout layout = new QGridLayout();
		layout.addWidget(genreList, 0, 0);
		layout.addLayout(bl, 0, 1);

		setLayout(layout);

	}

	private void initButtons() {
		addButton = new QPushButton(tr("&Add"));
		addButton.clicked.connect(this, "addItem()");

		removeButton = new QPushButton(tr("&Delete"));
		removeButton.clicked.connect(this, "removeItem()");

		cancelButton = new QPushButton(tr("&Cancel"));
		cancelButton.clicked.connect(this, "hide()");

		saveButton = new QPushButton(tr("&Save and Close"));
		saveButton.clicked.connect(this, "saveAndClose()");
	}

	private void initStuff() {
		setWindowTitle(tr("Edit genres"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
	}

	@SuppressWarnings("unused")
	private void addItem() {
		QEditGenresItem x = new QEditGenresItem(new Genre(-1, tr("New genre")),
				genreList);
		genreList.setCurrentItem(x);
		genreList.editItem(x);
	}

	@SuppressWarnings("unused")
	private void removeItem() {
		QEditGenresItem item = (QEditGenresItem) genreList.takeItem(genreList
				.currentRow());

		if (item != null) {
			logger.debug("removed: " + item.getGenre().getGenre() + " id: "
					+ item.getGenre().getId());
			removeOnSaveList.add(item.getGenre());

		}
	}

	@SuppressWarnings("unused")
	private void saveAndClose() {

		boolean genresChanged = false;

		// remove items from db:

		if (!removeOnSaveList.isEmpty()) {

			QMessageBox.StandardButton ret = QMessageBox
					.question(
							this,
							tr("Delete genres?"),
							tr("You are about to remove "
									+ removeOnSaveList.size()
									+ " genres from DB \n"
									+ "This may affect your Tracklist, do you want to delete these genres?"),
							new QMessageBox.StandardButtons(
									QMessageBox.StandardButton.Yes,
									QMessageBox.StandardButton.Cancel),
							QMessageBox.StandardButton.Yes);

			if (ret == QMessageBox.StandardButton.Yes) {
				for (Genre genre : removeOnSaveList) {
					logger.debug("removing genre: " + genre.getGenre()
							+ " with id:" + genre.getId());
					GenreCache.removeGenre(genre.getGenre());
				}
			}

		}

		/*
		 * TODO: find nicer solution... it cannot be that complicated to get an
		 * iteration above all items in the list... furthermorge
		 * 
		 * Furthermorge genreList.children().size() gives bullshit...
		 */
		//
		// int size = genreList.children().size();
		// int size2 = genreList.model().columnCount();
		boolean next = true;
		QEditGenresItem item;
		int i = 0;
		while (next) {
			item = (QEditGenresItem) genreList.item(i++);
			if (item != null) {

				if (item.getGenre().getId() == -1) {
					// this item is not yet in the db
					GenreCache.addGenre(item.text());
					int newID = GenreCache.getIDForGenre(item.text());
					item.getGenre().setId(newID);

					genresChanged = true;
					logger.debug("added new genre: " + item.text()
							+ "with id: " + newID);
				} else {
					String oldGenre = item.getGenre().getGenre();
					String newGenre = item.text();

					if (!oldGenre.equals(newGenre)) {
						GenreCache.renameGenre(oldGenre, newGenre);
						genresChanged = true;
						logger.debug("renamed genre from: " + oldGenre
								+ " to: " + item.text());
					}
				}

			} else {
				next = false;
			}

		}
		if (genresChanged) {
			signalHub.signalGenresChanged();
		}
		hide();
	}

//	private void renameListItem(QListWidgetItem item) {
//		System.out.println("doubleklick!");
////		genreList.editItem(item);
//	}
}
