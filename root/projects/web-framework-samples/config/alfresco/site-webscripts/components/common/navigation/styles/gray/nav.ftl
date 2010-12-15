<#macro render>

	<#if orientation == "horizontal">
	  <@horizontal page=rootPage/>
	</#if>

	<#if orientation == "vertical">
	  <@vertical page=rootPage/>
	</#if>

</#macro>


<#macro horizontal page>
   
   <table border="0" cellpadding="0" cellspacing="0" style="background-image: url(${baseDir}/menu_fill.png); background-repeat: repeat; height: 35px;">
   <tr>
   
      <td style="padding-right: 20px">
         <img src="${baseDir}/menu_left.png" height="35px" />
      </td>
      
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
         
      <td width="100%"></td>
      
      <td style="padding-left: 20px">
         <img src="${baseDir}/menu_right.png" height="35px"/>
      </td>
      
   </tr>
   </table>
   
   </span>
   
</#macro>

<#macro horizontalPage page>

   <#assign href = linkbuilder.page(page.id, context.formatId)>
   
   <#if page.id == currentPageId>

      <td>
         <img src="${baseDir}/selected_button_left.png" height="35px" />
      </td>
      <td nowrap style="padding-left: 10px; padding-right: 10px; background-image: url(${baseDir}/selected_button_fill.png); background-repeat: repeat; height: 35px;">
         <a href="${href}" style="color: white; font-face: Verdana; font-weight: bold; text-decoration: none;">${page.title}</a>
      </td>
      <td>
         <img src="${baseDir}/selected_button_right.png" height="35px" />
      </td>
         
   <#else>

      <td>
         <img src="${baseDir}/button_left.png" height="35px" />
      </td>
      <td nowrap style="padding-left: 10px; padding-right: 10px;" nowrap>
         <a href="${href}" style="color: black; font-face: Verdana; font-weight: bold; text-decoration: none;">${page.title}</a>
      </td>
      <td>
         <img src="${baseDir}/button_right.png" height="35px" />
      </td>
      
   </#if>

</#macro>






<#macro vertical page>
   
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
   
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
      
               <@verticalPage page=siblingPage />
      
            </#list>
            
         </#if>
      </#if>

   </table>

</#macro>
   
   
   
   
   
   
   
<#macro verticalPage page>

   <#assign href = linkbuilder.page(page.id, context.formatId)>
   
   <tr>
   	<td width="100%" style="background-image: url(${baseDir}/menu_fill.png); background-repeat: repeat; height: 35px;">
   
   		<table width="100%" cellspacing="0" cellpadding="0" border="0">
   		<tr>
   
			<td style="padding-right: 0px">
				<img src="${baseDir}/menu_left.png" height="35px" />
			</td>
            

		<#if page.id == currentPageId>

			<td>
				<img src="${baseDir}/selected_button_left.png" height="35px" />
			</td>
			<td style="padding-left: 10px; padding-right: 10px; background-image: url(${baseDir}/selected_button_fill.png); background-repeat: repeat; height: 35px;" width="100%">
				<a href="${href}" style="color: white; font-face: Verdana; font-weight: bold; text-decoration: none;">${page.title}</a>
			</td>
			<td>
				<img src="${baseDir}/selected_button_right.png" height="35px" />
			</td>
         
   		<#else>

			<td>
				<img src="${baseDir}/button_left.png" height="35px" />
			</td>
			<td style="padding-left: 10px; padding-right: 10px;" width="100%">
				<a href="${href}" style="color: black; font-face: Verdana; font-weight: bold; text-decoration: none;">${page.title}</a>
			</td>
			<td>
				<img src="${baseDir}/button_right.png" height="35px" />
			</td>
      
		</#if>
         
			<td style="padding-left: 0px">
				<img src="${baseDir}/menu_right.png" height="35px"/>
			</td>
      
   		</tr>
   		</table>

   	</td>
   </tr>

</#macro>
