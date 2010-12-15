<#macro body>
   <div>
      <#list templateConfig.regions as templateRegion>
         <div id="${templateRegion.id}" style="position:absolute; height: ${templateRegion.height}; width: ${templateRegion.width} auto; top: ${templateRegion.y}; left: ${templateRegion.x};">
	    <@region id="${templateRegion.id}" scope="${templateRegion.scope}" />
         </div>		  
      </#list>
   </div>
</#macro>