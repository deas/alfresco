<#macro doclibUrl doc>
   <a href="${url.context}/page/site/${doc.location.site}/document-details?nodeRef=${doc.nodeRef}" class="theme-color-1">${doc.displayName?html}</a>
</#macro>
<script type="text/javascript">//<![CDATA[
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet">
   <div class="title">${msg("header.docSummary")}</div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   <#if docs.message?exists>
      <div class="detail-list-item first-item last-item">
         <div class="error">${docs.message}</div>
      </div>
   <#else>
      <#if docs.items?size == 0>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noItems")}</span>
      </div>
      <#else>
         <#list docs.items as doc>
            <#assign modifiedBy><a href="${url.context}/page/user/${doc.modifiedByUser?url}/profile" class="theme-color-1">${doc.modifiedBy?html}</a></#assign>
      <div class="detail-list-item <#if doc_index = 0>first-item<#elseif !doc_has_next>last-item</#if>">
         <div>
            <div class="icon">
               <img src="${url.context}/res/components/images/generic-file-32.png" alt="${doc.displayName?html}" />
            </div>
            <div class="details">
               <h4><@doclibUrl doc /></h4>
               <div>
                  ${msg("text.modified-by", modifiedBy)} ${msg("text.modified-on", xmldate(doc.modifiedOn)?string(msg("date-format.defaultFTL")))}
               </div>
            </div>
         </div>
      </div>
         </#list>
      </#if>
   </#if>
   </div>
</div>