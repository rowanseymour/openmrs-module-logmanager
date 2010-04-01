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
		
		// Create / modify the memory appender defined in OpenMRS's log4j.xml
		// and configure it to be used as the system appender
		MemoryAppender sysApp = (MemoryAppender)LogManager.getRootLogger().getAppender(Constants.SYSTEM_APPENDER_NAME);
		if (sysApp == null) {
			sysApp = new MemoryAppender();
			sysApp.setBufferSize(Constants.DEF_SYSTEM_APPENDER_SIZE);
			sysApp.activateOptions();
			LogManager.getRootLogger().addAppender(sysApp);
		}
		else if (sysApp.getBufferSize() != Constants.DEF_SYSTEM_APPENDER_SIZE) {
			sysApp.setBufferSize(Constants.DEF_SYSTEM_APPENDER_SIZE);
			sysApp.activateOptions();
		}
		
		// Store as static member of AppenderProxy so it can't be lost
		// even if another module now modifies the root log4j logger
		AppenderProxy.setSystemAppender(new AppenderProxy(sysApp));
	}
	

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down log manager module");
	}
}
