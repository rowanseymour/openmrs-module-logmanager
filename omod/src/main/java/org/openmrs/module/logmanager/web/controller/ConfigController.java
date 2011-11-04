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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.w3c.dom.Document;

/**
 * Controller for tools page
 */
public class ConfigController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(ConfigController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		// Log4j configuration operations
		if (request.getParameter("save") != null)
			saveConfiguration(request);
		else if (request.getParameter("clear") != null)
			clearConfiguration(request);
		else if (request.getParameter("import") != null)	
			importConfiguration(request);	
		else if (request.getParameter("reload") != null)
			reloadConfigurations(request);
		
		List<Map<String, Object>> log4jConfigs = getModuleConfigs();
		model.put("log4jConfigs", log4jConfigs);
		model.put("internalConfigName", "log4j.xml");
		model.put("externalConfigName", Constants.EXTERNAL_CONFIG_NAME);
		model.put("externalConfigPath", OpenmrsUtil.getApplicationDataDirectory() + File.separator + Constants.EXTERNAL_CONFIG_NAME);
		
		return new ModelAndView(getViewName(), model);
	}
	
	/**
	 * Handles a save configuration request
	 * @param request the http request
	 */
	private void saveConfiguration(HttpServletRequest request) {
		LogManagerService svc = Context.getService(LogManagerService.class);
		svc.saveConfiguration();
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".config.saveSuccess", null);
	}
	
	/**
	 * Handles a clear configuration request
	 * @param request the http request
	 */
	private void clearConfiguration(HttpServletRequest request) {
		LogManagerService svc = Context.getService(LogManagerService.class);	
		svc.clearConfiguration();
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".config.clearSuccess", null);
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
				LogManagerService svc = Context.getService(LogManagerService.class);
				Reader reader = new InputStreamReader(importFile.getInputStream());
				Document document = LogManagerUtils.readDocument(reader, new Log4jEntityResolver());
				reader.close();
				
				StringWriter str = new StringWriter();
				LogManagerUtils.writeDocument(document, str);
				
				log.warn(str.toString());
				
				if (document != null) {
					svc.loadConfiguration(document);
					
					WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".config.importSuccess", new Object[] { filename });	
				}
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
	private void reloadConfigurations(HttpServletRequest request) {	
		LogManagerService svc = Context.getService(LogManagerService.class);
		boolean succeeded = true;
		
		if (request.getParameter("internalConfig") != null)
			svc.loadConfigurationFromSource();
		
		if (request.getParameter("externalConfig") != null)
			svc.loadConfiguration();
		
		String[] moduleConfigs = request.getParameterValues("moduleConfigs");
		if (moduleConfigs != null)
			svc.loadConfigurationFromModules(moduleConfigs);
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".config." + (succeeded ? "reloadSuccess" : "reloadError"), null);
	}
	
	/**
	 * Gets a list of log4j config sources
	 * @return the list of maps
	 */
	private List<Map<String, Object>> getModuleConfigs() {
		List<Map<String, Object>> log4jConfigs = new ArrayList<Map<String, Object>>();
		
		// Load log4j config files from each module if they exist
		Collection<Module> modules = ModuleFactory.getLoadedModules();
		for (Module module : modules) {
			if (module.getLog4j() != null) {
				Map<String, Object> modConfig = new HashMap<String, Object>();
				modConfig.put("moduleId", module.getModuleId());
				modConfig.put("usesRoot", LogManagerUtils.isModuleModifyingRoot(module));
				modConfig.put("outsideNS", LogManagerUtils.isModuleModifyingLoggerOutsideNS(module));
				log4jConfigs.add(modConfig);
			}
		}
		
		return log4jConfigs;
	}
}
