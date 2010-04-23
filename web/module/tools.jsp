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

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.injectEvent" />
</b>
<form method="post" class="box" id="injectForm">
	<table cellpadding="2" cellspacing="2" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.logger"/></th>
			<td>
				<input type="text" name="injectLoggerName" value="${injectLoggerName}" style="width: 400px" />
				<c:if test="${loggerNameError}">
					<span class="error"><spring:message code="${moduleId}.error.invalidName" /></span>
				</c:if>
			</td>
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.level"/></th>
			<td>
				<logmgr_tag:levelList name="injectLevel" value="${logmgr:levelToInt(injectLevel)}" showALL="false" showOFF="false" showInherit="false" />
			</td>
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.tools.message"/></th>
			<td>
				<textarea name="injectMessage" style="width: 400px" rows="2">${injectMessage}</textarea>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<input type="submit" name="inject" value="<spring:message code="${moduleId}.tools.inject"/>" />
			</td>
		</tr>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>