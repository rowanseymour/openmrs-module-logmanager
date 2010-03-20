<h2><spring:message code="logmanager.title"/></h2>

<ul id="menu">
	<li class="first<c:if test='<%= request.getRequestURI().contains("logger") %>'> active</c:if>">
		<a href="${pageContext.request.contextPath}/module/logmanager/logger.list">
			<spring:message code="logmanager.menu.loggers" /></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("appender") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/logmanager/appender.list">
			<spring:message code="logmanager.menu.appenders"/></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("viewer") || request.getRequestURI().contains("event") || request.getRequestURI().contains("serverLog.form") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/logmanager/viewer.htm">
			<spring:message code="logmanager.menu.viewer"/></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("config") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/logmanager/config.htm">
			<spring:message code="logmanager.menu.config"/></a>
	</li>
</ul>