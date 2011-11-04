<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="logmgr_tag" tagdir="/WEB-INF/tags/module/logmanager" %>

<%@ attribute name="pagingInfo" required="true" type="org.openmrs.module.logmanager.util.PagingInfo" %>

<div style="background-color: #DDD; padding: 2px; overflow: hidden">
	<div style="float: left">
		<logmgr_tag:pagerControls pagingInfo="${pagingInfo}" />
	</div>		
	<div style="float: right; margin: 2px">		
		<logmgr_tag:pagerInfo pagingInfo="${pagingInfo}" />
	</div>
</div>