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
         <@mimetypeOption mt="application/pdf" />
         <@mimetypeOption mt="audio/x-aiff" />
         <@mimetypeOption mt="application/acp" />
         <@mimetypeOption mt="image/x-portable-anymap" />
         <@mimetypeOption mt="image/x-dwg" />
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
         <@mimetypeOption mt="audio/x-mpeg" />
         <@mimetypeOption mt="video/mpeg" />
         <@mimetypeOption mt="video/mpeg2" />
         <@mimetypeOption mt="video/mp4" />
         <@mimetypeOption mt="video/x-ms-wma" />
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
         <@mimetypeOption mt="application/vnd.stardivision.chart" />
         <@mimetypeOption mt="application/vnd.stardivision.calc" />
         <@mimetypeOption mt="application/vnd.stardivision.draw" />
         <@mimetypeOption mt="application/vnd.stardivision.impress" />
         <@mimetypeOption mt="application/vnd.stardivision.impress-packed" />
         <@mimetypeOption mt="application/vnd.stardivision.math" />
         <@mimetypeOption mt="application/vnd.stardivision.writer" />
         <@mimetypeOption mt="application/vnd.stardivision.writer-global" />
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
   <#if mt=="text/plain">
      <#return "Plain Text">
   <#elseif mt=="image/png">
      <#return "PNG Image">
   <#elseif mt=="image/jpeg">
      <#return "JPEG Image">
   <#elseif mt=="image/gif">
      <#return "GIF Image">
   <#elseif mt=="text/csv">
      <#return "Comma Separated Values (CSV)">
   <#elseif mt=="text/html">
      <#return "HTML">
   <#elseif mt=="text/xml">
      <#return "XML">
   <#elseif mt=="application/xhtml+xml">
      <#return "XHTML">
   <#elseif mt=="application/pdf">
      <#return "Adobe PDF Document">
   <#elseif mt=="text/css">
      <#return "Style Sheet">
   <#elseif mt=="application/zip">
      <#return "ZIP">
   <#elseif mt=="application/vnd.ms-excel">
      <#return "Microsoft Excel">
   <#elseif mt=="application/vnd.ms-powerpoint">
      <#return "Microsoft PowerPoint">
   <#elseif mt=="application/msword">
      <#return "Microsoft Word">
   <#elseif mt=="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
      <#return "Microsoft Excel 2007">   
   <#elseif mt=="application/vnd.openxmlformats-officedocument.presentationml.presentation">
      <#return "Microsoft PowerPoint 2007">
   <#elseif mt=="application/vnd.openxmlformats-officedocument.wordprocessingml.document">
      <#return "Microsoft Word 2007">
   <#elseif mt=="application/vnd.ms-outlook">
      <#return "Microsoft Outlook Message">
   <#elseif mt=="text/richtext">
      <#return "Rich Text">
   <#elseif mt=="application/rtf">
      <#return "Rich Text Format">   
   <#elseif mt=="audio/x-aiff">
      <#return "AIFF Audio">
   <#elseif mt=="application/acp">
      <#return "Alfresco Content Package">
   <#elseif mt=="image/x-portable-anymap">
      <#return "Anymap Image">
   <#elseif mt=="image/x-dwg">
      <#return "AutoCAD Drawing">
   <#elseif mt=="image/x-dwt">
      <#return "AutoCAD Template">
   <#elseif mt=="audio/basic">
      <#return "Basic Audio">
   <#elseif mt=="image/bmp">
      <#return "Bitmap Image">
   <#elseif mt=="image/cgm">
      <#return "CGM Image">
   <#elseif mt=="message/rfc822">
      <#return "Email">
   <#elseif mt=="image/x-portable-graymap">
      <#return "Greymap Image">
   <#elseif mt=="application/x-gzip">
      <#return "GZIP">
   <#elseif mt=="application/x-gtar">
      <#return "GZIP Tarball">
   <#elseif mt=="application/vnd.oasis.opendocument.text-web">
      <#return "HTML Document Template">
   <#elseif mt=="text/calendar">
      <#return "iCalendar File">
   <#elseif mt=="image/ief">
      <#return "IEF Image">
   <#elseif mt=="application/java">
      <#return "Java Class">   
   <#elseif mt=="application/x-javascript">
      <#return "Java Script">   
   <#elseif mt=="image/jpeg2000">
      <#return "JPEG 2000">
   <#elseif mt=="application/x-latex">
      <#return "Latex">
   <#elseif mt=="application/x-troff-man">
      <#return "Man Page">
   <#elseif mt=="text/mediawiki">
      <#return "MediaWiki Markup">
   <#elseif mt=="audio/x-mpeg">
      <#return "MPEG Audio">
   <#elseif mt=="video/mpeg">
      <#return "MPEG Video">
   <#elseif mt=="video/mpeg2">
      <#return "MPEG2 Video">
   <#elseif mt=="video/mp4">
      <#return "MPEG4 Video">
   <#elseif mt=="video/x-ms-wma">
      <#return "MS Streaming Audio">
   <#elseif mt=="video/x-ms-asf">
      <#return "MS Streaming Video (asf)">
   <#elseif mt=="video/x-ms-wmv">
      <#return "MS Streaming Video (wmv)">
   <#elseif mt=="video/x-msvideo">
      <#return "MS Video">
   <#elseif mt=="application/octet-stream">
      <#return "Octet Stream">
   <#elseif mt=="application/vnd.oasis.opendocument.chart">
      <#return "OpenDocument Chart">
   <#elseif mt=="application/vnd.oasis.opendocument.database">
      <#return "OpenDocument Database">  
   <#elseif mt=="application/vnd.oasis.opendocument.graphics">
      <#return "OpenDocument Drawing">
   <#elseif mt=="application/vnd.oasis.opendocument.graphics-template">
      <#return "OpenDocument Drawing Template">   
   <#elseif mt=="application/vnd.oasis.opendocument.formula">
      <#return "OpenDocument Formula">
   <#elseif mt=="application/vnd.oasis.opendocument.image">
      <#return "OpenDocument Image">
   <#elseif mt=="application/vnd.oasis.opendocument.text-master">
      <#return "OpenDocument Master Document">
   <#elseif mt=="application/vnd.oasis.opendocument.presentation">
      <#return "OpenDocument Presentation">
   <#elseif mt=="application/vnd.oasis.opendocument.presentation-template">
      <#return "OpenDocument Presentation Template">
   <#elseif mt=="application/vnd.oasis.opendocument.spreadsheet">
      <#return "OpenDocument Spreadsheet">
   <#elseif mt=="application/vnd.oasis.opendocument.spreadsheet-template">
      <#return "OpenDocument Spreadsheet Template">
   <#elseif mt=="application/vnd.oasis.opendocument.text">
      <#return "OpenDocument Text (OpenOffice 2.0)">   
   <#elseif mt=="application/vnd.oasis.opendocument.text-template">
      <#return "OpenDocument Text Template">
   <#elseif mt=="application/vnd.sun.xml.calc">
      <#return "OpenOffice 1.0/StarOffice6.0 Calc 6.0">
   <#elseif mt=="application/vnd.sun.xml.draw">
      <#return "OpenOffice 1.0/StarOffice6.0 Draw 6.0">
   <#elseif mt=="application/vnd.sun.xml.impress">
      <#return "OpenOffice 1.0/StarOffice6.0 Impress 6.0">
   <#elseif mt=="application/vnd.sun.xml.writer">
      <#return "OpenOffice 1.0/StarOffice6.0 Writer 6.0">
   <#elseif mt=="image/x-portable-pixmap">
      <#return "Pixmap Image">
   <#elseif mt=="image/x-portable-bitmap">
      <#return "Portable Bitmap">
   <#elseif mt=="application/postscript">
      <#return "Postscript">
   <#elseif mt=="application/eps">
      <#return "EPS Type PostScript">
   <#elseif mt=="video/quicktime">
      <#return "Quicktime Video">
   <#elseif mt=="video/x-rad-screenplay">
      <#return "RAD Screen Display">
   <#elseif mt=="image/x-cmu-raster">
      <#return "Raster Image">
   <#elseif mt=="image/x-rgb">
      <#return "RGB Image">
   <#elseif mt=="image/svg">
      <#return "Scalable Vector Graphics Image">
   <#elseif mt=="video/x-sgi-movie">
      <#return "SGI Video">
   <#elseif mt=="application/sgml">
      <#return "SGML">
   <#elseif mt=="text/sgml">
      <#return "SGML">
   <#elseif mt=="application/x-sh">
      <#return "Shell Script">
   <#elseif mt=="application/x-shockwave-flash">
      <#return "Shockwave Flash">
   <#elseif mt=="video/x-flv">
      <#return "Flash Video">
   <#elseif mt=="application/x-fla">
      <#return "Flash Source">
   <#elseif mt=="application/vnd.stardivision.chart">
      <#return "StarChart 5.x">
   <#elseif mt=="application/vnd.stardivision.calc">
      <#return "StarCalc 5.x">
   <#elseif mt=="application/vnd.stardivision.draw">
      <#return "StarDraw 5.x">
   <#elseif mt=="application/vnd.stardivision.impress">
      <#return "StarImpress 5.x">
   <#elseif mt=="application/vnd.stardivision.impress-packed">
      <#return "StarImpress Packed 5.x">
   <#elseif mt=="application/vnd.stardivision.math">
      <#return "StarMath 5.x">
   <#elseif mt=="application/vnd.stardivision.writer">
      <#return "StarWriter 5.x">
   <#elseif mt=="application/vnd.stardivision.writer-global">
      <#return "StarWriter 5.x global">
   <#elseif mt=="text/tab-separated-values">
      <#return "Tab Separated Values">
   <#elseif mt=="application/x-tar">
      <#return "Tarball">
   <#elseif mt=="application/x-tex">
      <#return "Tex">
   <#elseif mt=="application/x-texinfo">
      <#return "Tex Info">
   <#elseif mt=="image/tiff">
      <#return "TIFF Image">
   <#elseif mt=="x-world/x-vrml">
      <#return "VRML">
   <#elseif mt=="audio/x-wav">
      <#return "WAV Audio">
   <#elseif mt=="application/wordperfect">
      <#return "WordPerfect">
   <#elseif mt=="image/x-xbitmap">
      <#return "XBitmap Image">
   <#elseif mt=="image/x-xpixmap">
      <#return "XPixmap Image">
   <#elseif mt=="image/x-xwindowdump">
      <#return "XWindow Dump">
   <#elseif mt=="application/x-compress">
      <#return "Z Compress">
   <#else>
      <#return msg("form.control.mimetype.unknown")>
   </#if>
</#function>

<#macro mimetypeOption mt>
   <option value="${mt}"<#if mimetype==mt> selected="selected"</#if>>${getMimetypeLabel("${mt}")}</option>
</#macro>
              