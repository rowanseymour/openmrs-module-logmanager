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

import java.net.URL;

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
	 * Reloads the log4j configuration
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
				String sysAppName = Config.getCurrent().getSystemAppenderName();
				MemoryAppender sysApp = (MemoryAppender)LogManager.getRootLogger().getAppender(sysAppName);
				if (sysApp != null)
					AppenderProxy.setSystemAppender(new AppenderProxy(sysApp));
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
