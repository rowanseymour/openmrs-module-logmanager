package org.openmrs.module.logmanager;

import java.util.Collection;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.log4j.Appender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.util.MemoryAppender;

/**
 * Used to edit appender objects without passing them directly to the SimpleFormController.
 * This ensures that an appender is only modified if all properties have been validated
 */
public class AppenderProxy {
	protected Appender target = null;
	protected boolean existing = true;
	
	protected String name;
	protected LayoutType layoutType;
	protected String layoutPattern;
	protected int bufferSize;
	protected String remoteHost;
	protected int port;
	
	/**
	 * Creates an appender proxy based on the given appender
	 * @param target the appender
	 * @param existing true if appender exists in the log4j system
	 */
	public AppenderProxy(Appender target, boolean existing) {
		this.target = target;
		this.existing = existing;
		
		this.name = target.getName();
		if (target.getLayout() != null) {
			this.layoutType = LayoutType.fromLayout(target.getLayout());
			if (target.getLayout() instanceof PatternLayout)
				this.layoutPattern = ((PatternLayout)target.getLayout()).getConversionPattern();
		}
		
		if (target instanceof MemoryAppender)
			this.bufferSize = ((MemoryAppender)target).getBufferSize();
		else if (target instanceof SocketAppender) {
			this.remoteHost = ((SocketAppender)target).getRemoteHost();
			this.port = ((SocketAppender)target).getPort();
		}
	}
	
	public void updateTarget() {
		target.setName(name);
		
		if (target.requiresLayout()) {
			switch (layoutType) {
			case SIMPLE:
				target.setLayout(new SimpleLayout());
				break;
			case PATTERN:
				target.setLayout(new PatternLayout(layoutPattern));
				break;
			}
		}
		
		if (target instanceof MemoryAppender)
			((MemoryAppender)target).setBufferSize(bufferSize);
		else if (target instanceof SocketAppender) {
			((SocketAppender)target).setRemoteHost(remoteHost);
			((SocketAppender)target).setPort(port);
		}
	}

	public Appender getTarget() {
		return target;
	}
	
	public boolean isExisting() {
		return existing;
	}
	
	public int getId() {
		return target.hashCode();
	}
	
	public AppenderType getType() {
		return AppenderType.fromAppender(target);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	public boolean isRestartOnUpdateRequired() {
		return this.target instanceof OptionHandler;
	}
	
	public String getLayoutStr() {
		if (target.requiresLayout()) {
			Class<?> layoutClazz = target.getLayout().getClass();
			return layoutClazz.isAssignableFrom(PatternLayout.class)
				? ((PatternLayout)target.getLayout()).getConversionPattern()
				: layoutClazz.getSimpleName();
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
	
	/**
	 * Clears the events from this appender
	 */
	public void clear() {
		if (!isClearable())
			throw new RuntimeException("Attemped to clear a non-clearable appender");
		
		// Buffer is a private field in the MemoryAppender class
		CircularFifoBuffer buffer = (CircularFifoBuffer)LogManagerUtils.getPrivateField(target, "buffer");
		
		if (buffer != null)
			buffer.clear();
	}
	
	public boolean isClearable() {
		return target instanceof MemoryAppender;
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
}
