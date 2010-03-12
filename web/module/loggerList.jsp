<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/logger.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript">
function onChangePreset(value) {
	var newNameTxt = document.presetForm.newPresetName;
	var loadBtn = document.presetForm.loadPreset;
	var deleteBtn = document.presetForm.deletePreset;
	
	if (value > 0) {
		newNameTxt.style.display = "none";
		loadBtn.disabled = false;
		deleteBtn.disabled = false;
	}
	else {
		newNameTxt.style.display = "";
		newNameTxt.focus();
		loadBtn.disabled = true;
		deleteBtn.disabled = true;
	}
}

function onChangeAddLoggerName(value) {
	var addBtn = document.addForm.addLogger;
	addBtn.disabled = (value.length == 0);
}

function onSavePreset() {
	var presetLst = document.presetForm.preset;
	return (presetLst.value > 0) ? confirm('<spring:message code="${moduleId}.loggers.confirmUpdatePreset"/>') : true;
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.loggerPresets" />
</b>
<form method="post" class="box" name="presetForm">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.loggers.preset"/></th>
			<td>
				<select name="preset" onchange="onChangePreset(this.value)">
					<option value="0">&lt;<spring:message code="general.new"/>...&gt;</option>
					<c:forEach items="${presets}" var="preset">
						<option value="${preset.presetId}" ${activePreset == preset.presetId ? 'selected="selected"' : ''}>${preset.name}</option>
					</c:forEach>
				</select>
				
				<input type="text" name="newPresetName" size="30" maxlength="50" value="${newPresetName}" ${activePreset != null ? 'style="display: none"' : ''}/>
				<c:if test="${newPresetNameError != null}">
					<span class="error"><spring:message code="${moduleId}.error.name"/></span>
				</c:if>
			</td>
			<td align="right" valign="top">
				<input type="submit" name="savePreset"
					value="<spring:message code="general.save"/>"
					onclick="return onSavePreset();"
				/>
				<input type="submit" name="loadPreset"
					value="<spring:message code="${moduleId}.load"/>"
					${activePreset == null ? 'disabled="disabled"' : ''}
				/>
				<input type="submit" name="deletePreset"
					value="<spring:message code="general.delete"/>"
					${activePreset == null ? 'disabled="disabled"' : ''} 
					onclick="return confirm('<spring:message code="${moduleId}.loggers.confirmDeletePreset"/>');"
				/>
			</td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.rootLogger" />
</b>
<form method="post" class="box" name="rootLoggerForm">
	<table cellpadding="2" cellspacing="2" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.loggers.level"/></th>
			<td>
				<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[rootLogger.level]}"
						width="16" height="16" title="${levelLabels[rootLogger.level]}" />
						
						${levelLabels[rootLogger.level]}
			</td>
			<td rowspan="2" align="right" valign="top">
				<input type="button" value="<spring:message code="general.edit"/>" 
					onclick="location.href='logger.form?root'" />
			</td>
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.loggers.appenders"/></th>
			<td>
				<c:forEach var="appender" items="${rootLogger.appenders}" varStatus="status">								
					<c:choose>
						<c:when test="${!empty appender.name}">
							<a href="appender.form?editId=${appender.id}">${appender.name}</a>${!status.last ? ", " : ""}
						</c:when>
						<c:otherwise>
							<a href="appender.form?editId=${appender.id}"><i><spring:message code="${moduleId}.anonymous"/></i></a>${!status.last ? ", " : ""}
						</c:otherwise>
					</c:choose>	
				</c:forEach>
			</td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.addLogger" />
</b>
<form method="get" class="box" name="addForm" action="logger.form">
	<table cellpadding="2" cellspacing="0" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.loggers.name"/></th>
			<td>
				<input type="text" name="logger" style="width: 400px" onkeyup="onChangeAddLoggerName(this.value)" />
			</td>
			<td align="right" valign="top">
				<input type="submit" name="addLogger" value="<spring:message code="general.add"/>" disabled="disabled" />
			</td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.existingLoggers" />
</b>
<form method="post" class="box" name="loggersForm">
	<table cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<th>&nbsp;</th>
			<th><spring:message code="${moduleId}.loggers.name"/></th>
			<th><spring:message code="${moduleId}.loggers.level"/></th>
			<th><spring:message code="${moduleId}.loggers.appenders"/></th>
			<th>&nbsp;</th>
		</tr>
	
		<c:forEach var="logger" items="${loggers}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td valign="top" width="16">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[logger.effectiveLevel]}"
						width="16" height="16" title="${levelLabels[logger.effectiveLevel]}" />
				</td>
				<td>
					<a href="logger.form?logger=${logger.name}">
						${logger.name}
					</a>
				</td>
				<td>
					${logger.level != null ? levelLabels[logger.level] : levelNullLabel}
				<td>
					<c:forEach var="appender" items="${logger.appenders}" varStatus="status">					
						<c:choose>
							<c:when test="${!empty appender.name}">
								<a href="appender.form?editId=${appender.id}">${appender.name}</a>${!status.last ? ", " : ""}
							</c:when>
							<c:otherwise>
								<a href="appender.form?editId=${appender.id}"><i><spring:message code="${moduleId}.anonymous"/></i></a>${!status.last ? ", " : ""}
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</td>
				<td align="right">
					<input type="image" src="${pageContext.request.contextPath}/images/trash.gif"
						onclick="return confirm('<spring:message code="${moduleId}.loggers.confirmDeleteLogger"/>');"
						name="deleteLogger" value="${logger.name}"
						title="<spring:message code="general.delete"/>" /></a>
				</td>
			</tr>
		</c:forEach>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>