<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="manage-tags">
   <div id="${el}-dialogTitle" class="hd">${msg("label.title")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="post">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-name">${msg("label.newName")}</label>:</div>
            <div class="yui-u"><input id="${el}-name" type="text" name="name" tabindex="0" value="${tagName?html}"/>&nbsp;*</div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>   
   </div>
</div>