<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/tools.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<style type="text/css">
.switchButton {
	width: 65px;
}
</style>

<script type="text/javascript">
// Called when the user clicks the select all/none links
function logmgr_onToggleSelectAll(state) {
	boxes = document.configForm.configs;
	for (i = 0; i < boxes.length; i++)
		boxes[i].checked = state;

	document.configForm.reload.disabled = !state;	
}

// Called when the user clicks a config checkbox
function logmgr_onClickConfig(state) {
	someSelected = false;
	boxes = document.configForm.configs;
	// Could be a single checkbox or an array of checkboxes.. oh javscript..
	if (boxes.length == undefined)
		someSelected = boxes.checked;
	else {
		for (i = 0; i < boxes.length; i++) {
			if (boxes[i].checked) {
				someSelected = true;
				break;
			}
		}
	}
	document.configForm.reload.disabled = !someSelected;
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.log4jConfiguration" />
</b>
<form method="post" class="box" name="configForm" enctype="multipart/form-data">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td width="150">			
				<input type="submit" name="clear" class="switchButton"
					value="<spring:message code="${moduleId}.tools.clear" />" 
					onclick="return confirm('<spring:message code="${moduleId}.tools.confirmClear" />')"
				/>
			</td>
			<td><spring:message code="${moduleId}.tools.clearMsg" /></td>
		</tr>
		<tr>
			<td>			
				<input type="submit" name="import" id="importButton" class="switchButton"
					value="<spring:message code="${moduleId}.tools.import" />"
					disabled="disabled"
				/>		
			</td>
			<td>
				<spring:message code="${moduleId}.tools.importMsg" />
				<input type="file" id="importFile" name="importFile" accept="text/xml"
					onchange="document.configForm.import.disabled = (this.value == '')"
				/>
			</td>
		</tr>
		<tr>
			<td>			
				<input type="submit" name="export" class="switchButton" value="<spring:message code="${moduleId}.tools.export" />" />
			</td>
			<td><spring:message code="${moduleId}.tools.exportMsg" /></td>
		</tr>
		<tr>
			<td>
				<input type="submit" name="reload" class="switchButton"
					value="<spring:message code="${moduleId}.tools.reload" />" 
					disabled="disabled"
				/>
			</td>
			<td><spring:message code="${moduleId}.tools.reloadMsg" /></td>
		</tr>
	</table>
	
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th align="left">Source</th>
			<th align="right">
				<small>
					<spring:message code="general.select" />:
					<a href="javascript:logmgr_onToggleSelectAll(true)"><spring:message code="${moduleId}.all" /></a>
					<a href="javascript:logmgr_onToggleSelectAll(false)"><spring:message code="general.none" /></a>
				</small>
			</th>
		</tr>
	
		<c:forEach var="log4jConfig" items="${log4jConfigs}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td align="left">
					<input type="checkbox" name="configs" value="${log4jConfig.moduleId}" onclick="logmgr_onClickConfig()" />
					${log4jConfig.display}
				</td>
				<td align="left">
					<c:choose>
						<c:when test="${log4jConfig.usesRoot}">
							<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/icon_warn.png" />
							Modifies root logger
						</c:when>
						<c:when test="${log4jConfig.outsideNS}">
							<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/icon_info.png" />
							Modifies loggers outside of module namespace
						</c:when>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.specialLoggers" />
</b>
<form method="post" class="box" id="specialForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td colspan="3" style="padding-bottom: 8px;">
				<span style="font-style: italic">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/icon_warn.png" />
					<spring:message code="${moduleId}.tools.logMessageVolumeWarning" />
				</span>
			</td>
		</tr>
		<tr>
			<td width="150">
				<input type="submit" class="switchButton"
					name="${apiProfilingStarted ? 'stop' : 'start'}APIProfiling"
					value="<spring:message code="${moduleId}.tools.${apiProfilingStarted ? 'stop' : 'start'}" />"
				/>
			</td>
			<td><b><spring:message code="${moduleId}.tools.APIProfiling" /></b></td>
			<td><spring:message code="${moduleId}.tools.${apiProfilingStarted ? 'stop' : 'start'}APIProfilingMsg" arguments="${apiProfilingLoggerName},${apiProfilingStarted ? 'WARN' : 'TRACE'}" /></td>
		</tr>
		<tr>
			<td width="150">
				<input type="submit" class="switchButton"
					name="${hibernateSQLStarted ? 'stop' : 'start'}HibernateSQL"
					value="<spring:message code="${moduleId}.tools.${hibernateSQLStarted ? 'stop' : 'start'}" />"
				/>
			</td>
			<td><b><spring:message code="${moduleId}.tools.hibernateSQL" /></b></td>
			<td><spring:message code="${moduleId}.tools.${hibernateSQLStarted ? 'stop' : 'start'}HibernateSQLMsg" arguments="${hibernateSQLLoggerName},${hibernateSQLStarted ? 'DEBUG' : 'OFF'}" /></td>
		</tr>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>