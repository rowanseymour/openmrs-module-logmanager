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
package org.openmrs.module.logmanager.validator;

import org.openmrs.module.logmanager.Options;
import org.openmrs.module.logmanager.Constants;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for config objects
 */
public class OptionsValidator implements Validator {

	/**
	 * @see org.springframework.validation.Validator#supports(Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return Options.class.isAssignableFrom(clazz);
	}

	/**
	 * @see org.springframework.validation.Validator#validate(Object, Errors)
	 */
	public void validate(Object obj, Errors errors) {
		Options config = (Options)obj;
		
		if (!config.getSystemAppenderName().matches("[\\w\\.\\- ]+"))
			errors.rejectValue("systemAppenderName", Constants.MODULE_ID + ".error.name");
	}
}
