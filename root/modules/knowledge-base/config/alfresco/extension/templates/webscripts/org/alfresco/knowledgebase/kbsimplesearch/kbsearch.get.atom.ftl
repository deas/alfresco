<#compress>
<#if args.q?exists>
  		<#assign searchTerms=args.q>
 <#else>
        <#assign searchTerms=''>
  </#if>  
<#assign datetimeformat="dd MMM yyyy HH:mm:ss">

	  <#if args.c?exists>
	  		<#assign itemsPerPage=args.c?number>
	  <#else>
	  		<#assign itemsPerPage=10>
	  </#if>
	  
      <#assign maxresults=itemsPerPage>
      
      <#if args.p?exists>
      		<#assign startIndex=args.p?number>
      <#else>
      		<#assign startIndex=1>
      </#if>
      <#assign totalResults=resultset?size>
      <#assign startPage=startIndex>
      <#assign totalPages=totalResults/itemsPerPage>
   	  <#assign temp=totalPages?int>
   	  
   	  <#if temp==totalPages>  <#else><#assign totalPages=temp+1> </#if>   
    
    <#assign pagination='false'>
          <#assign p=1>
          
          <#assign index=0>
          
         <#if args.p?exists && pagination='false'>
	    	<#assign p=args.p?number>
	    	<#assign pagination='true'>
	    	<#assign index=(args.p?number * itemsPerPage) - itemsPerPage>
	    	<#assign maxresults=(args.p?number*itemsPerPage?number)>
         </#if>  
</#compress>
<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/" xmlns:relevance="http://a9.com/-/opensearch/extensions/relevance/1.0/" xmlns:alf="http://www.alfresco.org/opensearch/1.0/">
	  <generator version="${server.version}">Alfresco (${server.edition})</generator>
	  <title>Alfresco Keyword Search: ${searchTerms}</title> 
	  <updated>${xmldate(date)}</updated>
	  <icon>${absurl(url.context)}/images/logo/AlfrescoLogo16.ico</icon>
	  <totalResults>${totalResults}</totalResults>
	  <opensearch:startIndex>${startIndex}</opensearch:startIndex>
	  <opensearch:itemsPerPage>${itemsPerPage}</opensearch:itemsPerPage>
	  <opensearch:Query role="request" searchTerms="${searchTerms}" startPage="${startPage}" count="${itemsPerPage}"/>
	  <link rel="alternate" href="${absurl(scripturl("?q=${searchTerms?url}&p=${startPage}&c=${itemsPerPage}")?xml)}" type="text/html"/>
	  <link rel="self" href="${absurl(scripturl("?q=${searchTerms?url}&p=${startPage}&c=${itemsPerPage}")?xml)}" type="application/atom+xml"/>
	<#if startPage &gt; 1>
	  <link rel="first" href="${absurl(scripturl("?q=${searchTerms?url}&p=1&c=${itemsPerPage}")?xml)}" type="application/atom+xml"/>
	  <link rel="previous" href="${absurl(scripturl("?q=${searchTerms?url}&p=${startPage - 1}&c=${itemsPerPage}")?xml)}" type="application/atom+xml"/>
	</#if>
	<#if startPage &lt; totalPages>
	  <link rel="next" href="${absurl(scripturl("?q=${searchTerms?url}&p=${startPage + 1}&c=${itemsPerPage}")?xml)}" type="application/atom+xml"/> 
	  <link rel="last" href="${absurl(scripturl("?q=${searchTerms?url}&p=${totalPages}&c=${itemsPerPage}")?xml)}" type="application/atom+xml"/>
	</#if>
	  <link rel="search" type="application/opensearchdescription+xml" href="${absurl(url.serviceContext)}/api/search/keyword/description.xml"/>
		  <#list resultset as row>
			  <#if (row_index>=index?number)>
				  <entry>
					    <title>${row.childAssocs["kb:published"][0].name}</title>
					    <originallink href="${absurl(url.context)}${row.downloadUrl}"/>
					    <link rel="alternate" href="${absurl(url.context)}${row.childAssocs["kb:published"][0].downloadUrl}"/>
					    <icon>${absurl(url.context)}${row.icon16}</icon>
					    <id>urn:uuid:${row.id}</id>
					    <alf:noderef>${row.nodeRef}</alf:noderef>
					    <updated>${row.properties.modified?string(datetimeformat)}</updated>
					    <summary>${row.properties.description}</summary>
					    <author> 
					      <name>${row.properties.creator}</name>
					    </author> 
					    <category>${row.properties["kb:articleType"].name}</category>
				  </entry>
			  </#if>
			  <#if row_index =maxresults-1>
			     <#assign index=row_index>
			    <#break>
			  </#if>
		</#list>
</feed>
