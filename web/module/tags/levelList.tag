<%@ tag language="java" pageEncoding="UTF-8" import="org.openmrs.module.logmanager.log4j.LevelProxy" %>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" %>
<%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="showOFF" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showALL" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showAny" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showInherit" required="false" type="java.lang.Boolean" %>
<%@ attribute name="value" required="false" type="org.openmrs.module.logmanager.log4j.LevelProxy" %>

<c:set var="level_OFF" value="<%= LevelProxy.OFF.getIntValue() %>" />
<c:set var="level_FATAL" value="<%= LevelProxy.FATAL.getIntValue() %>" />
<c:set var="level_ERROR" value="<%= LevelProxy.ERROR.getIntValue() %>" />
<c:set var="level_WARN" value="<%= LevelProxy.WARN.getIntValue() %>" />
<c:set var="level_INFO" value="<%= LevelProxy.INFO.getIntValue() %>" />
<c:set var="level_DEBUG" value="<%= LevelProxy.DEBUG.getIntValue() %>" />
<c:set var="level_TRACE" value="<%= LevelProxy.TRACE.getIntValue() %>" />
<c:set var="level_ALL" value="<%= LevelProxy.ALL.getIntValue() %>" />

<select name="${name}">
	<c:if test="${showAny}">
		<option value="${level_ALL}" ${value.intValue == level_ALL ? 'selected="selected"' : ""}>
			&lt;<spring:message code="general.allOptions" />&gt;
		</option>
	</c:if>
	<c:if test="${showInherit}">
		<option value="" ${value == null ? 'selected="selected"' : ""}>
			&lt;<spring:message code="logmanager.level.inherit" />&gt;
		</option>
	</c:if>
	<c:if test="${showOFF}">
		<option value="${level_OFF}" ${value.intValue == level_OFF ? 'selected="selected"' : ""}>
			Off
		</option>
	</c:if>
	<option value="${level_FATAL}" ${value.intValue == level_FATAL ? 'selected="selected"' : ""}>
		Fatal
	</option>
	<option value="${level_ERROR}" ${value.intValue == level_ERROR ? 'selected="selected"' : ""}>
		Error
	</option>
	<option value="${level_WARN}" ${value.intValue == level_WARN ? 'selected="selected"' : ""}>
		Warn
	</option>
	<option value="${level_INFO}" ${value.intValue == level_INFO ? 'selected="selected"' : ""}>
		Info
	</option>
	<option value="${level_DEBUG}" ${value.intValue == level_DEBUG ? 'selected="selected"' : ""}>
		Debug
	</option>
	<option value="${level_TRACE}" ${value.intValue == level_TRACE ? 'selected="selected"' : ""}>
		Trace
	</option>
	<c:if test="${showALL}">
		<option value="${level_ALL}" ${value.intValue == level_ALL ? 'selected="selected"' : ""}>
			All
		</option>
	</c:if>
</select>