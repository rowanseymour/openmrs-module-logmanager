<%@ tag language="java" pageEncoding="UTF-8" import="org.openmrs.module.logmanager.impl.AppenderType" %>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="false" type="org.openmrs.module.logmanager.impl.AppenderType" %>
<%@ attribute name="showUnknown" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showNTEventLog" required="false" type="java.lang.Boolean" %>

<c:set var="valUnknown" value="<%= AppenderType.UNKNOWN %>" />
<c:set var="valNTEventLog" value="<%= AppenderType.NT_EVENT_LOG %>" />

<select name="${name}">
	<c:forEach items="<%= AppenderType.values() %>" var="val">
		<c:if test="${(val != valUnknown || showUnknown) && (val != valNTEventLog || showNTEventLog)}">
			<option value="${val.ordinal}" ${value == val ? 'selected="selected"' : '' }>
				<c:out value="${val}" />
			</option>
		</c:if>
	</c:forEach>
</select>