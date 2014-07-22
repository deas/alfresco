[#ftl]

[#macro head]
<head>
   <title>[#nested]</title> 
   <link rel="stylesheet" href="${resourceurl('/css/webscripts.css', true)}" type="text/css" />
</head>
[/#macro]

[#macro header]
<table>
   <tr>
      <td>
      	<img src="${resourceurl('/images/logo/AlfrescoLogo32.png', true)}" alt="Alfresco" />
      </td>
      <td><span class="title">[#nested]</span></td>
   </tr>
</table>
<table>
   <tr><td colspan="2">${server.edition?html} v${server.version?html} schema ${server.schema?html}</td></tr>
</table>
[/#macro]

[#macro indexheader size=-1]
[@header][#nested][/@header]
<table>
   <tr><td>[#if size == -1]${webscripts?size}[#else]${size}[/#if] Web Scripts</td></tr>
</table>
[/#macro]

[#macro onlinedoc]
<table>
    <tr><td><a href="http://wiki.alfresco.com/wiki/HTTP_API">Online documentation</a>.</td></tr>
</table>
[/#macro]

[#macro home]
<table>
   <tr><td><a href="${url.serviceContext}/index">Back to Web Scripts Home</a></td></tr>
</table>
[/#macro]

[#macro parent path pathname]
[#if path.parent?exists]
   <br>
   <table>
      <tr><td><a href="${url.serviceContext}/index/${pathname}${path.parent.path}">Up to ${pathname} ${path.parent.path}</a>
   </table>
[/#if]
[/#macro]