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
import org.openmrs.module.logmanager.LoggerProxy;
import org.openmrs.module.logmanager.util.PagingInfo;
import org.openmrs.module.logmanager.web.IconFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for logger list page
 */
public class LoggerListController extends ParameterizableViewController {
	
	protected static final Log log = LogFactory.getLog(LoggerListController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		// Get paging info
		int offset = ServletRequestUtils.getIntParameter(request, "offset", 0);
		PagingInfo paging = new PagingInfo(offset, Constants.RESULTS_PAGE_SIZE);
		
		boolean incImplicit = request.getParameter("incImplicit") != null;
		
		model.put("loggers", svc.getLoggers(incImplicit, paging));
		model.put("rootLogger", LoggerProxy.getRootLogger());
		model.put("paging", paging);
		model.put("incImplicit", incImplicit);
		model.put("levelLabels", IconFactory.getLevelLabelMap());
		model.put("levelNullLabel", "<i>&lt;Inherit&gt;</i>");
		model.put("levelIcons", IconFactory.getLevelIconMap());
		
		return new ModelAndView(getViewName(), model);
	}
}