/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.logmanager.log4j;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.openmrs.module.logmanager.Options;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.util.MemoryAppender;
import org.w3c.dom.Document;

/**
 * Configuration management for log4j
 */
public class ConfigurationManager {
	
	protected static final Log log = LogFactory.getLog(ConfigurationManager.class);
	
	/**
	 * Clears the log4j configuration
	 */
	public static void clearConfiguration() {
		BasicConfigurator.resetConfiguration();
	}
	
	/**
	 * Loads the given DOM document as a log4j configuration 
	 * @param document the DOM document
	 */
	public static void parseConfiguration(Document document) {	
		
		DOMConfigurator.configure(document.getDocumentElement());
		
		// Reloading the OpenMRS log4j.xml file may recreate the appender
		// being used as the system appender, so reset the system appender
		resetSystemAppender();
		
		log.debug("Parsed log4j configuration from DOM document");
	}
	
	/**
	 * Reads the configuration document at the specified path
	 * @param path the path of the document
	 * @return the document
	 */
	public static Document readConfiguration(String path) {
		File file = new File(path);
		if (!file.exists())
			return null;
		
		try {
			Reader reader = new FileReader(file);
			Document document = LogManagerUtils.readDocument(reader, new Log4jEntityResolver());
			reader.close();		
			return document;
		} catch (Exception e) {
			log.error(e);
			return null;
		}	
	}
	
	/**
	 * Resets the system appender after configuration changes, i.e. check if an
	 * appender exists in the log4j system with the system appender name, and if 
	 * so update the static member of AppenderProxy
	 */
	public static void resetSystemAppender() {
		String sysAppName = Options.getCurrent().getSystemAppenderName();
		MemoryAppender sysApp = (MemoryAppender)LogManager.getRootLogger().getAppender(sysAppName);
		if (sysApp != null) {
			AppenderProxy.setSystemAppender(new AppenderProxy(sysApp));
			log.info("Reset system appender (" + sysAppName + ")");
		}
	}
	
	/**
	 * Ensure that the system appender exists
	 * and configure it to be used as the system appender
	 * @return true if appender already existed
	 */
	public static boolean ensureSystemAppenderExists() {
		boolean existed = true;
		
		String sysAppName = Options.getCurrent().getSystemAppenderName();
		
		MemoryAppender sysApp = (MemoryAppender)LogManager.getRootLogger().getAppender(sysAppName);
		
		// If appender wasn't found, recreate it
		if (sysApp == null) {
			sysApp = new MemoryAppender();
			sysApp.setName(sysAppName);
			sysApp.setLayout(new PatternLayout(Constants.DEF_LAYOUT_CONVERSION_PATTERN));
			sysApp.activateOptions();
			LogManager.getRootLogger().addAppender(sysApp);
			existed = false;
			
			log.warn("System appender (" + sysAppName + ") had to be recreated. This may be due to another module modifying the root logger.");
		}
		
		// Store as static member of AppenderProxy so it can't be lost
		// even if another module now modifies the root log4j logger
		AppenderProxy.setSystemAppender(new AppenderProxy(sysApp));
		
		return existed;
	}
}
