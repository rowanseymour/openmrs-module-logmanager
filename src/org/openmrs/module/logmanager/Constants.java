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

/**
 * Constants used by the log manager module
 */
public class Constants {
	// Module properties
	public static final String MODULE_ID = "logmanager";
	
	// Privileges
	public static final String PRIV_MANAGE_SERVER_LOG = "Manage Server Log";
	
	// Defaults
	public static final String DEF_APPENDER = "MEMORY_APPENDER";
	public static final String DEF_LAYOUT = "%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n";
	public static final int DEF_PORT = 4560;
	
	public static final int RESULTS_PAGE_SIZE = 25;
	public static final int VIEWER_PAGE_SIZE = 100;
}
