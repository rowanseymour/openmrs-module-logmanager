<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="template/localInclude.jsp"%>

<logmgr_tag:modulePage />

<openmrs:require privilege="Manage Server Log" otherwise="/login.htm" redirect="/module/logmanager/appender.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/admin/maintenance/localHeader.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">
	<c:choose>
		<c:when test="${logger.existing}">
			<spring:message code="${moduleId}.loggers.editLogger" />
		</c:when>
		<c:otherwise>
			<spring:message code="${moduleId}.loggers.addLogger" />
		</c:otherwise>
	</c:choose>
</b>
<form:form commandName="logger" cssClass="box">
	<table cellpadding="2" cellspacing="2" width="100%">
		<tr>
			<th width="150"><spring:message code="${moduleId}.loggers.name"/></th>
			<td>
				<c:choose>
					<c:when test="${logger.root}">
						&lt;ROOT&gt;
					</c:when>
					<c:when test="${logger.existing}">
						${logger.name}
					</c:when>
					<c:otherwise>
						<form:input path="name" cssStyle="width: 400px" />
						<form:errors path="name" cssClass="error" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th><spring:message code="${moduleId}.loggers.level"/></th>
			<td>
				<spring:bind path="logger.level">
					<logmgr_tag:levelList name="${status.expression}" value="${status.value}" showALL="true" showOFF="true" showInherit="${not logger.root}" />
				</spring:bind>
			</td>
		</tr>	
		<tr>
			<th valign="top"><spring:message code="${moduleId}.loggers.appenders"/></th>
			<td>
				<c:choose>
					<c:when test="${!empty appenders}">
						<c:forEach items="${appenders}" var="appender" varStatus="status">
							<input type="checkbox" name="appenders" value="${appender.id}" 
								${appRelations[appender] == 1 || appRelations[appender] == 2 ? 'checked="checked"' : ''}
								${appRelations[appender] == 2 ? 'disabled="disabled"' : ''} />
							<span>
								<c:choose>
									<c:when test="${!empty appender.name}">
										${appender.name}
									</c:when>
									<c:otherwise>
										<i>${appender.displayName}</i>
									</c:otherwise>
								</c:choose>
							</span>
							<br/>
						</c:forEach>
						<br />
						<i><spring:message code="${moduleId}.loggers.inheritedAppendersMsg"/></i>
					</c:when>
					<c:otherwise>
						<i><spring:message code="${moduleId}.appenders.noAppenders"/></i>
					</c:otherwise>
				</c:choose>			
			</td>
		</tr>
		<tr>
			<td colspan="2" align="right">
				<input type="submit" value="<spring:message code="general.save"/>" />
				<input type="button" value="<spring:message code="general.cancel"/>" onclick="location.href='logger.list'" />
			</td>
		</tr>
	</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>