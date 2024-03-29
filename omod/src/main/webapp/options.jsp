<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/options.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">
	<spring:message code="${moduleId}.options.moduleOptions" />
</b>
<logmgr_form:form commandName="options" cssClass="box" action="options.htm">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<td nowrap="nowrap"><spring:message code="${moduleId}.options.loadExternalConfigOnStartup"/></td>
			<td>
				<logmgr_form:checkbox path="loadExternalConfigOnStartup" />
			</td>
		</tr>
		<tr>
			<td><spring:message code="${moduleId}.options.saveExternalConfigOnShutdown"/></td>
			<td>
				<logmgr_form:checkbox path="saveExternalConfigOnShutdown" />
			</td>
		</tr>
		<tr>
			<td><spring:message code="${moduleId}.options.systemAppenderName"/></td>
			<td>
				<logmgr_form:input cssStyle="width: 300px" path="systemAppenderName" />
				<logmgr_form:errors path="systemAppenderName" cssClass="error" />
			</td>
		</tr>
		<tr>
			<td><spring:message code="${moduleId}.options.alwaysRecreateSystemAppender"/></td>
			<td>
				<logmgr_form:checkbox path="alwaysRecreateSystemAppender" />
			</td>
		</tr>
		<tr>
			<td><spring:message code="${moduleId}.options.logUncaughtExceptions"/></td>
			<td>
				<logmgr_form:checkbox path="logUncaughtExceptions" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="<spring:message code="general.save" />" />
			</td>
		</tr>
	</table>
</logmgr_form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>