<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/tools.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<style type="text/css">
.switchButton {
	width: 65px;
}
</style>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.specialLoggers" />
</b>
<form method="post" class="box" id="specialForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td colspan="3" style="padding-bottom: 8px;">
				<span style="font-style: italic">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/icon_warn.png" />
					<spring:message code="${moduleId}.tools.logMessageVolumeWarning" />
				</span>
			</td>
		</tr>
		<tr>
			<td width="150">
				<input type="submit" class="switchButton"
					name="${apiProfilingStarted ? 'stop' : 'start'}APIProfiling"
					value="<spring:message code="${moduleId}.tools.${apiProfilingStarted ? 'stop' : 'start'}" />"
				/>
			</td>
			<td><b><spring:message code="${moduleId}.tools.APIProfiling" /></b></td>
			<td><spring:message code="${moduleId}.tools.${apiProfilingStarted ? 'stop' : 'start'}APIProfilingMsg" arguments="${apiProfilingLoggerName},${apiProfilingStarted ? 'WARN' : 'TRACE'}" /></td>
		</tr>
		<tr>
			<td width="150">
				<input type="submit" class="switchButton"
					name="${hibernateSQLStarted ? 'stop' : 'start'}HibernateSQL"
					value="<spring:message code="${moduleId}.tools.${hibernateSQLStarted ? 'stop' : 'start'}" />"
				/>
			</td>
			<td><b><spring:message code="${moduleId}.tools.hibernateSQL" /></b></td>
			<td><spring:message code="${moduleId}.tools.${hibernateSQLStarted ? 'stop' : 'start'}HibernateSQLMsg" arguments="${hibernateSQLLoggerName},${hibernateSQLStarted ? 'DEBUG' : 'OFF'}" /></td>
		</tr>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>