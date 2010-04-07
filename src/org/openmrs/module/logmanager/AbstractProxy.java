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

import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for proxy classes. Contains a properties object which is dynamically
 * created with a set of properties - these can be used to proxy the properties of
 * subclasses of the target class 
 * @param <T> the target object class
 */
public abstract class AbstractProxy<T> {

	protected static final Log log = LogFactory.getLog(AbstractProxy.class);
	
	/**
	 * The target object
	 */
	protected T target;
	
	/**
	 * The dynamic properties object
	 */
	protected Object properties;
	
	/**
	 * The names of the dynamic property currently stored
	 */
	protected String[] propertyNames; 
	
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
	 * Gets the dynamic properties object used for spring binding
	 * @return the properties
	 */
	public Object getProperties() {
		return properties;
	}

	/**
	 * Gets a property value
	 * @param name the name of the property
	 * @return the value of the property
	 */
	public Object getProperty(String name) {
		try {
			return PropertyUtils.getSimpleProperty(properties, name);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	/**
	 * Sets a property value
	 * @param name the name of the property
	 * @param value the value of the property
	 */
	public void setProperty(String name, Object value) {
		try {
			PropertyUtils.setSimpleProperty(properties, name, value);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * Copies properties from the target
	 * @param names the property names
	 */
	protected void copyPropertesFromTarget(String[] names) {
		BeanGenerator generator = new BeanGenerator();
		generator.setSuperclass(Object.class);
		
		try {
			// Add each property definition to the generator
			for (String name : names) {
				Class<?> type = PropertyUtils.getPropertyType(target, name);	
				generator.addProperty(name, type);
			}
			
			properties = generator.create();
			
			// Copy each property value from the target
			for (String name : names) {
				Object value = PropertyUtils.getSimpleProperty(target, name);
				PropertyUtils.setSimpleProperty(properties, name, value);
			}
		} catch (Exception e) {
			log.error(e);
		}
		
		propertyNames = names.clone();
	}
	
	/**
	 * Updates the properties on the target
	 * @param names the property names
	 */
	protected void updatePropertiesOnTarget(String[] names) {
		try {
			for (String name : names) {
				Object value = PropertyUtils.getSimpleProperty(properties, name);	
				PropertyUtils.setSimpleProperty(target, name, value);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * Gets the names of the dynamic properties
	 * @return the property names
	 */
	public String[] getPropertyNames() {
		return propertyNames;
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
