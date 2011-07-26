<div id="${args.htmlid}-dialog" class="manage-tags">
   <div id="${args.htmlid}-dialogTitle" class="hd">${msg("label.title")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.newName")}</label>:</div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="name" tabindex="0" value="${tagName}"/>&nbsp;*</div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
        </form>   
   </div>
</div>