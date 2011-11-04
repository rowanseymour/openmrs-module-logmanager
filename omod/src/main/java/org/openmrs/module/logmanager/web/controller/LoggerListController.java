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
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.Preset;
import org.openmrs.module.logmanager.impl.LoggerProxy;
import org.openmrs.module.logmanager.impl.ManagerProxy;
import org.openmrs.module.logmanager.web.util.IconFactory;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.openmrs.module.logmanager.web.view.AutocompleteView;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for logger list page
 */
public class LoggerListController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(LoggerListController.class);
	
	protected AutocompleteView autocompleteView;
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
		LogManagerService svc = Context.getService(LogManagerService.class);
		int presetId = ServletRequestUtils.getIntParameter(request, "preset", 0);
		
		if (request.getParameter("json") != null) {
			String term = request.getParameter("term");
			int limit = ServletRequestUtils.getIntParameter(request, "limit", 10);
			model.put(autocompleteView.getSourceKey(), svc.getLoggers(term, limit));
			return new ModelAndView(autocompleteView, model);
		}
		
		// Process preset management requests
		if (request.getParameter("savePreset") != null)
			saveLoggerPreset(presetId, request, model);
		else if (request.getParameter("loadPreset") != null)
			loadLoggerPreset(presetId, request, model);
		else if (request.getParameter("deletePreset") != null)
			deleteLoggerPreset(presetId, request, model);
		
		// There is no mechanism for removing loggers in log4j 1.2
		// so instead we nullify its level and remove its appenders
		// so that it will be effectively ignored
		String delLogger = request.getParameter("deleteLogger");
		if (delLogger != null) {
			LoggerProxy logToRemove = ManagerProxy.getLogger(delLogger, false);
			if (logToRemove != null)
				logToRemove.makeImplicit(true);
		}
		
		model.put("presets", svc.getPresets());
		model.put("loggers", svc.getLoggers(false));
		model.put("rootLogger", ManagerProxy.getRootLogger());
		model.put("levelNullLabel", "<i>&lt;Inherit&gt;</i>");
		model.put("levelIcons", IconFactory.getLevelIconMap());
		
		return new ModelAndView(getViewName(), model);
	}
	
	/**
	 * Save/create a logger preset 
	 * @param presetId the preset id (zero for a new preset)
	 * @param request the http request object
	 * @param model the model
	 */
	private void saveLoggerPreset(int presetId, HttpServletRequest request, Map<String, Object> model) {
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		Preset preset = null;
		if (presetId == 0) {
			String name = request.getParameter("newPresetName");
			if (name != null && name.matches("[\\w\\.\\- ]+")) {
				preset = new Preset(name);
				WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".loggers.presetCreated", null);
			}
			else {
				model.put("newPresetName", name);
				model.put("newPresetNameError", true);
				return;
			}
		}
		else {
			preset = svc.getPreset(presetId);
			WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".loggers.presetUpdated", null);
		}
		
		svc.saveCurrentLoggersAsPreset(preset);
		model.put("activePreset", preset.getPresetId());
		
	}
	
	/**
	 * Load a logger preset 
	 * @param presetId the preset id (zero for a new preset)
	 * @param request the http request object
	 * @param model the model
	 */
	private void loadLoggerPreset(int presetId, HttpServletRequest request, Map<String, Object> model) {
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		// Load and activate preset
		Preset preset = svc.getPreset(presetId);
		svc.activatePreset(preset);
		
		model.put("activePreset", presetId);
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".loggers.presetLoaded", null);
	}
	
	/**
	 * Delete a logger preset 
	 * @param presetId the preset id (zero for a new preset)
	 * @param request the http request object
	 * @param model the model
	 */
	private void deleteLoggerPreset(int presetId, HttpServletRequest request, Map<String, Object> model) {
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		Preset preset = svc.getPreset(presetId);
		svc.deletePreset(preset);
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".loggers.presetDeleted", null);
	}

	/**
	 * Gets the autocomplete view
	 * @return the autocomplete view
	 */
	public AutocompleteView getAutocompleteView() {
		return autocompleteView;
	}

	/**
	 * Sets the autocomplete view
	 * @param autocompleteView the autocomplete view
	 */
	public void setAutocompleteView(AutocompleteView autocompleteView) {
		this.autocompleteView = autocompleteView;
	}
}
