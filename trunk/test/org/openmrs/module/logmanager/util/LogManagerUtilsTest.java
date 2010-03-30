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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.AppenderType;
import org.openmrs.module.logmanager.util.LogManagerUtils;

/**
 * Test cases for the utility class LogManagerUtils
 */
public class LogManagerUtilsTest {

	@Test
	public void getPrivateField() {
		AppenderProxy app = new AppenderProxy(AppenderType.CONSOLE, "Test Appender");
		
		// Check access to private fields of appender proxy class
		Assert.assertEquals(LogManagerUtils.getPrivateField(app, "type"), AppenderType.CONSOLE);
		Assert.assertEquals(LogManagerUtils.getPrivateField(app, "name"), "Test Appender");
	}
}