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
		int bufferSize = (Integer)appender.getProperty("bufferSize");
		
		if (bufferSize < 1 || bufferSize > Constants.MAX_APPENDER_BUFFER_SIZE)
			errors.rejectValue("properties.bufferSize", Constants.MODULE_ID + ".error.bufferSize",
				new Object[]{ Constants.MAX_APPENDER_BUFFER_SIZE }, "");
	}
	
	/**
	 * Validation specific to socket appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateSocketAppender(AppenderProxy appender, Errors errors) {
		String remoteHost = (String)appender.getProperty("remoteHost");
		int port = (Integer)appender.getProperty("port");
		int reconnectionDelay = (Integer)appender.getProperty("reconnectionDelay");
		
		if (remoteHost == null || remoteHost.isEmpty())
			errors.rejectValue("properties.remoteHost", Constants.MODULE_ID + ".error.host");
		
		if (port < Constants.MIN_APPENDER_PORT || port > Constants.MAX_APPENDER_PORT)
			errors.rejectValue("properties.port", Constants.MODULE_ID + ".error.port",
				new Object[]{ Constants.MIN_APPENDER_PORT, Constants.MAX_APPENDER_PORT }, "");
		
		if (reconnectionDelay < 0)
			errors.rejectValue("properties.reconnectionDelay", Constants.MODULE_ID + ".error.reconnectionDelay");
	}
	
	/**
	 * Validation specific to NT event log appenders
	 * @param appender the appender to validate
	 * @param errors the errors
	 */
	private void validateNTEventLogAppender(AppenderProxy appender, Errors errors) {
		String source = (String)appender.getProperty("source");
		
		if (source == null || source.isEmpty())
			errors.rejectValue("source", Constants.MODULE_ID + ".error.source");
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
