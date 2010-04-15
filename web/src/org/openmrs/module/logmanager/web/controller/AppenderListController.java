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
package org.openmrs.module.logmanager.web.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.Options;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.module.logmanager.web.util.ContextProvider;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Abstract base class for statistical query controllers
 */
public class AppenderListController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(AppenderListController.class);

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		// Delete appender if specified
		int deleteId = ServletRequestUtils.getIntParameter(request, "deleteId", 0);
		if (deleteId != 0)
			deleteAppender(deleteId, request);
		
		// Clear appender if specified
		int clearId = ServletRequestUtils.getIntParameter(request, "clearId", 0);
		if (clearId != 0)
			clearAppender(clearId, request);
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		// Get sorted list of existing appenders
		Collection<AppenderProxy> appenders = svc.getAppenders(true);	
		
		model.put("appenders", appenders);
		model.put("isWindows", LogManagerUtils.isWindowsServer());
		
		return new ModelAndView(getViewName(), model);
	}
	
	/**
	 * Deletes the specified appender
	 * @param appenderId the id of the appender
	 * @param request the http request
	 */
	private void deleteAppender(int appenderId, HttpServletRequest request) {
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		// Find appender and delete it
		AppenderProxy appender = svc.getAppender(appenderId);
		if (appender != null)
			svc.deleteAppender(appender);
		
		// Save configuration if required
		if (Options.getCurrent().isAutoSaveToExternalConfig())
			svc.saveConfiguration();
		
		String name = appender.getName();
		if (name == null || name.isEmpty())
			name = ContextProvider.getMessage(Constants.MODULE_ID + ".anonymous");
		WebUtils.setInfoMessage(request, 
				Constants.MODULE_ID + ".appenders.deleteSuccess", new Object[] { name });
	}
	
	/**
	 * Clears the specified appender
	 * @param appenderId the appender id
	 * @param request the http request
	 */
	private void clearAppender(int appenderId, HttpServletRequest request) {
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		// Find appender and clear it
		AppenderProxy appender = svc.getAppender(appenderId);
		if (appender.isClearable())
			appender.clear();
		
		// Save configuration if required
		if (Options.getCurrent().isAutoSaveToExternalConfig())
			svc.saveConfiguration();
		
		String name = appender.getName();
		if (name == null || name.isEmpty())
			name = ContextProvider.getMessage(Constants.MODULE_ID + ".anonymous");
		WebUtils.setInfoMessage(request, 
				Constants.MODULE_ID + ".appenders.clearSuccess", new Object[] { name });
	}
}
