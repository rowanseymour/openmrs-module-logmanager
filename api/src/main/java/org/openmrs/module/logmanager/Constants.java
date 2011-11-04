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

import org.openmrs.web.WebConstants;

/**
 * Constants used by the log manager module
 */
public class Constants {
	// Module properties
	public static final String MODULE_ID = "logmanager";
	public static final String MODULE_PACKAGE = "org.openmrs.module." + MODULE_ID;
	
	// Privileges
	public static final String PRIV_VIEW_SERVER_LOG = "View Server Log";
	public static final String PRIV_MANAGE_SERVER_LOG = "Manage Server Log";
	
	// Global property keys
	public static final String PROP_LOAD_EXTERNAL_CONFIG_ON_STARTUP = MODULE_ID + ".loadExternalConfigOnStartup";
	public static final String PROP_SAVE_EXTERNAL_CONFIG_ON_SHUTDOWN = MODULE_ID + ".saveExternalConfigOnShutdown";
	public static final String PROP_SYSTEM_APPENDER_NAME = MODULE_ID + ".systemAppenderName";
	public static final String PROP_ALWAYS_RECREATE_SYSTEM_APPENDER = MODULE_ID + ".alwaysRecreateSystemAppender";
	public static final String PROP_LOG_UNCAUGHT_EXCEPTIONS = MODULE_ID + ".logUncaughtExceptions";
	
	// Global property defaults
	public static final boolean DEF_LOAD_EXTERNAL_CONFIG_ON_STARTUP = true;
	public static final boolean DEF_SAVE_EXTERNAL_CONFIG_ON_SHUTDOWN = true;
	public static final String DEF_SYSTEM_APPENDER_NAME = "MEMORY_APPENDER";
	public static final boolean DEF_ALWAYS_RECREATE_SYSTEM_APPENDER = true;
	public static final boolean DEF_LOG_UNCAUGHT_EXCEPTIONS = true;
	
	// Appender defaults / limits
	public static final String DEF_LAYOUT_CONVERSION_PATTERN = "%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n";
	public static final int MAX_MEMORY_APPENDER_BUFFER_SIZE = 10000;				// # events
	public static final int MAX_FILE_APPENDER_BUFFER_SIZE = 1048576; 				// 1 MiB
	public static final long MAX_ROLLING_FILE_APPENDER_MAX_FILE_SIZE = 104857600; 	// 100 MiB
	public static final int MAX_ROLLING_FILE_APPENDER_MAX_BACKUP_INDEX = 100; 		// # files
	public static final int MIN_SOCKET_APPENDER_PORT = 1024;
	public static final int MAX_SOCKET_APPENDER_PORT = 65535;
	public static final String DEF_NT_EVENT_LOG_APPENDER_SOURCE = "OpenMRS";
	
	public static final String INTERNAL_CONFIG_NAME = "log4j.xml";
	public static final String EXTERNAL_CONFIG_NAME = WebConstants.WEBAPP_NAME + "-log4j.xml";
	
	// Other constants
	public static final int EVENT_REPORT_PREV_EVENTS = 5;
	public static final int RESULTS_PAGE_SIZE = 25;
	public static final int VIEWER_PAGE_SIZE = 100;
	public static final String LOGGER_API_PROFILING = "org.openmrs.api";
	public static final String LOGGER_HIBERNATE_SQL = "org.hibernate.SQL";
	
	// Because Java enums are a pain to use with request parameters..
	public static final int BOOL_OP_LE = -1;
	public static final int BOOL_OP_EQ = 0;
	public static final int BOOL_OP_GE = 1;
}
