<#macro render>

   <!-- CSS for Horizontal Menu -->
   <style type="text/css">
   
   <!--
	.tab-container${htmlid} {
		border-top: 1px black solid;
		border-left: 1px black solid;
		border-right: 1px black solid;
		border-bottom: 1px black solid;
	}

	.tab${htmlid} {
		border-top: 1px #efefef solid;
		border-right: 1px #efefef solid;
		border-left: 1px #efefef solid;
		
		padding-left: 20px;		
		padding-right: 20px;
		padding-top: 5px;
		padding-bottom: 5px;		
		
		color: #000000;
	}

	.tab-selected${htmlid} {
		border-top: 1px #ffffff solid;
		border-right: 1px #ffffff solid;
		border-left: 1px #ffffff solid;		

		padding-left: 20px;		
		padding-right: 20px;
		padding-top: 5px;
		padding-bottom: 5px;

		font-weight: bold;
		color: #000000;

		background-image: url(${baseDir}/tab_selected.gif);
		background-repeat: repeat-x;
		background-position: left top;
		background-color: #FFFFFF;		
	}

	.tab${htmlid}, .bg${htmlid} {
		background-image: url(${baseDir}/gray_fade.gif);
		background-repeat: repeat-x;
		background-position: left top;
	}

	.tab${htmlid} a:link,
	.tab${htmlid} a:hover,
	.tab${htmlid} a:visited,
	.tab${htmlid} a:active {
		color: #000000;
		text-decoration: none;
	}

	.tab-selected${htmlid} a:link,
	.tab-selected${htmlid} a:hover,
	.tab-selected${htmlid} a:visited,
	.tab-selected${htmlid} a:active {
		color: #000000;
		font-weight: bold;
		text-decoration: none;
	}

   -->
	
   </style>

   <#if orientation == "horizontal">
      <@horizontal page=rootPage/>
   </#if>

   <#if orientation == "vertical">
      <@vertical page=rootPage/>
   </#if>

</#macro>


<#macro horizontal page>
   
   <table border="0" cellpadding="0" cellspacing="0" width="100%" class="tab-container${htmlid}">
   <tr>
   
      <!-- Show this page? -->
      <#if showTopPage>
         <@horizontalPage page=page />
      </#if>
      
      <!-- Show Child Pages? -->	
      <#if showChildren>
         <#list sitedata.findChildPages(page.id) as childPage>
      
            <@horizontalPage page=childPage />
      
         </#list>
      </#if>

      <!-- Show Siblings Pages? -->	
      <#if showSiblings>
         <#assign parentPages = sitedata.findParentPages(page.id)>
         <#if parentPages?size &gt; 0>
         
            <#assign parentPage = parentPages[0]>
            <#list sitedata.findChildPages(parentPage.id) as siblingPage>
      
               <@horizontalPage page=siblingPage />
      
            </#list>
            
         </#if>
      </#if>
      
      <!-- spacer -->
      <td width="100%" class="bg${htmlid}"></td>
      	
   </tr>
   </table>
   
</#macro>   

<#macro horizontalPage page>

   <#assign href = linkbuilder.page(page.id, context.formatId)>
   <#assign className = "tab">
   
   <#if page.id == currentPageId>
      <#assign className="tab-selected">
   </#if>

   <td align="center" valign="middle" class="${className}${htmlid}" nowrap><a href="${href}">${page.title}</a></td>
   <td width="1px" style="background-color: black;">
   	<img src="${baseDir}/spacer.gif" width="1px"/>
   </td>

</#macro>


<#macro vertical page>

   <table border="0" cellpadding="0" cellspacing="0" width="100%" class="tab-container${htmlid}">
   
      <!-- Show this page? -->
      <#if showTopPage>
         <@verticalPage page=page />
      </#if>
      
      <!-- Show Child Pages? -->	
      <#if showChildren>
         <#list sitedata.findChildPages(page.id) as childPage>
      
            <@verticalPage page=childPage />
      
         </#list>
      </#if>

      <!-- Show Siblings Pages? -->	
      <#if showSiblings>
         <#assign parentPages = sitedata.findParentPages(page.id)>
         <#if parentPages?size &gt; 0>
         
            <#assign parentPage = parentPages[0]>
            <#list sitedata.findChildPages(parentPage.id) as siblingPage>
      
               <@horizontalPage page=siblingPage />
      
            </#list>
            
         </#if>
      </#if>

   </tr>
   </table>

</#macro>

<#macro verticalPage page>

   <#assign href = linkbuilder.page(page.id, context.formatId)>
   <#assign className = "tab">
   
   <#if page.id == currentPageId>
      <#assign className = "tab-selected">
   </#if>

   <tr>
      <td align="center" valign="middle" class="${className}${htmlid}" height="28px"><a href="${href}">${page.title}</a></td>
   </tr>

</#macro>
