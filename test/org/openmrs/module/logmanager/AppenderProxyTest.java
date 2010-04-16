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
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.xml.XMLLayout;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.log4j.AppenderType;
import org.openmrs.module.logmanager.log4j.LayoutProxy;

/**
 * Test cases for the class AppenderProxy
 */
public class AppenderProxyTest {
	
	private AppenderProxy proxy1, proxy2, proxy3, proxy4;
	private ConsoleAppender appender;
	
	@Before
	public void init() {
		// Create a proxy of a new appender
		proxy1 = new AppenderProxy(AppenderType.MEMORY, "APP1");
		
		// Create a proxy of an existing appender
		appender = new ConsoleAppender();
		appender.setName("APP2");
		appender.setTarget("System.err");
		proxy2 = new AppenderProxy(appender);
		
		// Create a file appender with a layout
		proxy3 = new AppenderProxy(AppenderType.FILE, "APP3");
		proxy3.setLayout(new LayoutProxy(new SimpleLayout()));
		
		// Create a socket appender
		proxy4 = new AppenderProxy(AppenderType.SOCKET, "APP4");
	}
	
	@Test
	public void isExisting() {
		Assert.assertFalse(proxy1.isExisting());
		Assert.assertTrue(proxy2.isExisting());
		Assert.assertFalse(proxy3.isExisting());
		Assert.assertFalse(proxy4.isExisting());
	}
	
	@Test
	public void getType() {
		Assert.assertEquals(AppenderType.MEMORY, proxy1.getType());
		Assert.assertEquals(AppenderType.CONSOLE, proxy2.getType());
		Assert.assertEquals(AppenderType.FILE, proxy3.getType());
		Assert.assertEquals(AppenderType.SOCKET, proxy4.getType());
	}
	
	@Test 
	public void getName() {
		Assert.assertEquals("APP1", proxy1.getName());
		Assert.assertEquals("APP2", proxy2.getName());
	}
	
	@Test 
	public void setName() {
		proxy1.setName("APP5");
		proxy2.setName("APP6");
		
		Assert.assertEquals("APP5", proxy1.getName());
		Assert.assertEquals("APP6", proxy2.getName());
	}
	
	@Test
	public void getId() {
		Assert.assertTrue(proxy1.getId() != 0);
		Assert.assertEquals(appender.hashCode(), proxy2.getId());
		Assert.assertTrue(proxy3.getId() != 0);
		Assert.assertTrue(proxy4.getId() != 0);
	}
	
	@Test
	public void isActivationRequired() {
		// All these appender types use activation
		Assert.assertTrue(proxy1.isActivationRequired());
		Assert.assertTrue(proxy2.isActivationRequired());
		Assert.assertTrue(proxy3.isActivationRequired());
		Assert.assertTrue(proxy4.isActivationRequired());
	}
	
	@Test
	public void isLayoutRequired() {
		// Socket appenders don't use layouts
		Assert.assertTrue(proxy4.isActivationRequired());
		// The other types do...
		Assert.assertTrue(proxy1.isActivationRequired());
		Assert.assertTrue(proxy2.isActivationRequired());
		Assert.assertTrue(proxy3.isActivationRequired());
	}
	
	@Test
	public void getLayout() {
		Assert.assertNotNull(proxy3.getLayout());
		Assert.assertNull(proxy4.getLayout());
	}
	
	@Test
	public void setLayout() {
		proxy1.setLayout(new LayoutProxy(new HTMLLayout()));
		proxy2.setLayout(new LayoutProxy(new XMLLayout()));
		
		Assert.assertNotNull(proxy1.getLayout());
		Assert.assertNotNull(proxy2.getLayout());
	}
	
	@Test
	public void isViewable() {
		// Only memory appenders are viewable
		Assert.assertTrue(proxy1.isViewable());
		
		Assert.assertFalse(proxy2.isViewable());
		Assert.assertFalse(proxy3.isViewable());
		Assert.assertFalse(proxy4.isViewable());
	}
}
