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

import java.util.Collection;

import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.log4j.LayoutProxy;
import org.openmrs.module.logmanager.log4j.LayoutType;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for appender objects
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
		LayoutProxy layout = appender.getLayout();
		
		// General validation
		if (!appender.getName().matches("[\\w\\.\\- ]+"))
			errors.rejectValue("name", Constants.MODULE_ID + ".error.name");
		else if (!appender.isExisting() && isAppenderNameInUse(appender.getName()))
			errors.rejectValue("name", Constants.MODULE_ID + ".error.nameAlreadyInUse");	
		
		// Validate the layout
		if (layout != null)
			validateLayout(layout, errors);

		// Subclass validation
		switch (appender.getType()) {
		case MEMORY:
			validateMemoryAppender(appender, errors);
			break;
		case FILE:
			validateFileAppender(appender, errors);
			break;
		case ROLLING_FILE:
			validateRollingFileAppender(appender, errors);
			break;
		case DAILY_ROLLING_FILE:
			validateDailyRollingFileAppender(appender, errors);
			break;
		case SOCKET:
			validateSocketAppender(appender, errors);
			break;
		case NT_EVENT_LOG:
			validateNTEventLogAppender(appender, errors);
			break;
		}		
	}
	
	private void validateLayout(LayoutProxy layout, Errors errors) {
		if (layout.getType() == LayoutType.PATTERN && layout.getConversionPattern().isEmpty())
			errors.rejectValue("layout.conversionPattern", Constants.MODULE_ID + ".error.layout.conversionPattern");
	}
	
	/**
	 * Validation specific to memory appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateMemoryAppender(AppenderProxy appender, Errors errors) {
		validatePropertyRange(appender, "bufferSize",
				1, Constants.MAX_MEMORY_APPENDER_BUFFER_SIZE, errors);
	}
	
	/**
	 * Validation specific to file appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateFileAppender(AppenderProxy appender, Errors errors) {	
		// Check if file is writable
		String file = (String)appender.getProperty("file");
		
		// Check if file path is not empty
		if (file == null || file.isEmpty())
			errors.rejectValue("properties.file", Constants.MODULE_ID + ".error.empty");
		else if (!LogManagerUtils.isPathWritable(file))
			errors.rejectValue("properties.file", Constants.MODULE_ID + ".error.fileNotWritable");
		
		validatePropertyRange(appender, "bufferSize", 1, Constants.MAX_FILE_APPENDER_BUFFER_SIZE, errors);
	}
	
	/**
	 * Validation specific to rolling file appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateRollingFileAppender(AppenderProxy appender, Errors errors) {	
		// Validate superclass
		validateFileAppender(appender, errors);
		
		validatePropertyRange(appender, "maximumFileSize",
				0l, Constants.MAX_ROLLING_FILE_APPENDER_MAX_FILE_SIZE, errors);
		
		validatePropertyRange(appender, "maxBackupIndex",
				0, Constants.MAX_ROLLING_FILE_APPENDER_MAX_BACKUP_INDEX, errors);
	}
	
	/**
	 * Validation specific to daily rolling file appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateDailyRollingFileAppender(AppenderProxy appender, Errors errors) {	
		// Validate superclass
		validateFileAppender(appender, errors);
		
		validateStringPropertyNotEmpty(appender, "datePattern", errors);
	}
	
	/**
	 * Validation specific to socket appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateSocketAppender(AppenderProxy appender, Errors errors) {	
		validateStringPropertyNotEmpty(appender, "remoteHost", errors);
		
		validatePropertyRange(appender, "port", Constants.MIN_SOCKET_APPENDER_PORT,
				Constants.MAX_SOCKET_APPENDER_PORT, errors);
		
		int reconnectionDelay = (Integer)appender.getProperty("reconnectionDelay");
		if (reconnectionDelay < 0)
			errors.rejectValue("properties.reconnectionDelay", Constants.MODULE_ID + ".error.reconnectionDelay");
	}
	
	/**
	 * Validation specific to NT event log appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateNTEventLogAppender(AppenderProxy appender, Errors errors) {
		
		validateStringPropertyNotEmpty(appender, "source", errors);
	}
	
	/**
	 * Validates a specific integer property
	 * @param appender the appender
	 * @param name the property name
	 * @param errors the errors
	 */
	private void validateStringPropertyNotEmpty(AppenderProxy appender, String name, Errors errors) {
		String value = (String)appender.getProperty(name);
		
		if (value == null || value.isEmpty())
			errors.rejectValue("properties." + name, Constants.MODULE_ID + ".error.empty");
	}
	
	/**
	 * Validates a specific integer property
	 * @param appender the appender
	 * @param name the property name
	 * @param min the minimum value (inclusive)
	 * @param max the maximum value (inclusive)
	 * @param errors the errors
	 */
	@SuppressWarnings("unchecked")
	private <T extends Comparable<T>> void validatePropertyRange(AppenderProxy appender, String name, T min, T max, Errors errors) {
		T value = (T)appender.getProperty(name);
		
		if (value.compareTo(min) < 0 || value.compareTo(max) > 0)
			errors.rejectValue("properties." + name, Constants.MODULE_ID + ".error.range", new Object[]{ min, max }, "");
	}
	
	/**
	 * Checks to see if the given appender name is already being used
	 * @param name the name to check
	 * @return true if name is in use
	 */
	private boolean isAppenderNameInUse(String name) {
		LogManagerService svc = Context.getService(LogManagerService.class);
		Collection<AppenderProxy> appenders = svc.getAppenders(false);
		for (AppenderProxy appender : appenders)
			if (name.equals(appender.getName()))
				return true;
		return false;
	}
}
