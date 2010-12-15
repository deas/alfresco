<#if success>
   Successfully created RM site '<a href="${url.context}/page/site/${args["shortname"]}/dashboard">${args["shortname"]}</a>'.
</#if>
<#if code?exists><br>Error code: ${code}</#if>
<#if error?exists><br>Error: ${error}</#if>