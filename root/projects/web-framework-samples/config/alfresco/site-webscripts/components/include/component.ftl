<#macro unconfigured>

<div style="border-top: 1px #999999 solid; border-left: 1px #999999 solid; border-right: 1px #000000 solid; border-bottom: 1px #000000 solid; padding: 8px; margin: 2px; background-color: #eeeeee; color: #000000" align="center" valign="middle">

<#if instance.properties["url"]?exists>
	${instance.properties["url"]}
<#else>
	${instance.properties["component-type-id"]}
</#if>
<br/>
<br/>
This component needs to be configured.

</div>

</#macro>
