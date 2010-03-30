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

import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.junit.Test;

/**
 * Test cases for the class ELFunctions
 */
public class ELFunctionsTest {

	@Test
	public void formatTimeStamp() {
		String res1 = ELFunctions.formatTimeStamp(0l);
		String res2 = ELFunctions.formatTimeStamp(1000000l);
		
		Assert.assertTrue(res1 != null && !res1.isEmpty());
		Assert.assertTrue(res2 != null && !res2.isEmpty());
	}
	
	@Test
	public void formatTimeDiff() {
		String res1 = ELFunctions.formatTimeDiff(0l);
		String res2 = ELFunctions.formatTimeDiff(1000000l);
		
		Assert.assertTrue(res1 != null && !res1.isEmpty());
		Assert.assertTrue(res2 != null && !res2.isEmpty());
	}
	
	@Test
	public void formatLocInfo() {
		String res1 = ELFunctions.formatLocInfo(new LocationInfo("test.java", "Foo", "bar", "123"));
		String res2 = ELFunctions.formatLocInfo(new LocationInfo("", "", "", ""));
		
		Assert.assertTrue(res1 != null && !res1.isEmpty());
		Assert.assertTrue(res2 != null && !res2.isEmpty());
	}
	
	@Test
	public void formatMessage() {
		String res1 = ELFunctions.formatMessage("Hello World");
		String res2 = ELFunctions.formatMessage("X\nX");
		
		Assert.assertEquals("Hello World", res1);
		Assert.assertEquals("X<br/>X", res2);
	}
	
	@Test
	public void levelToInt() {
		Assert.assertEquals(new Integer(Level.OFF_INT), ELFunctions.levelToInt(Level.OFF));
		Assert.assertEquals(new Integer(Level.INFO_INT), ELFunctions.levelToInt(Level.INFO));
		Assert.assertEquals(new Integer(Level.ALL_INT), ELFunctions.levelToInt(Level.ALL));
	}
	
	@Test
	public void _hashCode() {
		Object o1 = new String("hello");
		Object o2 = new Integer(1234);
		Assert.assertEquals(new Integer(o1.hashCode()), ELFunctions.hashCode(o1));
		Assert.assertEquals(new Integer(o2.hashCode()), ELFunctions.hashCode(o2));
	}
}
