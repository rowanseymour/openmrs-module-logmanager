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
package org.openmrs.module.logmanager.propertyeditor;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.module.logmanager.impl.LevelProxy;

/**
 * Test cases for the class LevelEditor
 */
public class LevelEditorTest {

	@Test
	public void getAsText() {
		// Test conversion of level enum to integer
		LevelEditor editor1 = new LevelEditor();
		editor1.setValue(LevelProxy.ERROR);
		Assert.assertEquals("" + LevelProxy.ERROR.getIntValue(), editor1.getAsText());
		
		LevelEditor editor2 = new LevelEditor();
		editor2.setValue(LevelProxy.TRACE);
		Assert.assertEquals("" + LevelProxy.TRACE.getIntValue(), editor2.getAsText());
		
		// Test null object, should return empty string
		LevelEditor editor3 = new LevelEditor();
		Assert.assertEquals("", editor3.getAsText());
	}
	
	@Test
	public void setAsText() {
		// Test conversion of integer to level enum
		LevelEditor editor1 = new LevelEditor();
		editor1.setAsText("" + LevelProxy.ERROR.getIntValue());
		Assert.assertEquals(LevelProxy.ERROR, editor1.getValue());
		
		LevelEditor editor2 = new LevelEditor();
		editor2.setAsText("" + LevelProxy.TRACE.getIntValue());
		Assert.assertEquals(LevelProxy.TRACE, editor2.getValue());
		
		// Test empty string, should return null object
		LevelEditor editor3 = new LevelEditor();
		editor3.setAsText("");
		Assert.assertNull(editor3.getValue());
	}
}
