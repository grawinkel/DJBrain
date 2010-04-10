/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */
package net.z0id.djbrain.imexport;

import java.io.File;
import java.io.IOException;

import net.z0id.djbrain.db.GenreCache;
import net.z0id.djbrain.objects.Track;

import org.apache.log4j.Logger;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.blinkenlights.jid3.v2.ID3V2Tag;

/**
 * @author meatz
 * 
 */
public class ID3TagHelper {

	private static Logger logger = Logger.getLogger(ID3TagHelper.class);

	/**
	 * @param track
	 * @throws IOException
	 * @throws TagException
	 */
	public static void updateTagsForTrack(Track track) {

		File file = new File(track.getFilename());

		if (file.canWrite()) {

			MP3File mp3File = new MP3File(file);

			ID3V2Tag tag2;
			try {
				tag2 = mp3File.getID3V2Tag();

				if (tag2 == null) {
					ID3V1Tag tag1 = mp3File.getID3V1Tag();
					if (tag1 == null) {

						logger.error("no tag available");
						return;
					} else {
						tag1.setArtist(track.getArtist());
						tag1.setTitle(track.getTrackname());
						tag1.setAlbum(track.getLabel());
						tag1.setYear(track.getReleased() + "");
						// tag1.setGenre(
						// GenreCache.getGenreForId(track.getGenreId()));
						tag1.setComment(track.getComment());

						mp3File.setID3Tag(tag1);
						mp3File.sync();
						logger.debug("id3v1 written");
					}
				} else {
					tag2.setArtist(track.getArtist());
					tag2.setTitle(track.getTrackname());
					tag2.setAlbum(track.getLabel());
					tag2.setYear(track.getReleased());
					tag2.setGenre(GenreCache.getGenreForId(track.getGenreId()));
					tag2.setComment(track.getComment());

					mp3File.setID3Tag(tag2);
					mp3File.sync();
					logger.debug("id3v2 written");
				}

			} catch (ID3Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			logger.error("file: " + track.getFilename() + " is not writable ");
		}
	}
}