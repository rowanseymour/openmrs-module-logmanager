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
 * View to render DOM document as XML
 */
public class DocumentXmlView extends AbstractView {
	
	protected String filenameKey = "filename";
	protected String sourceKey = "source";

	@SuppressWarnings("rawtypes")
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Disable caching
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		
		// Get document and filename from model
		Document document = (Document)model.get(sourceKey);
		String filename = (String)model.get(filenameKey);

		if (document != null) {
			// Set response headers
			response.setContentType("text/xml");
			if (filename != null)
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			
			// Write document XML to response stream
			LogManagerUtils.writeDocument(document, response.getWriter());
		}
		else
			response.getWriter().write("ERROR: Document is null");
	}

	/**
	 * Gets the key used to access the source document in the model
	 * @return the source key
	 */
	public String getSourceKey() {
		return sourceKey;
	}

	/**
	 * Sets the key used to access the source document in the model
	 * @param sourceKey the source key
	 */
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	/**
	 * Gets the key used to access the filename in the model
	 * @return the filename key
	 */
	public String getFilenameKey() {
		return filenameKey;
	}

	/**
	 * Sets the key used to access the filename in the model
	 * @param filenameKey the filename key
	 */
	public void setFilenameKey(String filenameKey) {
		this.filenameKey = filenameKey;
	}
}
