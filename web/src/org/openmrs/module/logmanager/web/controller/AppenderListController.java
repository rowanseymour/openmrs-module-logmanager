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
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.web.WebConstants;
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
		
		int deleteId = ServletRequestUtils.getIntParameter(request, "deleteAppender", 0);
		if (deleteId != 0) {
			AppenderProxy appToRemove = svc.getAppender(deleteId);
			if (appToRemove != null)
				svc.deleteAppender(appToRemove);
		}
		
		// Clear appender if specified
		int clearId = ServletRequestUtils.getIntParameter(request, "clearId", 0);
		if (clearId != 0) {
			AppenderProxy appender = svc.getAppender(clearId);
			if (appender.isClearable())
				appender.clear();
			
			String msg = getMessageSourceAccessor().getMessage(Constants.MODULE_ID + ".appenders.clearSuccess");
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, msg);
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		// Get sorted list of existing appenders
		Collection<AppenderProxy> appenders = svc.getAppenders(true);	
		model.put("appenders", appenders);
		
		return new ModelAndView(getViewName(), model);
	}
	
	
}
