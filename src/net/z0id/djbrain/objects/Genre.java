/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.objects;

/**
 * @author meatz
 *
 */
public class Genre {
	
private int id;
private String genre="";

/**
 * 
 */
public Genre(){
}

/**
 * @param id
 * @param genre
 */
public Genre(int id , String genre){
	this.id = id;
	this.genre = genre;
}


/**
 * @return genre
 */
public String getGenre() {
	return genre;
}


/**
 * @param genre
 */
public void setGenre(String genre) {
	this.genre = genre;
}


/**
 * @return id
 */
public int getId() {
	return id;
}


/**
 * @param id
 */
public void setId(int id) {
	this.id = id;
}

@Override
public String toString() {
	return id+" - " + genre;
}


}
