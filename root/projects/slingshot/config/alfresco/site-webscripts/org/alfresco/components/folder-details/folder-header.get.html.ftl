<#if folder??>
   <#include "../../include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.component.FolderHeader("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         rootPage: "${rootPage?js_string}",
         rootLabelId: "${rootLabelId?js_string}",
         showFavourite: ${(showFavourite == "true")?string},
         showLikes: ${(showLikes == "true")?string},
         showComments: ${(showComments == "true")?string},
         displayName: "${(folder.displayName!folder.fileName)?js_string}",
         likes:
         {
            <#if folder.likes??>
            isLiked: ${(folder.likes.isLiked!false)?string},
            totalLikes: ${(folder.likes.totalLikes!0)?c}
            </#if>
         },
         isFavourite: ${(folder.isFavourite!false)?string}
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div class="folder-header">

      <div class="folder-info">

         <!-- Path-->
         <div class="folder-path">
            <#list paths as path>
               <#if path_index != 0>
                  <span class="separator"> &gt; </span>
               </#if>
               <span class="folder-link<#if paths?size - path_index == 1> folder-closed<#elseif path_index != 0> folder-open</#if>">
                  <a href="${siteURL(path.href)}">
                     ${path.label?html}
                  </a>
               </span>
            </#list>
         </div>

         <!-- Icon -->
         <img src="${url.context}/components/images/filetypes/generic-folder-48.png"
              title="${(folder.displayName!folder.fileName)?html}" class="folder-thumbnail" width="48" />

         <!-- Title -->
         <h1 class="thin dark">
            ${(folder.displayName!folder.fileName)?html}
         </h1>

         <!-- Modified & Social -->
         <div>
            <#assign modifyUser = node.properties["cm:modifier"]>
            <#assign modifyDate = node.properties["cm:modified"]>
            <#assign modifierLink = userProfileLink(modifyUser.userName, modifyUser.displayName, 'class="theme-color-1"') >
            ${msg("label.modified-by-user-on-date", modifierLink, xmldate(modifyDate.iso8601)?string(msg("date-format.defaultFTL")))}
            <#if showLikes == "true">
            <span id="${el}-like" class="item item-separator"></span>
            </#if>
            <#if showFavourite == "true">
            <span id="${el}-favourite" class="item item-separator"></span>
            </#if>
            <#if showComments == "true">
            <span class="item item-separator item-social">
               <a href="#" name="@commentNode" rel="${nodeRef?js_string}" class="theme-color-1 comment ${el}" title="${msg("comment.folder.tip")}" tabindex="0">${msg("comment.folder.label")}</a>
            </span>
            </#if>
         </div>

      </div>

      <div class="clear"></div>

   </div>
<#else>
   <div class="folder-header">
      <div class="status-banner theme-bg-color-2 theme-border-4">
      ${msg("banner.folder-not-found")}
      </div>
   </div>
</#if>