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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class contains the parameters required to create a logger and is used
 * with the logger form because logger objects cannot be created directly
 */
public class LoggerProxy {
	protected Logger target;
	protected boolean existing;
	
	protected String name;
	protected Level level;
	
	/**
	 * Default constructor
	 */
	public LoggerProxy() {
	}
	
	/**
	 * Creates a logger proxy object from an existing logger
	 * @param logger the logger
	 * @return the logger target
	 */
	public LoggerProxy(Logger target) {
		this.target = target;
		this.existing = true;
		
		this.name = target.getName();
		this.level = target.getLevel();
	}
	
	public void updateTarget() {
		// Create target if it doesn't exist already
		if (target == null)
			target = LogManager.getLogger(name);
		
		target.setLevel(level);
	}
	
	/**
	 * Gets the name of the logger
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the logger
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the level of the logger
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Sets the name of the logger
	 * @param level the level to set
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * @return the existing
	 */
	public boolean isExisting() {
		return existing;
	}
}
