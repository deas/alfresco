<div id="${args.htmlid}-dialog" class="edit-listofvalues">
   <div id="${args.htmlid}-dialogTitle" class="hd">&nbsp;</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-constraintTitle">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-constraintTitle" type="text" name="constraintTitle" tabindex="0" />&nbsp;*</div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
