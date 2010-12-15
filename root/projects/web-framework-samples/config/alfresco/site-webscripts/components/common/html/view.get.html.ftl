<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />
	
<#else>

	${html}

</#if>
