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
import org.openmrs.module.logmanager.log4j.LayoutType;

/**
 * Test cases for the class LayoutTypeEditor
 */
public class LayoutTypeEditorTest {

	@Test
	public void getAsText() {
		// Test conversion of layout type enum to integer
		LayoutTypeEditor editor1 = new LayoutTypeEditor();
		editor1.setValue(LayoutType.SIMPLE);
		Assert.assertEquals("" + LayoutType.SIMPLE.ordinal(), editor1.getAsText());
		
		LayoutTypeEditor editor2 = new LayoutTypeEditor();
		editor2.setValue(LayoutType.XML);
		Assert.assertEquals("" + LayoutType.XML.ordinal(), editor2.getAsText());
		
		// Test null object, should return empty string
		LayoutTypeEditor editor3 = new LayoutTypeEditor();
		Assert.assertEquals("", editor3.getAsText());
	}
	
	@Test
	public void setAsText() {
		// Test conversion of integer to level enum
		LayoutTypeEditor editor1 = new LayoutTypeEditor();
		editor1.setAsText("" + LayoutType.SIMPLE.ordinal());
		Assert.assertEquals(LayoutType.SIMPLE, editor1.getValue());
		
		LayoutTypeEditor editor2 = new LayoutTypeEditor();
		editor2.setAsText("" + LayoutType.XML.ordinal());
		Assert.assertEquals(LayoutType.XML, editor2.getValue());
		
		// Test empty string, should return null object
		LayoutTypeEditor editor3 = new LayoutTypeEditor();
		editor3.setAsText("");
		Assert.assertNull(editor3.getValue());
	}
}
