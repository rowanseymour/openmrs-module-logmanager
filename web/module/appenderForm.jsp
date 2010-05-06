<%@ page import="org.openmrs.module.logmanager.impl.LayoutType" %>
<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/appender.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript">
function onChangeAttachTo(value) {
	if (value == "")
		$("#attachToOther").show();
	else
		$("#attachToOther").hide();
}

function onChangeLayoutType(value) {
	if (value == <%= LayoutType.PATTERN.ordinal() %>)
		$("#layout\\.conversionPattern").show();
	else
		$("#layout\\.conversionPattern").hide();
	
	if (value == <%= LayoutType.HTML.ordinal() %> || value == <%= LayoutType.XML.ordinal() %>)
		$("#useLocationSpan").show();
	else
		$("#useLocationSpan").hide();
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
			<th width="150"><spring:message code="general.name"/></th>
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
				<c:if test="${appender.type.ordinal == 7}">
					<p style="font-style: italic">
						<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/icon_warn.png" />
						<spring:message code="${moduleId}.appenders.ntEventLogWarning" arguments="http://logging.apache.org/log4j/1.2/index.html" />
					</p>
				</c:if>
			</td>
		</tr>
		<%------------------ Layout fields ------------------%>
		<c:if test="${appender.layoutRequired}">
			<tr>
				<th valign="top"><spring:message code="${moduleId}.appenders.layout"/></th>
				<td>
					<c:choose>
						<c:when test="${appender.layout.type.ordinal > 0}">
							<spring:bind path="layout.type">
								<logmgr_tag:layoutTypeField name="${status.expression}" value="${status.value}" showUnknown="false" onchange="onChangeLayoutType(this.value)" />
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
		<%------------------ CONSOLE fields ------------------%>
		<c:if test="${appender.type.ordinal == 1}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.target"/></th>
				<td>
					<logmgr_form:select path="properties.target">
						<logmgr_form:option value="System.out" />
						<logmgr_form:option value="System.err" />
					</logmgr_form:select>
				</td>		
			</tr>
			<tr>
				<th><spring:message code="${moduleId}.appenders.options"/></th>
				<td>
					<logmgr_form:checkbox path="properties.follow" />
					<spring:message code="${moduleId}.appenders.honorReassignmentsAfterConfiguration"/>
				</td>		
			</tr>
		</c:if>
		<%------------------ MEMORY fields ------------------%>
		<c:if test="${appender.type.ordinal == 2}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.bufferSize"/></th>
				<td>
					<logmgr_form:input path="properties.bufferSize" size="6" />
					<logmgr_form:errors path="properties.bufferSize" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<%------------------ FILE fields ------------------%>
		<c:if test="${appender.type.ordinal == 3 || appender.type.ordinal == 4 || appender.type.ordinal == 5}">		
			<tr>
				<th><spring:message code="${moduleId}.appenders.file"/></th>
				<td>
					<logmgr_form:input path="properties.file" cssStyle="width: 300px" />
					<logmgr_form:errors path="properties.file" cssClass="error" />
				</td>
			</tr>
			<tr>
				<th rowspan="2" valign="top"><spring:message code="${moduleId}.appenders.options"/></th>
				<td>
					<logmgr_form:checkbox path="properties.append" />
					<spring:message code="${moduleId}.appenders.appendToFileContents"/>
				</td>		
			</tr>
			<tr>
				<td>
					<logmgr_form:checkbox path="properties.bufferedIO" />
					<spring:message code="${moduleId}.appenders.bufferedIO"/>
				</td>		
			</tr>
			<tr>
				<th><spring:message code="${moduleId}.appenders.bufferSize"/></th>
				<td>
					<logmgr_form:input path="properties.bufferSize" size="8" />
					<spring:message code="${moduleId}.bytes"/>
					<logmgr_form:errors path="properties.bufferSize" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<%------------------ ROLLING FILE fields ------------------%>
		<c:if test="${appender.type.ordinal == 4}">		
			<tr>
				<th><spring:message code="${moduleId}.appenders.maximumFileSize"/></th>
				<td>
					<logmgr_form:input path="properties.maximumFileSize" size="8" />
					<spring:message code="${moduleId}.bytes"/>
					<logmgr_form:errors path="properties.maximumFileSize" cssClass="error" />
				</td>
			</tr>
			<tr>
				<th><spring:message code="${moduleId}.appenders.maxBackupIndex"/></th>
				<td>
					<logmgr_form:input path="properties.maxBackupIndex" />
					<logmgr_form:errors path="properties.maxBackupIndex" cssClass="error" />
				</td>
			</tr>
		</c:if>
		<%------------------ DAILY ROLLING FILE fields ------------------%>
		<c:if test="${appender.type.ordinal == 5}">		
			<tr>
				<th><spring:message code="${moduleId}.appenders.datePattern"/></th>
				<td>
					<logmgr_form:input path="properties.datePattern" />
					<logmgr_form:errors path="properties.datePattern" cssClass="error" />
				</td>
			</tr>
		</c:if>
		<%------------------ SOCKET fields ------------------%>
		<c:if test="${appender.type.ordinal == 6}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.host"/></th>
				<td>
					<logmgr_form:input path="properties.remoteHost" />
					<logmgr_form:errors path="properties.remoteHost" cssClass="error" />
					<spring:message code="${moduleId}.appenders.port"/>
					<logmgr_form:input path="properties.port" size="5" />
					<logmgr_form:errors path="properties.port" cssClass="error" />
				</td>
			</tr>
			<tr>
				<th><spring:message code="${moduleId}.appenders.application"/></th>
				<td>
					<logmgr_form:input path="properties.application" />
					<logmgr_form:errors path="properties.application" cssClass="error" />
				</td>
			</tr>
			<tr>
				<th><spring:message code="${moduleId}.appenders.reconnectionDelay"/></th>
				<td>
					<logmgr_form:input path="properties.reconnectionDelay" size="6"  />
					<spring:message code="${moduleId}.milliseconds"/>
					<logmgr_form:errors path="properties.reconnectionDelay" cssClass="error" />
				</td>
			</tr>
			<tr>
				<th><spring:message code="${moduleId}.appenders.options"/></th>
				<td>
					<logmgr_form:checkbox path="properties.locationInfo" />
					<spring:message code="${moduleId}.appenders.useLocationInformation"/>
				</td>		
			</tr>
		</c:if>
		<%------------------ NT EVENT LOG fields ------------------%>
		<c:if test="${appender.type.ordinal == 7}">
			<tr>
				<th><spring:message code="${moduleId}.appenders.source"/></th>
				<td>
					<logmgr_form:input path="properties.source" />
					<logmgr_form:errors path="properties.source" cssClass="error" />
				</td>		
			</tr>
		</c:if>
		<%------------------ Attach To ------------------%>
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
					<logmgr_tag:loggerField id="attachToOther" name="attachToOther" cssStyle="width: 300px; display: none" />
				</td>
			</tr>
		</c:if>
		<tr>
			<td>&nbsp;</td>
			<td>
				<input type="submit" value="<spring:message code="general.save"/>" />
				<input type="button" value="<spring:message code="general.cancel"/>" onclick="location.href='appender.list'" />
			</td>
		</tr>
	</table>
</logmgr_form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>