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
import org.openmrs.module.logmanager.Config;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.log4j.ConfigurationManager;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for configuration page
 */
public class ModuleConfigController extends SimpleFormController {
	
	protected static final Log log = LogFactory.getLog(ModuleConfigController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
            BindException errors) throws Exception {
		
		// Persist the config's settings and make current
		Config config = (Config)command;
		config.save();
		Config.setCurrent(config);
			
		// System appender name may have been changed
		ConfigurationManager.ensureSystemAppenderExists();
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".config.saveSuccess", null);
		
		return new ModelAndView(new RedirectView(getSuccessView()));
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return new Config();
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {		
		Map<String, Object> model = new HashMap<String, Object>();	
		
		return model;
	}
}
