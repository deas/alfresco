<#if field.control.params.property??>
   <#-- use the supplied property to retrieve the mimetype value -->
   <#assign mimetype="">
   <#assign contentUrl=form.data["prop_" + field.control.params.property?replace(":", "_")]!"">
   <#if contentUrl?? && contentUrl != "">
      <#assign mtBegIdx=contentUrl?index_of("mimetype=")+9>
      <#assign mtEndIdx=contentUrl?index_of("|", mtBegIdx)>
      <#assign mimetype=contentUrl?substring(mtBegIdx, mtEndIdx)>
   </#if>
<#else>
   <#assign mimetype=field.value>
</#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${msg("form.control.mimetype.label")}:</span>
         <span class="viewmode-value">${getMimetypeLabel("${mimetype}")}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${msg("form.control.mimetype.label")}:</label>
      <#-- TODO: Make this control make an AJAX callback to get list of mimetypes OR use dataTypeParamters structure -->
      <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
              <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
              <#if field.control.params.style??>style="${field.control.params.style}"</#if>>
         <option value="">${msg("form.control.mimetype.unknown")}</option>
         <@mimetypeOption mt="application/x-zip" />
         <@mimetypeOption mt="application/illustrator" />
         <@mimetypeOption mt="application/x-indesign" />
         <@mimetypeOption mt="application/pdf" />
         <@mimetypeOption mt="audio/x-aiff" />
         <@mimetypeOption mt="application/acp" />
         <@mimetypeOption mt="image/x-portable-anymap" />
         <@mimetypeOption mt="image/vnd.dwg" />
         <@mimetypeOption mt="image/x-dwt" />
         <@mimetypeOption mt="audio/basic" />
         <@mimetypeOption mt="image/bmp" />
         <@mimetypeOption mt="image/cgm" />   
         <@mimetypeOption mt="message/rfc822" />
         <@mimetypeOption mt="image/gif" />
         <@mimetypeOption mt="image/x-portable-graymap" />
         <@mimetypeOption mt="application/x-gzip" />
         <@mimetypeOption mt="application/x-gtar" />
         <@mimetypeOption mt="text/csv" />
         <@mimetypeOption mt="text/html" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.text-web" />
         <@mimetypeOption mt="text/calendar" />
         <@mimetypeOption mt="image/ief" />
         <@mimetypeOption mt="application/java" />
         <@mimetypeOption mt="application/x-javascript" />
         <@mimetypeOption mt="image/jpeg2000" />
         <@mimetypeOption mt="image/jpeg" />
         <@mimetypeOption mt="application/x-latex" />
         <@mimetypeOption mt="application/x-troff-man" />
         <@mimetypeOption mt="text/mediawiki" />
         <@mimetypeOption mt="application/vnd.ms-excel" />
         <@mimetypeOption mt="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" />
         <@mimetypeOption mt="application/vnd.ms-powerpoint" />
         <@mimetypeOption mt="application/vnd.openxmlformats-officedocument.presentationml.presentation" />
         <@mimetypeOption mt="application/msword" />
         <@mimetypeOption mt="application/vnd.openxmlformats-officedocument.wordprocessingml.document" />
         <@mimetypeOption mt="application/vnd.ms-outlook" />
         <@mimetypeOption mt="application/vnd.ms-project" />
         <@mimetypeOption mt="application/vnd.apple.keynote" />
         <@mimetypeOption mt="application/vnd.apple.pages" />
         <@mimetypeOption mt="application/vnd.apple.numbers" />
         <@mimetypeOption mt="audio/x-mpeg" />
         <@mimetypeOption mt="audio/mpeg" />
         <@mimetypeOption mt="video/mpeg" />
         <@mimetypeOption mt="video/mpeg2" />
         <@mimetypeOption mt="video/mp4" />
         <@mimetypeOption mt="video/x-m4v" />
         <@mimetypeOption mt="audio/ogg" />
         <@mimetypeOption mt="video/ogg" />
         <@mimetypeOption mt="video/webm" />
         <@mimetypeOption mt="audio/x-ms-wma" />
         <@mimetypeOption mt="video/x-ms-asf" />
         <@mimetypeOption mt="video/x-ms-wmv" />
         <@mimetypeOption mt="video/x-msvideo" />
         <@mimetypeOption mt="application/octet-stream" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.chart" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.database" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.graphics" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.graphics-template" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.formula" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.image" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.text-master" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.presentation" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.presentation-template" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.spreadsheet" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.spreadsheet-template" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.text" />
         <@mimetypeOption mt="application/vnd.oasis.opendocument.text-template" />
         <@mimetypeOption mt="application/vnd.sun.xml.calc" />
         <@mimetypeOption mt="application/vnd.sun.xml.draw" />
         <@mimetypeOption mt="application/vnd.sun.xml.impress" />
         <@mimetypeOption mt="application/vnd.sun.xml.writer" />
         <@mimetypeOption mt="image/x-portable-pixmap" />
         <@mimetypeOption mt="text/plain" />
         <@mimetypeOption mt="image/png" />
         <@mimetypeOption mt="image/x-portable-bitmap" />
         <@mimetypeOption mt="application/eps" />
         <@mimetypeOption mt="application/postscript" />
         <@mimetypeOption mt="video/quicktime" />
         <@mimetypeOption mt="video/x-rad-screenplay" />
         <@mimetypeOption mt="image/x-cmu-raster" />
         <@mimetypeOption mt="image/x-rgb" />
         <@mimetypeOption mt="image/vnd.adobe.photoshop" />
         <@mimetypeOption mt="image/vnd.adobe.premiere" />
         <@mimetypeOption mt="audio/vnd.adobe.soundbooth" />
         <@mimetypeOption mt="text/richtext" />
         <@mimetypeOption mt="application/rtf" />
         <@mimetypeOption mt="image/svg" />
         <@mimetypeOption mt="video/x-sgi-movie" />
         <@mimetypeOption mt="application/sgml" />
         <@mimetypeOption mt="text/sgml" />
         <@mimetypeOption mt="application/x-sh" />
         <@mimetypeOption mt="application/x-shockwave-flash" />
         <@mimetypeOption mt="video/x-flv" />
         <@mimetypeOption mt="application/x-fla" />
         <@mimetypeOption mt="text/css" />
         <@mimetypeOption mt="text/tab-separated-values" />
         <@mimetypeOption mt="application/x-tar" />
         <@mimetypeOption mt="application/x-tex" />
         <@mimetypeOption mt="application/x-texinfo" />
         <@mimetypeOption mt="image/tiff" />
         <@mimetypeOption mt="x-world/x-vrml" />
         <@mimetypeOption mt="audio/x-wav" />
         <@mimetypeOption mt="application/wordperfect" />
         <@mimetypeOption mt="image/x-xbitmap" />
         <@mimetypeOption mt="application/xhtml+xml" />
         <@mimetypeOption mt="text/xml" />
         <@mimetypeOption mt="image/x-xpixmap" />
         <@mimetypeOption mt="image/x-xwindowdump" />
         <@mimetypeOption mt="application/x-compress" />
         <@mimetypeOption mt="application/zip" />
      </select>
   </#if>
</div>

<#function getMimetypeLabel mt>
   <#-- Share specific overrides to the repository list -->
   <#local localMimetypes = {
      "text/javascript": "JavaScript",
      "image/jpeg2000", "JPEG 2000",
      "audio/x-mpeg": "MPEG Audio (x-mpeg)"
     }>

   <#-- Combine with the Repository list (local takes precidence) -->
   <#local mimetypes = mimetypes.displaysByMimetype + localMimetypes>

   <#-- Look up the description for this type, falling back on the unknown -->
   <#local label=mimetypes[mt]!msg("form.control.mimetype.unknown")>
   <#return label>
</#function>

<#macro mimetypeOption mt>
   <option value="${mt}"<#if mimetype==mt> selected="selected"</#if>>${getMimetypeLabel("${mt}")}</option>
</#macro>
