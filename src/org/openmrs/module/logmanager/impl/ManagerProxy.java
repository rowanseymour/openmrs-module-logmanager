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
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Proxy class for LogManager methods
 */
public class ManagerProxy {
	
	protected static final Log log = LogFactory.getLog(ManagerProxy.class);
	
	/**
	 * Gets the appender with the specified id
	 * @param id the appender id
	 * @return the appender
	 */
	public static AppenderProxy getAppender(int id) {
		Collection<AppenderProxy> appenders = getAppenders();
		for (AppenderProxy appender : appenders)
			if (appender.getId() == id)
				return appender;
		return null;
	}
	
	/**
	 * Gets all the appenders currently attached to a logger
	 * @return the set of appenders
	 */
	@SuppressWarnings("unchecked")
	public static Set<AppenderProxy> getAppenders() {
		Set<AppenderProxy> appenders = new HashSet<AppenderProxy>();
		
		// Add system appender
		if (AppenderProxy.getSystemAppender() != null)
			appenders.add(AppenderProxy.getSystemAppender());
		
		// Add appenders attached to the root logger
		Enumeration<Appender> rootAppenders = LogManager.getRootLogger().getAllAppenders();
		while (rootAppenders.hasMoreElements())
			appenders.add(new AppenderProxy(rootAppenders.nextElement()));
		
		// Search for appenders on all other loggers
		Enumeration<Logger> loggersEnum = LogManager.getCurrentLoggers();
		while (loggersEnum.hasMoreElements()) {
			Logger logger = loggersEnum.nextElement();
			Enumeration<Appender> appendersEnum = logger.getAllAppenders();
			
			while (appendersEnum.hasMoreElements())
				appenders.add(new AppenderProxy(appendersEnum.nextElement()));
		}
		
		return appenders;
	}
	
	/**
	 * Gets all loggers currently being used
	 * @param incImplicit true to include loggers with inherited levels and appenders
	 * @return the list of loggers
	 */
	@SuppressWarnings("unchecked")
	public static List<LoggerProxy> getLoggers(boolean incImplicit) {
		Enumeration<Logger> loggersEnum = (Enumeration<Logger>)LogManager.getCurrentLoggers();
		
		// Convert enum to a list
		List<Logger> loggers = incImplicit ? Collections.list(loggersEnum) : getExplicitLoggersFromEnum(loggersEnum);
		
		// Sort list by logger name
		Collections.sort(loggers, new Comparator<Logger>() {
			public int compare(Logger log1, Logger log2) {
				return log1.getName().compareTo(log2.getName());
			}
		});
		
		// Convert to proxy objects
		List<LoggerProxy> proxies = new ArrayList<LoggerProxy>();
		for (Logger logger : loggers)
			proxies.add(new LoggerProxy(logger));
		
		return proxies;
	}
	
	/**
	 * Gets all loggers with matching name prefix
	 * @param prefix the name prefix
	 * @param limit the maximum number of loggers to return
	 * @return the list of loggers
	 */
	@SuppressWarnings("unchecked")
	public static List<LoggerProxy> getLoggers(String prefix, int limit) {
		Enumeration<Logger> loggersEnum = (Enumeration<Logger>)LogManager.getCurrentLoggers();
		List<Logger> loggers = new ArrayList<Logger>();
		
		// Filter based on logger name and max count
		if (prefix != null) {
			while (loggersEnum.hasMoreElements()) {
				Logger logger = loggersEnum.nextElement();
				if (logger.getName().startsWith(prefix)) {
					loggers.add(logger);
					
				}
			}
		}
		
		// Sort list by logger name
		Collections.sort(loggers, new Comparator<Logger>() {
			public int compare(Logger log1, Logger log2) {
				return log1.getName().compareTo(log2.getName());
			}
		});
		
		// Convert first N loggers to proxy objects
		List<LoggerProxy> proxies = new ArrayList<LoggerProxy>();
		int count = 0;
		for (Logger logger : loggers) {
			proxies.add(new LoggerProxy(logger));
			if (++count >= limit)
				break;
		}
		
		return proxies;
	}
	
	/**
	 * Gets the root logger
	 * @return the root logger
	 */
	public static LoggerProxy getRootLogger() {
		return new LoggerProxy(LogManager.getRootLogger());
	}
	
	/**
	 * Gets the specified logger
	 * @param name the logger name
	 * @param force forces logger creation if it doesn't exist
	 * @return the logger or null if logger doesn't exist
	 */
	public static LoggerProxy getLogger(String name, boolean force) {
		if (force)
			return new LoggerProxy(LogManager.getLogger(name));
		
		Logger target = LogManager.exists(name);	
		return (target != null) ? new LoggerProxy(target) : null;
	}
	
	/**
	 * Logs an event in the logging system
	 * @param loggerName the name of the logger
	 * @param level the event level
	 * @param message the event message
	 */
	public static void logEvent(String loggerName, LevelProxy level, String message) {	
		LoggerProxy logger = getLogger(loggerName, true);
		logger.log(Level.toLevel(level.getIntValue()), message);	
	}
	
	/**
	 * Gets all loggers with explicit level or appenders from an enumeration of loggers
	 * @param loggersEnum the enumeration of loggers
	 * @return the list of loggers
	 */
	private static List<Logger> getExplicitLoggersFromEnum(Enumeration<Logger> loggersEnum) {
		List<Logger> loggers = new ArrayList<Logger>();
		
		while (loggersEnum.hasMoreElements()) {
			Logger logger = loggersEnum.nextElement();
			if (logger.getLevel() != null || logger.getAllAppenders().hasMoreElements())
				loggers.add(logger);
		}
		
		return loggers;
	}
}
