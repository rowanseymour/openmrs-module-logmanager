<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" %>
<%@ taglib prefix="logmgr" uri="/WEB-INF/view/module/logmanager/taglibs/logmgr.tld" %>

<%@ attribute name="value" required="true" type="java.lang.Integer" %>
<%@ attribute name="width" required="true" type="java.lang.Integer" %>
<%@ attribute name="label" required="false" type="java.lang.String" %>

<div style="background-color: #${value > 100 ? 'ff7777' : 'b5ddba'};
	<c:if test="${value <= 100}">		
		background-image: url(${pageContext.request.contextPath}/moduleResources/logmanager/images/progress_bar_bk.gif);		
		background-repeat: repeat-y;
		background-position: -${100 - value}px 0px;
	</c:if>	
		width: ${width}px;
		text-align: center;
		height: 12px;">
	${label}
</div>