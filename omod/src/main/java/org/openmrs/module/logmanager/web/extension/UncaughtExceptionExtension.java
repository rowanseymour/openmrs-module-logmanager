package org.openmrs.module.logmanager.web.extension;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openmrs.module.Extension;
//import org.openmrs.module.logmanager.web.util.ContextProvider;
import org.openmrs.module.logmanager.Options;
import org.openmrs.module.logmanager.web.util.ContextProvider;

/**
 * Invoked from uncaughtException.jsp
 */
public class UncaughtExceptionExtension extends Extension {
	
	protected static final Log log = LogFactory.getLog(UncaughtExceptionExtension.class);
	
	/**
	 * @see org.openmrs.module.Extension#getOverrideContent(java.lang.String)
	 */
	@Override
	public String getOverrideContent(String bodyContent) {	
		
		// Get exception object
		HttpServletRequest request = ContextProvider.getServletRequest();
		
		if (Options.getCurrent().isLogUncaughtExceptions()) {
			Exception exception = (Exception)request.getAttribute("javax.servlet.error.exception");
			String className = "";
			if (exception != null && exception.getStackTrace().length > 0)
				className = exception.getStackTrace()[0].getClassName();
	
			// Create log message
			Logger logger = LogManager.getLogger("org.openmrs");
			logger.log(className, Level.ERROR, exception.getMessage(), exception);
		}

		// Create link to server log
		return "<a href=\"" + request.getContextPath() + "/module/logmanager/viewer.htm\">View server log</a>";
	}

	/**
	 * @see org.openmrs.module.Extension#getMediaType()
	 */
	@Override
	public MEDIA_TYPE getMediaType() {
		return MEDIA_TYPE.html;
	}

}
