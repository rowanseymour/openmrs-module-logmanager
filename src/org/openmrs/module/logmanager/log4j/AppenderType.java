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
package org.openmrs.module.logmanager.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.nt.NTEventLogAppender;
import org.openmrs.util.MemoryAppender;

/**
 * Different types of appender
 */
public enum AppenderType {
	UNKNOWN,
	CONSOLE,
	MEMORY,
	FILE,
	ROLLING_FILE,
	DAILY_ROLLING_FILE,
	SOCKET,
	NT_EVENT_LOG;
	
	/**
	 * Bean-property wrapper for the ordinal method so it can be used in EL
	 */
	public int getOrdinal() {
		return ordinal();
	}
	
	/**
	 * Overridden to show class names
	 */
	@Override
	public String toString() {
		switch (this) {
		case CONSOLE:
			return "Console";
		case MEMORY:
			return "Memory";
		case FILE:
			return "File";	
		case ROLLING_FILE:
			return "Rolling file";
		case DAILY_ROLLING_FILE:
			return "Daily rolling file";
		case SOCKET:
			return "Socket";
		case NT_EVENT_LOG:
			return "NT event log";
		}
		return "Unknown";
	}
	
	/**
	 * Gets the type of the specified appender
	 * @param appender the appender whose type to return
	 * @return the type
	 */
	public static AppenderType fromAppender(Appender appender) {
		if (appender instanceof ConsoleAppender)
			return CONSOLE;
		else if (appender instanceof MemoryAppender)
			return MEMORY;
		else if (appender instanceof DailyRollingFileAppender)
			return DAILY_ROLLING_FILE;
		else if (appender instanceof RollingFileAppender)
			return ROLLING_FILE;
		else if (appender instanceof FileAppender)
			return FILE;	
		else if (appender instanceof SocketAppender)
			return SOCKET;
		else if (appender instanceof NTEventLogAppender)
			return NT_EVENT_LOG;
		else
			return UNKNOWN;
	}
	
	/**
	 * Gets the equivalent enum value of the specified integer
	 * @param value the integer value
	 * @return the enum value
	 */
	public static AppenderType fromOrdinal(int value) {
		return AppenderType.values()[value];
	}
}
