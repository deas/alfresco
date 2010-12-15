<span class="breadcrumb">

<#assign first = true>
<#list pages as page>

	<#if first == false>
		${separatorChar}
	</#if>
	
	<#assign title = page.id>
	<#if page.title?exists>
		<#assign title = page.title>
	</#if>
	
	<#assign selected = false>
	<#if page.id == currentPageId>
		<#assign selected = true>
	</#if>
	
	<@anchor page="${page.id}">
		<#if selected><B></#if>
		${title}
		<#if selected></B></#if>
	</@anchor>
	
	<#assign first = false>
	
</#list>
	
</span>
