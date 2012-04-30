<@markup id="cssDependencies" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/tree.css" group="documentlibrary_deps"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/categories.css" group="documentlibrary_deps"/>
</@>

<@markup id="jsDependencies">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/tree.js" group="documentlibrary_deps"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/categories.js" group="documentlibrary_deps"/>
</@>

<@markup id="preInstantiationJs">
</@>

<@markup id="widgetInstantiation">
   <@createWebScriptWidgets group="documentlibrary"/>
</@>

<@markup id="postInstantiationJs">
</@>

<@markup id="html">
   <#assign el=args.htmlid?html>   
   <div id="${htmlid!""}">
      <div class="categoryview filter">
         <h2 id="${el}-h2">${msg("header.library")}</h2>
         <div id="${el}-treeview" class="category"></div>
      </div>
   </div>
</@>