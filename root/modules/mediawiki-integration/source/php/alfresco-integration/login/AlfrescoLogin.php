<?php
	
// Store the title if one has been passed
$title = null;
if (isset($_REQUEST["title"]) == true)
{
	$title = $_REQUEST["title"];
}		
	
if (isset($_REQUEST["doLogin"]) == true)
{
	// Get the login details
	$username = null;
	if (isset($_REQUEST["loginForm:user-name"]) == true)
	{
		$username = $_REQUEST["loginForm:user-name"];
	}
	$password = null;
	if (isset($_REQUEST["loginForm:user-password"]) == true)
	{
		$password = $_REQUEST["loginForm:user-password"];
	}	
	
	// Set a null failure message
	$failure = null;
	
	if ($username != null && $password != null)
	{
		try
		{
			// Try and create a ticket
			$alfRepository = new Repository();
			$ticket = $alfRepository->authenticate($username, $password);
			if (isset($ticket) == true)
			{
				// Redirect to the index page with the ticket information
				$url = "/alfresco/php/wiki/index.php?alfTicket=".$ticket."&alfUser=".$username;
				if ($title != null)
				{
					$url .= "&title=".$title;
				}				
				header( "Location: ".$url);
				exit;
			}
		}
		catch (Exception $e)
		{
			// Need to indicate that the login failed
			$failure = "Login to MediaWiki failed, please try again.";
		}	
	}
}


?>

<body bgcolor="#ffffff" style="background-image: url(/alfresco/images/logo/AlfrescoFadedBG.png); background-repeat: no-repeat; background-attachment: fixed">

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<title>Alfresco MediaWiki - Login</title>
	<link rel="search" type="application/opensearchdescription+xml" href="/alfresco/wcservice/api/search/keyword/description.xml" title="Alfresco Keyword Search">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="/alfresco/css/main.css" type="text/css">
</head>

<body>

	<form id="loginForm" name="loginForm" method="post" action="<?php echo $_SERVER['PHP_SELF'] ?>" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded">
		<table width=100% height=98% align=center>
			<tr width=100% align=center>
				<td valign=middle align=center width=100%>
					<table cellspacing=0 cellpadding=0 border=0>
						<tr>
							<td width=7><img src='/alfresco/images/parts/white_01.gif' width=7 height=7 alt=''></td>
							<td background='/alfresco/images/parts/white_02.gif'><img src='/alfresco/images/parts/white_02.gif' width=7 height=7 alt=''></td>
							<td width=7><img src='/alfresco/images/parts/white_03.gif' width=7 height=7 alt=''></td>

						</tr>
						<tr>
							<td background='/alfresco/images/parts/white_04.gif'><img src='/alfresco/images/parts/white_04.gif' width=7 height=7 alt=''></td>
							<td bgcolor='white'>
<table border=0 cellspacing=4 cellpadding=2>
<tr>
<td colspan=2>
<img src='/alfresco/images/logo/AlfrescoLogo200.png' width=200 height=58 alt="Alfresco" title="Alfresco">
</td>
</tr>

<tr>
<td colspan=2>
<span class='mainSubTitle'>Enter MediaWiki Login details:</span>
</td>
</tr>

<tr>
<td>
User Name:
</td>
<td>

<input id="loginForm:user-name" name="loginForm:user-name" type="text" value="<?php echo $username ?>" style="width:150px" />
</td>
</tr>

<tr>
<td>
Password:
</td>
<td>

<input type="password" id="loginForm:user-password" name="loginForm:user-password" style="width:150px" />
</td>
</tr>

<tr>
<td>
Language:
</td>
<td>



<select id="loginForm:language" name="loginForm:language" size="1" style="width:150px" onchange="document.forms['loginForm'].submit(); return true;">	<option value="en_US" selected="selected">English</option></select>
</td>

</tr>

<tr>
<td colspan=2 align=right>
<input id="loginForm:submit" name="loginForm:submit" type="submit" value="Login" />
</td>
</tr>

<?php
if ($failure != null)
{
?>
   <tr>
      <td colspan=2>
         <span class='mainSubTitle' style="color:red"><?php echo $failure ?></span>
      </td>
   </tr>
<?php
}
?>

<tr>
<td colspan=2>


</td>
</tr>
</table>

</td><td background='/alfresco/images/parts/white_06.gif'>

<img src='/alfresco/images/parts/white_06.gif' width=7 height=7 alt=''></td></tr>
<tr><td width=7><img src='/alfresco/images/parts/white_07.gif' width=7 height=7 alt=''></td>
<td background='/alfresco/images/parts/white_08.gif'>
<img src='/alfresco/images/parts/white_08.gif' width=7 height=7 alt=''></td>
<td width=7><img src='/alfresco/images/parts/white_09.gif' width=7 height=7 alt=''></td></tr>
</table>

<div id="no-cookies" style="display:none">
<table cellpadding="0" cellspacing="0" border="0" style="padding-top:16px;">
<tr>
<td>
<table cellspacing='0' cellpadding='0' style='border-width: 0px; width: 100%'><tr><td style='width: 7px;'><img src='/alfresco/images/parts/yellowInner_01.gif' width='7' height='7' alt=''/></td><td style='background-image: url(/alfresco/images/parts/yellowInner_02.gif)'><img src='/alfresco/images/parts/yellowInner_02.gif' width='7' height='7' alt=''/></td><td style='width: 7px;'><img src='/alfresco/images/parts/yellowInner_03.gif' width='7' height='7' alt=''/></td></tr><tr><td style='background-image: url(/alfresco/images/parts/yellowInner_04.gif)'><img src='/alfresco/images/parts/yellowInner_04.gif' width='7' height='7' alt=''/></td><td style='background-color:#ffffcc;'>
<table cellpadding="0" cellspacing="0" border="0">
<tr>
<td valign=top style="padding-top:2px" width=20><img src="/alfresco/images/icons/info_icon.gif" height="16" width="16" /></td>
<td class="mainSubText">
Cookies must be enabled in your browser for the Alfresco MediaWiki Integration to function correctly.

</td>
</tr>
</table>
</td><td style='background-image: url(/alfresco/images/parts/yellowInner_06.gif)'><img src='/alfresco/images/parts/yellowInner_06.gif' width='7' height='7' alt=''/></td></tr><tr><td style='width: 7px;'><img src='/alfresco/images/parts/yellowInner_07.gif' width='7' height='7' alt=''/></td><td style='background-image: url(/alfresco/images/parts/yellowInner_08.gif)'><img src='/alfresco/images/parts/yellowInner_08.gif' width='7' height='7' alt=''/></td><td style='width: 7px;'><img src='/alfresco/images/parts/yellowInner_09.gif' width='7' height='7' alt=''/></td></tr></table>
</td>
</tr>
</table>
</div>
<script>
document.cookie="_alfTest=_alfTest"
var cookieEnabled = (document.cookie.indexOf("_alfTest") != -1);
if (cookieEnabled == false)
{
document.getElementById("no-cookies").style.display = 'inline';
}
</script>

</td>
</tr>

</table>


  <input type="hidden" name="doLogin" value="true"/> 
<?php
  if ($title != null)
  {
?>
	 <input type="hidden" name="title" value="<?php echo $title ?>"/>
<?php
  }  
?>
  
 
</form>


</body>
</html>