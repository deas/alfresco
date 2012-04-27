<@markup id="cssDependencies" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/tree.css" group="documentlibrary"/>
</@>

<@markup id="jsDependencies">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/tree.js" group="documentlibrary"/>
</@>

<@markup id="preInstantiationJs">
</@>

<@markup id="widgetInstantiation">
   <@createWebScriptWidgets group="documentlibrary"/>
</@>

<@markup id="postInstantiationJs">
</@>

<@markup id="html">
   <#assign id=args.htmlid?html>
   <div id="${htmlid!""}">
      <div class="treeview filter">
         <h2 id="${id}-h2" class="alfresco-twister">${msg("header.library")}</h2>
         <div id="${id}-treeview" class="tree"></div>
      </div>
   </div>
</@>