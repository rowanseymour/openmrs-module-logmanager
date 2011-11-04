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
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.logmanager.impl.LoggerProxy;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View to render object in model as JSON
 */
public class AutocompleteView extends AbstractView {

	protected String sourceKey = "source";
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PrintWriter writer = response.getWriter();
		Object source = model.get(sourceKey);
		
		// Disable caching
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("application/json");
		
		writer.write("[");
		
		if (source != null) {		
			if (source instanceof Collection) {
				Collection<?> collection = (Collection<?>)source;
				Object[] items = collection.toArray();
				for (int i = 0; i < items.length; i++) {
					Object item = items[i];
					String label = (item instanceof LoggerProxy) ? ((LoggerProxy)item).getName() : item.toString();
					
					if (i > 0)
						writer.write(',');
					
					writer.write("{\"label\":\"" + label + "\", \"value\":\"" + label + "\"}");
				}
			}
		}
		else
			writer.write("\"ERROR: Source object is null\"");
		
		writer.write("]");
	}

	/**
	 * @return the sourceKey
	 */
	public String getSourceKey() {
		return sourceKey;
	}

	/**
	 * @param sourceKey the sourceKey to set
	 */
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
}
