<%@ taglib prefix="openmrs_tag" tagdir="/WEB-INF/tags" %>

<h2><spring:message code="logmanager.title"/></h2>

<ul id="menu">
	<li <c:if test='<%= request.getRequestURI().contains("/hosts") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/zabbix/logger.list">
			<spring:message code="zabbix.menu.hosts" /></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("/options") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/zabbix/options.form">
			<spring:message code="zabbix.menu.options"/></a>
	</li>
</ul>