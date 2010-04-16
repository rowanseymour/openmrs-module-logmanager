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
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Test cases for the class WebUtils
 */
public class WebUtilsTest {
	
	@Test
	public void getSessionedIntParameter() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		// Should sue default when parameter doesn't exist
		Assert.assertEquals(123, WebUtils.getSessionedIntParameter(request, "test1", 123, "test."));
		
		// Should use request value when it exists
		request.addParameter("test1", "" + 456);
		Assert.assertEquals(456, WebUtils.getSessionedIntParameter(request, "test1", 123, "test."));
		
		// Should remember in session when request doesn't exist
		request.removeParameter("test1");
		Assert.assertEquals(456, WebUtils.getSessionedIntParameter(request, "test1", 123, "test."));
	}
}
