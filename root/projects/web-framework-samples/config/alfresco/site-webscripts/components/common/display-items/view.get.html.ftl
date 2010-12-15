<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />

<#else>

	<#import "${view}/view.ftl" as view />

	<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td width="100%">

	<@view.render/>

	</td>
	</tr>
	</table>

</#if>