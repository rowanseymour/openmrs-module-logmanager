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
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td  width="30%" nowrap="nowrap"><spring:message code="${moduleId}.options.systemAppenderName"/></td>
			<td>
				<logmgr_form:input cssStyle="width: 300px" path="systemAppenderName" />
				<logmgr_form:errors path="systemAppenderName" cssClass="error" />
			</td>
			<td align="right" valign="top" rowspan="3">
				<input type="submit" value="<spring:message code="general.save" />" />
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
			<td><spring:message code="${moduleId}.options.autoSaveToExternalConfig"/></td>
			<td>
				<logmgr_form:checkbox path="autoSaveToExternalConfig" />
			</td>
		</tr>
	</table>
</logmgr_form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>