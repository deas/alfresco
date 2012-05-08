<#if form.mode == "edit" && ((form.data['prop_bpm_status']?? && form.data['prop_bpm_status'] != 'Completed') || form.data['prop_bpm_status']?? == false)>

<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.ActivitiTransitions("${fieldHtmlId}").setOptions(
   {
      currentValue: "${field.control.params.options?js_string}",
      hiddenFieldName: "${field.name}"
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