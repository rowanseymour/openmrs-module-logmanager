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
package org.openmrs.module.logmanager.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.LoggerProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Builds a DOM representation of the current logging configuration
 */
public class DOMConfigurationBuilder {
	
	protected static final Log log = LogFactory.getLog(DOMConfigurationBuilder.class);
	
	protected static final String LOG4J_NAMESPACE = "http://jakarta.apache.org/log4j/";
	protected static final String LOG4J_TAG_PREFIX = "log4j";
	
	/**
	 * Gets a DOM representation of the current logging configuration
	 * @return the DOM document
	 */
	public static Document createDocument() {	
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			// Create document element
			Element documentElem = document.createElementNS(LOG4J_NAMESPACE, "configuration");
			documentElem.setPrefix(LOG4J_TAG_PREFIX);			
			document.appendChild(documentElem);
			
			// Create appender elements
			for (AppenderProxy appender : svc.getAppenders(false)) {
				String appenderName = appender.getName();
				// We can't save appenders with no name...
				if (appenderName != null && !appenderName.isEmpty()) {
					Element appenderElem = createAppenderElement(document, appender);
					documentElem.appendChild(appenderElem);
				}
			}

			// Create logger elements
			for (LoggerProxy logger : svc.getLoggers(false)) {
				Element loggerElem = createLoggerElement(document, logger);
				documentElem.appendChild(loggerElem);
			}
			
			// Create root logger element
			Element rootElem = createLoggerElement(document, LoggerProxy.getRootLogger());
			documentElem.appendChild(rootElem);
			
			return document;
			
		} catch (ParserConfigurationException e) {
			log.error(e);
		}
		return null;
	}
	
	/**
	 * Creates a DOM element from the specified appender
	 * @param document the DOM document
	 * @param appender the appender
	 * @return the appender element
	 */
	private static Element createAppenderElement(Document document, AppenderProxy appender) {
		Element element = document.createElement("appender");
		
		// Create name attribute
		element.setAttribute("name", appender.getName());
		
		// Create class attribute
		String className = appender.getTarget().getClass().getName();
		element.setAttribute("class", className);
		
		// Create layout element
		Layout layout = appender.getTarget().getLayout();
		if (layout != null) {
			Element layoutElem = createLayoutElement(document, layout);		
			element.appendChild(layoutElem);
		}
		
		return element;
	}
	
	/**
	 * Creates a DOM element from the specified layout
	 * @param document the DOM document
	 * @param layout the layout
	 * @return the layout element
	 */
	private static Element createLayoutElement(Document document, Layout layout) {
		Element element = document.createElement("layout");
		
		// Create class attribute
		String layoutClassName = layout.getClass().getName();
		element.setAttribute("class", layoutClassName);
		
		if (layout instanceof PatternLayout) {
			Element paramElem = document.createElement("param");
			paramElem.setAttribute("name", "ConversionPattern");
			paramElem.setAttribute("value", ((PatternLayout)layout).getConversionPattern());
			element.appendChild(paramElem);
		}
		
		return element;
	}
	
	/**
	 * Creates a DOM element from the specified logger
	 * @param document the DOM document
	 * @param logger the logger
	 * @return the logger element
	 */
	private static Element createLoggerElement(Document document, LoggerProxy logger) {
		Element element = document.createElement(logger.isRoot() ? "root" : "logger");
		
		// Create name attribute
		if (!logger.isRoot())
			element.setAttribute("name", logger.getName());
		
		// Create level element
		Element levelElem = document.createElement("level");
		levelElem.setAttribute("value", logger.getLevel().toString());	
		element.appendChild(levelElem);
		
		// Create appender-ref elements
		for (AppenderProxy appender : logger.getAppenders()) {
			String appenderName = appender.getName();
			// We can't save appenders with no name...
			if (appenderName != null && !appenderName.isEmpty()) {
				Element appenderElem = document.createElement("appender-ref");
				appenderElem.setAttribute("ref", appender.getName());
				
				element.appendChild(appenderElem);
			}
		}
		
		return element;
	}
}
