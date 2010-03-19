<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="View Server Log" otherwise="/login.htm" redirect="/module/logmanager/viewer.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">
	<spring:message code="${moduleId}.viewer.viewLoggingEvent" />
</b>
<form method="post" class="box" name="eventViewForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td align="right">
				<input type="button"
					onclick="location.href='viewer.htm?appId=${appender.id}'"
					value="<spring:message code="general.back"/>"
				/>
			</td>
		</tr>
	</table>

	<table cellpadding="2" cellspacing="2" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.viewer.level" /></th>
			<td><img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[event.level]}" /> ${levelLabels[event.level]}</td>	
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.viewer.logger" /></th>
			<td>${event.loggerName}</td>	
		</tr>	
		<tr>
			<th><spring:message code="${moduleId}.viewer.time" /></th>
			<td>${logmgr:formatTimestamp(event.timeStamp)}</td>	
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.viewer.thread" /></th>
			<td>${event.threadName}</td>	
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.viewer.message" /></th>
			<td>${logmgr:formatMessage(event.message)}</td>	
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.viewer.class" /></th>
			<td>${event.locationInformation.className}</td>	
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.viewer.file" /></th>
			<td>${event.locationInformation.fileName} (<spring:message code="${moduleId}.viewer.line" /> ${event.locationInformation.lineNumber})</td>	
		</tr>		
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>