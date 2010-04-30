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
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.impl.ConfigurationBuilder;
import org.openmrs.module.logmanager.impl.ConfigurationManager;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.module.logmanager.web.view.DocumentXmlView;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.w3c.dom.Document;

/**
 * Controller for tools page
 */
public class ConfigViewerController extends AbstractController {

	protected static final Log log = LogFactory.getLog(ConfigViewerController.class);

	protected DocumentXmlView xmlView;

	/**
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		Document document = null;
		String filename = "log4j.xml";
		String source = request.getParameter("src");
		if (source == null)
			source = "current";

		if (source.equals("internal")) {
			// Get internal configuration
			URL url = ConfigurationManager.class.getResource("/" + Constants.INTERNAL_CONFIG_NAME);	
			Reader reader = new InputStreamReader(url.openStream());			
			document = LogManagerUtils.readDocument(reader, new Log4jEntityResolver());
			reader.close();
		}
		else if (source.equals("external")) {
			String path = OpenmrsUtil.getApplicationDataDirectory() + File.separator + Constants.EXTERNAL_CONFIG_NAME;
			File file = new File(path);
			if (file.exists()) {
				Reader reader = new FileReader(file);
				document = LogManagerUtils.readDocument(reader, new Log4jEntityResolver());
				reader.close();
				filename = Constants.EXTERNAL_CONFIG_NAME;
			}
		}
		else if (source.equals("current")) {
			// Get the current configuration
			document = ConfigurationBuilder.currentConfiguration();
		}
		else {
			// Look for a module with that as its id
			Module module = ModuleFactory.getModuleById(source);
			if (module != null)
				document = module.getLog4j();
		}

		model.put(xmlView.getSourceKey(), document);
		model.put(xmlView.getFilenameKey(), filename);

		return new ModelAndView(xmlView, model);
	}

	/**
	 * Sets the DocumentXmlView used to export the log4j configuration
	 * 
	 * @param xmlView
	 *            the DocumentXmlView
	 */
	public void setXmlView(DocumentXmlView xmlView) {
		this.xmlView = xmlView;
	}
}
