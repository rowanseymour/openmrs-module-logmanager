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
package org.openmrs.module.logmanager.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.Preset;
import org.openmrs.module.logmanager.QueryField;
import org.openmrs.module.logmanager.db.LogManagerDAO;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.log4j.ConfigurationManager;
import org.openmrs.module.logmanager.log4j.DOMConfigurationBuilder;
import org.openmrs.module.logmanager.log4j.EventProxy;
import org.openmrs.module.logmanager.log4j.LevelProxy;
import org.openmrs.module.logmanager.log4j.LogManagerProxy;
import org.openmrs.module.logmanager.log4j.LoggerProxy;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.module.logmanager.util.PagingInfo;
import org.openmrs.util.OpenmrsUtil;
import org.w3c.dom.Document;

/**
 * Implementation of the log manager service
 */
public class LogManagerServiceImpl extends BaseOpenmrsService implements LogManagerService {

	protected static final Log log = LogFactory.getLog(LogManagerServiceImpl.class);
	
	protected LogManagerDAO dao;
	
	/**
	 * Sets the database access object for this service
	 * @param dao the database access object
	 */
	public void setLogManagerDAO(LogManagerDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getLoggers(boolean)
	 */
	public List<LoggerProxy> getLoggers(boolean incImplicit) {
		return LogManagerProxy.getLoggers(incImplicit);
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppender(int)
	 */
	public AppenderProxy getAppender(int id) throws APIException {
		return LogManagerProxy.getAppender(id);
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppenders()
	 */
	public Collection<AppenderProxy> getAppenders(boolean sorted) {
		Set<AppenderProxy> appenders = LogManagerProxy.getAppenders();
			
		// Optionally sort into a list
		if (sorted) {
			List<AppenderProxy> appenderList = new ArrayList<AppenderProxy>(appenders);
			Collections.sort(appenderList, new Comparator<AppenderProxy>() {
				public int compare(AppenderProxy ap1, AppenderProxy ap2) {
					String s1 = ap1.getName() != null ? ap1.getName() : "";
					String s2 = ap2.getName() != null ? ap2.getName() : "";
					return s1.compareToIgnoreCase(s2);
				}	
			});
			return appenderList;
		}
		else
			return appenders;
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#addAppender(org.openmrs.module.logmanager.log4j.AppenderProxy, java.lang.String)
	 */
	public void addAppender(AppenderProxy appender, String loggerName)
			throws APIException {
		
		if (loggerName == null)
			LogManager.getRootLogger().addAppender(appender.getTarget());
		else
			LogManager.getLogger(loggerName).addAppender(appender.getTarget());
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#deleteAppender(AppenderProxy)
	 */
	@SuppressWarnings("unchecked")
	public void deleteAppender(AppenderProxy appender) throws APIException {
		// Get all loggers
		Enumeration<Logger> loggersEnum = (Enumeration<Logger>)LogManager.getCurrentLoggers();
		
		// Remove from root logger
		LogManager.getRootLogger().removeAppender(appender.getTarget());
		
		// Remove appender from all other loggers
		while (loggersEnum.hasMoreElements()) {
			Logger logger = loggersEnum.nextElement();
			logger.removeAppender(appender.getTarget());
		}
		
		// Close appender to release resources
		appender.getTarget().close();
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppenderEvents(Appender, Level, int, QueryField, String, Paging)
	 */
	public List<EventProxy> getAppenderEvents(AppenderProxy appender, LevelProxy level, int levelOp, QueryField queryField, String queryValue, PagingInfo paging) throws APIException {

		Collection<LoggingEvent> eventsAll = appender.getLoggingEvents();
		List<EventProxy> events = new LinkedList<EventProxy>();
		
		for (LoggingEvent event : eventsAll) {			
			if (level != null && !testLevel(new LevelProxy(event.getLevel()), levelOp, level))
				continue;
			
			if (queryValue != null) {
				switch (queryField) {
				case LOGGER_NAME:
					if (!event.getLoggerName().contains(queryValue))
						continue;
					break;
				case CLASS_NAME:
					if (!event.locationInformationExists() || !event.getLocationInformation().getClassName().contains(queryValue))
						continue;
					break;
				case FILE_NAME:
					if (!event.locationInformationExists() || !event.getLocationInformation().getFileName().contains(queryValue))
						continue;
					break;
				} 
			}
			
			events.add(0, new EventProxy(event));
		}
		
		if (paging != null)
			// Select only the events for this page
			return selectListPage(events, paging);
		
		return events;
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppenderEvent(org.openmrs.module.logmanager.log4j.AppenderProxy, int)
	 */
	public EventProxy getAppenderEvent(AppenderProxy appender, int id) {
		return getAppenderEvent(appender, id, null, 0);
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppenderEvent(AppenderProxy, int, List, int)
	 */
	public EventProxy getAppenderEvent(AppenderProxy appender, int id, List<EventProxy> contextEvents, int contextCount) throws APIException {
		Collection<LoggingEvent> events = appender.getLoggingEvents();
		LoggingEvent prevEvent = null;
		
		for (Iterator<LoggingEvent> iter = events.iterator(); iter.hasNext(); ) {
			LoggingEvent e = iter.next();
			
			if (e.hashCode() == id) {
				if (contextEvents != null) {
					// Add next n events in list
					if (contextEvents != null && contextCount > 0)
						for (; iter.hasNext() && contextEvents.size() <= contextCount; )
							contextEvents.add(new EventProxy(iter.next()));
					// And previous and next events
					else if (contextCount == -1) {
						contextEvents.add(iter.hasNext() ? new EventProxy(iter.next()) : null);
						contextEvents.add(prevEvent != null ? new EventProxy(prevEvent) : null);
					}
				}
				
				return new EventProxy(e);
			}
			
			prevEvent = e;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#updateAppender(AppenderProxy)
	 */
	public void updateAppender(AppenderProxy appender) {
		// Ensure appender exists and is synced with proxy
		appender.updateTarget();
		
		// Some appenders require initialising after options have been loaded
		if (appender.isActivationRequired())
			appender.activate();
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getPreset(int)
	 */
	public Preset getPreset(int presetId) throws APIException {
		return dao.getPreset(presetId);
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getPresets()
	 */
	public List<Preset> getPresets() throws APIException {
		return dao.getPresets();
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#savePreset(Preset)
	 */
	public void saveCurrentLoggersAsPreset(Preset preset) throws APIException {	
		Map<String, Integer> loggerMap = preset.getLoggerMap();
		loggerMap.clear();
		
		// Add root logger to map
		LoggerProxy rootLogger = LogManagerProxy.getRootLogger();
		loggerMap.put("ROOT", rootLogger.getLevelInt());
		
		// Add all other loggers
		List<LoggerProxy> loggers = getLoggers(false);
		for (LoggerProxy logger : loggers)
			loggerMap.put(logger.getName(), logger.getLevelInt());
		
		dao.savePreset(preset);
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#deletePreset(Preset)
	 */
	public void deletePreset(Preset preset) throws APIException {
		dao.deletePreset(preset);
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#saveConfiguration()
	 */
	public void clearConfiguration() throws APIException {
		ConfigurationManager.clearConfiguration();
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#saveConfiguration()
	 */
	public void saveConfiguration() throws APIException {
		try {
			String path = OpenmrsUtil.getApplicationDataDirectory() + File.separator + Constants.EXTERNAL_CONFIG_NAME;
			FileWriter writer = new FileWriter(path);

			Document document = DOMConfigurationBuilder.currentConfiguration();
			LogManagerUtils.writeDocument(document, writer);
			
			writer.close();
			
		} catch (IOException e) {
			throw new APIException("Unable to save external logging configuration", e);
		}
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#loadConfiguration()
	 */
	public void loadConfiguration() throws APIException {
		String path = OpenmrsUtil.getApplicationDataDirectory() + File.separator + Constants.EXTERNAL_CONFIG_NAME;
		
		Document document = ConfigurationManager.readConfiguration(path);	
		if (document != null) 
			ConfigurationManager.parseConfiguration(document);	
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#loadConfiguration(org.w3c.dom.Document)
	 */
	public void loadConfiguration(Document document) throws APIException {
		ConfigurationManager.parseConfiguration(document);
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#loadConfigurationFromSource()
	 */
	public void loadConfigurationFromSource() throws APIException {
		try {
			URL url = LogManagerServiceImpl.class.getResource("/" + Constants.INTERNAL_CONFIG_NAME);	
			
			// Read as document
			Reader reader = new InputStreamReader(url.openStream());			
			Document document = LogManagerUtils.readDocument(reader, new Log4jEntityResolver());
			reader.close();
			
			ConfigurationManager.parseConfiguration(document);
		} catch (Exception e) {
			throw new APIException("Unable to read internal logging configuration", e);
		}
	}

	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#loadConfigurationFromModules(java.lang.String[])
	 */
	public void loadConfigurationFromModules(String[] moduleIds) throws APIException {
		// Load from each specified module
		for (String moduleId : moduleIds) {
			try {
				Module module = ModuleFactory.getModuleById(moduleId);
				Document log4jDoc = module.getLog4j();
				if (module.getLog4j() != null)
					ConfigurationManager.parseConfiguration(log4jDoc);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getMySQLVersion()
	 */
	public String getMySQLVersion() throws APIException {
		return dao.getMySQLVersion();
	}
	
	/**
	 * Selects a range of elements from a list
	 * @param <T> the type of each list element
	 * @param list the list to select from
	 * @return a new list containing only those list elements in the current page
	 */
	private static <T> List<T> selectListPage(List<T> list, PagingInfo paging) {
		List<T> selection = new ArrayList<T>();
		
		int lStart = Math.max(0, Math.min(paging.getPageOffset(), list.size()));
		int lEnd = Math.max(0, Math.min(paging.getPageOffset() + paging.getPageSize(), list.size()));
		for (int l = lStart; l < lEnd; l++)
			selection.add(list.get(l));
		
		paging.setResultsTotal(list.size());
		
		return selection;
	}
	
	/**
	 * Compares two level values with a given boolean operator 
	 * @param level1 the first level value
	 * @param levelOp the boolean operator (-1: LE, 0: EQ, 1: GE)
	 * @param level2 the second level value
	 * @return value of the comparison
	 */
	private boolean testLevel(LevelProxy level1, int levelOp, LevelProxy level2) {
		if (level1.getIntValue() == LevelProxy.ALL.getIntValue() || level2.getIntValue() == LevelProxy.ALL.getIntValue())
			return true;
		
		switch (levelOp) {
		case Constants.BOOL_OP_LE:
			return (level1.getIntValue() <= level2.getIntValue());
		case Constants.BOOL_OP_GE:
			return (level1.getIntValue() >= level2.getIntValue());
		default:
			return (level1.getIntValue() == level2.getIntValue());
		}
	}
}
