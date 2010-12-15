<#assign el=args.htmlid>
<div id="${el}-dialog" class="html-upload hidden">
   <div class="hd">
      <span id="${el}-title-span"></span>
   </div>
   <div class="bd">
      <form id="${el}-htmlupload-form"
            method="post" enctype="multipart/form-data" accept-charset="utf-8"
            action="${url.context}/proxy/alfresco/api/upload.html">
         <input type="hidden" id="${el}-siteId-hidden" name="siteId" value=""/>
         <input type="hidden" id="${el}-containerId-hidden" name="containerId" value=""/>
         <input type="hidden" id="${el}-username-hidden" name="username" value=""/>
         <input type="hidden" id="${el}-updateNodeRef-hidden" name="updateNodeRef" value=""/>
         <input type="hidden" id="${el}-uploadDirectory-hidden" name="uploadDirectory" value=""/>
         <input type="hidden" id="${el}-overwrite-hidden" name="overwrite" value=""/>
         <input type="hidden" id="${el}-thumbnails-hidden" name="thumbnails" value=""/>
         <input type="hidden" id="${el}-destination-hidden" name="destination" value=""/>
         <input type="hidden" id="${el}-successCallback-hidden" name="successCallback" value=""/>
         <input type="hidden" id="${el}-successScope-hidden" name="successScope" value=""/>
         <input type="hidden" id="${el}-failureCallback-hidden" name="failureCallback" value=""/>
         <input type="hidden" id="${el}-failureScope-hidden" name="failureScope" value=""/>

         <p>
            <span id="${el}-singleUploadTip-span">${msg("label.singleUploadTip")}</span>
            <span id="${el}-singleUpdateTip-span">${msg("label.singleUpdateTip")}</span>
         </p>

         <div id="${el}-recordTypeSection-div">
            <div class="yui-gd <#if (contentTypes?size == 1)>hidden</#if>">
               <div class="yui-u first">
                  <label for="${el}-contentType-select">${msg("label.contentType")}</label>
               </div>
               <div class="yui-u">
                  <select id="${el}-contentType-select" name="contentType">
                     <#if (contentTypes?size > 0)>
                        <#list contentTypes as contentType>
                           <option value="${contentType.id}">${msg(contentType.value)}</option>
                        </#list>
                     </#if>
                  </select>
               </div>
            </div>
            <div class="yui-g">
               <h2>${msg("section.recordType")}</h2>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${el}-recordTypes-select">${msg("label.recordType")}</label>
               </div>
               <div class="yui-u" id="${el}-recordTypes-select-container">
                  <select id="${el}-recordTypes-select" name="aspects">
                     <#if (recordTypes?size > 0)>
                        <#list recordTypes as recordType>
                           <option value="${recordType.id}"<#if recordType_index = 0> selected</#if>>${msg("recordType." + recordType.value)}</option>
                        </#list>
                     </#if>
                  </select>
               </div>
            </div>
            <div class="yui-g">
               <h2>${msg("section.file")}</h2>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${el}-filedata-file">${msg("label.file")}</label>
            </div>
            <div class="yui-u">
               <input type="file" id="${el}-filedata-file" name="filedata" />
            </div>
         </div>
   
         <div id="${el}-versionSection-div">
            <div class="yui-g">
               <h2>${msg("section.version")}</h2>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${el}-minorVersion-radioButton">${msg("label.version")}</label>
               </div> 
               <div class="yui-u">
                  <input id="${el}-minorVersion-radioButton" type="radio" name="majorVersion" checked="checked" value="false" /> ${msg("label.minorVersion")}
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">&nbsp;
               </div>
               <div class="yui-u">
                  <input id="${el}-majorVersion-radioButton" type="radio" name="majorVersion" value="true" /> ${msg("label.majorVersion")}
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${el}-description-textarea">${msg("label.comments")}</label>
               </div>
               <div class="yui-u">
                  <textarea id="${el}-description-textarea" name="description" cols="80" rows="4"></textarea>
               </div>
            </div>
         </div>

         <div class="bdft">
            <input id="${el}-upload-button" type="button" value="${msg("button.upload")}" />
            <input id="${el}-cancel-button" type="button" value="${msg("button.cancel")}" />
         </div>

      </form>

   </div>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.RecordsHtmlUpload("${el}").setMessages(
      ${messages}
      );
Alfresco.util.relToTarget("${el}-singleUploadTip-span");
//]]></script>
