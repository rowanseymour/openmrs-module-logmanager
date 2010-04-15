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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openmrs.module.logmanager.Constants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for tools page
 */
public class ToolsController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(ToolsController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
			
		// Special logger switches
		if (request.getParameter("startAPIProfiling") != null)		
			setProfilingLogging(true);
		else if (request.getParameter("stopAPIProfiling") != null)		
			setProfilingLogging(false);
		else if (request.getParameter("startHibernateSQL") != null)		
			setHibernateSQLLogging(true);
		else if (request.getParameter("stopHibernateSQL") != null)		
			setHibernateSQLLogging(false);
		
		Logger profilingLogger = LogManager.exists(Constants.LOGGER_API_PROFILING);
		Level profilingLoggerLevel = (profilingLogger != null) ? profilingLogger.getEffectiveLevel() : null;
		
		model.put("apiProfilingLoggerName", Constants.LOGGER_API_PROFILING);
		model.put("apiProfilingStarted", (profilingLoggerLevel != null) ? (profilingLoggerLevel.toInt() <= Level.TRACE.toInt()) : false);
		
		Logger sqlLogger = LogManager.exists(Constants.LOGGER_HIBERNATE_SQL);
		Level sqlLoggerLevel = (sqlLogger != null) ? sqlLogger.getEffectiveLevel() : null;
		
		model.put("hibernateSQLLoggerName", Constants.LOGGER_HIBERNATE_SQL);
		model.put("hibernateSQLStarted", (sqlLoggerLevel != null) ? (sqlLoggerLevel.toInt() <= Level.DEBUG.toInt()) : false);
		
		return new ModelAndView(getViewName(), model);
	}
	
	/**
	 * Toggles profiling of API service methods
	 * @param on true to enable logging, else false
	 */
	private void setProfilingLogging(boolean on) {
		LogManager.getLogger("org.openmrs.api").setLevel(on ? Level.TRACE : Level.WARN);
	}
	
	/**
	 * Toggles logging of SQL in Hibernate
	 * @param on true to enable logging, else false
	 */
	private void setHibernateSQLLogging(boolean on) {
		LogManager.getLogger("org.hibernate.SQL").setLevel(on ? Level.DEBUG : Level.OFF);
	}
}
