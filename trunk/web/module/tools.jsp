<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/tools.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.log4jConfiguration" />
</b>
<form method="post" class="box" name="configForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td width="150">			
				<input type="submit" name="clear" value="<spring:message code="${moduleId}.tools.clear" />" />
			</td>
			<td><spring:message code="${moduleId}.tools.clearMsg" /></td>
		</tr>
		<tr>
			<td>
				<input type="submit" name="reload" value="<spring:message code="${moduleId}.tools.reload" />" />
			</td>
			<td><spring:message code="${moduleId}.tools.reloadMsg" /></td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.hibernateSQLLogging" />
</b>
<form method="post" class="box" name="hibernateForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<c:choose>
				<c:when test="${sqlLoggerStarted}">
					<td width="150">
						<input type="submit" name="stopSQL" value="<spring:message code="${moduleId}.tools.stop" />" />
					</td>
					<td>
						<spring:message code="${moduleId}.tools.stopMsg" arguments="${sqlLoggerName}" />
					</td>
				</c:when>
				<c:otherwise>
					<td width="150">
						<input type="submit" name="startSQL" value="<spring:message code="${moduleId}.tools.start" />" />
					</td>
					<td>
						<spring:message code="${moduleId}.tools.startMsg" arguments="${sqlLoggerName}" />
					</td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>