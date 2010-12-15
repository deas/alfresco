<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head profile="http://a9.com/-/spec/opensearch/1.1/"> 
    <#assign searchTerms=args.q>
    <title>Alfresco Keyword Search: ${searchTerms}</title> 
    <link rel="stylesheet" href="/alfresco/css/main.css" TYPE="text/css">
    <link rel="search" type="application/opensearchdescription+xml" href="${url.serviceContext}/api/search/keyword/description.xml" title="Alfresco Keyword Search"/>
    <#if args.c?exists><#assign itemsPerPage=args.c?number><#else><#assign itemsPerPage=10></#if>
    <#assign c=itemsPerPage>
    <#if args.p?exists><#assign startIndex=args.p?number><#else><#assign startIndex=1></#if>
    <#assign totalResults=resultset?size>
    <meta name="totalResults" content="${totalResults}"/>
    <meta name="startIndex" content="${startIndex}"/>
    <meta name="itemsPerPage" content="${itemsPerPage}"/>
    
    <#assign totalPages=totalResults/itemsPerPage>
    <#assign temp=totalPages?int><#if temp==totalPages>  <#else><#assign totalPages=temp+1> </#if>
  </head>
  <body>
    <table>
      <tr>
        <td><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
        <td><nobr><span class="mainTitle">Alfresco KB Search</span></nobr></td>
     </tr>
    </table>
    <br>
    
    <br>
    <table>
    <#assign pagination='false'>
    <#assign p=1>
    
    <#assign index=0>
    
    <#if args.p?exists && pagination='false'>
                          <#assign p=args.p?number>
                          <#assign pagination='true'>
                           <#assign index=(args.p?number * itemsPerPage) - itemsPerPage>
                           <#assign itemsPerPage=(args.p?number*itemsPerPage?number)>
     </#if>  
     
     <#if resultset?size = 0>
               <br/>
                 <table border="0" cellpadding="0" cellspacing="0"  width="100%">
                 <tr align="left"><td align="left" style="font-size:130%" class="recordSetHeader">Sorry, no articles matched your search criteria</td></tr>
                 </table>
                 <!--<table style="margin-left:5px;">-->
             <#else>
                             
                 <br/>
            <table border="0" cellpadding="0" cellspacing="0"  width="100%">
                 <tr align="left"><td align="left" style="font-size:130%" class="recordSetHeader">Search Results - <#if (resultset?size > itemsPerPage)>Displaying ${itemsPerPage}  of <#else> Displaying ${resultset?size} of</#if> ${resultset?size} Article(s) Found</td></tr>
            </table>
    <table>
<#list resultset as row>  
   <#if (row_index>=index?number)>
     
      <tr>
      <td>${row_index}</td>
      <td><img src="${url.context}${row.icon16}"/>
      <a href="${absurl(url.context)}${row.childAssocs["kb:published"][0].downloadUrl}">${row.name}</a></td>
      </tr>
     <#-- <#if row.properties.description == true> -->
      <tr>
      <td></td>
      <td>${row.properties.description}</td>
      </tr>
     <#-- </#if> -->
   </#if>
   <#if row_index =itemsPerPage-1>
                           <#assign index=row_index>
                          <#break>
               </#if>
</#list>
</#if>
    </table>
    <br>
    <table>
      <tr>
        <td><a href="${scripturl("?q=${searchTerms?url}&p=1&c=${c}")}">first</a></td>
<#if startIndex &gt; 1>
        <td><a href="${scripturl("?q=${searchTerms?url}&p=${startIndex - 1}&c=${c}")}">previous</a></td>
</#if>
       
<#if startIndex &lt; totalPages>
        <td><a href="${scripturl("?q=${searchTerms?url}&p=${startIndex + 1}&c=${c}")}">next</a></td>
</#if>
        <td><a href="${scripturl("?q=${searchTerms?url}&p=${totalPages}&c=${c}")}">last</a></td>
      </tr>
    </table>
  </body>
</html>
