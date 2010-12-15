<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />

<#else>

	<#if container == "iframe">
		<IFRAME src="${src}"></IFRAME>
	<#else>
		<#if container == "div">
		
			<div id="include${instance.id}">
				${data}
			</div>

		</#if>
	</#if>

</#if>