package org.openmrs.module.logmanager.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openmrs.api.context.Context;
import org.openmrs.module.logmanager.Constants;
import org.openmrs.module.logmanager.LogManagerService;
import org.openmrs.module.logmanager.log4j.AppenderProxy;
import org.openmrs.module.logmanager.log4j.LogManagerProxy;
import org.openmrs.module.logmanager.log4j.LoggerProxy;
import org.openmrs.module.logmanager.propertyeditor.LevelEditor;
import org.openmrs.module.logmanager.web.util.WebUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for logger form page
 */
public class LoggerFormController extends SimpleFormController {

	protected static final Log log = LogFactory.getLog(LoggerFormController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Level.class, new LevelEditor());
		super.initBinder(request, binder);
	}

	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		
		LogManagerService svc = Context.getService(LogManagerService.class);
		LoggerProxy logger = (LoggerProxy)command;
		
		logger.removeAllAppenders();
		
		// Add those appenders specified on form
		String[] appIds = request.getParameterValues("appenders");
		if (appIds != null) {
			for (String appIdStr : appIds) {
				int appId = Integer.parseInt(appIdStr);
				AppenderProxy appender = svc.getAppender(appId);
				logger.addAppender(appender);
			}
		}
		
		// Update real logger object
		logger.updateTarget();
		
		String msg = "";
		if (logger.isRoot())
			msg = "editRootSuccess";
		else if (logger.isExisting())
			msg = "editSuccess";
		else
			msg = "addSuccess";
		
		WebUtils.setInfoMessage(request, Constants.MODULE_ID + ".loggers." + msg, null);
		
		return new ModelAndView(new RedirectView(getSuccessView()));
	}

	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		
		LoggerProxy proxy = (LoggerProxy)command;
		Map<String, Object> map = new HashMap<String, Object>();
		
		LogManagerService svc = Context.getService(LogManagerService.class);
		
		Map<AppenderProxy, Integer> appRelations = new HashMap<AppenderProxy, Integer>();
		for (AppenderProxy appender : proxy.getEffectiveAppenders())
			appRelations.put(appender, 2);
		for (AppenderProxy appender : proxy.getAppenders())
			appRelations.put(appender, 1);
		
		map.put("appenders", svc.getAppenders(true));
		map.put("appRelations", appRelations);
		
		return map;
	}

	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		
		// If logger name is specified, load existing logger
		String name = request.getParameter("logger");
		if (name != null) {
			Logger logger = LogManager.exists(name);
			if (logger != null)
				return new LoggerProxy(logger);
		}
		// Else if root is specified get it
		else if (request.getParameter("root") != null)
			return LogManagerProxy.getRootLogger();
	
		// Else create new logger with given name
		return new LoggerProxy(name, Level.INFO);
	}

}
