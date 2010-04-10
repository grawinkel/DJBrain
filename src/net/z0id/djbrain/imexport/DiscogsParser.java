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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import net.z0id.djbrain.objects.Track;

/**
 * @author meatz
 * 
 */
public class DiscogsParser {

	/**
	 * @param file
	 * @return ArrayList<Track>
	 * @throws IOException
	 * @throws
	 * @throws Exception
	 */
	public static ArrayList<Track> getTracklist(File file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		Track track = null;
		int trackCount = 0;
		int currentcount = 0;

		ArrayList<Track> tracklist = new ArrayList<Track>();
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if (trackCount == 0) {
				if (line.contains("<tr>")) {
					// track = new Track();
				}
				if (line.contains("<tr>")) {

				}
				if (line.contains("</tr>")) {
					trackCount++;
				}

			} else {
				/**
				 * <tr>
				 * <td>Catalog#</td>
				 * <td>Artist</td>
				 * <td>Title</td>
				 * <td>Label</td>
				 * <td>Format</td>
				 * <td>Rating</td>
				 * <td>Released</td>
				 * <td>Discogs Link</td>
				 * </tr>
				 * 
				 * 
				 * 
				 * <tr>
				 * <td align=left>DUMB_006</td>
				 * <td align=left>Falko Brocksieper</td>
				 * <td align=left>Positive Is Clockwise / Post Meridian</td>
				 * <td align=left>Dumb-Unit</td>
				 * <td align=left>12"</td>
				 * <td align=left></td>
				 * <td align=left>2002</td>
				 * <td align=left>http://www.discogs.com/release/39590</td>
				 * </tr
				 */
				if (line.contains("<tr>")) {
					track = new Track();
				}
				if (line.contains("<td")) {
					switch (currentcount) {
					case 0:
						track.setCatalognr(extract(line));
						currentcount++;
						break;
					case 1:
						track.setArtist(extract(line));
						currentcount++;
						break;
					case 2:
						track.setTrackname(extract(line));
						currentcount++;
						break;
					case 3:
						track.setLabel(extract(line));
						currentcount++;
						break;
					case 4:
						String mediatype = extract(line);
						//TODO: also handle 5" etc...
						mediatype = mediatype.replaceAll("12\"", "Vinyl");
						track.setMediatype(mediatype);
						currentcount++;
						break;
					case 5:
						track.setRating(extractInt(line));
						currentcount++;
						break;
					case 6:
						track.setReleased(Integer.parseInt(extract(line)));
						currentcount++;
						break;
					case 7:
						track.setComment(extract(line));
						currentcount++;
						break;
					default:
						break;
					}

				}
				if (line.contains("</tr>")) {
					tracklist.add(track);
//					track.toString();
					trackCount++;
					currentcount = 0;
				}
			}

		}

		return tracklist;

	}

	private static int extractInt(String line) {

		try {
			return Integer.parseInt(extract(line));
		} catch (NumberFormatException nfe) {
			return 0;
		}

	}

	private static String extract(String line) {
		if (line == null || line.trim().equalsIgnoreCase(""))
			return "";
		int a = line.indexOf("left>") + 5;
		int b = line.lastIndexOf("</td>");
		line = line.substring(a, b);
		System.out.println(line);
		return line;
	}
}
