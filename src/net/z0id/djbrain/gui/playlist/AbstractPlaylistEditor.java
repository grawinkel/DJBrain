/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */


package net.z0id.djbrain.gui.playlist;

import net.z0id.djbrain.gui.SignalHub;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QBoxLayout;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 *
 */
public abstract class AbstractPlaylistEditor extends QWidget {
	
	protected QLabel playlistNameLabel;
	protected QLabel commentLabel;
	protected QLineEdit playlistName;
	protected QTextEdit comment;
	protected QPushButton actionButton;
	private QPushButton cancelButton;
	protected SignalHub signalHub;

	
	protected AbstractPlaylistEditor(SignalHub signalHub){
	 
			this.signalHub = signalHub;
			
			playlistName = new QLineEdit();
			comment = new QTextEdit();
		
			playlistNameLabel = new QLabel(tr("Name:"));
			commentLabel = new QLabel(tr("Comment:"));
			
		initButtons();			
		
		initStuff();
		
		QGridLayout layout = new QGridLayout();
		layout.addWidget(playlistNameLabel, 0, 0);
		layout.addWidget(playlistName, 0, 1);
		layout.addWidget(commentLabel, 1, 0,Qt.AlignmentFlag.AlignTop);
		layout.addWidget(comment, 1, 1);
		
		QBoxLayout bl = new QBoxLayout(QBoxLayout.Direction.LeftToRight);
		
		bl.insertStretch(10);
		bl.addWidget( actionButton );
		bl.addWidget( cancelButton );
	 
		layout.addLayout(bl, 2, 1, 1,2);	      
		setLayout(layout);
		
	}
		
	void clear(){
		playlistName.clear();
		comment.clear();
	}
	
	@SuppressWarnings("unused")
	private void cancelButtonClicked() {
		clear();
		 hide();
	}
	
	
	 private void initButtons() {
			actionButton = new QPushButton();
			actionButton.clicked.connect(this,
					tr("actionButtonClicked()"));
			
			cancelButton = new QPushButton(tr("&Cancel"));
			cancelButton.clicked.connect(this,
					tr("cancelButtonClicked()"));		
		}
	 
	 protected abstract void initStuff();
}
