<@markup id="css" >
   <@link rel="stylesheet" type="text/css" href="${url.context}/${sitedata.getDojoPackageLocation('dijit')}/themes/claro/claro.css" group="share" forceAggregation="true"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/js/alfresco/css/global.css" group="share" forceAggregation="true"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/js/alfresco/css/header.css" group="share" forceAggregation="true"/>
   <#if config.global.header?? && config.global.header.dependencies?? && config.global.header.dependencies.css??>
      <#list config.global.header.dependencies.css as cssFile>
         <@link href="${url.context}/res${cssFile}" group="header"/>
      </#list>
   </#if>
</@>

<@markup id="js">
   <#if config.global.header?? && config.global.header.dependencies?? && config.global.header.dependencies.js??>
      <#list config.global.header.dependencies.js as jsFile>
         <@script src="${url.context}/res${jsFile}" group="header"/>
      </#list>
   </#if>
</@>

<@markup id="widgets">
   <@processJsonModel group="share" forceAggregation="true"/>
</@>

<@markup id="html">
   <div id="share-header"></div>
</@>
