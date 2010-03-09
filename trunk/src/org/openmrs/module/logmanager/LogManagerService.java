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

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.logmanager.util.PagingInfo;
import org.springframework.transaction.annotation.Transactional;

/**
 * The log manager service
 */
@Transactional
public interface LogManagerService extends OpenmrsService {
	
	/**
	 * Gets all loggers currently being used by log4j
	 * @param incImplicit true to include loggers with only inherited levels and appenders
	 * @param paging the paging values (may be null)
	 * @return the list of loggers
	 * @throws APIException
	 */
	public List<LoggerProxy> getLoggers(boolean incImplicit, PagingInfo paging) throws APIException;
	
	/**
	 * Gets the appender with the given id
	 * @param id the id
	 * @return the appender
	 * @throws APIException
	 */
	public AppenderProxy getAppender(int id) throws APIException;
	
	/**
	 * Gets all the appenders currently attached to a logger
	 * @param sorted true if method should return a sorted list, otherwise returns a set
	 * @return the set of appenders
	 */
	public Collection<AppenderProxy> getAppenders(boolean sorted) throws APIException;
	
	/**
	 * Gets logging events from a suitable appender
	 * @param appender the appender which must be a MemoryAppender
	 * @param level the level of events to return (may be null)
	 * @param queryField the query search field 
	 * @param queryValue the query search value (may be null)
	 * @param paging the paging values (may be null)
	 * @return the list of logging events
	 * @throws APIException
	 */
	public List<LoggingEvent> getAppenderEvents(AppenderProxy appender, Level level, QueryField queryField, String queryValue, PagingInfo paging) throws APIException;
}
