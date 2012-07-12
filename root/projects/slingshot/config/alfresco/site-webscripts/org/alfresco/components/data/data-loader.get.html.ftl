<@markup id="css" >
   <#-- No CSS Dependencies -->
   <@link href="" group=""/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/data/data-loader.js" group="data"/>
</@>

<@markup id="widgets">
   <@createWidgets group="data"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
   </@>
</@>