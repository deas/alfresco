<#-- Records Report - Template to apply to a records space to report on status of records -->

<style>
body {font:small/1.2em arial,helvetica,clean,sans-serif;font:x-small;margin-top: 10px; margin-right: 10px; margin-bottom: 0px; margin-left: 10px;min-width:500px;}
</style>

<#assign datetimeformat="dd MMM yyyy HH:mm">

<#assign currentpath="+PATH:\"${space.qnamePath}//*\"">

<#assign current_date=date?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")>
<#assign before=incrementDate(date, -1000*60*60*24*7)?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")>
<#assign after=incrementDate(date, 1000*60*60*24*7)?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")>
<#assign hundred_years=incrementDate(date, -1000*60*60*24*365*100)?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")>

<#assign range_last_7="[\"${before}\" TO \"${current_date}\"]">
<#assign range_next_7="[\"${current_date}\" TO \"${after}\"]">
<#assign range_before_today="[\"${hundred_years}\" TO \"${current_date}\"]">

<#assign rma="{http://www.alfresco.org/model/record/1.0}">

<#macro debug errcat="" errmsg="">
    <#-- <tr><th colspan="2">${errcat}</th><td colspan="8">${errmsg}</td></tr> -->
</#macro>

<#macro standardHeaders title extra="">
    <tr><td colspan="10"><h3>${title}</h3></td></tr>
    <tr Style="font-size:130%;font-weight:bold;color:#0000FF;">
        <td colspan="2">ID</td>
        <td colspan="2">Title</td>
        <td colspan="3">File Plan</td>
        <td>Originator</td>
        <td>Date Filed</td>
        <td>${extra}</td>
    </tr>
</#macro>

<#macro standardProperties child extraProperty="">
    <tr>
    <!-- Set up workspace path to child and it's associated parent and file plan -->
    <#assign childRef=child.nodeRef>
    <#assign childWorkspace=childRef[0..childRef?index_of("://")-1]>
    <#assign childStorenode=childRef[childRef?index_of("://")+3..]>
    <#assign childPath="${childWorkspace}/${childStorenode}">

    <#if child.parent.hasAspect("rma:filePlan")>
        <#assign fileplan=child.parent>
    <#elseif child.parent.parent?exists && child.parent.hasAspect("rma:filePlan")>
        <#assign fileplan=child.parent.parent>
    <#else>
        <#assign fileplan=child.parent>
    </#if>

    <#assign fpRef=fileplan.nodeRef>
    <#assign fpWorkspace=fpRef[0..fpRef?index_of("://")-1]>
    <#assign fpStorenode=fpRef[fpRef?index_of("://")+3..]>
    <#assign fileplanPath="${fpWorkspace}/${fpStorenode}">

    <td width=16> <#-- Record properties icon -->
        <#if child.isDocument>
        	<a href="${url.context}/navigate/showDocDetails/${childPath}">
       	<#else>
       	    <a href="${url.context}/navigate/showSpaceDetails/${childPath}">
       	</#if>
        <img src="${url.context}/images/icons/View_details.gif" border=0 align=absmiddle alt="Record Details" title="Record Details"></a>
    </td>
    <td> <#-- Record identifier -->
        <#if child.isDocument>
        	<a href="${url.context}/navigate/showDocDetails/${childPath}">
       	<#else>
       	    <a href="${url.context}/navigate/showSpaceDetails/${childPath}">
       	</#if>
        ${child.properties["rma:recordIdentifier"]}</a>
    </td>
    <td width=16> <#-- Record icon -->
        <#if child.isDocument>
        	<a href="${url.context}/download/direct/${childPath}/${child.name}">
        </#if>
        <img src="${url.context}${child.icon16}" width=16 height=16 border=0 align=absmiddle alt="View Record" title="View Record">
        <#if child.isDocument>
        	</a>
        </#if>
    </td>
    <td> <#-- Record title -->
        <#if child.isDocument>
        	<a href="${url.context}/download/direct/${childPath}/${child.name}">${child.properties["cm:title"]}</a>
        <#else>
        	${child.properties["cm:name"]}
        </#if>
    </td>
    <td width=16> <#-- Fileplan icon -->
        <a href="${url.context}/navigate/browse/${fileplanPath}">
        <img src="${url.context}${fileplan.icon16}" width=16 height=16 border=0 align=absmiddle alt="Fileplan Contents" title="Fileplan Contents"></a>
    </td>
    <td width=16> <#-- Fileplan properties icon -->
        <a href="${url.context}/navigate/showDocDetails/${fileplanPath}">
        <img src="${url.context}/images/icons/View_details.gif" border=0 align=absmiddle alt="Fileplan Details" title="Fileplan Details"></a>
    </td>
    <td>
        <a href="${url.context}/navigate/showDocDetails/${fileplanPath}">
        ${fileplan.name}</a>
    </td>
    <td>
        ${child.properties["rma:originator"]}
    </td>
    <td>
        ${child.properties["rma:dateFiled"]?string(datetimeformat)}
    </td>
    <td>
        ${extraProperty}
    </td>
    </tr>
</#macro>

<#macro standardFooters>
    <tr><td colspan="10"><hr/></td></tr>
    <tr><td colspan="10"></td></tr>
</#macro>


<table width="100%"  border="0" cellpadding="1" cellspacing="1">

<@standardHeaders title="Recently Modified Records" extra="Modified"/>
<#assign query="${currentpath} +ASPECT:\"${rma}record\" +@cm\\:modified:${range_last_7}">
<@debug errcat="QUERY" errmsg=query/>
<#list space.childrenByLuceneSearch[query] as child>
    <@standardProperties child=child extraProperty=child.properties["cm:modified"]?string(datetimeformat) />
</#list>
<@standardFooters/>


<@standardHeaders title="Vital Records Due for Review" extra="Next Review Due"/>
<#assign query="${currentpath} +ASPECT:\"${rma}record\" +ASPECT:\"${rma}vitalrecord\" +@rma\\:nextReviewDate:${range_before_today}">
<@debug errcat="QUERY" errmsg=query/>
<#list space.childrenByLuceneSearch[query]?sort_by(['properties', 'rma:nextReviewDate']) as child>
        <@standardProperties child=child extraProperty=child.properties["rma:nextReviewDate"]?string(datetimeformat) />
</#list>
<@standardFooters/>


<@standardHeaders title="Records Due for Cutoff" extra="Cutoff Date"/>
<#assign query="${currentpath} +ASPECT:\"${rma}record\" -ASPECT:\"${rma}cutoff\" +ASPECT:\"${rma}cutoffSchedule\" +@rma\\:cutoffDateTime:${range_next_7}">
<@debug errcat="QUERY" errmsg=query/>
<#list space.childrenByLuceneSearch[query]?sort_by(['properties', 'rma:cutoffDateTime']) as child>
        <@standardProperties child=child extraProperty=child.properties["rma:cutoffDateTime"]?string(datetimeformat) />
</#list>
<@standardFooters/>


<@standardHeaders title="Records Retention Due for Expiry" extra="Expiry Date"/>
<#assign query="${currentpath} +ASPECT:\"${rma}record\" +ASPECT:\"${rma}held\" +@rma\\:holdUntil:${range_next_7}">
<@debug errcat="QUERY" errmsg=query/>
<#list space.childrenByLuceneSearch[query]?sort_by(['properties', 'rma:holdUntil']) as child>
        <@standardProperties child=child extraProperty=child.properties["rma:holdUntil"]?string(datetimeformat) />
</#list>
<@standardFooters/>

</table>