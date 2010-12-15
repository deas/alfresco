<#include "include/mobile.ftl" />
<@templateHeader>
</@>

<@templateBody>

<#-- if nodeRef exists then a GET REQUEST-->
<#if page.url.args.nodeRef?exists>
<@region id="header" scope="global" protected=true />
</#if>
<@region id="workflow" scope="template" protected=true />
</@>