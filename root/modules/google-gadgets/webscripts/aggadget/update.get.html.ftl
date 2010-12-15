<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Update File Content</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
      <style type="text/css">
td
{
   font-family: Trebuchet MS, Arial, sans-serif;
   font-size: 12px;
   color: #515D6B;
}
      </style>
   </head>
   
   <body>
   
   <div class="main">
      <div class="titlebar">Update Content for '${args.name}'</div>
      <div class="dialog">
         <table width="100%">
            <form action="${url.service}" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
            <tr><td width="33%">File:&nbsp;</td><td width="66%"><input type="file" name="file" size="35"/></td></tr>
            <tr>
               <td colspan="2">
                  <input type="submit" name="submit" value="Upload" style="font-weight:bold;width:60px"/>
                  <input type="button" name="cancel" value="Cancel" style="font-weight:bold;width:60px;margin-left:12px;" onclick="javascript:history.back();"/>
               </td>
            </tr>
            <input type="hidden" name="id" value="${args.id}"/>
            <input type="hidden" name="returl" value="${args.returl}"/>
            </form>
         </table>
      </div>
   </div>
   
   </body>
</html>