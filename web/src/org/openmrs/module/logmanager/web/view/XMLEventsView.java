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
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLLayout;
import org.openmrs.ImplementationId;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.LogManagerService;
import org.springframework.web.servlet.view.AbstractView;

/**
 * XML export view of logging events
 */
public class XMLEventsView extends AbstractView {
	
	protected static final SimpleDateFormat dfFilename = new SimpleDateFormat("yyyyMMdd-HHmm");
	
	/**
	 * Gets the filename of the XML response
	 * @param model the model from the viewer page controller
	 * @return the filename string
	 */
	protected String getFilename(Map<String, Object> model) {
		return "log-" + dfFilename.format(new Date()) + ".olog";
	}

	/**
	 * @see org.springframework.web.servlet.view.AbstractView
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Respond as a CSV file
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + getFilename(model) + "\"");
		
		// Disable caching
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		
		PrintWriter out = response.getWriter();
		XMLLayout layout = new XMLLayout();	

		// Create dummy event for system information
		LoggingEvent infoEvent = createSysInfoEvent();
		out.print(layout.format(infoEvent));
		
		LoggingEvent modsEvent = createModInfoEvent(model);
		out.print(layout.format(modsEvent));
	
		// Write each logging event using the XML layout
		for(LoggingEvent event : (List<LoggingEvent>)model.get("events"))
			out.print(layout.format(event));
	}
	
	/**
	 * Creates a dummy logging event containing system information
	 * @return the logging event
	 */
	private LoggingEvent createSysInfoEvent() {
		AdministrationService admSvc = Context.getAdministrationService();
		LogManagerService logSvc = Context.getService(LogManagerService.class);
		SortedMap<String, String> sysVars = admSvc.getSystemVariables();
		ImplementationId implId = admSvc.getImplementationId();
		StringBuilder sb = new StringBuilder();
		
		if (implId != null)
			sb.append("Implementation: " + implId.getName() + " (" + implId.getImplementationId() + ")\n");
		sb.append("OpenMRS version: " + sysVars.get("OPENMRS_VERSION") + "\n");
		sb.append("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + "\n");
		sb.append("Server: " + getServletContext().getServerInfo() + "\n");
		sb.append("MySQL: " + logSvc.getMySQLVersion() + "\n");
		sb.append("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") + "\n");
		sb.append("Hostname: " + sysVars.get("OPENMRS_HOSTNAME") + "\n");
		
		return new LoggingEvent("", LogManager.getRootLogger(), Level.INFO, sb.toString(), null);	
	}
	
	@SuppressWarnings("unchecked")
	private LoggingEvent createModInfoEvent(Map<String, Object> model) {
		Map<String, String> modMap = (Map<String, String>)model.get("modules");
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<String, String> entry : modMap.entrySet()) {
			String name = entry.getKey();
			String version = entry.getValue();
			
			sb.append(name + " (" + version + ")\n");
		}
		
		return new LoggingEvent("", LogManager.getRootLogger(), Level.INFO, sb.toString(), null);
	}
}
