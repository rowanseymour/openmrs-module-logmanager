<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/appender.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript">
function clearAppender(id) {
	form = document.forms["appendersForm"];
	form.clearId.value = id;
	form.submit();
}
</script>

<b class="boxHeader">
	<spring:message code="${moduleId}.appenders.createAppender" />
</b>
<form method="get" class="box" name="newAppenderForm" action="appender.form">
	<table cellpadding="2" cellspacing="2">
		<tr>
			<th>
				<spring:message code="general.name"/>:
			</th>
			<td style="padding-right: 40px">
				<input type="text" name="newName" style="width: 200px" />
			</td>
			<th>
				<spring:message code="${moduleId}.appenders.type"/>:
			</th>
			<td style="padding-right: 40px">
				<logmgr_tag:appenderTypeField name="newType" showUnknown="false" showNTEventLog="${isWindows}" />
			</td>		
			<td>
				<input type="submit" value="<spring:message code="${moduleId}.create"/>..." />
			</td>
		</tr>
	</table>
</form>

<br/>

<b class="boxHeader">
	<spring:message code="${moduleId}.appenders.existingAppenders" />
</b>
<form method="post" class="box" name="appendersForm">
	<input type="hidden" id="clearId" name="clearId" />

	<table cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<th>&nbsp;</th>
			<th><spring:message code="${moduleId}.appenders.name"/></th>
			<th><spring:message code="${moduleId}.appenders.type"/></th>
			<th><spring:message code="${moduleId}.appenders.layout"/></th>
			<th><spring:message code="${moduleId}.appenders.messages"/></th>
		</tr>
			
		<c:forEach var="appender" items="${appenders}" varStatus="rowStatus">
			<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td width="20">
					<c:choose>
						<c:when test="${!appender.systemAppender}">
							<input type="image" src="${pageContext.request.contextPath}/images/trash.gif"
								onclick="return confirm('<spring:message code="${moduleId}.appenders.confirmDeleteAppender"/>');"
								name="deleteId" value="${appender.id}"
								title="<spring:message code="general.delete"/>"
							/>
						</c:when>
						<c:otherwise>
							<img src="${pageContext.request.contextPath}/images/lock.gif" title="<spring:message code="${moduleId}.appenders.systemAppender"/>" />
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<a href="appender.form?editId=${appender.id}">
						<c:choose><c:when test="${!empty appender.name}">${appender.name}</c:when><c:otherwise><i><spring:message code="${moduleId}.anonymous"/></i></c:otherwise></c:choose></a>
					<c:if test="${appender.systemAppender}">
						(<i><spring:message code="${moduleId}.system"/></i>)
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${appender.type.ordinal > 0}">
							<c:out value="${appender.type}" />
						</c:when>
						<c:otherwise>
							<i>${appender.target.class.simpleName}</i>
						</c:otherwise>
					</c:choose>
				</td>
				<td><c:out value="${appender.layout}" /></td>
				<td>
					<c:if test="${appender.viewable}">
						<a href="viewer.htm?appId=${appender.id}"><spring:message code="general.view"/></a>
					</c:if>
					<c:if test="${appender.clearable}">
						<a href="javascript:clearAppender(${appender.id})"><spring:message code="general.clear"/></a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		
		<c:if test="${empty appenders}">
			<tr>
				<td colspan="4" style="padding: 10px; font-style: italic; text-align: center">
					<spring:message code="${moduleId}.appenders.noAppenders"/>
				</td>
			</tr>
		</c:if>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>