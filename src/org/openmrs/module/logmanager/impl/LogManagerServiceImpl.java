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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.QueryField;
import org.openmrs.module.logmanager.util.PagingInfo;

/**
 * Implementation of the log manager service
 */
public class LogManagerServiceImpl extends BaseOpenmrsService implements LogManagerService {

	protected static final Log log = LogFactory.getLog(LogManagerServiceImpl.class);
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getRootLogger()
	 */
	public Logger getRootLogger() {
		return LogManager.getRootLogger();
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getLoggers(boolean, PagingInfo)
	 */
	@SuppressWarnings("unchecked")
	public List<Logger> getLoggers(boolean incImplicit, PagingInfo paging) {
		Enumeration<Logger> loggersEnum = (Enumeration<Logger>)LogManager.getCurrentLoggers();
		
		// Convert enum to a list
		List<Logger> loggersAll = incImplicit ? Collections.list(loggersEnum) : getNonDynamicLoggersFromEnum(loggersEnum);
		
		// Sort list by logger name
		Collections.sort(loggersAll, new Comparator<Logger>() {
			public int compare(Logger log1, Logger log2) {
				return log1.getName().compareTo(log2.getName());
			}
		});
		
		if (paging != null)
			// Select only the loggers for this page
			return selectListPage(loggersAll, paging);
		
		return loggersAll;
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppender(int)
	 */
	public AppenderProxy getAppender(int id) throws APIException {
		Set<AppenderProxy> appenders = getAppenders();
		for (AppenderProxy appender : appenders)
			if (appender.getId() == id)
				return appender;
		return null;
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppenders()
	 */
	@SuppressWarnings("unchecked")
	public Set<AppenderProxy> getAppenders() {
		Set<AppenderProxy> appenders = new HashSet<AppenderProxy>();
		
		// Add appenders attached to the root logger
		Enumeration<Appender> rootAppenders = LogManager.getRootLogger().getAllAppenders();
		while (rootAppenders.hasMoreElements())
			appenders.add(new AppenderProxy(rootAppenders.nextElement(), true));
		
		// Search for appenders on all other loggers
		Enumeration<Logger> loggersEnum = LogManager.getCurrentLoggers();
		while (loggersEnum.hasMoreElements()) {
			Logger logger = loggersEnum.nextElement();
			Enumeration<Appender> appendersEnum = logger.getAllAppenders();
			
			while (appendersEnum.hasMoreElements())
				appenders.add(new AppenderProxy(appendersEnum.nextElement(), true));
		}
		
		return appenders;
	}
	
	/**
	 * @see org.openmrs.module.logmanager.LogManagerService#getAppenderEvents(Appender, Level, QueryField, String, Paging)
	 */
	public List<LoggingEvent> getAppenderEvents(AppenderProxy appender, Level level, QueryField queryField, String queryValue, PagingInfo paging) throws APIException {

		Collection<LoggingEvent> eventsAll = appender.getLoggingEvents();
		List<LoggingEvent> events = new LinkedList<LoggingEvent>();
		
		for (LoggingEvent event : eventsAll) {			
			if (level != null && level.toInt() != Level.ALL_INT && level.toInt() != event.getLevel().toInt())
				continue;
			
			if (queryValue != null) {
				switch (queryField) {
				case LOGGER_NAME:
					if (!event.getLoggerName().startsWith(queryValue))
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
			
			events.add(0, event);
		}
		
		if (paging != null)
			// Select only the events for this page
			return selectListPage(events, paging);
		
		return events;
	}

	/**
	 * Gets all non-dynamic loggers from an enumeration of loggers
	 * @param loggersEnum the enumeration of loggers
	 * @return a list of non-dynamic loggers
	 */
	private static List<Logger> getNonDynamicLoggersFromEnum(Enumeration<Logger> loggersEnum) {
		List<Logger> loggers = new ArrayList<Logger>();
		
		while (loggersEnum.hasMoreElements()) {
			Logger logger = loggersEnum.nextElement();
			if (logger.getLevel() != null)
				loggers.add(logger);
		}
		
		return loggers;
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
}
