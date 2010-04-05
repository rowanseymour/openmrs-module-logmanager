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
	
	document.getElementById("layout.conversionPattern").style.display = showPattern ? "" : "none";
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
<logmgr_form:form commandName="appender" cssClass="box">
	<table cellpadding="2" cellspacing="2" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.appenders.name"/></th>
			<td>
				<logmgr_form:input path="name" cssStyle="width: 300px" disabled="${appender.systemAppender}" />
				<logmgr_form:errors path="name" cssClass="error" />
				<c:if test="${appender.systemAppender}">
					<img src="${pageContext.request.contextPath}/images/lock.gif" title="<spring:message code="${moduleId}.appenders.systemAppender"/>" />
				</c:if>
			</td>
		</tr>
		<tr>
			<th valign="top"><spring:message code="${moduleId}.appenders.type"/></th>
			<td>
				<c:choose>
					<c:when test="${appender.type.ordinal > 0}">
						<c:out value="${appender.type}" />
					</c:when>
					<c:otherwise>
						<i>${appender.target.class.simpleName}</i>
					</c:otherwise>
				</c:choose>
				<c:if test="${appender.type.ordinal == 4}">
					<p style="font-style: italic">
						<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/icon_warn.png" />
						<spring:message code="${moduleId}.appenders.ntEventLogWarning" arguments="http://logging.apache.org/log4j/1.2/index.html" />
					</p>
				</c:if>
			</td>
		</tr>
		<c:if test="${appender.requiresLayout}">
			<tr>
				<th valign="top"><spring:message code="${moduleId}.appenders.layout"/></th>
				<td>
					<c:choose>
						<c:when test="${appender.layout.type.ordinal > 0}">
							<spring:bind path="layout.type">
								<logmgr_tag:layoutTypeList name="${status.expression}" value="${status.value}" showUnknown="false" onchange="onChangeLayoutType(this.value)" />
							</spring:bind>
							<logmgr_form:input path="layout.conversionPattern" cssStyle="width: 300px; display: ${appender.layout.type.ordinal == 3 ? '' : 'none' }" />
							<logmgr_form:errors path="layout.conversionPattern" cssClass="error" />
							
							<span id="useLocationSpan" style="display: ${(appender.layout.type.ordinal == 4 || appender.layout.type.ordinal == 5) ? '' : 'none' }">
								<logmgr_form:checkbox path="layout.locationInfo" />
								<spring:message code="${moduleId}.appenders.useLocationInformation"/>
							</span>
						</c:when>
						<c:otherwise>
							${appender.layout.target.class.simpleName}
						</c:otherwise>
					</c:choose>
				</td>		
			</tr>
		</c:if>
		<c:if test="${appender.type.ordinal == 2}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.bufferSize"/></th>
				<td>
					<logmgr_form:input path="bufferSize" size="6" />
					<logmgr_form:errors path="bufferSize" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<c:if test="${appender.type.ordinal == 3}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.host"/></th>
				<td>
					<logmgr_form:input path="remoteHost" />
					<logmgr_form:errors path="remoteHost" cssClass="error" />
					<spring:message code="${moduleId}.appenders.port"/>
					<logmgr_form:input path="port" size="5" />
					<logmgr_form:errors path="port" cssClass="error" />
				</td>
			</tr>
		</c:if>
		<c:if test="${appender.type.ordinal == 4}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.source"/></th>
				<td>
					<logmgr_form:input path="source" />
					<logmgr_form:errors path="source" cssClass="error" />
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
</logmgr_form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>