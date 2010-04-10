/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.debug;

import net.z0id.djbrain.db.DBConnection;

import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 *
 */
public class QDBDumpWidget extends QWidget{


	
	private QTextEdit dbdumpTextEdit;

	
		
		/**
		 * 
		 */
		public QDBDumpWidget(){
			dbdumpTextEdit = new QTextEdit();
		
			dbdumpTextEdit.setPlainText(DBConnection.getInstance().getDump());
		
		QGridLayout layout = new QGridLayout();
		layout.addWidget(dbdumpTextEdit, 0, 0);
	
		setWindowTitle(tr("Database dump"));
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));
		setLayout(layout);
		resize(1200,600);
		}
}
