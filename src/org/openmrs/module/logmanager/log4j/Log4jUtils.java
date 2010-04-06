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

public class Log4jUtils {
	
	protected static final Log log = LogFactory.getLog(Log4jUtils.class);
	
	/**
	 * Clears the log4j configuration
	 */
	public static void clearConfiguration() {
		BasicConfigurator.resetConfiguration();
	}
	
	/**
	 * Parses the given input stream as an XML log4j configuration 
	 * @param input the input stream
	 * @return true if parsing was successful, else false
	 */
	public static boolean parseConfiguration(InputStream input) {
		// Parse as DOM document
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			Document log4jDoc = builder.parse(input);
			DOMConfigurator.configure(log4jDoc.getDocumentElement());
			
			// Reloading the OpenMRS log4j.xml file may recreate the appender
			// being used as the system appender, so reset the system appender
			resetSystemAppender();

			log.info("Parsed log4j configuration file");	
			return true;
			
		} catch (Exception e) {
			log.warn("Invalid log4j configuration file", e);
			return false;
		}
	}
	
	/**
	 * Reloads the log4j configuration from OpenMRS core and started modules
	 * @param the list of module ids ($ means the main OpenMRS config)
	 */
	public static void reloadConfiguration(String[] moduleIds) {
		if (moduleIds == null)
			return;
		
		for (String moduleId : moduleIds) {
			if (moduleId.equals("$")) {
				// Load main OpenMRS log4j.xml
				URL url = Log4jUtils.class.getResource("/log4j.xml");
				DOMConfigurator.configure(url);
				
				// Reloading the OpenMRS log4j.xml file may recreate the appender
				// being used as the system appender, so reset the system appender
				resetSystemAppender();
			}
			else {
				try {
					Module module = ModuleFactory.getModuleById(moduleId);
					if (module.getLog4j() != null)
						DOMConfigurator.configure(module.getLog4j().getDocumentElement());
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}	
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
