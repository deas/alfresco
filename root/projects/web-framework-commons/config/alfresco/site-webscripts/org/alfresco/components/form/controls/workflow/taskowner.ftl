<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${field.label?html}:</span>
      <span id="${fieldHtmlId}" class="viewmode-value"><#if (!field.value?? || field.value?length == 0)>${msg("form.control.novalue")}</#if></span>
   </div>
</div>

<#if field.value?? && field.value?length &gt; 0>
<#assign ownerParts=field.value?split("|") />
<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("${fieldHtmlId}", function ()
{
   YAHOO.util.Dom.get("${fieldHtmlId}").innerHTML = Alfresco.util.userProfileLink("${ownerParts[0]}", "${ownerParts[1]} ${ownerParts[2]}");
}, this);
//]]></script>
</#if>