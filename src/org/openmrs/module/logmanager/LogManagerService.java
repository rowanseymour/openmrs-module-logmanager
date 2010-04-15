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
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.log4j.LoggerProxy;
import org.openmrs.module.logmanager.util.PagingInfo;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

/**
 * The log manager service
 */
@Transactional
public interface LogManagerService extends OpenmrsService {
	
	/**
	 * Gets all loggers currently being used by log4j
	 * @param incImplicit true to include loggers with inherited levels and appenders
	 * @return the list of loggers
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public List<LoggerProxy> getLoggers(boolean incImplicit) throws APIException;
	
	/**
	 * Gets the appender with the given id
	 * @param id the id
	 * @return the appender
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public AppenderProxy getAppender(int id) throws APIException;
	
	/**
	 * Gets all the appenders currently attached to a logger
	 * @param sorted true if method should return a sorted list, otherwise returns a set
	 * @return the set of appenders
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public Collection<AppenderProxy> getAppenders(boolean sorted) throws APIException;
	
	/**
	 * Adds the specified appender to the logging system
	 * @param appender the appender
	 * @param logger the logger (null means root)
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void addAppender(AppenderProxy appender, String loggerName) throws APIException;
	
	/**
	 * Deletes the specified appender from the logging system
	 * @param appender the appender
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void deleteAppender(AppenderProxy appender) throws APIException;
	
	/**
	 * Gets logging events from a suitable appender
	 * @param appender the appender which must be a MemoryAppender
	 * @param level the level of events to return (may be null)
	 * @param levelOp the boolean operation to use with level
	 * @param queryField the query search field 
	 * @param queryValue the query search value (may be null)
	 * @param paging the paging values (may be null)
	 * @return the list of logging events
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public List<LoggingEvent> getAppenderEvents(AppenderProxy appender, Level level, int levelOp, QueryField queryField, String queryValue, PagingInfo paging) throws APIException;

	/**
	 * Gets the logging event with the specified id
	 * @param appender the appender to search
	 * @param id the id of the logging event
	 * @return the logging event
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public LoggingEvent getAppenderEvent(AppenderProxy appender, int id) throws APIException;
	
	/**
	 * Gets the logging event with the specified id and the previous N logging events
	 * @param appender the appender to search
	 * @param id the id of the logging event
	 * @param prevEvents the list to receive previous events
	 * @param the number of previous events to include
	 * @return the logging event
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public LoggingEvent getAppenderEvent(AppenderProxy appender, int id, List<LoggingEvent> contextEvents, int contextCount) throws APIException;
	
	/**
	 * Gets the version of MySQL being used by OpenMRS
	 * @return the version string
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public String getMySQLVersion() throws APIException;
	
	/**
	 * Gets the preset with the given id
	 * @param presetId the preset id
	 * @return the presets
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public Preset getPreset(int presetId) throws APIException;
	
	/**
	 * Gets all presets
	 * @return the list of presets
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_VIEW_SERVER_LOG })
	@Transactional(readOnly=true)
	public List<Preset> getPresets() throws APIException;
	
	/**
	 * Saves the current logger configuration as the specified preset
	 * @param preset the preset to save
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void saveCurrentLoggersAsPreset(Preset preset) throws APIException;
	
	/**
	 * Deletes the specified preset
	 * @param preset the preset to delete
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void deletePreset(Preset preset) throws APIException;
	
	/**
	 * Clears the current logging configuration
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void clearConfiguration() throws APIException;
	
	/**
	 * Saves the current logging configuration to the external file
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void saveConfiguration() throws APIException;
	
	/**
	 * Loads the configuration from the external file
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void loadConfiguration() throws APIException;
	
	/**
	 * Loads a configuration document
	 * @param document the document to load
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void loadConfiguration(Document document) throws APIException;
	
	/**
	 * Loads the configuration document from the source code
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void loadConfigurationFromSource() throws APIException;
	
	/**
	 * Loads configuration documents from the specified modules
	 * @param document the document to load
	 * @throws APIException
	 */
	@Authorized( { Constants.PRIV_MANAGE_SERVER_LOG })
	public void loadConfigurationFromModules(String[] moduleIds) throws APIException;
}
