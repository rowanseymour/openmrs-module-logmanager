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
package org.openmrs.module.logmanager.web.view;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.XMLLayout;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.impl.EventProxy;
import org.springframework.web.servlet.view.AbstractView;

/**
 * XML export view of logging events
 */
public class EventsExportView extends AbstractView {
	
	protected static final SimpleDateFormat dfFilename = new SimpleDateFormat("yyyyMMdd-HHmm");
	
	/**
	 * Gets the filename of the XML response
	 * @param model the model from the viewer page controller
	 * @return the filename string
	 */
	protected String getFilename(Map<String, Object> model) {
		String format = (String)model.get("format");
		return "log-" + dfFilename.format(new Date()) + "." + format;
	}

	/**
	 * @see org.springframework.web.servlet.view.AbstractView
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Choose format
		String format = (String)model.get("format");
		Layout layout = null;
		if (format.equals("txt")) {
			layout = new PatternLayout(Constants.DEF_LAYOUT_CONVERSION_PATTERN);
			response.setContentType("text/plain");
		}
		else {
			layout = new XMLLayout();
			((XMLLayout)layout).setLocationInfo(true);
			response.setContentType("text/xml");
		}
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + getFilename(model) + "\"");
		
		// Disable caching
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		
		PrintWriter out = response.getWriter();
	
		// Write each logging event using the layout
		for(EventProxy event : (List<EventProxy>)model.get("events"))
			out.print(layout.format(event.getTarget()));
	}
}
