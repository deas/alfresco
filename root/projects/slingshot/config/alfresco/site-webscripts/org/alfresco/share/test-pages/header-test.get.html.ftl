<@markup id="css" >
   <@link rel="stylesheet" type="text/css" href="${url.context}/${sitedata.getDojoPackageLocation('dijit')}/themes/claro/claro.css" group="share" forceAggregation="true"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/js/alfresco/css/global.css" group="share" forceAggregation="true"/>
</@>

<@markup id="js"></@>

<@markup id="widgets">
   <@processJsonModel group="share" forceAggregation="true"/>
</@>
