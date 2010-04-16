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
package org.openmrs.module.logmanager.log4j;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Builds a DOM representation of the current logging configuration
 */
public class DOMConfigurationBuilder {
	
	protected static final Log log = LogFactory.getLog(DOMConfigurationBuilder.class);
	
	protected static final String LOG4J_NAMESPACE = "http://jakarta.apache.org/log4j/";
	protected static final String LOG4J_TAG_PREFIX = "log4j";
	
	// Parameter names for different layout types
	protected static final String[] PARAMS_LAYOUT_TTCC = { "categoryPrefixing", "contextPrinting", "threadPrinting" };
	protected static final String[] PARAMS_LAYOUT_PATTERN = { "conversionPattern" };
	protected static final String[] PARAMS_LAYOUT_HTML = { "title", "locationInfo" };
	protected static final String[] PARAMS_LAYOUT_XML = { "locationInfo", "properties" };
	
	// Map of layout types to parameter name arrays
	protected static final Map<LayoutType, String[]> layoutTypeParams = new HashMap<LayoutType, String[]>();
	
	static {
		layoutTypeParams.put(LayoutType.TTCC, PARAMS_LAYOUT_TTCC);
		layoutTypeParams.put(LayoutType.PATTERN, PARAMS_LAYOUT_PATTERN);
		layoutTypeParams.put(LayoutType.HTML, PARAMS_LAYOUT_HTML);
		layoutTypeParams.put(LayoutType.XML, PARAMS_LAYOUT_XML);
	}
	
	/**
	 * Builds a document based on an empty configuration
	 * @return the document
	 */
	public static Document emptyConfiguration() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			// Create document element
			Element documentElem = document.createElementNS(LOG4J_NAMESPACE, "configuration");
			documentElem.setPrefix(LOG4J_TAG_PREFIX);			
			document.appendChild(documentElem);
			
			return document;
		} catch (ParserConfigurationException e) {
			log.error(e);
			return null;
		}	
	}
	
	/**
	 * Gets a DOM representation of the current logging configuration
	 * @return the DOM document
	 */
	public static Document currentConfiguration() {	
		// Build empty configuration document
		Document document = emptyConfiguration();
		if (document == null)
			return null;
	
		// Get document element
		Element documentElem = document.getDocumentElement();
		
		// Create appender elements
		for (AppenderProxy appender : LogManagerProxy.getAppenders()) {
			String appenderName = appender.getName();
			// We can't save appenders with no name...
			if (appenderName != null && !appenderName.isEmpty())
				addAppenderElement(document, documentElem, appender);
		}

		// Create logger elements	
		for (LoggerProxy logger : LogManagerProxy.getLoggers(false))
			addLoggerElement(document, documentElem, logger);
		
		// Create root logger element
		addLoggerElement(document, documentElem, LogManagerProxy.getRootLogger());

		return document;
	}
	
	/**
	 * Adds a DOM element from the specified appender
	 * @param document the DOM document
	 * @param parent the parent document element
	 * @param appender the appender
	 * @return the appender element
	 */
	private static void addAppenderElement(Document document, Element parent, AppenderProxy appender) {
		Element element = document.createElement("appender");
		
		// Create name attribute
		element.setAttribute("name", appender.getName());
		
		// Create class attribute
		String className = appender.getTarget().getClass().getName();
		element.setAttribute("class", className);
		
		// Create param elements for this appender type
		String[] paramNames = appender.getPropertyNames();
		if (paramNames != null)
			addObjectParamElements(document, element, appender.getTarget(), paramNames);
		
		// Create layout element
		LayoutProxy layout = appender.getLayout();
		if (layout != null)
			addLayoutElement(document, element, layout);		
		
		parent.appendChild(element);
	}
	
	/**
	 * Adds a DOM element from the specified layout
	 * @param document the DOM document
	 * @param parent the parent appender element
	 * @param layout the layout
	 * @return the layout element
	 */
	private static void addLayoutElement(Document document, Element parent, LayoutProxy layout) {
		Element element = document.createElement("layout");
		
		// Create class attribute
		String layoutClassName = layout.getTarget().getClass().getName();
		element.setAttribute("class", layoutClassName);
		
		// Create param elements for this layout type
		String[] paramNames = layoutTypeParams.get(layout.getType());
		if (paramNames != null)
			addObjectParamElements(document, element, layout.getTarget(), paramNames);
		
		parent.appendChild(element);
	}
	
	/**
	 * Adds all appropriate parameter elements to the given layout element 
	 * @param document the DOM document
	 * @param parent the parent element
	 * @param object the object (Appender, Layout)
	 * @param paramNames the parameter names
	 */
	private static void addObjectParamElements(Document document, Element parent, Object object, String[] paramNames) {
		for (String paramName : paramNames) {
			try {
				String propValue = BeanUtils.getProperty(object, paramName);
				Element element = document.createElement("param");
				element.setAttribute("name", paramName);
				element.setAttribute("value", propValue);
				parent.appendChild(element);
			} catch (Exception e) {
				log.warn("Attempted to read invalid bean property \"" + paramName + "\" on layout");
			}
		}
	}
	
	/**
	 * Adds a DOM element from the specified logger
	 * @param document the DOM document
	 * @param parent the parent document element
	 * @param logger the logger
	 * @return the logger element
	 */
	private static void addLoggerElement(Document document, Element parent, LoggerProxy logger) {
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
		
		parent.appendChild(element);
	}
}
