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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.springframework.web.servlet.view.AbstractView;
import org.w3c.dom.Document;

/**
 * View for XML exports
 */
public class DocumentXmlView extends AbstractView {
	
	protected String filename;
	protected String sourceKey;

	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Get DOM document from model
		Document document = (Document)model.get(sourceKey);

		// Set response headers
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		
		// Disable caching
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		
		// Write document XML to response stream
		LogManagerUtils.writeDOMDocument(document, response.getWriter());
	}

	/**
	 * Gets the source key used to access the source document in the model
	 * @return the source key
	 */
	public String getSourceKey() {
		return sourceKey;
	}

	/**
	 * Sets the source key used to access the source document in the model
	 * @param sourceKey the source key
	 */
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	/**
	 * Gets the filename of the XML response
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the filename of the XML response
	 * @param filename the filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
