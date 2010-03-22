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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.openmrs.module.Activator;
import org.openmrs.util.MemoryAppender;

/**
 * This class contains the logic that is run every time this module is either
 * started or shutdown
 */
public class ModuleActivator implements Activator {

	private static final Log log = LogFactory.getLog(ModuleActivator.class);

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting log manager module");
		
		Config config = Config.getInstance();
		
		// Check for OpenMRS's default memory appender which sometimes get's
		// nuked by misbehaving modules, and recreate it if it doesn't exist
		if (LogManager.getRootLogger().getAppender(config.getDefaultAppenderName()) == null
				&& config.isRecreateDefaultAppender()) {
			
			MemoryAppender memApp = new MemoryAppender();
			memApp.setName(config.getDefaultAppenderName());
			memApp.activateOptions();
			LogManager.getRootLogger().addAppender(memApp);
			
			log.warn("Default appender had to be recreated. This is likely due to a module modifying the root logger.");
		}
	}
	

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down log manager module");
	}
}
