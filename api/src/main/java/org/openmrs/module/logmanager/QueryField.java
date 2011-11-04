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
 * Different types of log view query
 */
public enum QueryField {
	LOGGER_NAME,
	CLASS_NAME,
	FILE_NAME;
	
	/**
	 * Bean-property wrapper for the ordinal method so it can be used in EL
	 */
	public int getOrdinal() {
		return ordinal();
	}
	
	/**
	 * Gets the equivalent enum value of the specified integer
	 * @param value the integer value
	 * @return the enum value
	 */
	public static QueryField fromOrdinal(int value) {
		return QueryField.values()[value];
	}
}
