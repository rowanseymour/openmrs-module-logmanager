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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.logmanager.AppenderType;
import org.openmrs.module.logmanager.QueryField;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Test cases for the class WebUtils
 */
public class WebUtilsTest {
	
	@Test
	public void getAppenderTypeParameter() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("appenderType1", "" + AppenderType.CONSOLE.ordinal());
		request.addParameter("appenderType2", "" + AppenderType.MEMORY.ordinal());
		request.addParameter("appenderType3", "" + AppenderType.SOCKET.ordinal());
		request.addParameter("appenderType4", "" + AppenderType.NT_EVENT_LOG.ordinal());
		
		// Test valid parameter names, with and without defaults
		Assert.assertEquals(WebUtils.getAppenderTypeParameter(request, "appenderType1", null), AppenderType.CONSOLE);
		Assert.assertEquals(WebUtils.getAppenderTypeParameter(request, "appenderType2", null), AppenderType.MEMORY);
		Assert.assertEquals(WebUtils.getAppenderTypeParameter(request, "appenderType3", AppenderType.CONSOLE), AppenderType.SOCKET);
		Assert.assertEquals(WebUtils.getAppenderTypeParameter(request, "appenderType4", AppenderType.CONSOLE), AppenderType.NT_EVENT_LOG);
		
		// Test fall back to default with invalid parameter names
		Assert.assertEquals(WebUtils.getAppenderTypeParameter(request, "invalidParam", AppenderType.CONSOLE), AppenderType.CONSOLE);
		Assert.assertEquals(WebUtils.getAppenderTypeParameter(request, "invalidParam", AppenderType.NT_EVENT_LOG), AppenderType.NT_EVENT_LOG);
		
		// Test null return with null default
		Assert.assertNull(WebUtils.getAppenderTypeParameter(request, "invalidParam", null));
	}
	
	@Test
	public void getQueryFieldParameter() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("queryField1", "" + QueryField.CLASS_NAME.ordinal());
		request.addParameter("queryField2", "" + QueryField.FILE_NAME.ordinal());
		request.addParameter("queryField3", "" + QueryField.LOGGER_NAME.ordinal());
		
		// Test valid parameter names, with and without defaults
		Assert.assertEquals(WebUtils.getQueryFieldParameter(request, "queryField1", null), QueryField.CLASS_NAME);
		Assert.assertEquals(WebUtils.getQueryFieldParameter(request, "queryField2", QueryField.CLASS_NAME), QueryField.FILE_NAME);
		Assert.assertEquals(WebUtils.getQueryFieldParameter(request, "queryField3", QueryField.CLASS_NAME), QueryField.LOGGER_NAME);
		
		// Test fall back to default with invalid parameter names
		Assert.assertEquals(WebUtils.getQueryFieldParameter(request, "invalidParam", QueryField.CLASS_NAME), QueryField.CLASS_NAME);
		Assert.assertEquals(WebUtils.getQueryFieldParameter(request, "invalidParam", QueryField.LOGGER_NAME), QueryField.LOGGER_NAME);
		
		// Test null return with null default
		Assert.assertNull(WebUtils.getAppenderTypeParameter(request, "invalidParam", null));
	}
}
