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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.Options;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.QueryField;
import org.openmrs.module.logmanager.impl.AppenderProxy;
import org.openmrs.module.logmanager.impl.ConfigurationManager;
import org.openmrs.module.logmanager.impl.EventProxy;
import org.openmrs.module.logmanager.impl.LevelProxy;
import org.openmrs.module.logmanager.util.PagingInfo;
import org.openmrs.module.logmanager.web.util.IconFactory;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for log view page
 */
public class LogViewerController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(LogViewerController.class);
	
	protected static final String SESSION_ATTR_PREFIX = Constants.MODULE_ID + ".viewer.";
	
	protected View exportView;
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Ensure that the memory appender defined in OpenMRS's log4j.xml exists
		// and configure it to be used as the system appender
		if (Options.getCurrent().isAlwaysRecreateSystemAppender())
			if (!ConfigurationManager.ensureSystemAppenderExists())
				WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".viewer.systemAppenderRecreatedMsg", null);
		
		Map<String, Object> model = new HashMap<String, Object>();
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		// Get viewing filters
		int levelOp = ServletRequestUtils.getIntParameter(request, "levelOp", 0);
		model.put("levelOp", levelOp);
		LevelProxy level = new LevelProxy(ServletRequestUtils.getIntParameter(request, "level", LevelProxy.ALL.getIntValue()));
		model.put("level", level);
		
		QueryField queryField = QueryField.fromOrdinal(ServletRequestUtils.getIntParameter(request, "queryField", QueryField.CLASS_NAME.getOrdinal()));
		String queryValue = request.getParameter("queryValue");
		if (queryValue != null) {
			queryValue = queryValue.trim();
			if (queryValue.isEmpty())
				queryValue = null;
		}
		
		model.put("queryField", queryField);
		model.put("queryValue", queryValue);
		
		// Get paging info
		int offset = ServletRequestUtils.getIntParameter(request, "offset", 0);
		PagingInfo paging = new PagingInfo(offset, Constants.VIEWER_PAGE_SIZE);
		model.put("paging", paging);
		
		// Get list of existing viewable appenders 
		Collection<AppenderProxy> appendersAll = svc.getAppenders(true);
		List<AppenderProxy> appenders = new ArrayList<AppenderProxy>();	
		for (AppenderProxy app : appendersAll) {
			if (app.isViewable())
				appenders.add(app);
		}
		
		// Get specific appender or default to system appender
		int appId = ServletRequestUtils.getIntParameter(request, "appId", 0);
		AppenderProxy appender = null;
		if (appId != 0)
			appender = svc.getAppender(appId);
		else
			appender = AppenderProxy.getSystemAppender();
		
		List<EventProxy> events = new ArrayList<EventProxy>();
		if (appender != null) {
			if (appender.isViewable())
				events = svc.getAppenderEvents(appender, level, levelOp, queryField, queryValue, paging);
			else
				WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".error.invalidAppender", null);
		}
		else
			WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".error.noSuitableAppender", null);
		
		model.put("events", events);
		model.put("appender", appender);
		model.put("appenders", appenders);

		// Check request params for XML / TXT requests
		String format = request.getParameter("format");
		if (format != null && !format.isEmpty()) {
			model.put("format", format);
			return new ModelAndView(getExportView(), model);
		}
		
		boolean profiler = WebUtils.getSessionedIntParameter(request, "profiler", 0, SESSION_ATTR_PREFIX) == 1;
		model.put("profiler", profiler);
		
		// Calc time diffs for profiling
		if (profiler && events.size() > 0) {
			long[] timeDiffs = new long[events.size()];
			for (int e = 0; e < events.size() - 1; e++)
				// Sometimes events get wrong order, so max away in negative values
				timeDiffs[e] = Math.max(events.get(e).getTimeStamp() - events.get(e + 1).getTimeStamp(), 0);
			
			// Last event can't have a time diff so make it -1
			timeDiffs[events.size() - 1] = -1;
			
			model.put("timeDiffs", timeDiffs);			
		}
		
		model.put("levelIcons", IconFactory.getLevelIconMap());	
		return new ModelAndView(getViewName(), model);
	}

	/**
	 * @return the exportView
	 */
	public View getExportView() {
		return exportView;
	}

	/**
	 * @param exportView the exportView to set
	 */
	public void setExportView(View exportView) {
		this.exportView = exportView;
	}
}
