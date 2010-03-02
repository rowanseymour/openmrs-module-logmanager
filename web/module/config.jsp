<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/config.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">
	<spring:message code="${moduleId}.config.log4jConfiguration" />
</b>
<table class="box" cellpadding="2" cellspacing="0" width="100%">
	<tr>
		<td>
			<form method="post" name="clearForm">
				<input type="hidden" name="clear" value="1" /> 
				<input type="submit" value="<spring:message code="${moduleId}.config.clear" />" />
			</form>
		</td>
		<td><spring:message code="${moduleId}.config.clearMsg" /></td>
	</tr>
	<tr>
		<td>
			<form method="post" name="reloadForm">
				<input type="hidden" name="reload" value="1" /> 
				<input type="submit" value="<spring:message code="${moduleId}.config.reload" />" />
			</form>
		</td>
		<td><spring:message code="${moduleId}.config.reloadMsg" /></td>
	</tr>
</table>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.config.hibernateSQLLogging" />
</b>
<table class="box" cellpadding="2" cellspacing="0" width="100%">
	<tr>
		<td>
			<form method="post" name="hibernateSQLStartForm">
				<input type="hidden" name="startSQL" value="1" /> 
				<input type="submit" value="<spring:message code="${moduleId}.config.start" />" />
			</form>
		</td>
		<td><spring:message code="${moduleId}.config.startMsg" /></td>
	</tr>
	<tr>
		<td>
			<form method="post" name="hibernateSQLStopForm">
				<input type="hidden" name="stopSQL" value="1" /> 
				<input type="submit" value="<spring:message code="${moduleId}.config.stop" />" />
			</form>
		</td>
		<td><spring:message code="${moduleId}.config.stopMsg" /></td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>