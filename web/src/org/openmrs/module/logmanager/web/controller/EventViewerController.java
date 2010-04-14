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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.web.util.IconFactory;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.openmrs.module.logmanager.web.view.EventReportView;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for event view page
 */
public class EventViewerController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(EventViewerController.class);
	
	protected EventReportView reportView;
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
		LogManagerService svc = Context.getService(LogManagerService.class);
		boolean isReport = request.getParameter("report") != null;
		
		// Get specific appender or default to system appender
		int appId = ServletRequestUtils.getIntParameter(request, "appId", 0);
		AppenderProxy appender = null;
		if (appId != 0)
			appender = svc.getAppender(appId);
		else
			appender = AppenderProxy.getSystemAppender();
		
		// Find specific event and also its previous N events
		int eventId = ServletRequestUtils.getIntParameter(request, "eventId", 0);
		List<LoggingEvent> contextEvents = new ArrayList<LoggingEvent>();
		LoggingEvent event = svc.getAppenderEvent(appender, eventId, contextEvents, isReport ? Constants.EVENT_REPORT_PREV_EVENTS : -1);
		
		if (event == null)
			WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".error.invalidEvent", null);
		
		model.put("event", event);
		model.put("contextEvents", contextEvents);
		model.put("levelIcons", IconFactory.getLevelIconMap());	
		model.put("levelLabels", IconFactory.getLevelLabelMap());
		
		if (event != null && isReport)
			return new ModelAndView(reportView, model);
		else
			return new ModelAndView(getViewName(), model);
	}

	/**
	 * Sets the event report view
	 * @param reportView the event report view
	 */
	public void setReportView(EventReportView reportView) {
		this.reportView = reportView;
	}	
}
