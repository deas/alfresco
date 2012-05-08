<#if form.mode == "edit" && ((form.data['prop_bpm_status']?? && form.data['prop_bpm_status'] != 'Completed') || form.data['prop_bpm_status']?? == false)>
<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.Transitions("${fieldHtmlId}").setOptions(
   {
      currentValue: "${field.value?js_string}"
   }).setMessages(
      ${messages}
   );
})();
//]]></script>

<div class="form-field suggested-actions" id="${fieldHtmlId}">
   <div id="${fieldHtmlId}-buttons">
   </div>
</div>
</#if>