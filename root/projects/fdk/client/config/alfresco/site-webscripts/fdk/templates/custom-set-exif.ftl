<#if form.fields["prop_exif_flash"]??>
   <#if form.mode == "view">
      <#list set.children as item>
         <#if item.kind != "set">
            <@renderField field=form.fields[item.id] />
         </#if>
      </#list>
   <#else>
      <div class="yui-skin-sam">
         <div class="yui-cms-accordion multiple fade fixIE exif">
         <div class="yui-cms-item yui-panel selected">
            <div class="hd">${set.label}</div>
               <div class="bd">
                  <div class="fixed">
                  <#list set.children as item>
                     <#if item.kind != "set">
                        <@renderField field=form.fields[item.id] />
                     </#if>
                  </#list>
                  </div>
               </div>
               <div class="actions">
                  <a href="#" class="accordionToggleItem">&nbsp;</a>
               </div>
            </div>
         </div>
      </div>
   </#if>
</#if>
