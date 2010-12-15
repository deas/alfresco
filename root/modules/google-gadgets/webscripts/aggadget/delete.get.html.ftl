<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Delete Item</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
   </head>
   
   <body>
   
   <div class="main">
      <div class="titlebar">Confirm Deletion</div>
      <div class="dialog">
         <div style="padding-bottom:6px">Are you sure you want to delete the item '${args.name}'?</div>
         <form action="${url.service}" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
            <input type="hidden" name="name" value="${args.name}"/>
            <input type="hidden" name="id" value="${args.id}"/>
            <input type="hidden" name="returl" value="${args.returl}"/>
            <input type="submit" name="submit" value="OK" style="font-weight:bold;width:60px"/>
            <input type="button" name="cancel" value="Cancel" style="font-weight:bold;width:60px;margin-left:12px;" onclick="javascript:history.back();"/>
         </form>
      </div>
   </div>
   
   </body>
</html>