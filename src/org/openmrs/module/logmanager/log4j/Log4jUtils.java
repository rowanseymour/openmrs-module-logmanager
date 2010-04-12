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

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.Config;
import org.openmrs.util.MemoryAppender;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for log4j specific functions
 */
public class Log4jUtils {
	
	protected static final Log log = LogFactory.getLog(Log4jUtils.class);
	
	/**
	 * Clears the log4j configuration
	 */
	public static void clearConfiguration() {
		BasicConfigurator.resetConfiguration();
	}
	
	/**
	 * Loads the given input stream as a log4j configuration 
	 * @param input the input stream
	 * @return true if loading was successful, else false
	 */
	public static boolean loadConfiguration(InputStream input) {
		try {
			// Parse as DOM document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			Document log4jDoc = builder.parse(input);
	
			return loadConfiguration(log4jDoc);
			
		} catch (Exception e) {
			log.error("Unable to parse log4j configuration document", e);
			return false;
		}
	}
	
	/**
	 * Loads the given DOM document as a log4j configuration 
	 * @param document the DOM document
	 * @return true if loading was successful, else false
	 */
	public static boolean loadConfiguration(Document document) {
		
		DOMConfigurator.configure(document.getDocumentElement());
		
		// Reloading the OpenMRS log4j.xml file may recreate the appender
		// being used as the system appender, so reset the system appender
		resetSystemAppender();
		
		log.debug("Loaded log4j configuration from DOM document");
		
		return false;
	}
	
	/**
	 * Reloads the log4j configuration from OpenMRS core and started modules
	 * @param loadMain true if the main OpenMRS log4j.xml should be loaded
	 * @param the list of module ids from which log4j configs should be loaded
	 * @return true if all log4j files were loaded successfully
	 */
	public static boolean loadConfiguration(boolean loadMain, String[] moduleIds) {
		boolean allSucceeded = true;
		
		if (loadMain) {
			// Load main OpenMRS log4j.xml
			try {
				URL url = Log4jUtils.class.getResource("/log4j.xml");
				InputStream input = url.openStream();
				loadConfiguration(input);
			} catch (Exception e) {
				allSucceeded = false;
				log.error("Unable to read OpenMRS log4j configuration document", e);
			}
		}
		
		// Load from each specified module
		for (String moduleId : moduleIds) {
			try {
				Module module = ModuleFactory.getModuleById(moduleId);
				Document log4jDoc = module.getLog4j();
				if (module.getLog4j() != null) {
					if (!loadConfiguration(log4jDoc))
						allSucceeded = false;
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				allSucceeded = false;
			}
		}
		
		return allSucceeded;
	}
	
	/**
	 * Resets the system appender after configuration changes
	 */
	public static void resetSystemAppender() {
		String sysAppName = Config.getCurrent().getSystemAppenderName();
		MemoryAppender sysApp = (MemoryAppender)LogManager.getRootLogger().getAppender(sysAppName);
		if (sysApp != null)
			AppenderProxy.setSystemAppender(new AppenderProxy(sysApp));
	}
	
	/**
	 * Checks to see if the given module is modifying log4j's root logger
	 * @param module the module to check
	 * @return true if root logger is being modified
	 */
	public static boolean isModuleModifyingRoot(Module module) {
		Element log4jDocElm = module.getLog4j().getDocumentElement();
		NodeList roots = log4jDocElm.getElementsByTagName("root");
		return roots.getLength() > 0;
	}
	
	/**
	 * Checks to see if the given module is modifying loggers outside of its namespace
	 * @param module the module to check
	 * @return true if loggers are being modified
	 */
	public static boolean isModuleModifyingLoggerOutsideNS(Module module) {
		String moduleNS = "org.openmrs.module." + module.getModuleId();
		
		Element log4jDocElm = module.getLog4j().getDocumentElement();
		NodeList loggers = log4jDocElm.getElementsByTagName("logger");
		for (int n = 0; n < loggers.getLength(); n++) {
			NamedNodeMap attrs = loggers.item(n).getAttributes();
			Node nameNode = attrs.getNamedItem("name");
			if (!nameNode.getNodeValue().startsWith(moduleNS))
				return true;
		}
		return false;
	}
}
