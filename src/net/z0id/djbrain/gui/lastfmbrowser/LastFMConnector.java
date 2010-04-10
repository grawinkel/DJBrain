/*
 *   Copyright (C) 2007 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui.lastfmbrowser;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * @author muti
 * 
 */
public class LastFMConnector {

	public static final String TOP_TRACKS_FOR_ARTIST = "Get top tracks for artist:";

	public static final String RELATED_ARTISTS = "Get related artists:";

	public static final String TOP_TRACKS_FOR_TAG = "Get top tracks for tag:";

	/**
	 * @param artist
	 * @return List of Tracks
	 * @throws IOException
	 * @throws ValidityException
	 * @throws ParsingException
	 */
	public static List<LastFMArtist> getRelatedArtists(String artistString)
			throws IOException, ValidityException, ParsingException {
		artistString = URLEncoder.encode(artistString, "UTF-8").trim();
		URL url = new URL("http://ws.audioscrobbler.com/1.0/artist/"
				+ artistString + "/similar.xml");

		Element rootNode = getElementsFromUrl(url);

		Elements rootElements = rootNode.getChildElements();

		List<LastFMArtist> artists = new LinkedList<LastFMArtist>();
		for (int i = 0; i < rootElements.size(); i++) {
			Element e = rootElements.get(i);

			if (e.getLocalName().equals("artist")) {

				Elements subsubElements = e.getChildElements();
				LastFMArtist artist = new LastFMArtist();
				for (int k = 0; k < subsubElements.size(); k++) {
					Element subsubElement = subsubElements.get(k);
					if (subsubElement.getLocalName().equals("name")) {
						artist.setName(subsubElement.getValue());
					} else if (subsubElement.getLocalName().equals("url")) {
						artist.setUrl(subsubElement.getValue());
					}
				}
				artists.add(artist);
			}

		}

		return artists;
	}

	/**
	 * @param artist
	 * @return List of Tracks
	 * @throws IOException
	 * @throws ValidityException
	 * @throws ParsingException
	 */
	public static List<LastFMArtist> getTopTracksForArtist(String artistString)
			throws IOException, ValidityException, ParsingException {
		artistString = URLEncoder.encode(artistString, "UTF-8").trim();

		URL url = new URL("http://ws.audioscrobbler.com/1.0/artist/"
				+ artistString + "/toptracks.xml");

		Element rootNode = getElementsFromUrl(url);

		Elements rootElements = rootNode.getChildElements();

		String name = rootNode.getAttribute("artist").getValue();
		LastFMArtist artist = new LastFMArtist();
		artist.setName(name);

		List<LastFMTrack> tracks = new LinkedList<LastFMTrack>();
		for (int i = 0; i < rootElements.size(); i++) {
			Element e = rootElements.get(i);

			if (e.getLocalName().equals("track")) {

				Elements subsubElements = e.getChildElements();
				LastFMTrack track = new LastFMTrack();
				for (int k = 0; k < subsubElements.size(); k++) {
					Element subsubElement = subsubElements.get(k);
					if (subsubElement.getLocalName().equals("name")) {
						track.setTrackname(subsubElement.getValue());
					} else if (subsubElement.getLocalName().equals("url")) {
						track.setUrl(subsubElement.getValue());
					} else if (subsubElement.getLocalName().equals("reach")) {
						track.setRanking(subsubElement.getValue());
					}
				}
				tracks.add(track);
			}

		}
		artist.setTracks(tracks);

		return Arrays.asList(artist);
	}

	/**
	 * @param tag
	 * @return List of Tracks
	 * @throws IOException
	 * @throws ValidityException
	 * @throws ParsingException
	 */
	public static List<LastFMArtist> getTopTracksForTag(String tag)
			throws IOException, ValidityException, ParsingException {

		tag = URLEncoder.encode(tag, "UTF-8").trim();

		URL url = new URL("http://ws.audioscrobbler.com/1.0/tag/" + tag
				+ "/toptracks.xml");
		Hashtable<String, LastFMArtist> artistsTable = new Hashtable<String, LastFMArtist>();

		Element rootNode = getElementsFromUrl(url);

		Elements rootElements = rootNode.getChildElements();

		for (int i = 0; i < rootElements.size(); i++) {
			Element e = rootElements.get(i);

			// System.out.println(e.toXML());
			/*
			 * 
			 * <track name="Wonderwall" count="226" streamable="yes"> <artist
			 * name="Oasis"> <mbid/> <url>http://www.last.fm/music/Oasis</url>
			 * </artist> <url>http://www.last.fm/music/Oasis/_/Wonderwall</url>
			 * </track>
			 */

			if (e.getLocalName().equals("track")) {
				String artistname = e.getChildElements("artist").get(0)
						.getAttributeValue("name");

				String trackname = e.getAttributeValue("name");
				String trackurl = e.getChildElements("url").get(0).getValue();
				String trackcount = e.getAttributeValue("count");
			
				LastFMArtist artist = artistsTable.get(artistname);
		
				if (artist == null) {
					artist = new LastFMArtist();
					artist.setName(artistname);
					String artisturl = e.getChildElements("artist").get(0)
							.getChildElements("url").get(0).getValue();
					artist.setUrl(artisturl);
					artistsTable.put(artistname, artist);
				}
				
				LastFMTrack track = new LastFMTrack();
				track.setTrackname(trackname);
				track.setUrl(trackurl);
				track.setRanking(trackcount + " (count)");
				artist.addTrack(track);
			}
		}

		return new LinkedList<LastFMArtist>( artistsTable.values());

	}

	/* ============== private helpers ===================== */

	private static Element getElementsFromUrl(URL url) throws IOException,
			ValidityException, ParsingException {

		URLConnection con = url.openConnection();

		Builder parser = new Builder();

		Document doc = parser.build(con.getInputStream());

		Element rootNode = doc.getRootElement();

		return rootNode;
	}

}
