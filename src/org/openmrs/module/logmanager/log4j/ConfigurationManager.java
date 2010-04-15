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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.Options;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.util.MemoryAppender;
import org.openmrs.util.OpenmrsUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	 * Loads the internal log4j configuration from OpenMRS source
	 * @return true if configuration was loaded successfully
	 */
	public static boolean loadInternalConfiguration() {
		try {
			URL url = ConfigurationManager.class.getResource("/" + Constants.INTERNAL_CONFIG_NAME);	
			Reader reader = new InputStreamReader(url.openStream());			
			Document document = LogManagerUtils.readDocument(reader, new Log4jEntityResolver());
			parseConfiguration(document);
			reader.close();
		} catch (Exception e) {
			log.error("Unable to open internal log4j configuration", e);
			return false;
		}
		return true;
	}

	/**
	 * Loads the external log4j configuration file used by this module
	 * @return true if file was loaded successfully
	 */
	public static boolean loadExternalConfiguration() {
		String path = OpenmrsUtil.getApplicationDataDirectory() + File.separator + Constants.EXTERNAL_CONFIG_NAME;
		File file = new File(path);
		if (!file.exists())
			return false;
			
		try {
			Reader reader = new FileReader(file);
			Document document = LogManagerUtils.readDocument(reader, new Log4jEntityResolver());
			reader.close();		
			if (document == null)
				return false;
			parseConfiguration(document);
			
		} catch (Exception e) {
			log.warn("Unable to open external log4j configuration");
			return false;
		}
			
		return true;
	}
	
	/**
	 * Saves the log4j configuration to the external file used by this module
	 */
	public static void saveExternalConfiguration() {
		try {
			String path = OpenmrsUtil.getApplicationDataDirectory() + File.separator + Constants.EXTERNAL_CONFIG_NAME;
			FileWriter writer = new FileWriter(path);

			Document document = DOMConfigurationBuilder.currentConfiguration();
			LogManagerUtils.writeDocument(document, writer);
			
			writer.close();
			
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	/**
	 * Loads log4j configurations from specified modules
	 * @param the list of module ids from which configurations should be loaded
	 * @return true if all log4j files were loaded successfully
	 */
	public static boolean loadModuleConfigurations(String[] moduleIds) {
		boolean allSucceeded = true;
		
		// Load from each specified module
		for (String moduleId : moduleIds) {
			try {
				Module module = ModuleFactory.getModuleById(moduleId);
				Document log4jDoc = module.getLog4j();
				if (module.getLog4j() != null)
					parseConfiguration(log4jDoc);
			} catch (Exception e) {
				log.error(e);
				allSucceeded = false;
			}
		}
		
		return allSucceeded;
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
