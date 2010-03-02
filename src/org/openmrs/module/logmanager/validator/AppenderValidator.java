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

import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.AppenderType;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LayoutType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for Appender objects
 */
public class AppenderValidator implements Validator {

	/**
	 * @see org.springframework.validation.Validator#supports(Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return AppenderProxy.class.isAssignableFrom(clazz);
	}

	/**
	 * @see org.springframework.validation.Validator#validate(Object, Errors)
	 */
	public void validate(Object obj, Errors errors) {
		AppenderProxy appender = (AppenderProxy)obj;
		
		// General validation
		if (!appender.getName().matches("[\\w\\.\\- ]+"))
			errors.rejectValue("name", Constants.MODULE_ID + ".error.name");
		if (appender.getLayoutType() == LayoutType.PATTERN && appender.getLayoutPattern().isEmpty())
			errors.rejectValue("layout", Constants.MODULE_ID + ".error.layout");
		
		// Subclass validation
		if (appender.getType() == AppenderType.MEMORY)
			validateMemoryAppender(appender, errors);
		else if (appender.getType() == AppenderType.SOCKET)
			validateSocketAppender(appender, errors);
	}
	
	/**
	 * Validation specific to memory appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateMemoryAppender(AppenderProxy appender, Errors errors) {
		if (appender.getBufferSize() < 1 || appender.getBufferSize() > 100000)
			errors.rejectValue("bufferSize", Constants.MODULE_ID + ".error.bufferSize");
	}
	
	/**
	 * Validation specific to socket appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateSocketAppender(AppenderProxy appender, Errors errors) {
		if (appender.getRemoteHost().isEmpty())
			errors.rejectValue("host", Constants.MODULE_ID + ".error.host");
		if (appender.getPort() < 0 || appender.getPort() > 65535)
			errors.rejectValue("port", Constants.MODULE_ID + ".error.port");
	}
}
