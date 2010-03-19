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
	document.getElementById("attachToOther").style.display = (value == "") ? "" : "none";
}
function onChangeLayoutType(value) {
	var showPattern = (value == <%= LayoutType.PATTERN.ordinal() %>);
	var showUseLocation = (value == <%= LayoutType.HTML.ordinal() %> || value == <%= LayoutType.XML.ordinal() %>);
	
	document.getElementById("layoutPattern").style.display = showPattern ? "" : "none";
	document.getElementById("useLocationSpan").style.display = showUseLocation ? "" : "none";
}
</script>

<b class="boxHeader">
	<c:choose>
		<c:when test="${appender.existing}">
			<spring:message code="${moduleId}.appenders.editAppender" />
		</c:when>
		<c:otherwise>
			<spring:message code="${moduleId}.appenders.createAppender" />
		</c:otherwise>
	</c:choose>
</b>
<form:form commandName="appender" cssClass="box">
	<table cellpadding="2" cellspacing="2" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.appenders.name"/></th>
			<td>
				<form:input path="name" cssStyle="width: 300px" />
				<form:errors path="name" cssClass="error" />
			</td>
		</tr>
		<tr>
			<th valign="top"><spring:message code="${moduleId}.appenders.type"/></th>
			<td>
				<c:out value="${appender.type}" />
				<c:if test="${appender.type.ordinal == 4}">
					<p style="font-style: italic">
						<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/icon_warn.png" />
						<spring:message code="${moduleId}.appenders.ntEventLogWarning"/>
					</p>
				</c:if>
			</td>
		</tr>
		<c:if test="${appender.requiresLayout}">
			<tr>
				<th valign="top"><spring:message code="${moduleId}.appenders.layout"/></th>
				<td>
					<c:choose>
						<c:when test="${appender.layoutType.ordinal > 0}">
							<spring:bind path="layoutType">
								<logmgr_tag:layoutTypeList name="${status.expression}" value="${status.value}" showUnknown="false" onchange="onChangeLayoutType(this.value)" />
							</spring:bind>
							<form:input path="layoutPattern" cssStyle="width: 300px; display: ${appender.layoutType.ordinal == 3 ? '' : 'none' }" />
							<form:errors path="layoutPattern" cssClass="error" />
							
							<span id="useLocationSpan" style="display: ${(appender.layoutType.ordinal == 4 || appender.layoutType.ordinal == 5) ? '' : 'none' }">
								<form:checkbox path="layoutUsesLocation" />
								<spring:message code="${moduleId}.appenders.useLocationInformation"/>
							</span>
						</c:when>
						<c:otherwise>
							${appender.layout.class.simpleName}
						</c:otherwise>
					</c:choose>
				</td>		
			</tr>
		</c:if>
		<c:if test="${appender.type.ordinal == 2}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.bufferSize"/></th>
				<td>
					<form:input path="bufferSize" size="6" />
					<form:errors path="bufferSize" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<c:if test="${appender.type.ordinal == 3}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.host"/></th>
				<td>
					<form:input path="remoteHost" />
					<spring:message code="${moduleId}.appenders.port"/>
					<form:input path="port" size="5" />
				</td>
			</tr>
		</c:if>
		<c:if test="${appender.type.ordinal == 4}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.source"/></th>
				<td>
					<form:input path="source" />
					<form:errors path="source" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<c:if test="${not appender.existing}">
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