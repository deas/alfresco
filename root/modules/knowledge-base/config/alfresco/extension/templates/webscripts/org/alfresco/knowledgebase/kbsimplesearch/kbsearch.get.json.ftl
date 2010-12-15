<#assign datetimeformat="dd MMM yyyy HH:mm:ss">
{
"recordsReturned":${resultset?size},
"totalRecords":${resultset?size},
"startIndex":0,
"sort":null,
"dir":"asc",
"records":[<#list resultset as row>{
"id":${row_index+1},
"originallink":"${absurl(url.context)}${row.downloadUrl}",
"swflink":"${absurl(url.context)}${row.childAssocs["kb:published"][0].downloadUrl}",
"status":"${row.properties["kb:status"].name}",
"updated":"${row.properties.modified?string(datetimeformat)}",
"type":"${row.properties["kb:articleType"].name}",
"authorname":"${row.properties.creator}",
"title":"${row.properties.title}",
"description":"${row.properties.description}",
"visibility":"${row.properties["kb:visibility"].name}",
"modifier":"${row.properties.modifier}",
<#if row.properties["kb:alfrescoVersion"]?exists>
   <#list row.properties["kb:alfrescoVersion"] as versionValue>
	<#assign alfversion=versionValue.name/>
	      	<#if versionValue_has_next><#assign alfversion=alfversion+","/></#if>
   </#list>
<#else>
	<#assign alfversion="-"/>
</#if>
"version":"${alfversion}",
<#if row.properties["kb:tags"]?exists>
  <#list row.properties["kb:tags"] as tag>
     <#assign alftags=tag/>
         <#if tag_has_next><#assign alftags=alftags+"," /></#if>
  </#list>
<#else>
  <#assign alftags="-nil-" />
</#if>
"icon":"${absurl(url.context)}${row.icon16}",
"tags":"${alftags}"
},
</#list>
]}