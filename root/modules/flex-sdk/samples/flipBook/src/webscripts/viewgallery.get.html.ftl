
<#assign dataURL=absurl(url.context) + "/service/sample/gallery/" + path>

<html lang="en">
<head>

</head>

<body scroll='no'>
	<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
			id="Flexbook" width="100%" height="100%"
			codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
			<param name="movie" value="${absurl(url.context)}/service/api/path/content/workspace/SpacesStore/Company%20Home/Data%20Dictionary/SWF/Flexbook.swf?dataURL=${dataURL?url}&ticket=${ticket}" />
			<param name="quality" value="high" />
			<param name="bgcolor" value="#282828" />
			<param name="allowScriptAccess" value="sameDomain" />
			<embed src="${absurl(url.context)}/service/api/path/content/workspace/SpacesStore/Company%20Home/Data%20Dictionary/SWF/Flexbook.swf?dataURL=${dataURL?url}&ticket=${ticket}" quality="high" bgcolor="#282828"
				width="100%" height="100%" name="Flexbook" align="middle"
				play="true"
				loop="false"
				quality="high"
				allowScriptAccess="sameDomain"
				type="application/x-shockwave-flash"
				pluginspage="http://www.adobe.com/go/getflashplayer">
			</embed>
	</object>
</body>
</html>
