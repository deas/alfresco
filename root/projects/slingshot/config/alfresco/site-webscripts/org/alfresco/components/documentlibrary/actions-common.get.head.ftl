<#include "../component.head.inc">
<#-- Document Library Actions: Supports concatenated JavaScript files via build scripts -->
<#if DEBUG>
   <script type="text/javascript" src="${page.url.context}/res/components/documentlibrary/actions.js"></script>
   <script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></script>
   <script type="text/javascript" src="${page.url.context}/res/modules/documentlibrary/global-folder.js"></script>
   <script type="text/javascript" src="${page.url.context}/res/modules/documentlibrary/copy-move-to.js"></script>
   <script type="text/javascript" src="${page.url.context}/res/components/people-finder/people-finder.js"></script>
   <script type="text/javascript" src="${page.url.context}/res/modules/documentlibrary/permissions.js"></script>
   <script type="text/javascript" src="${page.url.context}/res/modules/documentlibrary/aspects.js"></script>
<#else>
   <script type="text/javascript" src="${page.url.context}/res/js/documentlibrary-actions-min.js"></script>
</#if>
<#-- Global Folder Picker (req'd by Copy/Move To) -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/documentlibrary/global-folder.css" />
<#-- People Finder Assets (req'd by Assign Workflow)  -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/people-finder/people-finder.css" />
<#-- Manage Permissions -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/documentlibrary/permissions.css" />
<#-- Manage Aspects -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/documentlibrary/aspects.css" />