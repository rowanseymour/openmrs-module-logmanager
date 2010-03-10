<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/logger.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.rootLogger" />
</b>
<form method="post" class="box" name="rootLoggerForm">
	<table cellpadding="2" cellspacing="2" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.loggers.level"/></th>
			<td>
				<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[rootLogger.level]}"
						width="16" height="16" title="${levelLabels[rootLogger.level]}" />
						
						${levelLabels[rootLogger.level]}
			</td>
			<td rowspan="2" align="right" valign="top">
				<input type="button" value="<spring:message code="general.edit"/>" 
					onclick="location.href='logger.form?root'" />
			</td>
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.loggers.appenders"/></th>
			<td>
				<c:forEach var="appender" items="${rootLogger.appenders}" varStatus="status">								
					<c:choose>
						<c:when test="${!empty appender.name}">
							<a href="appender.form?editId=${appender.id}">${appender.name}</a>${!status.last ? ", " : ""}
						</c:when>
						<c:otherwise>
							<a href="appender.form?editId=${appender.id}"><i>${appender.displayName}</i></a>${!status.last ? ", " : ""}
						</c:otherwise>
					</c:choose>	
				</c:forEach>
			</td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.addLogger" />
</b>
<form method="get" class="box" action="logger.form">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.loggers.name"/></th>
			<td>
				<input type="text" name="logger" style="width: 400px" />
			</td>
			<td align="right" valign="top">
				<input type="submit" value="<spring:message code="general.add"/>" />
			</td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.existingLoggers" />
</b>
<form method="get" class="box" name="loggersForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td>	
				<spring:message code="${moduleId}.loggers.includeImplicit" />
				
				<input type="checkbox" name="incImplicit" value="1" ${incImplicit ? 'checked="checked"' : ''} />
			</td>
			
			<td align="right">
				<input type="submit" value="<spring:message code="general.refresh"/>" />
			</td>
		</tr>
	</table>

	<table cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<th>&nbsp;</th>
			<th><spring:message code="${moduleId}.loggers.name"/></th>
			<th><spring:message code="${moduleId}.loggers.level"/></th>
			<th><spring:message code="${moduleId}.loggers.appenders"/></th>
		</tr>
	
		<c:forEach var="logger" items="${loggers}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td valign="top" width="16">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[logger.effectiveLevel]}"
						width="16" height="16" title="${levelLabels[logger.effectiveLevel]}" />
				</td>
				<td>
					<a href="logger.form?logger=${logger.name}">
						${logger.name}
					</a>
				</td>
				<td>
					${logger.level != null ? levelLabels[logger.level] : levelNullLabel}
				<td>
					<c:forEach var="appender" items="${logger.appenders}" varStatus="status">					
						<c:choose>
							<c:when test="${!empty appender.name}">
								${appender.name}${!status.last ? ", " : ""}
							</c:when>
							<c:otherwise>
								<i>${appender.displayName}</i>${!status.last ? ", " : ""}
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</table>
	
	<c:if test="${!empty loggers}">
		<logmgr_tag:pager pagingInfo="${paging}" />
	</c:if>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>