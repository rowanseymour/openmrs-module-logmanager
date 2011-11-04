<%@ page import="org.openmrs.module.logmanager.impl.LevelProxy" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="View Server Log" otherwise="/login.htm" redirect="/module/logmanager/viewer.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<c:set var="level_ERROR" value="<%= LevelProxy.ERROR %>" />

<style type="text/css">
#eventsTable td {
	font-size: 10px;
	vertical-align: top;
}
.rowLink {
	cursor: hand;
	cursor: pointer;
}
.rowLink td {
	border-top: 1px solid #FFF;
	border-bottom: 1px solid #FFF;
}
.rowLink:hover td {
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
			<td nowrap="nowrap">	
				<spring:message code="${moduleId}.viewer.from" />
				
				<select name="appId" style="width: 150px">
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
				
				<logmgr_tag:levelField name="level" value="${level}" showAny="true" />
				
				<spring:message code="${moduleId}.viewer.where" />
				
				<select name="queryField">
					<option value="0" ${queryField.ordinal == 0 ? "selected" : ""}><spring:message code="${moduleId}.viewer.logger" /></option>
					<option value="1" ${queryField.ordinal == 1 ? "selected" : ""}><spring:message code="${moduleId}.viewer.class" /></option>
					<option value="2" ${queryField.ordinal == 2 ? "selected" : ""}><spring:message code="${moduleId}.viewer.filename" /></option>
				</select>
				
				<spring:message code="${moduleId}.viewer.contains" />
				
				<input type="text" name="queryValue" value="${queryValue}" style="width: 150px" />
			</td>
			
			<td align="right" nowrap="nowrap">
				<!-- Causes profiler=0 to be sent when checkbox is unchecked -->
				<input type="hidden" name="profiler" value="${profiler ? 1 : 0}" />
				<input type="checkbox" onclick="this.form.profiler.value=this.checked?1:0" ${profiler ? 'checked="checked"' : ''} />
				
				<spring:message code="${moduleId}.viewer.showProfilerView" />	
				
				&nbsp;
				<a class="formatLink" href="javascript:submitViewForm('txt')">TXT</a>
				<a class="formatLink" href="javascript:submitViewForm('xml')">XML</a>
				&nbsp;
				&nbsp;
				
				<input type="button" onclick="submitViewForm('')" value="<spring:message code="general.refresh"/>" />
			</td>
		</tr>
	</table>

	<table cellpadding="2" cellspacing="0" width="100%" id="eventsTable">
		<tr>
			<th>&nbsp;</th>
			<th><spring:message code="${moduleId}.viewer.time"/></th>
			<c:if test="${profiler}">
				<th>&#916;</th>
				<th><spring:message code="${moduleId}.viewer.thread"/></th>
			</c:if>
			<th><spring:message code="${moduleId}.viewer.location"/></th>
			<th><spring:message code="${moduleId}.viewer.message"/></th>
		</tr>
	
		<c:forEach var="event" items="${events}" varStatus="rowStatus">
			<tr
				class="rowLink <c:choose><c:when test="${event.level.intValue >= level_ERROR.intValue}">errorLevel</c:when><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>"
				onclick="location.href='${pageContext.request.contextPath}/module/logmanager/event.htm?appId=${appender.id}&amp;eventId=${event.id}'"
			>
				<td width="16">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[event.level]}"
						title="<spring:message code="${moduleId}.level.${event.level.label}" />"
						width="16" height="16" />
				</td>
				<td nowrap="nowrap">
					${logmgr:formatTimeStamp(event.timeStamp, !profiler)}
				</td>
				<c:if test="${profiler}">
					<td>
						<c:if test="${timeDiffs[rowStatus.index] >= 0}">
							<logmgr_tag:progressBar width="100"
								value="${timeDiffs[rowStatus.index] / 10}"
								label="${logmgr:formatTimeDiff(timeDiffs[rowStatus.index])}"
							/>	
						</c:if>				
					</td>
					<td>
						${event.threadName}
					</td>
				</c:if>
				<td>
					<span title="${event.className}">
						${logmgr:formatLocInfo(event)}
					</span>
				</td>
				<td>
					${logmgr:formatMessage(event.message)}
					
					<c:if test="${event.throwableAttached}">
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