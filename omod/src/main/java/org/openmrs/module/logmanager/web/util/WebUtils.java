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
package org.openmrs.module.logmanager.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.web.WebConstants;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * Utility methods for the web front end
 */
public class WebUtils {
	
	protected static final Log log = LogFactory.getLog(WebUtils.class);
	
	/**
	 * Gets an int parameter from the request that is being "remembered" in the session
	 * @param request the http request
	 * @param name the name of the parameter
	 * @param def the default value of the parameter
	 * @param sessionPrefix the prefix to generate session attribute from parameter name
	 * @return the parameter value
	 */
	public static int getSessionedIntParameter(HttpServletRequest request, String name, int def, String sessionPrefix) {
		HttpSession session = request.getSession();
		int val = def;
		
		// If specified in request, read that and store in session
		if (request.getParameter(name) != null) {
			val = ServletRequestUtils.getIntParameter(request, name, def);
			session.setAttribute(sessionPrefix + name, val);
		}
		// Otherwise look for a matching attribute in the session
		else {
			Integer sessionVal = (Integer)session.getAttribute(sessionPrefix + name);
			if (sessionVal != null)
				val = sessionVal;
		}
		
		return val;
	}
	
	/**
	 * Sets the OpenMRS info message value which is displayed at the top of the page
	 * @param request the request object
	 * @param msgs the message source
	 * @param code the message code
	 */
	public static void setInfoMessage(HttpServletRequest request, String code, Object[] args) {
		String msg = ContextProvider.getMessage(code, args);
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, msg);
	}
	
	/**
	 * Sets the OpenMRS error message value which is displayed at the top of the page
	 * @param request the request object
	 * @param msgs the message source
	 * @param code the message code
	 */
	public static void setErrorMessage(HttpServletRequest request, String code, Object[] args) {
		String msg = ContextProvider.getMessage(code, args);
		request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, msg);
	}
}
