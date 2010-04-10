/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.brainstorm;

import java.util.ArrayList;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.SignalHub;
import net.z0id.djbrain.objects.Suggestion;

import org.apache.log4j.Logger;

import com.trolltech.qt.gui.QBoxLayout;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class BrainStormWidget extends QWidget {

	private static Logger logger = Logger.getLogger(BrainStormWidget.class);

	private BrainstormTree treeWidget;

//	private SignalHub signalHub;

	private int currentTrack;

	private static BrainStormWidget instance;

	private Suggestion currentSuggestion;

	/**
	 * @param signalCenter
	 */
	private BrainStormWidget(SignalHub signalHub) {
//		this.signalHub = signalHub;

		treeWidget = new BrainstormTree(signalHub, this);

		QBoxLayout layout = new QBoxLayout(QBoxLayout.Direction.TopToBottom);
		layout.addWidget(treeWidget, 70);
		layout.addWidget(getControl(), 30);
		setLayout(layout);

		setWindowTitle(tr("Brainstorm"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));

		signalHub.tracksDeleted.connect(this,"tracksDeleted(ArrayList)");
		resize(700, 500);
	}

 	private QTextEdit comment;

	private QSpinBox ratingSpinBox;

	private QPushButton savePushButton;

	private QPushButton closePushButton;

	private QLabel ratingLabel;

	private QLabel identifier;

	private QWidget getControl() {
		QWidget control = new QWidget();
		QBoxLayout boxlayout = new QBoxLayout(QBoxLayout.Direction.LeftToRight);

		control.setLayout(boxlayout);

		savePushButton = new QPushButton();
		savePushButton.setText(tr("Save"));
		savePushButton.clicked.connect(this, "savePushbuttonPressed()");
		savePushButton.setEnabled(false);
		closePushButton = new QPushButton();
		closePushButton.setText(tr("Close"));
		closePushButton.clicked.connect(this, "closePushbuttonPressed()");

		comment = new QTextEdit();
		comment.setAcceptDrops(false);
		comment.document().contentsChanged.connect(this,"contentChanged()");
		ratingSpinBox = new QSpinBox();
		ratingSpinBox.setRange(0, 5);
		ratingSpinBox.setSingleStep(1);
		ratingSpinBox.valueChanged.connect(this,"contentChanged()");

		ratingLabel = new QLabel(tr("Suggestions rating:"));
		identifier = new QLabel(tr("No Suggestion selected"));

		QGridLayout leftlayout = new QGridLayout();

		leftlayout.addWidget(identifier, 0, 0, 1, 2);
		leftlayout.addWidget(ratingLabel, 1, 0);
		leftlayout.addWidget(ratingSpinBox, 1, 1);
		leftlayout.addWidget(comment, 2, 0, 1, 2);

		// layout.addWidget(savePushButton,1,3);
		// layout.addWidget(closePushButton,2,3);
		boxlayout.addLayout(leftlayout, 80);
		QBoxLayout rightlayout = new QBoxLayout(
				QBoxLayout.Direction.TopToBottom);
		rightlayout.insertStretch(10);
		rightlayout.addWidget(savePushButton);
		rightlayout.addWidget(closePushButton);
		boxlayout.addLayout(rightlayout, 20);
		boxlayout.setMargin(0);
		return control;
	}

	@SuppressWarnings("unused")
	private void contentChanged(){

		//eyecancer-alert! sorry :/ will rewrite this... perhaps
		
	  if (currentSuggestion != null && (currentSuggestion.getRating() != ratingSpinBox.value() 
				|| !currentSuggestion.getComment().equals(comment.toPlainText())) ){
			savePushButton.setEnabled(true);	
		}else{
			savePushButton.setEnabled(false);	
		}
	
	}
	
	@SuppressWarnings("unused")
	private void tracksDeleted(ArrayList<Integer> deletedTracks){
		if (deletedTracks.contains((Integer) currentTrack) ){
			logger.debug("root item was deleted");
			treeWidget.clear();
			this.clear();
		}else{
			treeWidget.brainstorm(currentTrack);
			logger.debug("brainstorm("+currentTrack+")");
		}
	}
	/**
	 * @param signalHub
	 * @param signalCenter
	 * @param currentTrack
	 */
	public static void showBrainstorm(SignalHub signalHub, int currentTrack) {
		if (instance == null) {
			instance = new BrainStormWidget(signalHub);
		}
		
		instance.currentTrack = currentTrack;
		instance.treeWidget.brainstorm(currentTrack);
		instance.show();
	}
	
	/**
	 * @param identifier
	 * @param suggestion
	 */
	void setSuggestion(String identifier, Suggestion suggestion) {
		ratingSpinBox.setEnabled(true);
		comment.setEnabled(true);
		savePushButton.setEnabled(false);
		currentSuggestion = suggestion;
		this.identifier.setText(identifier);
		ratingSpinBox.setValue(suggestion.getRating());
		comment.setPlainText(suggestion.getComment());
	}
	
	private void clear(){
		currentSuggestion = null;
		this.identifier.setText("");
		ratingSpinBox.setValue(0);
		ratingSpinBox.setEnabled(false);
		comment.setPlainText(tr("no brainstorm available"));
		comment.setEnabled(false);
		savePushButton.setEnabled(false);
	}

	@SuppressWarnings("unused")
	private void savePushbuttonPressed() {
		
		DBConnection.getInstance().updateSuggestion(currentSuggestion);
		savePushButton.setEnabled(false);
	}

	@SuppressWarnings("unused")
	private void closePushbuttonPressed() {
		treeWidget.clear();
		this.hide();
	}


	/**
	 * @param signalHub
	 */
	public static void show(SignalHub signalHub) {
		if (instance == null) {
			instance = new BrainStormWidget(signalHub);
		}
		instance.clear();
		instance.show();
	}


	/**
	 * @return the currentTrack
	 */
	public int getCurrentTrack() {
		return currentTrack;
	}
}
