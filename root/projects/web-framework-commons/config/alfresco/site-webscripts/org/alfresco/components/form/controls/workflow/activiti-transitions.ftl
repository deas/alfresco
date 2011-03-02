<#if form.mode == "edit">

<#assign transitions="">
<#list field.control.params.options?split(",") as value>
   <#assign transitions=transitions+value+"|"+value>
   <#if value_has_next>
      <#assign transitions=transitions+",">
   </#if>
</#list>

<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.ActivitiTransitions("${fieldHtmlId}").setOptions(
   {
      currentValue: "${transitions?js_string}",
      hiddenFieldName: "${field.name}"
   }).setMessages(
      ${messages}
   );
})();
//]]></script>

<input type="hidden" name="prop_transitions" value="Next" />

<div class="form-field suggested-actions" id="${fieldHtmlId}">
   <div id="${fieldHtmlId}-buttons">
   </div>
</div>
</#if>