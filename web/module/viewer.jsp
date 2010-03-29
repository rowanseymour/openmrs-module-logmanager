<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="View Server Log" otherwise="/login.htm" redirect="/module/logmanager/viewer.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<c:set var="level_ERROR" value="<%= org.apache.log4j.Level.ERROR_INT %>" />

<style type="text/css">
.rowLink {
	cursor: hand;
	cursor: pointer;
}
.rowLink td {
	border-top: 1px solid #FFF;
	border-bottom: 1px solid #FFF;
}
.rowLink:hover {
	/*background-color: #EAFFE0;*/
}
.rowLink:hover td {
	/*background-color: #EAFFE0;*/
	border-top: 1px solid #DDD;
	border-bottom: 1px solid #DDD;
}
.errorLevel {
	background-color: #FFEAE0;
}
.throwable {
	background-color: #FFE0D0;
	padding: 2px;
	font-style: italic;
}
.formatLink {
	font-style: bold;
	text-decoration: none;
}
</style>

<script type="text/javascript">
function submitViewForm(format) {
	document.logViewForm.format.value = format;
	document.logViewForm.submit();
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.viewer.viewLogMessages" />
</b>
<form method="get" class="box" name="logViewForm">
	<input type="hidden" name="format" value="" />
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td>	
				<spring:message code="${moduleId}.viewer.from" />
				
				<select name="viewId">
					<c:forEach var="app" items="${appenders}">
						<option value="${app.id}" ${app.id == appender.id ? 'selected="selected"' : ""}>${app.name}</option>
					</c:forEach>
				</select>
				
				<spring:message code="${moduleId}.viewer.atLevel" />
				
				<select name="levelOp">
					<option value="-1" ${levelOp == -1 ? 'selected="selected"' : ''}>&le;</option>
					<option value="0"  ${levelOp == 0  ? 'selected="selected"' : ''}>=</option>
					<option value="1"  ${levelOp == 1  ? 'selected="selected"' : ''}>&ge;</option>
				</select>
				
				<logmgr_tag:levelList name="level" value="${level}" showAny="true" />
				
				<spring:message code="${moduleId}.viewer.where" />
				
				<select name="queryField">
					<option value="0" ${queryField.ordinal == 0 ? "selected" : ""}><spring:message code="${moduleId}.viewer.loggerName" /></option>
					<option value="1" ${queryField.ordinal == 1 ? "selected" : ""}><spring:message code="${moduleId}.viewer.className" /></option>
					<option value="2" ${queryField.ordinal == 2 ? "selected" : ""}><spring:message code="${moduleId}.viewer.filename" /></option>
				</select>
				
				<spring:message code="${moduleId}.viewer.matches" />
				
				<input type="text" name="queryValue" value="${queryValue}" style="width: 300px" />
			</td>
			
			<td align="right">
				<a class="formatLink" href="javascript:submitViewForm('txt')">TXT</a>
				<a class="formatLink" href="javascript:submitViewForm('xml')">XML</a>
				&nbsp;
				&nbsp;
				<input type="button" onclick="submitViewForm('')" value="<spring:message code="general.refresh"/>" />
			</td>
		</tr>
	</table>

	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th>&nbsp;</th>
			<th><spring:message code="${moduleId}.viewer.time"/></th>
			<c:if test="${!empty timeDiffs}">
				<th>&nbsp;</th>
			</c:if>
			<th><spring:message code="${moduleId}.viewer.location"/></th>
			<th><spring:message code="${moduleId}.viewer.message"/></th>
		</tr>
	
		<c:forEach var="event" items="${events}" varStatus="rowStatus">
			<tr
				class="rowLink <c:choose><c:when test="${logmgr:levelToInt(event.level) >= level_ERROR}">errorLevel</c:when><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>"
				onclick="location.href='${pageContext.request.contextPath}/module/logmanager/event.htm?appId=${appender.id}&amp;eventId=${logmgr:hashCode(event)}'"
			>
				<td valign="top" width="16">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[event.level]}"
						title="${levelLabels[event.level]}"
						width="16" height="16" />
				</td>
				<td nowrap="nowrap" style="font-size: 10px" valign="top">
					${logmgr:formatTimeStamp(event.timeStamp)}
				</td>
				<c:if test="${!empty timeDiffs}">
					<td style="font-size: 10px" valign="top">			
						${timeDiffs[rowStatus.index] >= 0 ? logmgr:formatTimeDiff(timeDiffs[rowStatus.index]) : ""}				
					</td>
				</c:if>
				<td style="font-size: 10px" valign="top">
					<span title="${event.locationInformation.className}">
						${logmgr:formatLocInfo(event.locationInformation)}
					</span>
				</td>
				<td style="font-size: 10px" valign="top">
					${logmgr:formatMessage(event.message)}
					
					<c:if test="${event.throwableInformation != null}">
						<div class="throwable">
							<spring:message code="${moduleId}.viewer.throwableAttached"/>...
						</div>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		
		<c:if test="${empty events}">
			<tr>
				<td colspan="4" style="padding: 10px; font-style: italic; text-align: center">
					<spring:message code="${moduleId}.viewer.noLogMessages"/>
				</td>
			</tr>
		</c:if>
	</table>
	
	<c:if test="${!empty events}">
		<logmgr_tag:pager pagingInfo="${paging}" />
	</c:if>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>