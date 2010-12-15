<#import "/fdk/templates/debug-utils.lib.ftl" as debugLib />

<div class="form-container">
   <div class="form-fields">
      <div class="debug">
         <div class="viewmode-field">form.mode = "${form.mode?js_string}"</div>
         <div class="viewmode-field">form.method = "${form.method?js_string}"</div>
         <div class="viewmode-field">form.enctype = "${form.enctype?js_string}"</div>
         <div class="viewmode-field">form.submissionUrl = "${form.submissionUrl?js_string}"</div>
         <div class="viewmode-field">form.showCaption = ${form.showCaption?string}</div>
         <div class="viewmode-field">form.showSubmitButton = ${form.showSubmitButton?string}</div>
         <div class="viewmode-field">form.showCancelButton = ${form.showCancelButton?string}</div>
         <div class="viewmode-field">form.showResetButton = ${form.showResetButton?string}</div>
         <#if form.viewTemplate??><div class="viewmode-field">form.viewTemplate = "${form.viewTemplate?js_string}"</div></#if>
         <#if form.editTemplate??><div class="viewmode-field">form.editTemplate = "${form.editTemplate?js_string}"</div></#if>
         <#if form.createTemplate??><div class="viewmode-field">form.createTemplate = "${form.createTemplate?js_string}"</div></#if>
         <#if form.destination??><div class="viewmode-field">form.destination = "${form.destination?js_string}"</div></#if>
         <#if form.redirect??><div class="viewmode-field">form.redirect = "${form.redirect?js_string}"</div></#if>
         <div class="viewmode-field">
            form.arguments = <@debugLib.dumpHash hash=form.arguments />
         </div>
         <div class="viewmode-field">
            form.data = <@debugLib.dumpHash hash=form.data />
         </div>
         <div class="viewmode-field">
            <div>form.structure =</div>
            <div>[</div>
            <#list form.structure as set>
            <@debugLib.dumpSet set=set last=!set_has_next />
            </#list>
            <div>]</div>
         </div>
         <div class="viewmode-field">
            <div>form.constraints =</div>
            <div>[</div>
            <#list form.constraints as constraint>
            <@debugLib.dumpConstraint constraint=constraint last=!constraint_has_next />
            </#list>
            <div>]</div>
         </div>
         <div class="viewmode-field">
            <div>form.fields =</div>
            <div>{</div>
            <#list form.fields?keys as key>
               <div style="margin-left: ${debugLib.indentMargin}px">${key}:</div>
               <@debugLib.dumpField field=form.fields[key] />
            </#list>
            <div>}</div>
         </div>
         <div class="viewmode-field">args keys = <@debugLib.dumpHashKeys hash=args /></div>
         <div class="viewmode-field">context.properties keys = <@debugLib.dumpHashKeys hash=context.properties /></div>
      </div>
   </div>
</div>
