<%@ tag language="java" pageEncoding="UTF-8" import="org.openmrs.module.logmanager.LayoutType" %>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="showUnknown" required="false" type="java.lang.Boolean" %>
<%@ attribute name="value" required="false" type="java.lang.Integer" %>
<%@ attribute name="onchange" required="false" %>

<c:set var="layout_unknown" value="<%= LayoutType.UNKNOWN %>" />
<c:set var="layout_simple" value="<%= LayoutType.SIMPLE %>" />
<c:set var="layout_ttcc" value="<%= LayoutType.TTCC %>" />
<c:set var="layout_pattern" value="<%= LayoutType.PATTERN %>" />
<c:set var="layout_html" value="<%= LayoutType.HTML %>" />
<c:set var="layout_xml" value="<%= LayoutType.XML %>" />

<select name="${name}" onchange="${onchange}">
	<c:if test="${showUnknown}">
		<option value="${layout_unknown.ordinal}" ${value == layout_unknown.ordinal ? 'selected="selected"' : '' }>
			<c:out value="${layout_unknown}" />
		</option>
	</c:if>
	<option value="${layout_simple.ordinal}" ${value == layout_simple.ordinal ? 'selected="selected"' : '' }>
		<c:out value="${layout_simple}" />
	</option>
	<option value="${layout_ttcc.ordinal}" ${value == layout_ttcc.ordinal ? 'selected="selected"' : '' }>
		<c:out value="${layout_ttcc}" />
	</option>
	<option value="${layout_pattern.ordinal}" ${value == layout_pattern.ordinal ? 'selected="selected"' : '' }>
		<c:out value="${layout_pattern}" />
	</option>
	<option value="${layout_html.ordinal}" ${value == layout_html.ordinal ? 'selected="selected"' : '' }>
		<c:out value="${layout_html}" />
	</option>
	<option value="${layout_xml.ordinal}" ${value == layout_xml.ordinal ? 'selected="selected"' : '' }>
		<c:out value="${layout_xml}" />
	</option>
</select>