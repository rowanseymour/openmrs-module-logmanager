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
package org.openmrs.module.logmanager.web.taglib;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for the class ELFunctions
 */
public class ELFunctionsTest {

	@Test
	public void formatTimeStamp() {
		String res1 = ELFunctions.formatTimeStamp(0l, true);
		String res2 = ELFunctions.formatTimeStamp(1000000l, true);
		String res3 = ELFunctions.formatTimeStamp(0l, false);
		String res4 = ELFunctions.formatTimeStamp(1000000l, false);
		
		Assert.assertTrue(res1 != null && !res1.isEmpty());
		Assert.assertTrue(res2 != null && !res2.isEmpty());
		Assert.assertTrue(res3 != null && !res3.isEmpty());
		Assert.assertTrue(res4 != null && !res4.isEmpty());
	}
	
	@Test
	public void formatTimeDiff() {
		String res1 = ELFunctions.formatTimeDiff(0l);
		String res2 = ELFunctions.formatTimeDiff(1000000l);
		
		Assert.assertTrue(res1 != null && !res1.isEmpty());
		Assert.assertTrue(res2 != null && !res2.isEmpty());
	}
	
	//@Test
	//public void formatLocInfo() {
		//String res1 = ELFunctions.formatLocInfo(new LocationInfo("test.java", "Foo", "bar", "123"));
		//String res2 = ELFunctions.formatLocInfo(new LocationInfo("?", "?", "?", "?"));
		
		//Assert.assertTrue(res1 != null && !res1.isEmpty());
		//Assert.assertEquals("Unknown", res2);
	//}
	
	@Test
	public void formatMessage() {
		String res1 = ELFunctions.formatMessage("Hello World");
		String res2 = ELFunctions.formatMessage("X\nX<X>X");
		
		Assert.assertEquals("Hello World", res1);
		Assert.assertEquals("X<br/>X&lt;X&gt;X", res2);
	}
}
