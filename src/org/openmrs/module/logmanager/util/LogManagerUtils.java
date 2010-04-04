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
package org.openmrs.module.logmanager.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.Config;
import org.openmrs.util.MemoryAppender;

/**
 * Various utility methods
 */
public class LogManagerUtils {
	
	protected static final Log log = LogFactory.getLog(LogManagerUtils.class);
	
	/**
	 * Ensure that the memory appender defined in OpenMRS's log4j.xml exists
	 * and configure it to be used as the system appender
	 * @return true if appender already existed
	 */
	public static boolean ensureSystemAppenderExists() {
		boolean existed = true;
		
		String sysAppName = Config.getCurrent().getSystemAppenderName();
		
		MemoryAppender sysApp = (MemoryAppender)LogManager.getRootLogger().getAppender(sysAppName);
		
		// If appender wasn't found, recreate it
		if (sysApp == null) {
			sysApp = new MemoryAppender();
			sysApp.setName(sysAppName);
			sysApp.activateOptions();
			LogManager.getRootLogger().addAppender(sysApp);
			existed = false;
		}
		
		// Store as static member of AppenderProxy so it can't be lost
		// even if another module now modifies the root log4j logger
		AppenderProxy.setSystemAppender(new AppenderProxy(sysApp));
		
		return existed;
	}
	
	/**
	 * Gets the value of a protected/private field of an object
	 * @param obj the object
	 * @param fieldName the name of the field
	 * @return
	 */
	public static Object getPrivateField(Object obj, String fieldName) {
		// Find the private field
		Field fields[] = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					return fields[i].get(obj);
				} catch (IllegalAccessException ex) {
					log.warn("Unable to access " + fieldName + " field on "
							+ obj.getClass().getSimpleName());
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets whether the server is running any version of Microsoft Windows
	 * @return true if server is Windows
	 */
	public static boolean isWindowsServer() {
		return System.getProperty("os.name").contains("Windows");
	}
	
	/**
	 * Creates an alphabetically sorted map of module names and versions
	 * @return the map
	 */
	public static Map<String, String> createModuleVersionMap() {
		Map<String, String> modMap = new TreeMap<String, String>();
		Collection<Module> modules = ModuleFactory.getStartedModules();
		for (Module module : modules)
			modMap.put(module.getName(), module.getVersion());
		return modMap;
	}
}
