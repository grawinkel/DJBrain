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

import java.util.LinkedList;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QCompleter;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QPalette;

/**
 * @author meatz
 * 
 */
public class SearchCombobox extends QComboBox {

	private LinkedList<String> history = new LinkedList<String>();
	private QPalette alternatePalette;
	private QPalette normalPalette;


	/**
	 * is emitted if a key is clicked within the combobox - textedit
	 */
	public Signal0 contentChanged;

	/**
	 * 
	 */
	public Signal0 enterPressed;
	

	/**
	 * Customized Combobox, which also gives the contentChanged Signal
	 */
	public SearchCombobox() {		
		alternatePalette = new QPalette();
		alternatePalette.setColor(QPalette.ColorRole.Text, QColor.lightGray);
 
		normalPalette = new QPalette();
		normalPalette.setColor(QPalette.ColorRole.Text, QColor.black);
 
		setAcceptDrops(false);
		
		setEditable(true);
		setCompleter( new QCompleter());
//		setAutoCompletion(true);
//		setAutoCompletionCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive);
		setDuplicatesEnabled(false);
		setFixedWidth(300);
		setAutoFillBackground(true);

		this.currentStringChanged.connect(this, "currentStringChanged(String)");

		contentChanged = new Signal0();
		enterPressed = new Signal0();
		
	}

	@SuppressWarnings("unused")
	private void currentStringChanged(String string) {
string = string.trim();
		if (!history.contains(string)) {
			history.addFirst(string);

			if (history.size() > 10) {
				history.removeLast();
			}
			this.clear();
			this.addItems(history);
		}
	}

	 
	/**
	 * this function sets the focus on this box,
	 * it is called from qmain by shortcut: ctrl+f
	 *
	 */
	public void setSearchFocus(){
		 this.setFocus();
	 }
 
	protected void keyPressEvent(QKeyEvent event) {
		if (event.key() == Qt.Key.Key_Return.value() ){
			enterPressed.emit();
		}else{
			contentChanged.emit();
		}
		super.keyPressEvent(event);
	}
	
	
	/**
	 * @param check
	 */
	public void setColored(boolean check){
		if (check){
			setPalette(alternatePalette);
		}else{
			setPalette(normalPalette);
		}
	}

}
