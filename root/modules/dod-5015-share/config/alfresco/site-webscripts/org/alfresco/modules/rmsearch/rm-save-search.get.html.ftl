<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.module.SaveSearch");
//]]></script>
<div id="${args.htmlid}-dialog" class="save-search">
   <div class="hd">${msg("header.savesearch")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" method="POST" action="" enctype="application/json">
         <input id="${args.htmlid}-query" type="hidden" name="query" value="" />
         <input id="${args.htmlid}-params" type="hidden" name="params" value="" />
         <input id="${args.htmlid}-sort" type="hidden" name="sort" value="" />
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="name" tabindex="0" maxlength="255" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-description" type="text" name="description" tabindex="0" maxlength="255" /></div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" tabindex="0"/>
            <input type="button" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
         </div>
      </form>
   </div>
</div>