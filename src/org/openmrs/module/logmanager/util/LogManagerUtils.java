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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.AppenderType;
import org.openmrs.module.logmanager.QueryField;
import org.openmrs.web.WebConstants;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Various utility methods
 */
public class LogManagerUtils {
	
	protected static final Log log = LogFactory.getLog(LogManagerUtils.class);
	
	/**
	 * Utility method to get a parsed appender type parameter
	 * @param request the HTTP request object
	 * @param name the name of the parameter
	 * @param def the default value if parameter doesn't exist or is invalid
	 * @return the appender type value
	 */
	public static AppenderType getAppenderTypeParameter(HttpServletRequest request, String name, AppenderType def) {
		String str = request.getParameter(name);
		if (str != null) {
			try {
				int i = Integer.parseInt(str);
				return AppenderType.values()[i];
			} catch (Exception ex) {
				log.warn("Invalid appender type value: " + str);
			}
		}
		return def;
	}
	
	/**
	 * Utility method to get a parsed query field parameter
	 * @param request the HTTP request object
	 * @param name the name of the parameter
	 * @param def the default value if parameter doesn't exist or is invalid
	 * @return the query field value
	 */
	public static QueryField getQueryFieldParameter(HttpServletRequest request, String name, QueryField def) {
		String str = request.getParameter(name);
		if (str != null) {
			try {
				int i = Integer.parseInt(str);
				return QueryField.values()[i];
			} catch (Exception ex) {
				log.warn("Invalid query field value: " + str);
			}
		}
		return def;
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
	 * Sets the OpenMRS info message value which is displayed at the top of the page
	 * @param request the request object
	 * @param msgs the message source
	 * @param code the message code
	 */
	public static void setInfoMessage(HttpServletRequest request, MessageSourceAccessor msgs, String code) {
		String msg = msgs.getMessage(code);
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, msg);
	}
	
	/**
	 * Sets the OpenMRS error message value which is displayed at the top of the page
	 * @param request the request object
	 * @param msgs the message source
	 * @param code the message code
	 */
	public static void setErrorMessage(HttpServletRequest request, MessageSourceAccessor msgs, String code) {
		String msg = msgs.getMessage(code);
		request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, msg);
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
