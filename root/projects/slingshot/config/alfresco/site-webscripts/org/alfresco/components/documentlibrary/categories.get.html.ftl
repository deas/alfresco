<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/tree.css" group="documentlibrary"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/categories.css" group="documentlibrary"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/tree.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/categories.js" group="documentlibrary"/>
</@>

<@markup id="pre">
</@>

<@markup id="widgets">
   <@createWidgets group="documentlibrary"/>
</@>

<@markup id="post">
</@>

<@markup id="html">
   <#assign el=args.htmlid?html>   
   <@uniqueIdDiv>
      <div class="categoryview filter">
         <h2 id="${el}-h2">${msg("header.library")}</h2>
         <div id="${el}-treeview" class="category"></div>
      </div>
   </@uniqueIdDiv>
</@>