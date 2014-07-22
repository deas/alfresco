<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Index of Failed Web Scripts</@wsLib.head>
  <body>
   <div>
   <@wsLib.indexheader size=failures?size>Index of Failed Web Scripts</@wsLib.indexheader>
   <br>
   <@wsLib.home/>
   <br>  
    <table>
      <tr><td>Path</td><td>Failure</td></tr>
      <#list failures?keys as path>
        <tr><td>${path}</td><td>${failures[path]}</td></tr>
      </#list>
    </table>
    </div>
  </body>
</html>