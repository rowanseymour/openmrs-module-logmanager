package org.openmrs.module.logmanager.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.OptionHandler;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.AppenderType;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LayoutType;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.LoggerProxy;
import org.openmrs.module.logmanager.propertyeditor.LayoutTypeEditor;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for appender form page
 */
public class AppenderFormController extends SimpleFormController {

	protected static final Log log = LogFactory.getLog(AppenderFormController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(LayoutType.class, new LayoutTypeEditor());
		super.initBinder(request, binder);
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		
		// Check logger to attach appender to
		String attachTo = request.getParameter("attachTo");
		String attachToOther = request.getParameter("attachToOther");
		if (attachTo != null && attachTo.isEmpty() && attachToOther.isEmpty())
			errors.rejectValue("attachTo", Constants.MODULE_ID + ".error.attachTo");
		
		return super.processFormSubmission(request, response, command, errors);
	}



	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		
		AppenderProxy appender = (AppenderProxy)command;
		boolean exists = appender.isExisting();
		
		// Some appenders require initialising after options have been loaded
		if (appender.isExisting()
				&& appender.getType() != AppenderType.CONSOLE // Closing a console appender
															  // will crash log4j...
															  // so why is it even possible??
				&& appender.isRestartOnUpdateRequired())
			appender.getTarget().close();
		
		// Ensure appender exists and is synced with proxy
		appender.updateTarget();
		
		// Some appenders require initialising after options have been loaded
		if (appender.isRestartOnUpdateRequired())
			((OptionHandler)appender.getTarget()).activateOptions();
		
		if (!exists) {
			String attachTo = request.getParameter("attachTo");
			if (attachTo.isEmpty())
				attachTo = request.getParameter("attachToOther");
			
			if (attachTo.equals("0"))
				LogManager.getRootLogger().addAppender(appender.getTarget());
			else
				LogManager.getLogger(attachTo).addAppender(appender.getTarget());
		}
		
		WebUtils.setInfoMessage(request, 
				Constants.MODULE_ID + ".appenders." + (exists ? "editSuccess" : "createSuccess"), 
				new Object[] { appender.getName() });
		
		return new ModelAndView(new RedirectView(getSuccessView()));
	}

	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		LogManagerService svc = Context.getService(LogManagerService.class);
		List<LoggerProxy> loggers = svc.getLoggers(false);
		
		map.put("loggers", loggers);
		return map;
	}

	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		// If id is specified, load existing appender
		int id = ServletRequestUtils.getIntParameter(request, "editId", 0);
		if (id != 0) {
			LogManagerService svc = Context.getService(LogManagerService.class);
			return svc.getAppender(id);
		}
		
		// Create new appender from parameters passed from appender list page
		String name = request.getParameter("newName");
		AppenderType type = WebUtils.getAppenderTypeParameter(request, "newType", AppenderType.CONSOLE);
		
		// Create the appender object based on the requested type
		AppenderProxy appender = new AppenderProxy(type, name);
		switch (type) {
		case MEMORY:
			appender.setBufferSize(100);
			break;
		case SOCKET:		
			appender.setRemoteHost(request.getRemoteAddr());
			appender.setPort(Constants.DEF_PORT);
			break;
		case NT_EVENT_LOG:
			appender.setSource(Constants.DEF_SOURCE);
			break;
		}
		
		// Default to pattern layout
		if (appender.getRequiresLayout()) {
			appender.setLayoutType(LayoutType.PATTERN);
			appender.setLayoutPattern(Constants.DEF_LAYOUT);
		}
		
		return appender;
	}

}
