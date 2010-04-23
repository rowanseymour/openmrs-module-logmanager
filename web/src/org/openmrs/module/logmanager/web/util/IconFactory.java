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
package org.openmrs.module.logmanager.web.util;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.logmanager.log4j.LevelProxy;

public class IconFactory {
	protected static Map<LevelProxy, String> levelIcons = new HashMap<LevelProxy, String>();

	static {
		levelIcons.put(LevelProxy.ALL, "icon_all.png");
		levelIcons.put(LevelProxy.TRACE, "icon_trace.png");
		levelIcons.put(LevelProxy.DEBUG, "icon_debug.png");
		levelIcons.put(LevelProxy.INFO, "icon_info.png");
		levelIcons.put(LevelProxy.WARN, "icon_warn.png");
		levelIcons.put(LevelProxy.ERROR, "icon_error.png");
		levelIcons.put(LevelProxy.FATAL, "icon_fatal.png");
		levelIcons.put(LevelProxy.OFF, "icon_off.png");
	}
	
	/**
	 * Gets the map of levels to icon filenames
	 * @return the map
	 */
	public static Map<LevelProxy, String> getLevelIconMap() {
		return levelIcons;
	}
}
