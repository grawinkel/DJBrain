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

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author meatz
 *
 */
public class LogAppender implements Appender {

	private LogWindow logWindow;
	
	public LogAppender(LogWindow logWindow){
		this.logWindow = logWindow;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#addFilter(org.apache.log4j.spi.Filter)
	 */
	public void addFilter(Filter arg0) {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#clearFilters()
	 */
	public void clearFilters() {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#doAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	public void doAppend(LoggingEvent event) {
		logWindow.doAppend(event);	
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getFilter()
	 */
	public Filter getFilter() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getLayout()
	 */
	public Layout getLayout() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getName()
	 */
	public String getName() {
			return "LogWindow";
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
			return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#setErrorHandler(org.apache.log4j.spi.ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler arg0) {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#setLayout(org.apache.log4j.Layout)
	 */
	public void setLayout(Layout arg0) {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#setName(java.lang.String)
	 */
	public void setName(String arg0) {
			
	}

}
