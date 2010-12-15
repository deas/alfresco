<#assign el=args.htmlid>
<div id="${el}-dialog" class="set-record-type">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-recordType">${msg("label.record-type")}</label></div>
            <div class="yui-u">
               <select id="${el}-recordType" name="added[]">
                  <#list recordTypes as recordType>
                     <option value="${recordType.name}">${msg(recordType.title)}</option>
                  </#list>
               </select>
            </div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
