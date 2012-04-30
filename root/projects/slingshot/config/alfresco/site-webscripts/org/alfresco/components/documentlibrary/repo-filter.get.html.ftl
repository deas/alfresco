<#include "include/toolbar.lib.ftl" />

<@markup id="cssDependencies" >
   <#-- CSS Dependencies -->
</@>

<@markup id="jsDependencies">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/repo-toolbar.js" group="documentlibrary_deps"/>
</@>

<@markup id="preInstantiationJs">
</@>

<@markup id="widgetInstantiation">
   <@createWebScriptWidgets group="documentlibrary"/>
</@>

<@markup id="postInstantiationJs">
</@>

<@markup id="html">
   <div id="${htmlid!""}">
      <@toolbarTemplate/>
   </div>
</@>