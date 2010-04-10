/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.db;

 
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.z0id.djbrain.objects.Genre;

/**
 * @author meatz
 * 
 */
public class GenreCache {

	private Hashtable<Integer, String> cache;

	private static GenreCache instance;
private static List<Genre> genreList;
	
private GenreCache() {
	}

	/**
	 * must be called before first usage
	 * 
	 */
	public static void init() {
		instance = new GenreCache();
		
		instance.cache = new Hashtable<Integer, String>();
		
		refresh();
	}

	/** ************* public functions ******************** */

	/**
	 * @param testGenre
	 * @return true if the db contains a genre with name testGenre
	 */
	public static boolean containsGenre(String testGenre) {
		if (testGenre == null) {
			return false;
		}
		return instance.cache.containsValue(testGenre);
	}

	
	/**
	 * @param id
	 * @return returns true if a genre with id exists
	 */
	public static boolean containsId(int id) {		
		return instance.cache.containsKey(id);
	}
	
	/**
	 * 
	 * @param genre
	 * @return the id of this genre
	 */
	public static int getIDForGenre(String genre) {
		Enumeration<Integer> keys = instance.cache.keys();
		while (keys.hasMoreElements()) {
			int i = (int) keys.nextElement();
			if (instance.cache.get(i).equals(genre)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * @param id
	 * @return genre for this id
	 */
	public static String getGenreForId(int id) {
		return instance.cache.get(id);
	}

	/**
	 * refreshes the genreCache from DB
	 * 
	 */
	public static void refresh() {

		genreList = DBConnection.getInstance().getAllGenres();
		instance.cache.clear();
		instance.cache.put(0, "" );
		for (Genre genre : genreList) {
			instance.cache.put(genre.getId(), genre.getGenre());
		}

	}

	
	/**
	 * add new genre to db and refresh the cache, will check for dups
	 * @param genre
	 */
	public static void addGenre(String genre) {
		//TODO use prepared statements here
		if (!containsGenre(genre)){
			DBConnection.getInstance().query("INSERT INTO GENRES (GENRE) VALUES (\'"+genre+"\');");
			refresh();
		}
	}

	/**
	 * rename genre from oldGenre to newGenre and refresh the cache
	 * @param oldGenre
	 * @param newGenre
	 */
	public static void renameGenre(String oldGenre, String newGenre) {
		//TODO use prepared statements here
		DBConnection.getInstance().query("update genres set genre=\'"+newGenre+"\' where genre=\'"+oldGenre+"\'");
		refresh();
}
	
	/**
	 * remove genre from db and refresh the cache
	 * @param genre
	 */
	public static void removeGenre(String genre){
		//TODO use prepared statements here
		DBConnection.getInstance().query("delete from genres where genre=\'"+genre+"\'");
		refresh();
	}
	
	/**
	 * @return  Enumeration containing all available Genres
	 */
	public static Enumeration<String> getAllGenres(){
		return instance.cache.elements();
	}

}
