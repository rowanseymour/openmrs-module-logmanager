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
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.log4j.AppenderType;

/**
 * Test cases for the class AbstractProxy
 */
public class AbstractProxyTest {
	
	static {
		LogManager.getRootLogger().addAppender(new ConsoleAppender());
	}
	
	private AppenderProxy proxyNew, proxyExisting;
	private ConsoleAppender appender;
	
	@Before
	public void init() {
		// Create a proxy of a new appender
		proxyNew = new AppenderProxy(AppenderType.SOCKET, "APP1");
		
		// Create a proxy of an existing appender
		appender = new ConsoleAppender();
		appender.setName("APP2");
		appender.setTarget("System.err");
		proxyExisting = new AppenderProxy(appender);
	}
	
	@Test
	public void getTarget() {
		Assert.assertNotNull(proxyNew.getTarget());
		Assert.assertEquals(appender, proxyExisting.getTarget());
	}
	
	@Test
	public void getProperties() {
		Assert.assertNotNull(proxyNew.getProperties());
		Assert.assertNotNull(proxyExisting.getProperties());
	}
	
	@Test
	public void getProperty() {
		Assert.assertNotNull(proxyNew.getProperty("reconnectionDelay"));
		Assert.assertEquals(new Integer(4560), (Integer)proxyNew.getProperty("port"));
		Assert.assertEquals(false, proxyExisting.getProperty("follow"));
		Assert.assertEquals("System.err", proxyExisting.getProperty("target"));
	}
	
	@Test
	public void setProperty() {
		proxyNew.setProperty("remoteHost", "localhost");
		Assert.assertEquals("localhost", proxyNew.getProperty("remoteHost"));
		
		proxyExisting.setProperty("target", "System.out");
		Assert.assertEquals("System.out", proxyExisting.getProperty("target"));
	}
	
	@Test
	public void getPropertyNames() {
		Assert.assertEquals(5, proxyNew.getPropertyNames().length);
		Assert.assertEquals(2, proxyExisting.getPropertyNames().length);
	}
	
	@Test
	public void _equals() {
		AppenderProxy otherProxy = new AppenderProxy(appender);
		
		Assert.assertFalse(proxyNew.equals(otherProxy));
		Assert.assertTrue(proxyExisting.equals(otherProxy));
	}
	
	@Test
	public void _hashCode() {
		Assert.assertEquals(proxyNew.getTarget().hashCode(), proxyNew.hashCode());
		Assert.assertEquals(appender.hashCode(), proxyExisting.hashCode());
	}
}
