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

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.nt.NTEventLogAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.util.MemoryAppender;

/**
 * Used to edit appender objects without passing them directly to the SimpleFormController.
 * This ensures that an appender is only modified if all properties have been validated
 */
public class AppenderProxy extends AbstractProxy<Appender> {
	
	protected AppenderType type;
	protected boolean existing;
	
	// Proxied properties
	protected String name;
	protected LayoutProxy layout;
	protected int bufferSize;
	protected String remoteHost;
	protected int port;
	protected String source;
	
	protected static AppenderProxy systemAppender;
	
	/**
	 * Creates a proxy for a new appender
	 * @param type the type
	 * @param name the name
	 */
	public AppenderProxy(AppenderType type, String name) {
		this.type = type;
		this.name = name;
		this.existing = false;
		
		// Create target based on type
		switch (type) {
		case CONSOLE:
			target = new ConsoleAppender();
			break;
		case MEMORY: 
			target = new MemoryAppender();
			break;
		case SOCKET:		
			target = new SocketAppender();
			port = Constants.DEF_APPENDER_PORT;
			break;
		case NT_EVENT_LOG:
			target = new NTEventLogAppender();
			source = Constants.DEF_APPENDER_SOURCE;
		}
		
		// Default to pattern layout if layout is required
		if (isRequiresLayout())
			layout = new LayoutProxy(LayoutType.PATTERN);
	}
	
	/**
	 * Creates a proxy for an existing appender
	 * @param target the appender
	 */
	public AppenderProxy(Appender target) {
		this.target = target;
		this.type = AppenderType.fromAppender(target);
		this.existing = true;
		
		this.name = target.getName();
		
		// Create proxy for layout
		if (target.getLayout() != null)
			this.layout = new LayoutProxy(target.getLayout());
		
		// Copy parameters based on type
		if (target instanceof MemoryAppender)
			this.bufferSize = ((MemoryAppender)target).getBufferSize();
		else if (target instanceof SocketAppender) {
			this.remoteHost = ((SocketAppender)target).getRemoteHost();
			this.port = ((SocketAppender)target).getPort();
		}
	}
	
	/**
	 * Updates the actual appender referenced by this proxy object
	 */
	public void updateTarget() {	
		// Update general properties
		target.setName(name);
		
		// Update layout
		if (layout != null) {
			layout.updateTarget();
			target.setLayout(layout.getTarget());
		}
		
		// Update subclass properties
		if (target instanceof MemoryAppender)
			((MemoryAppender)target).setBufferSize(bufferSize);
		else if (target instanceof SocketAppender) {
			((SocketAppender)target).setRemoteHost(remoteHost);
			((SocketAppender)target).setPort(port);
		}
		else if (target instanceof NTEventLogAppender)
			((NTEventLogAppender)target).setSource(source);
	}
	
	/**
	 * Gets whether this proxy references an existing appender
	 * @return true if appender already exists in log4j
	 */
	public boolean isExisting() {
		return existing;
	}
	
	/**
	 * Gets the id of this appender
	 * @return the appender id
	 */
	public int getId() {
		return target.hashCode();
	}
	
	/**
	 * Gets the type of the appender
	 * @return the type
	 */
	public AppenderType getType() {
		return type;
	}
	
	/**
	 * Gets the name of the appender
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the appender
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets whether appender must be closed and restarted after changing
	 * its properties
	 * @return true if appender must be restarted
	 */
	public boolean isRestartOnUpdateRequired() {
		return target instanceof OptionHandler;
	}
	
	/**
	 * Gets whether this appender requires a layout, based on its type. Even tho this
	 * module doesn't require it's memory appender to have a layout - the existing log
	 * viewer page does
	 * @return true if it requires a layout
	 */
	public boolean isRequiresLayout() {
		return target.requiresLayout();
	}
	
	/**
	 * Gets the layout used by this appender
	 * @return the layout
	 */
	public LayoutProxy getLayout() {
		return layout;
	}
	
	/**
	 * Sets the layout used by this appender
	 * @param layout the layout
	 */
	public void setLayout(LayoutProxy layout) {
		this.layout = layout;
	}
	
	/**
	 * Gets if this appender is viewable - i.e. is it a memory appender
	 * @return true if appender is viewable
	 */
	public boolean isViewable() {
		return target instanceof MemoryAppender;
	}
	
	/**
	 * Gets logging events from a memory appender
	 * @return the list of logging events
	 */
	@SuppressWarnings("unchecked")
	public Collection<LoggingEvent> getLoggingEvents() {
		if (!isViewable())
			throw new RuntimeException("Attemped to get events from a non-viewable appender");
		
		// Buffer is a private field in the MemoryAppender class
		CircularFifoBuffer buffer = (CircularFifoBuffer)LogManagerUtils.getPrivateField(target, "buffer");
		
		return buffer;
	}
	
	/**
	 * Gets whether this appender can be cleared, i.e. is it a memory appender
	 * @return true if appender can be cleared
	 */
	public boolean isClearable() {
		return target instanceof MemoryAppender;
	}
	
	/**
	 * Clears the events from this appender
	 */
	public void clear() {
		if (!isClearable())
			throw new RuntimeException("Attemped to clear events on a non-clearable appender");
		
		// Buffer is a private field in the MemoryAppender class
		CircularFifoBuffer buffer = (CircularFifoBuffer)LogManagerUtils.getPrivateField(target, "buffer");
		
		if (buffer != null)
			buffer.clear();
	}

	/**
	 * Gets the buffer size (applies to memory appenders)
	 * @return the buffer size
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * Sets the buffer size (applies to memory appenders)
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * Gets the remote host (applies to socket appenders)
	 * @return the remote host
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Sets the remote host (applies to socket appenders)
	 * @param remoteHost the remoteHost to set
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * Gets the remote port (applies to socket appenders)
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the remote port (applies to socket appenders)
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the source (applies to NT event log appenders)
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source (applies to NT event log appenders)
	 * @param source the source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Gets the system appender
	 * @return the system appender
	 */
	public static AppenderProxy getSystemAppender() {
		return systemAppender;
	}

	/**
	 * Sets the system appender
	 * @param systemAppender the new system appender
	 */
	public static void setSystemAppender(AppenderProxy systemAppender) {
		AppenderProxy.systemAppender = systemAppender;
	}
	
	/**
	 * Gets whether this appender is the system appender
	 * @return true if this appender is the system appender
	 */
	public boolean isSystemAppender() {
		return (this.target == systemAppender.target);
	}	
}
