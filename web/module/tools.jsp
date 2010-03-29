<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/tools.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript">
function onToggleSelectAll(state) {
	boxes = document.configForm.configs;
	for (i = 0; i < boxes.length; i++)
		boxes[i].checked = state;

	document.configForm.reload.disabled = !state;	
}
function onClickConfig(state) {
	boxes = document.configForm.configs;
	for (i = 0; i < boxes.length; i++) {
		if (boxes[i].checked) {
			document.configForm.reload.disabled = false;
			return;
		}
	}
	document.configForm.reload.disabled = true;
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.log4jConfiguration" />
</b>
<form method="post" class="box" name="configForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td width="150">			
				<input type="submit" name="clear"
					value="<spring:message code="${moduleId}.tools.clear" />" 
					onclick="return confirm('<spring:message code="${moduleId}.tools.confirmClear" />')"
				/>
			</td>
			<td><spring:message code="${moduleId}.tools.clearMsg" /></td>
		</tr>
		<tr>
			<td>
				<input type="submit" name="reload"
					value="<spring:message code="${moduleId}.tools.reload" />" 
					disabled="disabled"
				/>
			</td>
			<td><spring:message code="${moduleId}.tools.reloadMsg" /></td>
		</tr>
	</table>
	
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th>Source</th>
			<th>&nbsp;</th>
			<th align="right">
				<small>
					<spring:message code="general.select" />:
					<a href="javascript:onToggleSelectAll(true)"><spring:message code="${moduleId}.all" /></a>
					<a href="javascript:onToggleSelectAll(false)"><spring:message code="general.none" /></a>
				</small>
			</th>
		</tr>
	
		<c:forEach var="log4jConfig" items="${log4jConfigs}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td>${log4jConfig.display}</td>
				<td>
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
				<td align="right"><input type="checkbox" name="configs" value="${log4jConfig.moduleId}" onclick="onClickConfig()" /></td>
			</tr>
		</c:forEach>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.profiling" />
</b>
<form method="post" class="box" name="profilingForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<c:choose>
				<c:when test="${profilingStarted}">
					<td width="150">
						<input type="submit" name="stopProfiling" value="<spring:message code="${moduleId}.tools.stop" />" />
					</td>
					<td><spring:message code="${moduleId}.tools.stopProfilingMsg" arguments="${profilingLoggerName},WARN" /></td>
				</c:when>
				<c:otherwise>
					<td width="150">
						<input type="submit" name="startProfiling" value="<spring:message code="${moduleId}.tools.start" />" />
					</td>
					<td><spring:message code="${moduleId}.tools.startProfilingMsg" arguments="${profilingLoggerName},TRACE" /></td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.tools.hibernateSQLLogging" />
</b>
<form method="post" class="box" name="hibernateForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<c:choose>
				<c:when test="${sqlStarted}">
					<td width="150">
						<input type="submit" name="stopHibernateSQL" value="<spring:message code="${moduleId}.tools.stop" />" />
					</td>
					<td><spring:message code="${moduleId}.tools.stopHibernateSQLMsg" arguments="${sqlLoggerName},DEBUG" /></td>
				</c:when>
				<c:otherwise>
					<td width="150">
						<input type="submit" name="startHibernateSQL" value="<spring:message code="${moduleId}.tools.start" />" />
					</td>
					<td><spring:message code="${moduleId}.tools.startHibernateSQLMsg" arguments="${sqlLoggerName},OFF" /></td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>