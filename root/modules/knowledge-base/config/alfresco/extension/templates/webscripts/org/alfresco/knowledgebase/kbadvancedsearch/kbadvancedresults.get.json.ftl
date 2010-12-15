<#assign articleHome = companyhome.childByNamePath["Knowledge Base"]>
<#assign query="+PATH:\"${ articleHome.qnamePath}//.\" +ASPECT:\"{http://www.alfresco.org/model/knowledgebase/1.0}article\"">
<#if args.status?exists>
        <#assign status = args.status/>
	 <#if status!='Any' & args.status!="">
  		<#assign query=query+ " +@kb\\:status:\"+${status}+\""/>
      </#if>
</#if>
<#if args.article_modifier?exists>
        <#if args.article_modifier!="Any" & args.article_modifier!="">
            <#assign query=query + " +@cm\\:modifier:\"${ args.article_modifier}\"">
         </#if>
</#if>
<#if args.alfresco_version?exists>
      <#if args.alfresco_version!="Any" & args.alfresco_version!="">
            <#assign query=query + " +@kb\\:alfrescoVersion:\"+${args.alfresco_version}+\"">
      </#if>
</#if>
<#if args.visibility?exists>
      <#if args.visibility!="Any" & args.visibility!="">
           <#assign query=query + " +@kb\\:visibility:\"+${ args.visibility}+\"">
	  </#if>
</#if>
<#if args.modified?exists>
      <#if args.modified != "">
			  <#-- Format the modified date argument so we can use it in a Lucene query -->
              <#assign date1 = "${args.modified}"?date("dd/MM/yyyy")?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")>
              <#assign fromDate="${'${date1}'?replace('-','\\\\-')}">
              <#assign currentdate=date?string("yyyy\\-MM\\-dd'T'HH:mm:ss'.00Z'")>
              <#assign date_range="[${fromDate} TO ${currentdate}]">
              <#assign query = query + " +@cm\\:modified:${date_range}">   
       </#if>
</#if>
<#if !args.askid?exists>
	<#assign askid = ""/>
<#else>
     <#assign askid = args.askid/>
     <#if askid!=''>
        <#assign query=query+ " +@kb\\:kbId:\"+${askid}+\""/>
      </#if>
</#if>
<#if args.article_type?exists>
	   <#assign article_type = args.article_type/>
	   <#if article_type!='workspace://SpacesStore/kb:type-any' & article_type!=''>
	     	<#assign query=query+ " +@kb\\:articleType:\"+${article_type}+\""/>
	    </#if>
</#if>
<#if !args.searchText?exists>
	<#assign searchString = ""/>
<#else>
	<#assign searchString = args.searchText/>
	<#if args.searchText?exists && args.searchText != "">
		<#assign query=query + '+('>
        <#assign query=query + ' TEXT:"' + searchString + '"'>
        <#assign count=1>
        <#assign searchTermList = searchString?split(" ")>
        <#assign testquery="">
        <#if (searchTermList?size > 1)>
            <#list searchTermList as term>
                <#assign query=query + ' TEXT:"' + term?lower_case + '*"'>
          	</#list>
        </#if>
        <#assign query=query + ')'>
     </#if>
</#if>
<#assign query=query+ "+@kb\\:status:\"published\"">
<#assign resultset = companyhome.childrenByLuceneSearch[query]>
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
}<#if row_index+1 &lt; resultset?size>,</#if>
</#list>
]}	