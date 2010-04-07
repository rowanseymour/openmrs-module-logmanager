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
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for proxy classes
 * @param <T> the target object class
 */
public abstract class AbstractProxy<T> {

	protected static final Log log = LogFactory.getLog(AbstractProxy.class);
	
	/**
	 * The target object
	 */
	protected T target;
	
	/**
	 * The property map
	 */
	protected Map<String, Object> properties = new HashMap<String, Object>();
	
	/**
	 * Gets the target object
	 * @return the target object
	 */
	public T getTarget() {
		return target;
	}
	
	/**
	 * Updates the actual object targeted by this proxy
	 */
	public abstract void updateTarget();
	
	/**
	 * Gets the property map
	 * @return the property map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getStringProperties() {
		return (Map<String, String>)(Object)properties;
	}
	
	/**
	 * Gets the property map
	 * @return the property map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Integer> getIntProperties() {
		return (Map<String, Integer>)(Object)properties;
	}
	
	/**
	 * Gets a property value
	 * @param name the name of the property
	 * @return the value of the property
	 */
	public Object getProperty(String name) {
		return properties.get(name);
	}
	
	/**
	 * Sets a property value
	 * @param name the name of the property
	 * @param value the value of the property
	 */
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}
	
	/**
	 * Reads a parameter from the target and puts in it the parameter map
	 * @param name the name of the parameter
	 */
	protected void readProperty(String name) {
		try {
			Object value = PropertyUtils.getSimpleProperty(target, name);
			properties.put(name, value);
			
			log.trace("Read property name:" + name + " value:" + value + " type:" + (value != null ? value.getClass().getSimpleName() : "unknown"));
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * Updates a parameter on the target using value from the parameter map 
	 * @param paramName the name of the parameter
	 */
	protected void updateProperty(String name) {
		try {
			Object value = properties.get(name);
			PropertyUtils.setSimpleProperty(target, name, value);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		AbstractProxy<T> proxy = (AbstractProxy<T>)obj;
		return target.equals(proxy.target);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return target.hashCode();
	}
}
