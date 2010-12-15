<#assign el=args.htmlid>
<div id="${el}-dialog" class="flash-upload hidden">
   <div class="hd">
      <span id="${el}-title-span"></span>
   </div>
   <div class="bd">
      <div id="${el}-recordTypeSection-div">      
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
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;</div>
            <div class="yui-u">${msg("label.recordType.description")}</div>
         </div>

         <div class="yui-g">
            <h2>${msg("section.file")}</h2>
         </div>
      </div>
      <div class="browse-wrapper">
         <div class="center">
            <div id="${el}-flashuploader-div" class="browse">${msg("label.noFlash")}</div>
            <div class="label">${msg("label.browse")}</div>
         </div>
      </div>
      <div class="tip-wrapper">
         <span id="${el}-multiUploadTip-span">${msg("label.multiUploadTip")}</span>
         <span id="${el}-singleUpdateTip-span">${msg("label.singleUpdateTip")}</span>
      </div>

      <div id="${el}-filelist-table" class="fileUpload-filelist-table"></div>

      <div class="status-wrapper">
         <span id="${el}-status-span" class="status"></span>
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
               <input id="${el}-minorVersion-radioButton" type="radio" name="majorVersion" checked="checked" /> ${msg("label.minorVersion")}
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;
            </div>
            <div class="yui-u">
               <input id="${el}-majorVersion-radioButton" type="radio" name="majorVersion" /> ${msg("label.majorVersion")}
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

      <!-- Templates for a file row -->
      <div style="display:none">
         <div id="${el}-left-div" class="fileupload-left-div">
            <span class="fileupload-percentage-span hidden">&nbsp;</span>
            <select class="fileupload-contentType-select <#if (contentTypes?size == 1)>hidden</#if>">
               <#if (contentTypes?size > 0)>
                  <#list contentTypes as contentType>
                     <option value="${contentType.id}">${msg(contentType.value)}</option>
                  </#list>
               </#if>
            </select>
         </div>
         <div id="${el}-center-div" class="fileupload-center-div">
            <span class="fileupload-progressSuccess-span">&nbsp;</span>
            <img src="${url.context}/res/components/images/generic-file-32.png" class="fileupload-docImage-img" alt="file" />
            <span class="fileupload-progressInfo-span"></span>
         </div>
         <div id="${el}-right-div" class="fileupload-right-div">
            <span class="fileupload-fileButton-span">
               <button class="fileupload-file-button" value="Remove">${msg("button.remove")}</button>
            </span>
         </div>
      </div>
         <div class="bdft">
            <input id="${el}-upload-button" type="button" value="${msg("button.upload")}" />
            <input id="${el}-cancelOk-button" type="button" value="${msg("button.cancel")}" />
         </div>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.RecordsFlashUpload("${el}").setMessages(
   ${messages}
);
//]]></script>
