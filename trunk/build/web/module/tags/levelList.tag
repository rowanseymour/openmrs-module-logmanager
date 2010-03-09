<%@ tag language="java" pageEncoding="UTF-8" import="org.apache.log4j.Level" %>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" %>
<%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="showOFF" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showALL" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showAny" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showInherit" required="false" type="java.lang.Boolean" %>
<%@ attribute name="value" required="false" type="java.lang.Integer" %>

<c:set var="level_OFF" value="<%= Level.OFF_INT %>" />
<c:set var="level_FATAL" value="<%= Level.FATAL_INT %>" />
<c:set var="level_ERROR" value="<%= Level.ERROR_INT %>" />
<c:set var="level_WARN" value="<%= Level.WARN_INT %>" />
<c:set var="level_INFO" value="<%= Level.INFO_INT %>" />
<c:set var="level_DEBUG" value="<%= Level.DEBUG_INT %>" />
<c:set var="level_TRACE" value="<%= Level.TRACE_INT %>" />
<c:set var="level_ALL" value="<%= Level.ALL_INT %>" />

<select name="${name}">
	<c:if test="${showAny}">
		<option value="${level_ALL}" ${value == level_ALL ? 'selected="selected"' : ""}>
			&lt;<spring:message code="general.allOptions" />&gt;
		</option>
	</c:if>
	<c:if test="${showInherit}">
		<option value="" ${value == null ? 'selected="selected"' : ""}>
			&lt;<spring:message code="logmanager.level.inherit" />&gt;
		</option>
	</c:if>
	<c:if test="${showOFF}">
		<option value="${level_OFF}" ${value == level_OFF ? 'selected="selected"' : ""}>
			Off
		</option>
	</c:if>
	<option value="${level_FATAL}" ${value == level_FATAL ? 'selected="selected"' : ""}>
		Fatal
	</option>
	<option value="${level_ERROR}" ${value == level_ERROR ? 'selected="selected"' : ""}>
		Error
	</option>
	<option value="${level_WARN}" ${value == level_WARN ? 'selected="selected"' : ""}>
		Warn
	</option>
	<option value="${level_INFO}" ${value == level_INFO ? 'selected="selected"' : ""}>
		Info
	</option>
	<option value="${level_DEBUG}" ${value == level_DEBUG ? 'selected="selected"' : ""}>
		Debug
	</option>
	<option value="${level_TRACE}" ${value == level_TRACE ? 'selected="selected"' : ""}>
		Trace
	</option>
	<c:if test="${showALL}">
		<option value="${level_ALL}" ${value == level_ALL ? 'selected="selected"' : ""}>
			All
		</option>
	</c:if>
</select>