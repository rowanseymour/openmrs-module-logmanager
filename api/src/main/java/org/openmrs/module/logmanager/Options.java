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
package org.openmrs.module.logmanager;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * Holds the configuration options for this module
 */
public class Options {
	
	protected static Options config;
	
	protected boolean loadExternalConfigOnStartup;
	protected boolean saveExternalConfigOnShutdown;
	protected String systemAppenderName;
	protected boolean alwaysRecreateSystemAppender;
	protected boolean logUncaughtExceptions;
	
	/**
	 * The default constructor
	 */
	public Options() {
		load(); 
	}
	
	/**
	 * Gets the singleton instance of this class
	 * @return the config instance
	 */
	public static Options getCurrent() {
		if (config == null)
			config = new Options();
		return config;
	}
	
	/**
	 * Sets the singleton instance of this class
	 * @param config the config instance
	 */
	public static void setCurrent(Options config) {
		Options.config = config;
	}
	
	/**
	 * Loads the configuration from global properties
	 */
	public void load() {
		loadExternalConfigOnStartup = loadBooleanOption(Constants.PROP_LOAD_EXTERNAL_CONFIG_ON_STARTUP, Constants.DEF_LOAD_EXTERNAL_CONFIG_ON_STARTUP);
		saveExternalConfigOnShutdown = loadBooleanOption(Constants.PROP_SAVE_EXTERNAL_CONFIG_ON_SHUTDOWN, Constants.DEF_SAVE_EXTERNAL_CONFIG_ON_SHUTDOWN);
		systemAppenderName = loadStringOption(Constants.PROP_SYSTEM_APPENDER_NAME, Constants.DEF_SYSTEM_APPENDER_NAME);
		alwaysRecreateSystemAppender = loadBooleanOption(Constants.PROP_ALWAYS_RECREATE_SYSTEM_APPENDER, Constants.DEF_ALWAYS_RECREATE_SYSTEM_APPENDER);
		logUncaughtExceptions = loadBooleanOption(Constants.PROP_LOG_UNCAUGHT_EXCEPTIONS, Constants.DEF_LOG_UNCAUGHT_EXCEPTIONS);
	}
	
	/**
	 * Saves the configuration to global properties
	 */
	public void save() {
		saveOption(Constants.PROP_LOAD_EXTERNAL_CONFIG_ON_STARTUP, loadExternalConfigOnStartup);
		saveOption(Constants.PROP_SAVE_EXTERNAL_CONFIG_ON_SHUTDOWN, saveExternalConfigOnShutdown);
		saveOption(Constants.PROP_SYSTEM_APPENDER_NAME, systemAppenderName);
		saveOption(Constants.PROP_ALWAYS_RECREATE_SYSTEM_APPENDER, alwaysRecreateSystemAppender);
		saveOption(Constants.PROP_LOG_UNCAUGHT_EXCEPTIONS, logUncaughtExceptions);
	}
	
	/**
	 * Gets whether the external configuration should be loaded on module startup
	 * @return true if configuration should be loaded
	 */
	public boolean isLoadExternalConfigOnStartup() {
		return loadExternalConfigOnStartup;
	}

	/**
	 * Sets whether the external configuration should be loaded on module startup
	 * @param loadExternalConfigOnStartup true if configuration should be loaded
	 */
	public void setLoadExternalConfigOnStartup(boolean loadExternalConfigOnStartup) {
		this.loadExternalConfigOnStartup = loadExternalConfigOnStartup;
	}
	
	/**
	 * Gets whether the external configuration should be saved on module shutdown
	 * @return true if configuration should be saved
	 */
	public boolean isSaveExternalConfigOnShutdown() {
		return saveExternalConfigOnShutdown;
	}

	/**
	 * Sets whether the external configuration should be saved on module shutdown
	 * @param saveExternalConfigOnShutdown true if configuration should be saved
	 */
	public void setSaveExternalConfigOnShutdown(boolean saveExternalConfigOnShutdown) {
		this.saveExternalConfigOnShutdown = saveExternalConfigOnShutdown;
	}

	/**
	 * Gets the name of the system appender
	 * @return the system appender name
	 */
	public String getSystemAppenderName() {
		return systemAppenderName;
	}

	/**
	 * Sets the name of the system appender
	 * @param systemAppenderName the system appender name
	 */
	public void setSystemAppenderName(String systemAppenderName) {
		this.systemAppenderName = systemAppenderName;
	}

	/**
	 * Gets whether system appender should always be recreated if it doesn't exist
	 * @return the true if it should be recreated
	 */
	public boolean isAlwaysRecreateSystemAppender() {
		return alwaysRecreateSystemAppender;
	}

	/**
	 * Sets whether system appender should always be recreated if it doesn't exist
	 * @param alwaysRecreateSystemAppender the true if it should be recreated
	 */
	public void setAlwaysRecreateSystemAppender(boolean alwaysRecreateSystemAppender) {
		this.alwaysRecreateSystemAppender = alwaysRecreateSystemAppender;
	}
	
	/**
	 * Gets whether the module should log uncaught exceptions
	 * @return true to enable logging of uncaught exceptions
	 */
	public boolean isLogUncaughtExceptions() {
		return logUncaughtExceptions;
	}

	/**
	 * Sets whether the module should log uncaught exceptions
	 * @param logUncaughtExceptions true to enable logging of uncaught exceptions
	 */
	public void setLogUncaughtExceptions(boolean logUncaughtExceptions) {
		this.logUncaughtExceptions = logUncaughtExceptions;
	}

	/**
	 * Utility method to load a string option from global properties
	 * @param name the name of the global property
	 * @param def the default value if global property is invalid
	 * @return the string value
	 */
	private static String loadStringOption(String name, String def) {
		AdministrationService svc = Context.getAdministrationService();
		String s = svc.getGlobalProperty(name);
		return (s != null) ? s : def;
	}
	
	/**
	 * Utility method to load an integer option from global properties
	 * @param name the name of the global property
	 * @param def the default value if global property is invalid
	 * @return the integer value
	 */
	@SuppressWarnings("unused")
	private static int loadIntOption(String name, int def) {
		AdministrationService svc = Context.getAdministrationService();
		String s = svc.getGlobalProperty(name);
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Utility method to load an boolean option from global properties
	 * @param name the name of the global property
	 * @return the boolean value
	 */
	private static boolean loadBooleanOption(String name, boolean def) {
		AdministrationService svc = Context.getAdministrationService();
		String s = svc.getGlobalProperty(name);
		try {
			return Boolean.parseBoolean(s);
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Utility method to save an option to global properties
	 * @param name the name of the global property
	 * @param value the value of the global property
	 */
	private static void saveOption(String name, Object value) {
		AdministrationService svc = (AdministrationService)Context.getAdministrationService();
		GlobalProperty property = svc.getGlobalPropertyObject(name);
		property.setPropertyValue(String.valueOf(value));
		svc.saveGlobalProperty(property);
	}
}
