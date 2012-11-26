<#assign el=args.htmlid?html>
<div id="${el}-body" class="theme-overlay guest-message hidden">
   <#if (args.logo!"true") == "true">
      <div class="theme-company-logo"></div>
   </#if>
   <#if args.header??>
      <h3 class="thin ${args.headerClass!""}">${msg(args.header?html)}</h3>
   </#if>
   <#if args.header?? && args.text??>
      <hr/>
   </#if>
   <#if args.text??>
      <p class="${args.textClass!""}">${msg(args.text)?html}</p>
   </#if>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.GuestMessage("${args.htmlid?js_string}").setOptions({}).setMessages(${messages});
//]]></script>