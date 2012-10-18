<@standalone>
   <@markup id="css" >
   <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/preview/web-preview.css" group="${dependencyGroup}"/>
      <@link href="${url.context}/res/components/preview/WebPreviewerHTML.css" group="${dependencyGroup}" />
      <@link href="${url.context}/res/components/preview/Audio.css" group="${dependencyGroup}" />
      <@link href="${url.context}/res/components/preview/Image.css" group="${dependencyGroup}" />
   </@>

   <@markup id="js">
   <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/preview/web-preview.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/components/preview/WebPreviewer.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/js/flash/extMouseWheel.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/components/preview/FlashFox.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/components/preview/StrobeMediaPlayback.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/components/preview/Video.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/components/preview/Audio.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/components/preview/Flash.js" group="${dependencyGroup}"/>
      <@script src="${url.context}/res/components/preview/Image.js" group="${dependencyGroup}"/>
   </@>

   <@markup id="widgets">
      <#if node??>
         <@createWidgets group="${dependencyGroup}"/>
      </#if>
   </@>

   <@markup id="html">
      <@uniqueIdDiv>
         <#if node??>
            <#assign el=args.htmlid?html>
         <div id="${el}-body" class="web-preview">
            <div id="${el}-previewer-div" class="previewer">
               <div class="message"></div>
            </div>
         </div>
         </#if>
      </@>
   </@>

</@standalone>
