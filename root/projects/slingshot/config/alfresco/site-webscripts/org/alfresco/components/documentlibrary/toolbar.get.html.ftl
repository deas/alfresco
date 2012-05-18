<#include "include/toolbar.lib.ftl" />

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/toolbar.css" group="documentlibrary"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/toolbar.js" group="documentlibrary"/>
</@>

<@markup id="pre">
</@>

<@markup id="widgets">
   <@createWidgets group="documentlibrary"/>
</@>

<@markup id="post">
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <@toolbarTemplate/>
   </@uniqueIdDiv>
</@>
