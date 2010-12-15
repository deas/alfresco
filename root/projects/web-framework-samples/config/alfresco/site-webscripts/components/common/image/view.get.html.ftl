<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />
	
<#else>

	<#assign textString = "">
	<#if text?exists>
		<#assign textString = "title='" + text + "' ">
	</#if>
	<img src="${src}" border="0" ${textString} />

</#if>
