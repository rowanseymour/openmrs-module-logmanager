package org.openmrs.module.logmanager.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.OptionHandler;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.AppenderProxy;
import org.openmrs.module.logmanager.AppenderType;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LayoutType;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.propertyeditor.LayoutTypeEditor;
import org.openmrs.module.logmanager.util.LogManagerUtils;
import org.openmrs.util.MemoryAppender;
import org.openmrs.web.WebConstants;
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
		
		// Some appenders require initialising after options have been loaded
		if (appender.isExisting()
				&& appender.getType() != AppenderType.CONSOLE // Closing a console appender
															  // will crash log4j...
															  // so why is it even possible??
				&& appender.isRestartOnUpdateRequired())
			appender.getTarget().close();
		
		appender.updateTarget();
		
		// Some appenders require initialising after options have been loaded
		if (appender.isRestartOnUpdateRequired())
			((OptionHandler)appender.getTarget()).activateOptions();
		
		if (!appender.isExisting()) {
			String attachTo = request.getParameter("attachTo");
			if (attachTo.isEmpty())
				attachTo = request.getParameter("attachToOther");
			
			if (attachTo.equals("0"))
				LogManager.getRootLogger().addAppender(appender.getTarget());
			else
				LogManager.getLogger(attachTo).addAppender(appender.getTarget());
		}
		
		String msg = getMessageSourceAccessor().getMessage(Constants.MODULE_ID + ".appenders." + (appender.isExisting() ? "editSuccess" : "createSuccess"));
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, msg);
		
		return new ModelAndView(new RedirectView(getSuccessView()));
	}

	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		AppenderProxy appender = (AppenderProxy)command;
		
		LogManagerService svc = Context.getService(LogManagerService.class);
		List<Logger> loggers = svc.getLoggers(false, null);
		
		map.put("id", appender.getId());
		map.put("type", appender.getType());
		map.put("initLayoutType", appender.getLayoutType());
		map.put("existing", appender.isExisting());
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
		AppenderType type = LogManagerUtils.getAppenderTypeParameter(request, "newType", AppenderType.CONSOLE);
		
		// Create the actual target appender object based on the requested type
		Appender target = null;
		switch (type) {
		case CONSOLE:
			target = new ConsoleAppender();
			break;
		case MEMORY: 
			target = new MemoryAppender();
			break;
		case SOCKET:		
			target = new SocketAppender(request.getRemoteAddr(), Constants.DEF_PORT);
			break;
		}
		
		// Set general appender properties
		target.setName(name);
		if (target.requiresLayout())
			target.setLayout(new PatternLayout(Constants.DEF_LAYOUT));
		
		return new AppenderProxy(target, false);
	}

}
