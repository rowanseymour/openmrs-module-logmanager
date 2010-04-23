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
	 * Gets the target object
	 * @return the target object
	 */
	public T getTarget() {
		return target;
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

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return target.toString();
	}
}
