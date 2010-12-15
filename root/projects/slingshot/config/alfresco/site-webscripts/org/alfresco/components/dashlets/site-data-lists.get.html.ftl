<script type="text/javascript">//<![CDATA[
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<#assign site=page.url.templateArgs.site>

<div class="dashlet site-data-lists">
   <div class="title">${msg("header.datalists")}</div>
   <#if numLists?? && numLists==0 && create?? && create?string == "true">
      <div class="toolbar">
         <a id="${args.htmlid}-createLink-button" class="theme-color-1" title="${msg('list.createLink')}" href="${url.context}/page/site/${site}/data-lists">${msg("list.createLink")}</a>
      </div>
   </#if>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if numLists?? && numLists!=0>
   <#list lists as list>
      <div class="detail-list-item <#if list_index = 0>first-item<#elseif !list_has_next>last-item</#if>">
         <div>
            <div id="list">
               <a id="${args.htmlid}-details-span-${list_index}" href="${url.context}/page/site/${site}/data-lists?list=${list.name?html}" class="theme-color-1" title="${(list.title!"")?html}">${(list.title!"")?html}</a>
               <div class="description">${(list.description!"")?html}</div>
            </div>
         </div>
      </div>
   </#list>
<#else>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noLists")}</span>
      </div>
</#if>
   </div>
</div>