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

import org.apache.log4j.Level;

public class IconFactory {
	protected static Map<Level, String> levelIcons = new HashMap<Level, String>();
	protected static Map<Level, String> levelLabels = new HashMap<Level, String>();
	
	static {
		levelIcons.put(Level.ALL, "icon_all.png");
		levelIcons.put(Level.TRACE, "icon_trace.png");
		levelIcons.put(Level.DEBUG, "icon_debug.png");
		levelIcons.put(Level.INFO, "icon_info.png");
		levelIcons.put(Level.WARN, "icon_warn.png");
		levelIcons.put(Level.ERROR, "icon_error.png");
		levelIcons.put(Level.FATAL, "icon_fatal.png");
		levelIcons.put(Level.OFF, "icon_off.png");
		
		levelLabels.put(Level.ALL, "All");
		levelLabels.put(Level.TRACE, "Trace");
		levelLabels.put(Level.DEBUG, "Debug");
		levelLabels.put(Level.INFO, "Info");
		levelLabels.put(Level.WARN, "Warn");
		levelLabels.put(Level.ERROR, "Error");
		levelLabels.put(Level.FATAL, "Fatal");
		levelLabels.put(Level.OFF, "Off");
	}
	
	public static Map<Level, String> getLevelIconMap() {
		return levelIcons;
	}
	
	public static Map<Level, String> getLevelLabelMap() {
		return levelLabels;
	}
}
