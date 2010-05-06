<%@ tag language="java" pageEncoding="UTF-8" import="org.openmrs.module.logmanager.impl.LevelProxy" %>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" %>
<%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" %>
<%@ taglib prefix="openmrs" uri="/WEB-INF/taglibs/openmrs.tld" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="cssStyle" required="false" %>
<%@ attribute name="onChange" required="false" %>

<openmrs:htmlInclude file="/scripts/jquery/autocomplete/jquery.autocomplete.min.js" />
<openmrs:htmlInclude file="/moduleResources/logmanager/autocomplete.css" />

<script type="text/javascript">
$(document).ready(function() {
	$("#${id}").autocomplete(
		"logger.list",
		{ extraParams: { json: "" }, matchCase: true, matchContains: false, minChars: 3 }
	);
});
</script>

<input type="text"
	id="${id}"
	name="${name}"
	<c:if test="${cssStyle != null}">style="${cssStyle}"</c:if>
	<c:if test="${onChange != null}">onkeyup="${onChange}"</c:if>
/>
