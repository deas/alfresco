<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/create-content/create-content-mgr.css" group="create-content"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/create-content/create-content-mgr.js" group="create-content"/>
</@>

<@markup id="widgets">
   <@createWidgets group="create-content"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="create-content-mgr">
         <div class="heading">${msg("create-content-mgr.heading")}</div>
      </div>
   </@>
</@>
