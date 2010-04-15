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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;
import org.openmrs.module.logmanager.log4j.ConfigurationManager;
import org.openmrs.util.OpenmrsUtil;
import org.w3c.dom.Document;

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
		log.info("Starting log manager module...");
		
		// Look for external log4j.xml to load
		if (Options.getCurrent().isLoadExternalConfigOnStartup()) {
			String path = OpenmrsUtil.getApplicationDataDirectory() + File.separator + Constants.EXTERNAL_CONFIG_NAME;	
			Document document = ConfigurationManager.readConfiguration(path);	
			if (document != null) 
				ConfigurationManager.parseConfiguration(document);
		}
		
		// Ensure that the memory appender defined in OpenMRS's log4j.xml exists
		// and configure it to be used as the system appender
		if (Options.getCurrent().isAlwaysRecreateSystemAppender())
			ConfigurationManager.ensureSystemAppenderExists();			
	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down log manager module");
	}
}
