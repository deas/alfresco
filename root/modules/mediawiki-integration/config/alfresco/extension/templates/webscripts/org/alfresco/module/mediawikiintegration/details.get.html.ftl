<html>

<head>
	<title>MediaWiki Details</title>
</head>

<body>
<#if config?exists == true>
	<b>MediaWiki Details</b><br>	
	<br>
	<table cellpadding='2' cellspacing='2'>	
		<tr>
			<td>Name:&nbsp;&nbsp;</td>
			<td>${config.properties["mwcp:wgSitename"]?html}</td>
		</tr>		
		<tr>
			<td>Logo Image:&nbsp;&nbsp;</td>
			<td>
			<#if config.properties["mwcp:wgLogo"]?exists>			
				${config.properties["mwcp:wgLogo"]?html}
			<#else>
				default
			</#if>	
			</td>
		</tr>
	</table>
	<br>
</#if>	
	<b>MediaWiki Links</b><br>
	<br>
	<table cellpadding='2' cellspacing='2'>
		<tr><td><a href="${absurl(url.context)}/php/wiki/index.php?mediaWikiSpace=${mediawiki.nodeRef?string}&alfUser=${username?string}&alfTicket=${session.ticket?string}" target="new">MediaWiki Main Page</a></td></tr>
		<#if config?exists == true>
			<tr><td><a href="${absurl(url.context)}/command/ui/editcontentprops?noderef=${config.nodeRef?string}">Edit MediaWiki Configuration Values</a></td></tr>
		</#if>
	</table>	 	
</body>

</html>