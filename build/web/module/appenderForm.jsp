<%@ page import="org.openmrs.module.logmanager.LayoutType" %>
<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/appender.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript">
function onChangeAttachTo(value) {
	document.getElementById("attachToOther").style.display = (value == "") ? "inline" : "none";
}
function onChangeLayoutType(value) {
	document.getElementById("layoutPattern").style.display = (value == <%= LayoutType.PATTERN.ordinal() %>) ? "inline" : "none";
}
</script>

<b class="boxHeader">
	<c:choose>
		<c:when test="${existing}">
			<spring:message code="${moduleId}.appenders.editAppender" />
		</c:when>
		<c:otherwise>
			<spring:message code="${moduleId}.appenders.createAppender" />
		</c:otherwise>
	</c:choose>
</b>
<form:form commandName="appender" cssClass="box">
	<input type="hidden" name="id" value="${id}" />
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th width="200"><spring:message code="${moduleId}.appenders.name"/></th>
			<td>
				<form:input path="name" />
				<form:errors path="name" cssClass="error" />
			</td>
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.appenders.type"/></th>
			<td>
				<c:out value="${type}" />
			</td>
		</tr>
		<c:if test="${type.ordinal == 1 || type.ordinal == 2}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.layout"/></th>
				<td>
					<spring:bind path="layoutType">
						<logmgr_tag:layoutTypeList name="${status.expression}" value="${status.value}" showUnknown="${existing}" onchange="onChangeLayoutType(this.value)" />
					</spring:bind>
					<form:input path="layoutPattern" cssStyle="width: 300px; display: ${initLayoutType.ordinal == 2 ? 'inline' : 'none' }" />
					<form:errors path="layoutPattern" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<c:if test="${type.ordinal == 2}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.bufferSize"/></th>
				<td>
					<form:input path="bufferSize" size="6" />
					<form:errors path="bufferSize" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<c:if test="${type.ordinal == 3}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.host"/></th>
				<td>
					<form:input path="remoteHost" />
					<spring:message code="${moduleId}.appenders.port"/>
					<form:input path="port" size="5" />
				</td>
			</tr>
		</c:if>
		<c:if test="${!existing}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.attachTo"/></th>
				<td>
					<select name="attachTo" onchange="onChangeAttachTo(this.value)" style="width: 300px">
						<option value="0">&lt;ROOT&gt;</option>
						<c:forEach items="${loggers}" var="logger">
							<option>${logger.name}</option>
						</c:forEach>
						<option value=""><spring:message code="${moduleId}.other"/>...</option>
					</select>
					<input id="attachToOther" name="attachToOther" type="text" style="width: 300px; display: none" />
				</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" align="right">
				<input type="submit" value="<spring:message code="general.save"/>" />
				<input type="button" value="<spring:message code="general.cancel"/>" onclick="location.href='appender.list'" />
			</td>
		</tr>
	</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>