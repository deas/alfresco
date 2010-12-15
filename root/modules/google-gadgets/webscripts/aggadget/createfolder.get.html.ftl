<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Create Folder</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
      <style type="text/css">
td
{
   font-family: Trebuchet MS, Arial, sans-serif;
   font-size: 12px;
   color: #515D6B;
}
      </style>
      
      <script type="text/javascript" src="${url.context}/scripts/ajax/mootools.v1.11.js"></script>
      
      <script type="text/javascript">
window.onload = pageLoaded;

function pageLoaded()
{
   document.getElementById("name").focus();
}

/**
 * Validate folder name field on form submit
 */
function validate()
{
   var valid = false;
   var spaceName = $("name").value;
   if (spaceName.length != 0)
   {
      if (spaceName.test(/(.*[\"\*\\\>\<\?\/\:\|]+.*)|(.*[\.]?.*[\.]+$)|(.*[ ]+$)/i))
      {
         var field = $("name");
         field.addClass("formItemError");
         field.title = "Invalid folder name, please correct before continuing.";
         field.focus();
      }
      else
      {
         valid = true;
      }
   }
   return valid;
}
      </script>
   </head>
   
   <body>
   
   <div class="main">
      <div class="titlebar">Create a Folder</div>
      <div class="dialog">
         <table width="100%">
            <form action="${url.service}" method="post" enctype="multipart/form-data" accept-charset="UTF-8" onsubmit="return validate();">
            <tr><td width="33%">Name:&nbsp;</td><td width="66%"><input name="name" id="name" maxlength="1024"/></td></tr>
            <tr><td width="33%">Title:&nbsp;</td><td width="66%"><input name="title" maxlength="1024"/></td></tr>
            <tr><td width="33%">Description:&nbsp;</td><td width="66%"><input name="desc" maxlength="1024"/></td></tr>
            <tr>
               <td colspan="2">
                  <input type="submit" name="submit" value="OK" style="font-weight:bold;width:60px"/>
                  <input type="button" name="cancel" value="Cancel" style="font-weight:bold;width:60px;margin-left:12px;" onclick="javascript:history.back();"/>
               </td>
            </tr>
            <input type="hidden" name="fdrnodeid" value="${args.fdrnodeid}"/>
            <input type="hidden" name="returl" value="${args.returl}"/>
            </form>
         </table>
      </div>
   </div>
   
   </body>
</html>