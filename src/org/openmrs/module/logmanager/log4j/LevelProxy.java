package org.openmrs.module.logmanager.log4j;

import org.apache.log4j.Level;
import org.openmrs.module.logmanager.AbstractProxy;

/**
 * Proxy class of log4j level class
 */
public class LevelProxy extends AbstractProxy<Level> {
	/**
	 * Proxies of the standard log4j levels
	 */
	public static final LevelProxy OFF = new LevelProxy(Level.OFF);
	public static final LevelProxy FATAL = new LevelProxy(Level.FATAL);
	public static final LevelProxy ERROR = new LevelProxy(Level.ERROR);
	public static final LevelProxy WARN = new LevelProxy(Level.WARN);
	public static final LevelProxy INFO = new LevelProxy(Level.INFO);
	public static final LevelProxy DEBUG = new LevelProxy(Level.DEBUG);
	public static final LevelProxy TRACE = new LevelProxy(Level.TRACE);
	public static final LevelProxy ALL = new LevelProxy(Level.ALL);
	
	/**
	 * Constructs a level proxy of the given level
	 * @param target the level
	 */
	public LevelProxy(Level target) {
		this.target = target;
	}
	
	/**
	 * Constructs a level proxy based on an integer value
	 * @param value the integer value
	 */
	public LevelProxy(int value) {
		this.target = Level.toLevel(value);
	}
	
	/**
	 * Gets the integer value of this level
	 * @return the integer value
	 */
	public int getIntValue() {
		return target.toInt();
	}

	/**
	 * Gets the level label, e.g. "info"
	 * @return the level label
	 */
	public String getLabel() {
		return target.toString().toLowerCase();
	}
}
