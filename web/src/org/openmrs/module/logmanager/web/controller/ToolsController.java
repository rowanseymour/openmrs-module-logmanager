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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.log4j.DOMConfigurationBuilder;
import org.openmrs.module.logmanager.log4j.Log4jUtils;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.openmrs.module.logmanager.web.view.DocumentXmlView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.w3c.dom.Document;

/**
 * Controller for tools page
 */
public class ToolsController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(ToolsController.class);
	
	protected DocumentXmlView documentXmlView;
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		// Log4j configuration operations
		if (request.getParameter("clear") != null)
			clearConfiguration(request);
		else if (request.getParameter("import") != null)	
			importConfiguration(request);	
		else if (request.getParameter("export") != null) {	
			Document document = DOMConfigurationBuilder.createConfiguration();	
			model.put(documentXmlView.getSourceKey(), document);
			
			return new ModelAndView(documentXmlView, model);
		}
		else if (request.getParameter("reload") != null)
			reloadConfiguration(request);
			
		// Special logger switches
		else if (request.getParameter("startAPIProfiling") != null)		
			setProfilingLogging(true);
		else if (request.getParameter("stopAPIProfiling") != null)		
			setProfilingLogging(false);
		else if (request.getParameter("startHibernateSQL") != null)		
			setHibernateSQLLogging(true);
		else if (request.getParameter("stopHibernateSQL") != null)		
			setHibernateSQLLogging(false);
		
		List<Map<String, Object>> log4jConfigs = getLog4jConfigs();
		model.put("log4jConfigs", log4jConfigs);
		model.put("mainDisplay", "Main (log4j.xml)");
		
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
	 * Handles a clear configuration request
	 * @param request the http request
	 */
	private void clearConfiguration(HttpServletRequest request) {
		Log4jUtils.clearConfiguration();
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".tools.clearSuccess", null);
	}
	
	/**
	 * Handles an import configuration request
	 * @param request the http request
	 */
	private void importConfiguration(HttpServletRequest request) {
		if (request instanceof MultipartHttpServletRequest) {
			// Spring will have detected a multipart request and wrapped it
			MultipartHttpServletRequest mpRequest = (MultipartHttpServletRequest)request;
			MultipartFile importFile = mpRequest.getFile("importFile");
			
			// Check file exists and isn't empty
			if (importFile == null || importFile.isEmpty()) {
				log.error("Uploaded file is empty or invalid");
				return;
			}
			
			String filename = importFile.getOriginalFilename();
			
			// Check for xml extension
			if (!filename.toLowerCase().endsWith(".xml")) {
				WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".error.invalidConfigurationFile", new Object[] { filename });
				return;
			}
			
			// Parse as an XML configuration
			try {
				if (Log4jUtils.loadConfiguration(importFile.getInputStream()))
					WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".tools.importSuccess", new Object[] { filename });
				else
					WebUtils.setErrorMessage(request, Constants.MODULE_ID + ".error.invalidConfigurationFile", new Object[] { filename });
					
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	
	/**
	 * Handles an reload configuration request
	 * @param request the http request
	 */
	private void reloadConfiguration(HttpServletRequest request) {
		String[] configs = request.getParameterValues("configs");
		boolean loadMain = request.getParameter("mainConfig") != null;
		
		Log4jUtils.loadInternalConfiguration(loadMain, configs);
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".tools.reloadSuccess", null);
	}
	
	/**
	 * Gets a list of log4j config sources
	 * @return
	 */
	private List<Map<String, Object>> getLog4jConfigs() {
		List<Map<String, Object>> log4jConfigs = new ArrayList<Map<String, Object>>();
		
		// Load log4j config files from each module if they exist
		Collection<Module> modules = ModuleFactory.getLoadedModules();
		for (Module module : modules) {
			if (module.getLog4j() != null) {
				Map<String, Object> modConfig = new HashMap<String, Object>();
				modConfig.put("display", "Module: " + module.getModuleId() + " (log4j.xml)");
				modConfig.put("moduleId", module.getModuleId());
				modConfig.put("usesRoot", Log4jUtils.isModuleModifyingRoot(module));
				modConfig.put("outsideNS", Log4jUtils.isModuleModifyingLoggerOutsideNS(module));
				log4jConfigs.add(modConfig);
			}
		}
		
		return log4jConfigs;
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

	/**
	 * Sets the document XML view used to export the log4j configuration
	 * @param xsltView the xsltView to set
	 */
	public void setDocumentXmlView(DocumentXmlView documentXmlView) {
		this.documentXmlView = documentXmlView;
	}
}
