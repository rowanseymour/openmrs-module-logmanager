package org.openmrs.module.logmanager.log4j;

import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;
import org.openmrs.module.logmanager.AbstractProxy;

/**
 * Proxy for the LoggingEvent class
 */
public class EventProxy extends AbstractProxy<LoggingEvent> {

	protected LevelProxy level;
	
	/**
	 * Constructs a proxy of the given logging event
	 * @param target the logging event
	 */
	public EventProxy(LoggingEvent target) {
		this.target = target;
		this.level = new LevelProxy(target.getLevel());
	}
	
	/**
	 * Gets the id
	 * @return the id
	 */
	public int getId() {
		return target.hashCode();
	}
	
	/**
	 * Gets the level
	 * @return the level
	 */
	public LevelProxy getLevel() {
		return level;
	}
	
	/**
	 * Gets the logger name
	 * @return the logger name
	 */
	public String getLoggerName() {
		return target.getLoggerName();
	}
	
	/**
	 * Gets the message
	 * @return the message
	 */
	public String getMessage() {
		return (String)target.getMessage();
	}
	
	/**
	 * Gets the timestamp
	 * @return the timestamp
	 */
	public long getTimeStamp() {
		return target.getTimeStamp();
	}
	
	/**
	 * Gets the thread name
	 * @return the thread name
	 */
	public String getThreadName() {
		return target.getThreadName();
	}
	
	/**
	 * Gets the nested diagnostic context (NDC)
	 * @return the NDC
	 */
	public String getNDC() {
		return target.getNDC();
	}
	
	/**
	 * Gets the class name
	 * @return the class name
	 */
	public String getClassName() {
		return target.locationInformationExists() ? target.getLocationInformation().getClassName() : null;
	}
	
	/**
	 * Gets the file name
	 * @return the file name
	 */
	public String getFileName() {
		return target.locationInformationExists() ? target.getLocationInformation().getFileName() : null;
	}
	
	/**
	 * Gets the line number
	 * @return the line number
	 */
	public String getLineNumber() {
		return target.locationInformationExists() ? target.getLocationInformation().getLineNumber() : null;
	}
	
	/**
	 * Gets the method name
	 * @return the method name
	 */
	public String getMethodName() {
		return target.locationInformationExists() ? target.getLocationInformation().getMethodName() : null;
	}
	
	/**
	 * Gets the properties map
	 * @return the properties map
	 */
	@SuppressWarnings("unchecked")
	public Map getProperties() {
		return target.getProperties();
	}
	
	/**
	 * Gets if throwable information is attached
	 * @return true if throwable attached
	 */
	public boolean isThrowableAttached() {
		return target.getThrowableInformation() != null;
	}
	
	/**
	 * Gets the lines of the throwable
	 * @return the throwable lines
	 */
	public String[] getThrowableLines() {
		return target.getThrowableStrRep();
	}
}
