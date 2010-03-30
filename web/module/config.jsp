<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/config.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">
	<spring:message code="${moduleId}.config.options" />
</b>
<logmgr_form:form commandName="config" cssClass="box" action="config.htm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td><spring:message code="${moduleId}.config.defaultAppenderName"/></td>
			<td>
				<logmgr_form:input path="defaultAppenderName" size="32" />
				<logmgr_form:errors path="defaultAppenderName" cssClass="error" />
			</td>
			<td align="right" valign="top" rowspan="2">
				<input type="submit" value="<spring:message code="general.save" />" />
			</td>
		</tr>
		<tr>
			<td><spring:message code="${moduleId}.config.recreateDefaultAppender"/></td>
			<td>
				<logmgr_form:checkbox path="recreateDefaultAppender" />
				<logmgr_form:errors path="recreateDefaultAppender" cssClass="error" />
			</td>
		</tr>
		<tr>
			<td><spring:message code="${moduleId}.config.logUncaughtExceptions"/></td>
			<td>
				<logmgr_form:checkbox path="logUncaughtExceptions" />
				<logmgr_form:errors path="logUncaughtExceptions" cssClass="error" />
			</td>
		</tr>
	</table>
</logmgr_form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>