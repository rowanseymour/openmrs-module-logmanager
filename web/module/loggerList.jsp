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
		newNameTxt.style.visibility = "hidden";
		loadBtn.disabled = false;
		deleteBtn.disabled = false;
	}
	else {
		newNameTxt.style.visibility = "";
		newNameTxt.focus();
		loadBtn.disabled = true;
		deleteBtn.disabled = true;
	}
}

function onChangeAddLoggerName(value) {
	$('#addLogger').attr('disabled', (value.length == 0) ? 'disabled' : '');
}

function onSavePreset() {
	return ($("#presetList").val() > 0)
	 ? confirm('<spring:message code="${moduleId}.loggers.confirmUpdatePreset"/>') : true;
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.loggerPresets" />
</b>
<form method="post" class="box" name="presetForm">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th>
				<spring:message code="${moduleId}.loggers.preset"/>:
			</th>
			<td style="padding-right: 40px">
				<select name="preset" id="presetList" onchange="onChangePreset(this.value)">
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
	<table cellpadding="2" cellspacing="2">
		<tr>
			<th valign="top">
				<spring:message code="${moduleId}.level"/>:		
			</th>
			<td valign="top" style="padding-right: 40px">				
				<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[rootLogger.level]}"
						width="16" height="16" />		
				<spring:message code="${moduleId}.level.${rootLogger.level.label}"/>
			</td>
			<th valign="top">	
				<spring:message code="${moduleId}.loggers.appenders"/>:
			</th>
			<td valign="top" style="padding-right: 40px">	
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
			<td valign="top">
				<input type="button" value="<spring:message code="general.edit"/>..." 
					onclick="location.href='logger.form?root'" />
			</td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.loggers.addLogger" />
</b>
<form method="get" class="box" name="addForm" action="logger.form">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th>
				<spring:message code="${moduleId}.loggers.name"/>:
			</th>
			<td style="padding-right: 40px">
				<logmgr_tag:loggerField id="addLoggerName" name="logger" cssStyle="width: 400px" onChange="onChangeAddLoggerName(this.value)" />
			</td>
			<td valign="top">
				<input type="submit" id="addLogger" value="<spring:message code="general.add"/>..." disabled="disabled" />
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
			<th>&nbsp;</th>
			<th><spring:message code="general.name"/></th>
			<th><spring:message code="${moduleId}.level"/></th>
			<th><spring:message code="${moduleId}.loggers.appenders"/></th>		
		</tr>
	
		<c:forEach var="logger" items="${loggers}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td valign="top" width="20">
					<input type="image" src="${pageContext.request.contextPath}/images/trash.gif"
						onclick="return confirm('<spring:message code="${moduleId}.loggers.confirmDeleteLogger"/>');"
						name="deleteLogger" value="${logger.name}"
						title="<spring:message code="general.delete"/>" /></a>
				</td>
				<td valign="top" width="20">
					<img src="${pageContext.request.contextPath}/moduleResources/${moduleId}/images/${levelIcons[logger.effectiveLevel]}"
						width="16" height="16" title="<spring:message code="${moduleId}.level.${logger.effectiveLevel.label}" />" />
				</td>
				<td>
					<a href="logger.form?logger=${logger.name}">
						${logger.name}
					</a>
				</td>
				<td>
					<c:choose>
						<c:when test="${logger.level != null}">
							<spring:message code="${moduleId}.level.${logger.level.label}" />
						</c:when>
						<c:otherwise>
							<i><spring:message code="${moduleId}.level.inherit" /></i>
						</c:otherwise>
					</c:choose>
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
			</tr>
		</c:forEach>
		
		<c:if test="${empty loggers}">
			<tr>
				<td colspan="4" style="padding: 10px; font-style: italic; text-align: center">
					<spring:message code="${moduleId}.loggers.noLoggers"/>
				</td>
			</tr>
		</c:if>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>