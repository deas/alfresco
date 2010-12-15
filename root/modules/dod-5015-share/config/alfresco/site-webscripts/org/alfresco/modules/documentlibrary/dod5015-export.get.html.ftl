<#assign el=args.htmlid>
<div id="${el}-dialog" class="export">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST" enctype="multipart/form-data" accept-charset="utf-8">
         <input id="${el}-nodeRefs" type="hidden" name="nodeRefs" value=""/>
         <input type="hidden" name="format" value="html"/>
         <input id="${el}-failureCallbackFunction" type="hidden" name="failureCallbackFunction" value=""/>
         <input id="${el}-failureCallbackScope" type="hidden" name="failureCallbackScope" value=""/>
         <div class="yui-gd">
            <div class="yui-u">${msg("label.transferFormat")}</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><input id="${el}-acp" name="transferFormat" type="radio" value="false" checked="true"/></div>
            <div class="yui-u"><label for="${el}-acp">${msg("label.acp")}</label></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><input id="${el}-zip" name="transferFormat" type="radio" value="true"/></div>
            <div class="yui-u"><label for="${el}-zip">${msg("label.zip")}</label></div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
