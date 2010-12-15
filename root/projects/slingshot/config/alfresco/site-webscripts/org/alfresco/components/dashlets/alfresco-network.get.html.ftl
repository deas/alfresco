<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.widget.DashletResizer("${el}", "${instance.object.id}");
//]]></script>

<div class="dashlet">
   <div class="title">${msg("header.network")}</div>
   <div class="body scrollablePanel" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div class="detail-list-item">
         <h1 class="theme-color-2">Get more out of Alfresco.</h1>
         <p>With a subscription to the Alfresco Enterprise Network, you get access to the Alfresco Enterprise Edition. For this edition, the Alfresco QA team run over 6000 tests per stack to ensure stability, scalability and security. It is fully supported by the international Alfresco Support teams and maintained with Service Packs from Alfresco's Core Engineers.</p>
         <p>A portal to all Alfresco related information is available at <a href="http://network.alfresco.com/" rel="_blank" class="theme-color-2">http://network.alfresco.com</a> including the Enterprise Downloads, Enterprise Documentation, Support Handbook, Knowledge base, News and Alerts.</p>
         <p><a href="http://www.alfresco.com/services/subscription/" rel="_blank"><img src="${url.context}/res/components/images/network-dashlet-button.png" alt="Learn More" width="97" height="28" /></a></p>
      </div>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
   Alfresco.util.relToTarget("${el}");
//]]></script>