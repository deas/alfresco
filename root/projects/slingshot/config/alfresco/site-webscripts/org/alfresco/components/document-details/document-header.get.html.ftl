<#if document??>
   <#include "../../include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid?js_string>
   <#assign fileExtIndex = document.fileName?last_index_of(".")>
   <#assign fileExt = (fileExtIndex > -1)?string(document.fileName?substring(fileExtIndex + 1), "generic")>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.component.DocumentHeader("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         rootPage: "${rootPage?js_string}",
         rootLabelId: "${rootLabelId?js_string}",
         showFavourite: ${(showFavourite == "true")?string},
         showLikes: ${(showLikes == "true")?string},
         showComments: ${(showComments == "true")?string},
         displayName: "${(document.displayName!document.fileName)?js_string}",
         likes:
         {
            <#if document.likes??>
            isLiked: ${(document.likes.isLiked!false)?string},
            totalLikes: ${(document.likes.totalLikes!0)?c}
            </#if>
         },
         isFavourite: ${(document.isFavourite!false)?string}
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div class="document-header">

      <!-- Message banner -->
      <#if document.workingCopy??>
         <#if document.workingCopy.isWorkingCopy??>
            <#assign lockUser = node.properties["cm:workingCopyOwner"]>
         <#else>
            <#assign lockUser = node.properties["cm:lockOwner"]>
         </#if>
         <#if lockUser??>
            <div class="status-banner theme-bg-color-2 theme-border-4">
            <#assign lockedByLink = userProfileLink(lockUser.userName, lockUser.displayName, 'class="theme-color-1"') >
            <#if (document.workingCopy.googleDocUrl!"")?length != 0 >
               <#assign link><a href="${document.workingCopy.googleDocUrl}" target="_blank" class="theme-color-1">${msg("banner.google-docs.link")}</a></#assign>
               <#if lockUser.userName == user.name>
                  <span class="google-docs-owner">${msg("banner.google-docs-owner", link)}</span>
               <#else>
                  <span class="google-docs-locked">${msg("banner.google-docs-locked", lockedByLink, link)}</span>
               </#if>
            <#else>
               <#if lockUser.userName == user.name>
                  <#assign status><#if node.isLocked>lock-owner<#else>editing</#if></#assign>
                  <span class="${status}">${msg("banner." + status)}</span>
               <#else>
                  <span class="locked">${msg("banner.locked", lockedByLink)}</span>
               </#if>
            </#if>
            </div>
         </#if>
      </#if>

      <div class="document-info">

         <!-- Path-->
         <div class="document-path">
            <#list paths as path>
               <#if path_index != 0>
                  <span class="separator"> &gt; </span>
               </#if>
               <span class="folder-link<#if path_index != 0> folder-open</#if>">
                  <a href="${siteURL(path.href)}">${path.label?html}</a>
               </span>
            </#list>
         </div>

         <!-- Icon -->
         <img src="${url.context}/components/images/filetypes/${fileExt}-file-48.png"
              onerror="this.src='${url.context}/res/components/images/filetypes/generic-file-48.png'"
              title="${(document.displayName!document.fileName)?html}" class="document-thumbnail" width="48" />

         <!-- Title and Version -->
         <h1 class="thin dark">
            ${(document.displayName!document.fileName)?html}<span class="document-version">${document.version}</span>
         </h1>

         <!-- Modified & Social -->
         <div>
            <#assign modifyUser = node.properties["cm:modifier"]>
            <#assign modifyDate = node.properties["cm:modified"]>
            <#assign modifierLink = userProfileLink(modifyUser.userName, modifyUser.displayName, 'class="theme-color-1"') >
            ${msg("label.modified-by-user-on-date", modifierLink, xmldate(modifyDate.iso8601)?string(msg("date-format.defaultFTL")))}
            <#if showFavourite == "true">
            <span id="${el}-favourite" class="item item-separator"></span>
            </#if>
            <#if showLikes == "true">
            <span id="${el}-like" class="item item-separator"></span>
            </#if>
            <#if showComments == "true">
            <span class="item item-separator item-social">
               <a href="#" name="@commentNode" rel="${nodeRef?js_string}" class="theme-color-1 comment ${el}" title="${msg("comment.document.tip")}" tabindex="0">${msg("comment.document.label")}</a><#if commentCount??><span class="comment-count">${commentCount}</span></#if>
            </span>
            </#if>
         </div>

      </div>

      <div class="document-action">

         <!-- Download Button -->
         <span class="yui-button yui-link-button onDownloadDocumentClick">
            <span class="first-child">
               <a href="${url.context}/proxy/alfresco/${node.contentURL?js_string}?a=true" tabindex="0">${msg("button.download")}</a>
            </span>
         </span>

      </div>

      <div class="clear"></div>

   </div>
<#else>
   <div class="document-header">
      <div class="status-banner theme-bg-color-2 theme-border-4">
      ${msg("banner.document-not-found")}
      </div>
   </div>
</#if>