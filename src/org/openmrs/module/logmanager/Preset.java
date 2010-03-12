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

import java.util.HashMap;
import java.util.Map;

public class Preset {
	protected int presetId;
	protected String name;
	protected Map<String, Integer> loggerMap;
	
	/**
	 * Default constructor
	 */
	public Preset() {
	}
	
	/**
	 * Constructs a preset with the given name
	 * @param name the name
	 */
	public Preset(String name) {
		this.name = name;
		loggerMap = new HashMap<String, Integer>();
	}
	
	/**
	 * Gets the id
	 * @return the id
	 */
	public int getPresetId() {
		return presetId;
	}
	
	/**
	 * Sets the id
	 * @param id the id
	 */
	public void setPresetId(int presetId) {
		this.presetId =presetId;
	}

	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the map of logger names to log levels
	 * @return the map
	 */
	public Map<String, Integer> getLoggerMap() {
		return loggerMap;
	}
	
	/**
	 * Sets the map of logger names to log levels
	 * @param loggers the loggers to set
	 */
	public void setLoggerMap(Map<String, Integer> loggerMap) {
		this.loggerMap = loggerMap;
	}
}
