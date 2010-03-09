package org.openmrs.module.logmanager;

import java.util.Collection;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.xml.XMLLayout;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.util.MemoryAppender;

/**
 * Used to edit appender objects without passing them directly to the SimpleFormController.
 * This ensures that an appender is only modified if all properties have been validated
 */
public class AppenderProxy {
	protected Appender target = null;
	protected AppenderType type;
	
	// Proxied properties
	protected String name;
	protected LayoutType layoutType;
	protected int bufferSize;
	protected String remoteHost;
	protected int port;
	
	// Proxied properties of the layout
	protected String layoutPattern;
	protected boolean layoutUsesLocation;
	
	public AppenderProxy(AppenderType type, String name) {
		this.type = type;
		this.name = name;
	}
	
	/**
	 * Creates an appender proxy based on the given appender
	 * @param target the appender
	 */
	public AppenderProxy(Appender target) {
		this.target = target;
		this.type = AppenderType.fromAppender(target);
		
		this.name = target.getName();
		
		if (target.getLayout() != null) {
			Layout layout = target.getLayout();
			this.layoutType = LayoutType.fromLayout(layout);
			if (layout instanceof PatternLayout)
				this.layoutPattern = ((PatternLayout)layout).getConversionPattern();
			if (layout instanceof HTMLLayout)
				this.layoutUsesLocation = ((HTMLLayout)layout).getLocationInfo();
		}
		
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
		// Create target if it doesn't exist
		if (target == null) {
			switch (type) {
			case CONSOLE:
				target = new ConsoleAppender();
				break;
			case MEMORY: 
				target = new MemoryAppender();
				break;
			case SOCKET:		
				target = new SocketAppender();
				break;
			}
		}
		
		// Update general properties
		target.setName(name);
		
		// Update layout
		if (getRequiresLayout()) {
			Layout layout = null;
			switch (layoutType) {
			case SIMPLE:
				layout = new SimpleLayout();
				break;
			case TTCC:
				layout = new TTCCLayout();
				break;
			case PATTERN:
				layout = new PatternLayout(layoutPattern);
				break;
			case HTML:
				layout = new HTMLLayout();
				((HTMLLayout)layout).setLocationInfo(layoutUsesLocation);
				break;
			case XML:
				layout = new XMLLayout();
				((XMLLayout)layout).setLocationInfo(layoutUsesLocation);
				break;
			}
			target.setLayout(layout);
		}
		
		// Update subclass properties
		if (target instanceof MemoryAppender)
			((MemoryAppender)target).setBufferSize(bufferSize);
		if (target instanceof SocketAppender) {
			((SocketAppender)target).setRemoteHost(remoteHost);
			((SocketAppender)target).setPort(port);
		}
	}

	/**
	 * Gets the appender referenced by this proxy
	 * @return the target appender
	 */
	public Appender getTarget() {
		return target;
	}
	
	/**
	 * Gets whether this proxy references an actual appender
	 * @return true if appender exists in log4j
	 */
	public boolean isExisting() {
		return (target != null);
	}
	
	/**
	 * Gets the id of this appender or zero if target appender doesn't exist
	 * @return the appender id
	 */
	public int getId() {
		return (target != null) ? target.hashCode() : 0;
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
	
	public String getDisplayName() {
		return (name != null && !name.isEmpty()) ? name : ("Anonymous " + target.getClass().getSimpleName());
	}
	
	public boolean getRequiresLayout() {
		return (type == AppenderType.CONSOLE);
	}
	
	/**
	 * @return the layoutType
	 */
	public LayoutType getLayoutType() {
		return layoutType;
	}

	/**
	 * @param layoutType the layoutType to set
	 */
	public void setLayoutType(LayoutType layoutType) {
		this.layoutType = layoutType;
	}

	/**
	 * @return the layoutPattern
	 */
	public String getLayoutPattern() {
		return layoutPattern;
	}

	/**
	 * @param layoutPattern the layoutPattern to set
	 */
	public void setLayoutPattern(String layoutPattern) {
		this.layoutPattern = layoutPattern;
	}
	
	public boolean getLayoutUsesLocation() {
		return layoutUsesLocation;
	}
	
	public void setLayoutUsesLocation(boolean layoutUsesLocation) {
		this.layoutUsesLocation = layoutUsesLocation;
	}

	public boolean isRestartOnUpdateRequired() {
		return this.target instanceof OptionHandler;
	}
	
	public String getLayoutStr() {
		if (getRequiresLayout()) {
			if (layoutType == LayoutType.PATTERN)
				return layoutPattern;
			else if (layoutType != LayoutType.UNKNOWN) {
				return layoutType.toString();
			}
			else if (target != null)
				return target.getLayout().getClass().getSimpleName();
		}
		return "";
	}
	
	public boolean isViewable() {
		return target instanceof MemoryAppender;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<LoggingEvent> getLoggingEvents() {
		if (!isViewable())
			throw new RuntimeException("Attemped to get events from a non-viewable appender");
		
		// Buffer is a private field in the MemoryAppender class
		CircularFifoBuffer buffer = (CircularFifoBuffer)LogManagerUtils.getPrivateField(target, "buffer");
		
		return buffer;
	}
	
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
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * @return the remoteHost
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * @param remoteHost the remoteHost to set
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		AppenderProxy proxy = (AppenderProxy)obj;
		return proxy.target.equals(this.target);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return target != null ? target.hashCode() : super.hashCode();
	}
}
