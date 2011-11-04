<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/config.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<style type="text/css">
.switchButton {
	width: 65px;
}
</style>

<script type="text/javascript">
function logmgr_isArray(obj) {
	return (obj != undefined && obj.length != undefined);
}

// Called when the user clicks the select all/none links
function logmgr_onToggleSelectAll(state) {
	boxes = document.configForm.moduleConfigs;
	// Could be a single checkbox or an array of checkboxes.. or nothing.. oh javscript..
	if (logmgr_isArray(boxes)) {
		for (i = 0; i < boxes.length; i++)
			boxes[i].checked = state;
	} else if (boxes != undefined) {
		boxes.checked = state;
	}

	document.configForm.internalConfig.checked = state;
	document.configForm.externalConfig.checked = state;
	document.configForm.reload.disabled = !state;	
}

// Called when the user clicks a config checkbox
function logmgr_onClickConfig() {
	someSelected = false;
	boxes = document.configForm.moduleConfigs;
	if (logmgr_isArray(boxes)) {
		for (i = 0; i < boxes.length; i++) {
			if (boxes[i].checked) {
				someSelected = true;
				break;
			}
		}
	} else if (boxes != undefined) {
		someSelected = boxes.checked;
	}

	if (document.configForm.internalConfig.checked)
		someSelected = true;
	else if (document.configForm.externalConfig.checked)
		someSelected = true;
	
	document.configForm.reload.disabled = !someSelected;
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.config.log4jConfiguration" />
</b>
<form method="post" class="box" name="configForm" enctype="multipart/form-data">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<td width="150">			
				<input type="submit" name="save" class="switchButton"
					value="<spring:message code="general.save" />" 
				/>
			</td>
			<td><spring:message code="${moduleId}.config.saveMsg" arguments="${externalConfigPath}" /></td>
		</tr>
		<tr>
			<td>			
				<input type="submit" name="clear" class="switchButton"
					value="<spring:message code="${moduleId}.config.clear" />" 
					onclick="return confirm('<spring:message code="${moduleId}.config.confirmClear" />')"
				/>
			</td>
			<td><spring:message code="${moduleId}.config.clearMsg" /></td>
		</tr>
		<tr>
			<td>			
				<input type="submit" name="import" id="importButton" class="switchButton"
					value="<spring:message code="${moduleId}.config.import" />"
					disabled="disabled"
				/>		
			</td>
			<td>
				<spring:message code="${moduleId}.config.importMsg" />
				<input type="file" id="importFile" name="importFile" accept="text/xml"
					onchange="document.configForm.import.disabled = (this.value == '')"
				/>
			</td>
		</tr>
		<tr>
			<td>			
				<input type="button" name="export" class="switchButton"
					value="<spring:message code="${moduleId}.config.export" />"
					onclick="location.href='config.form?src=current'"
				/>
			</td>
			<td><spring:message code="${moduleId}.config.exportMsg" /></td>
		</tr>
		<tr>
			<td>
				<input type="submit" name="reload" class="switchButton"
					value="<spring:message code="${moduleId}.config.reload" />" 
					disabled="disabled"
				/>
			</td>
			<td><spring:message code="${moduleId}.config.reloadMsg" /></td>
		</tr>
	</table>
	
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th>
				<spring:message code="${moduleId}.config.source" />
				&nbsp;&nbsp;&nbsp;
				<small>
					<spring:message code="general.select" />:
					<a href="javascript:logmgr_onToggleSelectAll(true)"><spring:message code="${moduleId}.all" /></a>
					<a href="javascript:logmgr_onToggleSelectAll(false)"><spring:message code="general.none" /></a>
				</small>
			</th>
			<th>View</th>
			<th></th>
		</tr>
		
		<%------------- INTERNAL OPENMRS CONFIG ------------%>
		
		<tr class="evenRow">
			<td>
				<input type="checkbox" name="internalConfig" value="1" onclick="logmgr_onClickConfig()" />
				<spring:message code="${moduleId}.internal" />		
			</td>
			<td><a href="config.form?src=internal">${internalConfigName}</a></td>
			<td>&nbsp;</td>
		</tr>
		
		<%------------- EXTERNAL OPENMRS CONFIG ------------%>
		
		<tr class="oddRow">
			<td>
				<input type="checkbox" name="externalConfig" value="1" onclick="logmgr_onClickConfig()" />
				<spring:message code="${moduleId}.external" />
			</td>
			<td><a href="config.form?src=external">${externalConfigName}</a></td>
			<td>&nbsp;</td>
		</tr>
	
		<%--------------- MODULE CONFIGS ---------------%>
		
		<c:forEach var="log4jConfig" items="${log4jConfigs}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td>
					<input type="checkbox" name="moduleConfigs" value="${log4jConfig.moduleId}" onclick="logmgr_onClickConfig()" />
					<spring:message code="${moduleId}.module" />:
					${log4jConfig.moduleId}
				</td>
				<td><a href="config.form?src=${log4jConfig.moduleId}">${internalConfigName}</a></td>
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
			</tr>
		</c:forEach>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>