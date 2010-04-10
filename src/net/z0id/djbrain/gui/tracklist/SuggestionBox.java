/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.tracklist;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.gui.DJMimeData.Source;
import net.z0id.djbrain.objects.Suggestion;

import com.trolltech.qt.core.QMimeData;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class SuggestionBox extends QWidget {
	private SignalHub signalHub;

	private Suggestion currentSuggestion;

	private QLabel ratingLabel;

	private QSpinBox ratingSpinBox;

	private CommentTextField comment;

	private QPushButton saveButton;
	
	
	/**
	 * @param signalHub
	 */
	public SuggestionBox(SignalHub signalHub) {
		this.signalHub = signalHub;

		init();
		setAcceptDrops(true);

		saveButton.clicked.connect(this, "saveInformation()");

		signalHub.currentTrackChanged.connect(this,"disable()");
		
		comment.document().contentsChanged.connect(this,"contentChanged()");
		ratingSpinBox.valueChanged.connect(this,"contentChanged()");
		
		QGridLayout layout = new QGridLayout();

		layout.addWidget(ratingLabel, 0, 0, Qt.AlignmentFlag.AlignLeft);
		layout.addWidget(ratingSpinBox, 0, 1, Qt.AlignmentFlag.AlignRight);
		layout.addWidget(comment, 1, 0, 1, 2);
		layout.addWidget(saveButton, 2, 1, Qt.AlignmentFlag.AlignRight);
		layout.setMargin(0);
		setLayout(layout);
		disable();
	}

	@SuppressWarnings("unused")
	private void contentChanged(){
		  if (currentSuggestion != null && (currentSuggestion.getRating() != ratingSpinBox.value() 
				|| !currentSuggestion.getComment().equals(comment.toPlainText())) ){
			saveButton.setEnabled(true);	
		}else{
			saveButton.setEnabled(false);	
		}
	}
	
	/**
	 * @param suggestion
	 */
	public void setSuggestion(Suggestion suggestion) {
		enable(true);
		this.currentSuggestion = suggestion;
		this.ratingSpinBox.setValue(suggestion.getRating());
		this.comment.setPlainText(suggestion.getComment());
	}

	private void init() {
		ratingLabel = new QLabel(tr("Suggestions rating:"));
		ratingSpinBox = new QSpinBox();
		ratingSpinBox.setRange(0, 5);
		ratingSpinBox.setSingleStep(1);
		comment = new CommentTextField(this);

		saveButton = new QPushButton(tr("Save"));
	}

	@SuppressWarnings("unused")
	private void saveInformation() {
		
		currentSuggestion.setRating(ratingSpinBox.value());
		currentSuggestion.setComment(comment.toPlainText());
		
		if (DBConnection.getInstance().updateSuggestion(currentSuggestion)) {
			signalHub.setStatusBarText(tr("Suggestion was updated"));
		} else {
			signalHub
					.setStatusBarText(tr("An error occured while updating Suggeston. See log for details"));
		}
		saveButton.setEnabled(false);
	}
	
	
	@SuppressWarnings("unused")
	private void disable(){
		enable(false);
	}

	/** if true, the <code>suggestionbox</code> is enabled, disabled otherwise
	 * @param enable
	 */
	public void enable(boolean enable) {

		ratingSpinBox.setEnabled(enable);
		comment.setEnabled(enable);
		saveButton.setEnabled(false);
		if (!enable) {
			comment.setPlainText("");
			ratingSpinBox.setValue(0);
		}
	}

	/**
	 * @param id
	 */
	public void checkDisable(int id) {
		if (currentSuggestion!= null && currentSuggestion.getSuggestedTrackId() == id){
			disable();
		}
		
	}

	/* (non-Javadoc)
	 * @see com.trolltech.qt.gui.QWidget#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
	 */
	@Override
	protected void dragEnterEvent(QDragEnterEvent event) {
		if (event.source() != this) {
			QMimeData d = event.mimeData();
		
			if (d instanceof DJMimeData) {
				DJMimeData djMimeData = (DJMimeData) d;
				
				if (djMimeData.hasTracks() && djMimeData.getSource() == Source.SUGGESTIONLIST){
					event.accept();
					return;
				}
			}
		}
		event.ignore();
		}
	
	/* (non-Javadoc)
	 * @see com.trolltech.qt.gui.QWidget#dragMoveEvent(com.trolltech.qt.gui.QDragMoveEvent)
	 */
	@Override
	protected void dragMoveEvent(QDragMoveEvent event) {
		event.accept();
	}
	
	/* (non-Javadoc)
	 * @see com.trolltech.qt.gui.QWidget#dropEvent(com.trolltech.qt.gui.QDropEvent)
	 */
	@Override
	protected void dropEvent(QDropEvent event) {
		DJMimeData data = (DJMimeData) event.mimeData();	
		if (data != null) {
				int suggestionId =data.getFirstTrackId();
				setSuggestion(DBConnection.getInstance().getSuggestion( signalHub.getCurrentTrackId(), suggestionId ));
		}
	}
	
	private class CommentTextField extends QTextEdit{
		
		private SuggestionBox parent;
		
		/**
		 * @param parent
		 */
		public CommentTextField(SuggestionBox parent){
			super();
			this.parent = parent;
		}
		
		/* (non-Javadoc)
		 * @see com.trolltech.qt.gui.QTextEdit#dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent)
		 */
		@Override
		protected void dragEnterEvent(QDragEnterEvent arg0) {
			parent.dragEnterEvent(arg0);
		}
		
		/* (non-Javadoc)
		 * @see com.trolltech.qt.gui.QTextEdit#dragMoveEvent(com.trolltech.qt.gui.QDragMoveEvent)
		 */
		@Override
		protected void dragMoveEvent(QDragMoveEvent arg0) {
			parent.dragMoveEvent(arg0);
		}
		
		/* (non-Javadoc)
		 * @see com.trolltech.qt.gui.QTextEdit#dropEvent(com.trolltech.qt.gui.QDropEvent)
		 */
		@Override
		protected void dropEvent(QDropEvent arg0) {
			parent.dropEvent(arg0);
		}
	}
}
