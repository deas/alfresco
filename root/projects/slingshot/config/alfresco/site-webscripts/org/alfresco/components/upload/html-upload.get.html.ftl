<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="html-upload hidden">
   <div class="hd">
      <span id="${el}-title-span"></span>
   </div>
   <div class="bd">
      <form id="${el}-htmlupload-form"
            method="post" enctype="multipart/form-data" accept-charset="utf-8"
            action="${url.context}/proxy/alfresco/api/upload.html">
         <fieldset>
         <input type="hidden" id="${el}-siteId-hidden" name="siteId" value=""/>
         <input type="hidden" id="${el}-containerId-hidden" name="containerId" value=""/>
         <input type="hidden" id="${el}-destination-hidden" name="destination" value=""/>
         <input type="hidden" id="${el}-username-hidden" name="username" value=""/>
         <input type="hidden" id="${el}-updateNodeRef-hidden" name="updateNodeRef" value=""/>
         <input type="hidden" id="${el}-uploadDirectory-hidden" name="uploadDirectory" value=""/>
         <input type="hidden" id="${el}-overwrite-hidden" name="overwrite" value=""/>
         <input type="hidden" id="${el}-thumbnails-hidden" name="thumbnails" value=""/>
         <input type="hidden" id="${el}-successCallback-hidden" name="successCallback" value=""/>
         <input type="hidden" id="${el}-successScope-hidden" name="successScope" value=""/>
         <input type="hidden" id="${el}-failureCallback-hidden" name="failureCallback" value=""/>
         <input type="hidden" id="${el}-failureScope-hidden" name="failureScope" value=""/>

         <p id="${el}-singleUploadTip-span">${msg("label.singleUploadTip")}</p>
         <p id="${el}-singleUpdateTip-span">${msg("label.singleUpdateTip")}</p>

         <div>
            <div class="yui-g">
               <h2>${msg("section.file")}</h2>
            </div>
            <div class="yui-gd <#if (contentTypes?size == 1)>hidden</#if>">
               <div class="yui-u first">
                  <label for="${el}-contentType-select">${msg("label.contentType")}</label>
               </div>
               <div class="yui-u">
                  <select id="${el}-contentType-select" name="contentType" tabindex="0">
                     <#if (contentTypes?size > 0)>
                        <#list contentTypes as contentType>
                           <option value="${contentType.id}">${msg(contentType.value)}</option>
                        </#list>
                     </#if>
                  </select>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${el}-filedata-file">${msg("label.file")}</label>
               </div>
               <div class="yui-u">
                  <input type="file" id="${el}-filedata-file" name="filedata" tabindex="0" />
               </div>
            </div>
         </div>

         <div id="${el}-versionSection-div">
            <div class="yui-g">
               <h2>${msg("section.version")}</h2>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <span>${msg("label.version")}</span>
               </div> 
               <div class="yui-u">
                  <input id="${el}-minorVersion-radioButton" type="radio" name="majorVersion" checked="checked" value="false" tabindex="0" />
                  <label for="${el}-minorVersion-radioButton" id="${el}-minorVersion">${msg("label.minorVersion")}</label>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">&nbsp;
               </div>
               <div class="yui-u">
                  <input id="${el}-majorVersion-radioButton" type="radio" name="majorVersion" value="true" tabindex="0" />
                  <label for="${el}-majorVersion-radioButton" id="${el}-majorVersion">${msg("label.majorVersion")}</label>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${el}-description-textarea">${msg("label.comments")}</label>
               </div>
               <div class="yui-u">
                  <textarea id="${el}-description-textarea" name="description" cols="80" rows="4" tabindex="0"></textarea>
               </div>
            </div>
         </div>

         <div class="bdft">
            <input id="${el}-upload-button" type="button" value="${msg("button.upload")}" tabindex="0" />
            <input id="${el}-cancel-button" type="button" value="${msg("button.cancel")}" tabindex="0" />
         </div>

         </fieldset>
      </form>

   </div>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.HtmlUpload("${el}").setMessages(
   ${messages}
);
Alfresco.util.relToTarget("${el}-singleUploadTip-span");
//]]></script>
