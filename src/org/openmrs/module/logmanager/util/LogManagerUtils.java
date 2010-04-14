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

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Various utility methods
 */
public class LogManagerUtils {
	
	protected static final Log log = LogFactory.getLog(LogManagerUtils.class);
	
	/**
	 * Gets the value of a protected/private field of an object
	 * @param obj the object
	 * @param fieldName the name of the field
	 * @return
	 */
	public static Object getPrivateField(Object obj, String fieldName) {
		// Find the private field
		Field fields[] = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					return fields[i].get(obj);
				} catch (IllegalAccessException ex) {
					log.warn("Unable to access " + fieldName + " field on "
							+ obj.getClass().getSimpleName());
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets whether the server is running any version of Microsoft Windows
	 * @return true if server is Windows
	 */
	public static boolean isWindowsServer() {
		return System.getProperty("os.name").contains("Windows");
	}
	
	/**
	 * Creates an alphabetically sorted map of module names and versions
	 * @return the map
	 */
	public static Map<String, String> createModuleVersionMap() {
		Map<String, String> modMap = new TreeMap<String, String>();
		Collection<Module> modules = ModuleFactory.getStartedModules();
		for (Module module : modules)
			modMap.put(module.getName(), module.getVersion());
		return modMap;
	}
	
	/**
	 * Reads a DOM document from the given reader
	 * @param reader the reader to read from
	 * @param resolver the entity resolver (may be null)
	 * @return the document or null if document could not be read
	 */
	public static Document readDocument(Reader reader, EntityResolver resolver) {
		try {
			// Get input source
			InputSource source = new InputSource(reader);
			
			// Read into parser
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			
			// Optionally set the entity resolver
			if (resolver != null)
				builder.setEntityResolver(resolver); 

			return builder.parse(source);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	/**
	 * Writes the given DOM document as XML to the given writer
	 * @param document the document to write
	 * @param writer the writer to write to
	 */
	public static void writeDocument(Document document, Writer writer) {	
		try {
			// Get DOM source
			DOMSource source = new DOMSource(document);
			
			// Create an identity transformer
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result); 
		} catch (Exception e) {
			log.error(e);
		}
	}
}
