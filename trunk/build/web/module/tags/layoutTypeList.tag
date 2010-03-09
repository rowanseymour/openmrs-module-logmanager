<%@ tag language="java" pageEncoding="UTF-8" import="org.openmrs.module.logmanager.LayoutType" %>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="showUnknown" required="false" type="java.lang.Boolean" %>
<%@ attribute name="value" required="false" type="java.lang.Integer" %>
<%@ attribute name="onchange" required="false" %>

<select name="${name}" onchange="${onchange}">
	<c:if test="${showUnknown}">
		<option value="0" ${value == 0 ? 'selected="selected"' : '' }>
			<%= LayoutType.UNKNOWN %>
		</option>
	</c:if>
	<option value="1" ${value == 1 ? 'selected="selected"' : '' }>	
		<%= LayoutType.SIMPLE %>
	</option>
	<option value="2" ${value == 2 ? 'selected="selected"' : '' }>
		<%= LayoutType.PATTERN %>
	</option>
</select>