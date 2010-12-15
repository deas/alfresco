<#import "/fdk/templates/debug-utils.lib.ftl" as debugLib />

<div class="form-field debug">
   <div class="viewmode-field">
      fieldHtmlId = "${fieldHtmlId?js_string}"
   </div>
   <div class="viewmode-field">
      form.mode = "${form.mode!""}"
   </div>
   <div class="viewmode-field">
      field = <@debugLib.dumpField field=field indentLevel=0 last=true />
   </div>
</div>

