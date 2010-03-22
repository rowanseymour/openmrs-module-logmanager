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
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.QueryField;
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
public class ViewerController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(ViewerController.class);
	
	protected View exportView;
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		// Get viewing filters
		int levelOp = ServletRequestUtils.getIntParameter(request, "levelOp", 0);
		model.put("levelOp", levelOp);
		Level level = Level.toLevel(ServletRequestUtils.getIntParameter(request, "level", Level.ALL_INT));
		model.put("level", level.toInt());
		
		QueryField queryField = WebUtils.getQueryFieldParameter(request, "queryField", QueryField.CLASS_NAME);
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
		
		// Get specific appender or default to MEMORY_APPENDER
		int appId = ServletRequestUtils.getIntParameter(request, "appId", 0);
		AppenderProxy appender = null;
		if (appId != 0)
			appender = svc.getAppender(appId);
		else {
			// Try default appender from Constants
			Appender target = Logger.getRootLogger().getAppender(Constants.DEF_DEFAULT_APPENDER_NAME);
			if (target != null)
				appender = new AppenderProxy(target);
			// Resort to first viewable appender
			else if (appenders.size() > 0)
				appender = appenders.get(0);
		}
		
		if (appender != null) {
			if (appender.isViewable())
				model.put("events", svc.getAppenderEvents(appender, level, levelOp, queryField, queryValue, paging));
			else {
				model.put("events", new ArrayList<LoggingEvent>());
				WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".error.invalidAppender", null);
			}
		}
		else {
			model.put("events", new ArrayList<LoggingEvent>());
			WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".error.noSuitableAppender", null);
		}
		
		model.put("appender", appender);
		model.put("appenders", appenders);

		String format = request.getParameter("format");
		if (format != null && !format.isEmpty()) {
			model.put("format", format);
			return new ModelAndView(getExportView(), model);
		}
		
		model.put("levelIcons", IconFactory.getLevelIconMap());	
		model.put("levelLabels", IconFactory.getLevelLabelMap());	
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
