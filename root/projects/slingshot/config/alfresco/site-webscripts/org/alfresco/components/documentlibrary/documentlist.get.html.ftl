<#include "include/documentlist.lib.ftl" />
<#include "../form/form.dependencies.inc">

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/documentlist.css" group="documentlibrary"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/documentlist.js" group="documentlibrary"/>
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
      <@documentlistTemplate/>
   </@uniqueIdDiv>
</@>
