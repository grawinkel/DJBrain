/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */
package net.z0id.djbrain.gui.currenttrack;

import java.util.LinkedList;
import java.util.List;

import net.z0id.djbrain.gui.DJMimeData;
import net.z0id.djbrain.gui.DJMimeData.Source;
import net.z0id.djbrain.objects.Track;

import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QDrag;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMouseEvent;

/**
 * @author meatz
 * 
 */
public class DisplayedTrackWidget extends QLabel {
	private Track track;

	/**
	 * 
	 */
	public DisplayedTrackWidget() {

		QFont f = new QFont();
		f.setPointSize(10);
		f.setWeight(QFont.Weight.Bold.value());
		setFont(f);
	}

	/**
	 * @param track
	 */
	public void setTrack(Track track) {
		this.track = track;
	}

	@Override
	protected void mousePressEvent(final QMouseEvent event) {
		if (track != null) {
			final DJMimeData mimeData = new DJMimeData();

			final List<QUrl> urls = new LinkedList<QUrl>();
			if (track.isPlayable()){
				urls.add(QUrl.fromLocalFile(track.getFilename()));	
			}

			mimeData.setUrls(urls);
			mimeData.setSource(Source.CURRENTTRACK);
			mimeData.addTrackId(track.getId());
			final QDrag drag = new QDrag(this);
			drag.setMimeData(mimeData);
			drag.exec();
		}

	}

	/**
	 * 
	 */
	public void resetView() {
		setText("no track selected");
	}

	/**
	 * @param track
	 */
	public void setCurrentTrack(Track track) {
		if (track != null) {
			this.track = track;
			setText(track.toPlaylistString());
			this.update();
		}
	}

}
