<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />

<#else>

	<#import "styles/${style}/nav.ftl" as nav />

	<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td width="100%">

	<@nav.render/>

	</td>
	</tr>
	</table>

</#if>
