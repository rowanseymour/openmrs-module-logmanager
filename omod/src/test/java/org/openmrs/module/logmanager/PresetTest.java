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

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Test cases for the class Preset
 */
public class PresetTest {

	protected Preset preset1 = new Preset();
	protected Preset preset2 = new Preset();
	
	@Before
	public void init() {
		preset1.setPresetId(123);
		preset1.setName("Test1");
		preset1.setLoggerMap(new HashMap<String, Integer>());
	}
	
	@Test 
	public void getPresetId() {
		Assert.assertEquals(123, preset1.getPresetId());
		Assert.assertEquals(0, preset2.getPresetId());
	}
	
	@Test
	public void setPresetId() {
		preset1.setPresetId(456);
		preset2.setPresetId(789);
		
		Assert.assertEquals(456, preset1.getPresetId());
		Assert.assertEquals(789, preset2.getPresetId());
	}
	
	@Test
	public void getName() {
		Assert.assertEquals("Test1", preset1.getName());
		Assert.assertNull(preset2.getName());
	}
	
	@Test
	public void setName() {
		preset1.setName("Test3");
		preset2.setName("Test4");
		
		Assert.assertEquals("Test3", preset1.getName());
		Assert.assertEquals("Test4", preset2.getName());
	}
	
	@Test
	public void getLoggerMap() {
		Assert.assertNotNull(preset1.getName());
		Assert.assertNull(preset2.getName());
	}
	
	@Test
	public void setLoggerMap() {
		preset1.setLoggerMap(new HashMap<String, Integer>());
		preset2.setLoggerMap(new HashMap<String, Integer>());
		
		Assert.assertNotNull(preset1.getLoggerMap());
		Assert.assertNotNull(preset2.getLoggerMap());
	}
}
