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
package org.openmrs.module.logmanager.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.openmrs.module.logmanager.impl.LevelProxy;

/**
 * Used to edit a logging level
 */
public class LevelEditor extends PropertyEditorSupport {

	/**
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	@Override
	public String getAsText() {
		return "" + (getValue() != null ? ((LevelProxy)getValue()).getIntValue() : "");
	}

	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!text.isEmpty()) {
			int ordinal = Integer.parseInt(text);
			setValue(new LevelProxy(ordinal));
		}
		else
			setValue(null);
	}
	
}
