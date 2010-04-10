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

import net.z0id.djbrain.objects.Genre;

import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QListWidgetItem;

/**
 * @author meatz
 *
 */
public class QEditGenresItem extends QListWidgetItem{

	private Genre genre;
	
	/**
	 * @param genre
	 * @param parent
	 */
	public QEditGenresItem(Genre genre, QListWidget parent){
		super(parent);
		this.genre = genre;
 		this.setFlags(ItemFlag.ItemIsEditable,ItemFlag.ItemIsEnabled,ItemFlag.ItemIsUserCheckable);
		this.setText(genre.getGenre());
	}

	/**
	 * @return the genre
	 */
	public Genre getGenre() {
		return genre;
	}

	/**
	 * @param genre the genre to set
	 */
	public void setGenre(Genre genre) {
		this.genre = genre;
	}
}
