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
package org.openmrs.module.logmanager;

import junit.framework.Assert;

import org.apache.log4j.ConsoleAppender;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.log4j.AppenderType;

/**
 * Test cases for the class AppenderProxy
 */
public class AppenderProxyTest {
	
	private AppenderProxy proxyNew, proxyExisting;
	private ConsoleAppender appender;
	
	@Before
	public void init() {
		// Create a proxy of a new appender
		proxyNew = new AppenderProxy(AppenderType.MEMORY, "APP1");
		
		// Create a proxy of an existing appender
		appender = new ConsoleAppender();
		appender.setName("APP2");
		appender.setTarget("System.err");
		proxyExisting = new AppenderProxy(appender);
	}
	
	@Test
	public void isExisting() {
		Assert.assertFalse(proxyNew.isExisting());
		Assert.assertTrue(proxyExisting.isExisting());
	}
	
	@Test
	public void getType() {
		Assert.assertEquals(AppenderType.MEMORY, proxyNew.getType());
		Assert.assertEquals(AppenderType.CONSOLE, proxyExisting.getType());
	}
	
	@Test 
	public void getName() {
		Assert.assertEquals("APP1", proxyNew.getName());
		Assert.assertEquals("APP2", proxyExisting.getName());
	}
	
	@Test 
	public void setName() {
		proxyNew.setName("APP3");
		proxyExisting.setName("APP4");
		
		Assert.assertEquals("APP3", proxyNew.getName());
		Assert.assertEquals("APP4", proxyExisting.getName());
	}
	
	@Test
	public void getId() {
		Assert.assertTrue(proxyNew.getId() != 0);
		Assert.assertEquals(appender.hashCode(), proxyExisting.getId());
	}
}
