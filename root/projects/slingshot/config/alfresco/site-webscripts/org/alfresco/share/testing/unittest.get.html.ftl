<#if jsonModel??>
   <@processJsonModel group="share"/>
<#else>
   ${msg(jsonModelError)?html}
</#if>
