package org.openmrs.module.logmanager;

import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractEditableProxy<T> extends AbstractProxy<T> {
	
	protected static final Log log = LogFactory.getLog(AbstractEditableProxy.class);
	/**
	 * The dynamic properties object
	 */
	protected Object properties;
	
	/**
	 * The names of the dynamic property currently stored
	 */
	protected String[] propertyNames; 
	
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
}
