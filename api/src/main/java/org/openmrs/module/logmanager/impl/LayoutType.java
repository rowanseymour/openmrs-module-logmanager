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
package org.openmrs.module.logmanager.impl;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.xml.XMLLayout;

/**
 * The types of layout supported
 */
public enum LayoutType {
	UNKNOWN,
	SIMPLE,
	TTCC,
	PATTERN,
	HTML,
	XML;
	
	/**
	 * Bean-property wrapper for the ordinal method so it can be used in EL
	 */
	public int getOrdinal() {
		return ordinal();
	}
	
	/**
	 * Overridden to show class names
	 */
	@Override
	public String toString() {
		switch (this) {
		case SIMPLE:
			return "Simple";
		case TTCC:
			return "TTCC";
		case PATTERN:
			return "Pattern";
		case HTML:
			return "HTML";
		case XML:
			return "XML";
		}
		return "Unknown";
	}
	
	/**
	 * Gets the type of the specified layout
	 * @param appender the appender whose type to return
	 * @return the type
	 */
	public static LayoutType fromLayout(Layout layout) {
		if (layout instanceof SimpleLayout)
			return SIMPLE;
		else if (layout instanceof TTCCLayout)
			return TTCC;
		else if (layout instanceof PatternLayout)
			return PATTERN;
		else if (layout instanceof HTMLLayout)
			return HTML;
		else if (layout instanceof XMLLayout)
			return XML;
		else
			return UNKNOWN;
	}
	
	/**
	 * Gets the equivalent enum value of the specified integer
	 * @param value the integer value
	 * @return the enum value
	 */
	public static LayoutType fromOrdinal(int value) {
		return LayoutType.values()[value];
	}
}
