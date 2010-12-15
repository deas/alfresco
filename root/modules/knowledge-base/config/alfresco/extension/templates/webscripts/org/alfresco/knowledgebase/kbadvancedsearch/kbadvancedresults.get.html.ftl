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

<#-- Add or define the max results to display -->
<#if args.maxresults?exists>
     <#assign maxresults=args.maxresults ?number>
<#else>
     <#assign maxresults=5>
</#if>

<#if !args.askid?exists>
	<#assign askid = ""/>
<#else>
     <#assign askid = args.askid/>
     <#if askid!=''>
        <#setting number_format="0000">
        <#assign query=query+ " +@kb\\:kbId:\"+${askid?number}+\""/>
      </#if>
</#if>
<#setting number_format="number">
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

<#assign pagination='false'>
<#assign p=1>
<#assign index=0>

<#-- Run the article query -->
<#assign results = companyhome.childrenByLuceneSearch[query]>
                
<#-- If we have no results return a no results message, else, return the list of matching articles -->
<#if results?size = 0>
  <br/>
    <table border="0" cellpadding="0" cellspacing="0"  width="100%">
	    <tr align="left">
	    	<td align="left" style="font-size:130%" class="recordSetHeader">Sorry, no articles matched your search criteria</td>
	    </tr>
    </table>
    <br/>
<#else>
    <#assign total=results?size>
    <#assign pages=total/maxresults>
    <br/>
    <table border="0" cellpadding="0" cellspacing="0"  width="100%">
	    <tr align="left">
	    	<td align="left" style="font-size:130%" class="recordSetHeader">Search Results - 
	    		<#if (results?size > maxresults)>Displaying ${maxresults}  of <#else> Displaying ${results?size} of</#if> ${results?size} Article(s) Found
	    	</td>
	    </tr>
    </table>
    <table width="100%" border="0">
	     <tr><td></td></tr>
	     <tr><td></td></tr>
	 </table>
	<#assign flag='true' />
	<table border="0" cellpadding="2" cellspacing="2" >
		<tr>
			<th>s.no</th> <th>URL </th>
			<th>Title</th> <th>Status</th>
			<th>Type </th> 	<th>Created By</th>
			<th>Modifier</th><th>date of creation</th>
			<th>date of modification</th>
			<th>visibility</th><th>Tags</th>
			<th>Version</th>
		</tr>
		<#list results as child>
			<#if args.p?exists && pagination='false'>
			     <#assign p=args.p?number>
			     <#assign pagination='true'>
			     <#assign index=(args.p?number * maxresults) - maxresults>
			     <#assign maxresults=(args.p?number*maxresults?number)>
			</#if>     
	
			<#if (child_index>=index?number)>
				<#if flag='true'>
		    		<tr style="background:#dfe6ed; color: black;" align="center">
		   			  <#assign flag='false' />
		   		<#else>
		   			<#assign flag='true' />
		  			 <tr style="background:##ffffff; color: black;" align="center">
		   		</#if>
				<td>${child_index+1}</td>
				<td>
					<a href="/alfresco/navigate/showDocDetails/workspace/SpacesStore/${ child.id}" target="new">
						<img src="/alfresco/images/icons/View_details.gif" border=0 align=absmiddle alt="Article Details" title="Article Details" />
					</a>
			        <a href="/alfresco${child.url}" target="new" alt="View Article" title="View Article">${child.properties.name }</a>
				</td>
				<td>${child.properties.title}</td>
				<td><#if child.properties["kb:status"]?exists>${child.properties["kb:status"].name}</#if></td>
				<td><#if child.properties["kb:articleType"]?exists>${child.properties["kb:articleType"].name}</#if></td>
				<td>${child.properties.creator}</td>
				<td>${child.properties.modifier }</td>
				<td>${child.properties.created?date}</td>
				<td>${child.properties.modified ?date}</td>
				<td>
					<#if child.properties["kb:visibility"]?exists>
						${child.properties ["kb:visibility"].name}
					</#if>
				</td>
		        <td>
		        	<#if child.properties["kb:tags"]?exists>
		        		<#list child.properties["kb:tags"] as tag>
		        			${tag}
		        			<#if tag_has_next>,</#if>
		        		</#list>
		        	<#else>
		        		-nil-
		        	</#if>
		        </td>
		        <td> 
		        	<#if child.properties["kb:alfrescoVersion"]?exists>
		        		<#list child.properties["kb:alfrescoVersion"] as versionValue>
		        			${versionValue.name}
		        			<#if versionValue_has_next>,</#if>
		        		</#list>
		        	<#else>
		        		-
		        	</#if>
		        </td>
				</tr>
		       <#if child_index =maxresults-1>
		                <#assign index=child_index>
		               <#break>
		       </#if>
			</#if>
 	 	</#list>
	</table>
	<#assign temp=pages?int><#if temp==pages>  <#else><#assign pages=temp+1> </#if>
	<table cellspacing=0 cellpadding=0 class="recordSet" width="100%">
		<tr>
			<td colspan=99 align=center>
				<span class=pager>
					Page ${p} of ${pages}
					<#if (pages>=2 && p!=1)>  
						<a href="#" onclick="javascript:pagination('1');">      
							<img src="/alfresco/images/icons/FirstPage.gif" width=16 height=16 border=0 alt="First Page" title="First Page">
						</a>
						<a href="#" onclick="javascript:pagination('${p-1}');"> 
							<img src="/alfresco/images/icons/PreviousPage.gif" width=16 height=16 border=0 alt="Previous Page" title="Previous Page">
						</a>
					<#else>                                                                             
						<img src="/alfresco/images/icons/FirstPage_unavailable.gif" width=16 height=16 border=0>    
						<img src="/alfresco/images/icons/PreviousPage_unavailable.gif" width=16 height=16 border=0>  
					</#if>
					
					<#assign x=pages?number>
					<#list 1..x as i>
		 				<#if i=p?number>
		          			<b> ${i}</b>
		 				<#else>
		                	<a href="#" onclick="javascript:pagination('${i}');">  ${i}</a>
						</#if>
					</#list>  
		
					<#if (pages>=2 && p<pages)>
						<a href="#" onclick="javascript:pagination('${p+1}');"> 
							<img src="/alfresco/images/icons/NextPage.gif" width=16 height=16 border=0 alt="Next Page" title="Next Page">
						</a>
						<a href="#" onclick="javascript:pagination('${pages}');"> 
							<img src="/alfresco/images/icons/LastPage.gif" width=16 height=16 border=0 alt="Last Page" title="Last Page">
						</a>
					<#else>
						<img src="/alfresco/images/icons/NextPage_unavailable.gif" width=16 height=16 border=0>     
						<img src="/alfresco/images/icons/LastPage_unavailable.gif" width=16 height=16 border=0>
					</#if>
				</span>
			</td>
		</table>
</#if>
