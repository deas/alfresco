<#import "dynamic-tables.ftl" as tablesTemplate />
<#import "dynamic-absolute.ftl" as absoluteTemplate />

<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>${page.title}</title>
      ${head}
   </head>
   <body>
   
<#if ready?exists>

	<#assign rendered = false>

	<#if "${layoutType}" == "table layout" && !rendered>
		<@tablesTemplate.body/>
		<#assign rendered = true>
	</#if>

	<#if "${layoutType}" == "table" && !rendered>
		<@tablesTemplate.body/>
		<#assign rendered = true>
	</#if>

	<#if !rendered>
		<@absoluteTemplate.body/>
		<#assign rendered = true>
	</#if>

</#if>

   </body>
   
</html>