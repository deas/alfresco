<#assign el=args.htmlid?html>
<#if (node?exists)>
<script type="text/javascript">//<![CDATA[
new Alfresco.WebPreview("${el}").setOptions(
{
   nodeRef: "${node.nodeRef}",
   name: "${node.name?js_string}",
   icon: "${node.icon}",
   mimeType: "${node.mimeType}",
   previews: [<#list node.previews as p>"${p}"<#if (p_has_next)>, </#if></#list>],
   size: "${node.size}",
   disableI18nInputFix: ${args.disableI18nInputFix!"false"}
}).setMessages(${messages});
//]]></script>
</#if>
<div class="web-preview shadow">
   <div class="hd">
      <div class="title">
         <h4>
            <img id="${el}-title-img" src="${url.context}/res/components/images/generic-file-32.png" alt="File" />
            <span id="${el}-title-span"></span>
         </h4>
      </div>
   </div>
   <div class="bd">
      <div id="${el}-shadow-swf-div" class="preview-swf">
         <div id="${el}-swfPlayerMessage-div"><#if (node?exists)>${msg("label.preparingPreviewer")}</#if></div>
      </div>
   </div>
</div>