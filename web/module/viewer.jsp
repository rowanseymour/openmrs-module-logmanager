<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="View Server Log" otherwise="/login.htm" redirect="/module/logmanager/viewer.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<style type="text/css">
.throwable {
	background-color: #FFDBDB;
	padding: 2px;
	margin-top: 3px;
}
.throwable a {
	text-decoration: none;
	color: black;
}
.throwable a img {
	border: 0;
}
</style>

<script type="text/javascript">
function showThrowable(index, show) {
	document.getElementById("throwable_show_" + index).style.display = show ? "none" : "block";
	document.getElementById("throwable_message_" + index).style.display = show ? "block" : "none";
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.viewer.viewLogMessages" />
</b>
<form method="get" class="box" name="logViewForm">
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
				<input type="submit" value="<spring:message code="general.refresh"/>" />
				<input type="submit" name="xml" value="<spring:message code="${moduleId}.viewer.exportXML"/>" />
			</td>
		</tr>
	</table>

	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th>&nbsp;</th>
			<th><spring:message code="${moduleId}.viewer.time"/></th>
			<th><spring:message code="${moduleId}.viewer.location"/></th>
			<th><spring:message code="${moduleId}.viewer.message"/></th>
		</tr>
	
		<c:forEach var="event" items="${events}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td valign="top" width="16">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[event.level]}"
						title="${event.level}"
						width="16" height="16" />
				</td>
				<td nowrap="nowrap" style="font-size: 10px" valign="top">
					${logmgr:formatTimestamp(event.timeStamp)}
				</td>
				<td style="font-size: 10px" valign="top">
					<span title="${event.locationInformation.className}">
						${logmgr:formatLocInfo(event.locationInformation)}
					</span>
				</td>
				<td style="font-size: 10px" valign="top">
					${logmgr:formatMessage(event.message)}
					
					<c:if test="${event.throwableInformation != null}">
						<div id="throwable_${rowStatus.index}" class="throwable">
							<div id="throwable_show_${rowStatus.index}">
								<a href="javascript:showThrowable(${rowStatus.index}, true)">
									<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/expand.png" />
									
									${event.throwableStrRep[0]}
								</a>
							</div>
							<div id="throwable_message_${rowStatus.index}" style="display: none">
								<div id="throwable_hide_${rowStatus.index}" style="margin-bottom: 5px">
									<a href="javascript:showThrowable(${rowStatus.index}, false)">
										<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/collapse.png" />
										
										Hide
									</a>
								</div>
								<c:forEach items="${event.throwableStrRep}" var="throwableMsg">
									${throwableMsg}
									<br/>
								</c:forEach>
							</div>
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