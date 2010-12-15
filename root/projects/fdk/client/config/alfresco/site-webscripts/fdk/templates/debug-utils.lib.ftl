<#assign indentMargin=16 />

<#macro dumpValue value>
<#escape x as jsonUtils.encodeJSONString(x)>
<#if value?is_date>"${xmldate(value)}"
<#elseif value?is_boolean>${value?string}
<#elseif value?is_number>${value?c}
<#else>"${value?js_string}"
</#if>
</#escape>
</#macro>

<#macro dumpSequence sequence>
<#escape x as jsonUtils.encodeJSONString(x)>
<div>[</div>
<#list sequence as e>
   <div>
   <#if val?is_sequence><@dumpSequence sequence=val />
   <#elseif val?is_hash><@dumpHash hash=val />
   <#else><@dumpValue value=val />
   </#if><#if e_has_next>,</#if>
   </div>
</#list>
<div>]</div>
</#escape>
</#macro>

<#macro dumpHash hash indentLevel=0>
<#escape x as jsonUtils.encodeJSONString(x)>
<#local first = true>
<div style="margin-left: ${indentLevel*indentMargin}px">{</div>
<#list hash?keys as key>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">
   ${key?js_string}:
   <#if hash[key]??>
      <#local val = hash[key]>
      <#if val?is_sequence><@dumpSequence sequence=val />
      <#elseif val?is_hash><@dumpHash hash=val />
      <#else><@dumpValue value=val />
      </#if>
   <#else>
   null
   </#if>
   <#if key_has_next>,</#if>
   </div>
</#list>
<div style="margin-left: ${indentLevel*indentMargin}px">}</div>
</#escape>
</#macro>

<#macro dumpHashKeys hash>
<#escape x as jsonUtils.encodeJSONString(x)>
<#local first = true>
<#list hash?keys as key><#if !first>, <#else><#local first = false></#if>${key?js_string}</#list>
</#escape>
</#macro>

<#macro dumpSet set indentLevel=1 last=false>
<#escape x as jsonUtils.encodeJSONString(x)>
   <div style="margin-left: ${indentLevel*indentMargin}px">{</div>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">kind: "${set.kind?js_string}",</div>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">id: "${set.id?js_string}",</div>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">label: "${set.label?js_string}",</div>
   <#if set.appearance??><div style="margin-left: ${(indentLevel+1)*indentMargin}px">appearance: "${set.appearance?js_string}",</div></#if>
   <#if set.template??><div style="margin-left: ${(indentLevel+1)*indentMargin}px">template: "${set.template?js_string}",</div></#if>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">
      children:
      <div>[</div>
      <#list set.children as element>
         <#if element.kind == "set">
            <@dumpSet set=element indentLevel=indentLevel last=!element_has_next />
         <#else>
            <div style="margin-left: ${indentLevel*indentMargin}px">{ kind: "${element.kind?js_string}", id: "${element.id?js_string}" }<#if element_has_next>,</#if></div>
         </#if>
      </#list>
      <div>]</div>
   </div>
   <div style="margin-left: ${indentLevel*indentMargin}px">}<#if !last>,</#if></div>
</#escape>
</#macro>

<#macro dumpConstraint constraint indentLevel=1 last=false>
<#escape x as jsonUtils.encodeJSONString(x)>
   <div style="margin-left: ${indentLevel*indentMargin}px">{</div>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">fieldId: "${constraint.fieldId?js_string}",</div>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">id: "${constraint.id?js_string}",</div>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">validationHandler: "${constraint.validationHandler?js_string}",</div>
   <#if constraint.event??><div style="margin-left: ${(indentLevel+1)*indentMargin}px">event: "${constraint.event?js_string}",</div></#if>
   <#if constraint.message??><div style="margin-left: ${(indentLevel+1)*indentMargin}px">message: "${constraint.message?js_string}",</div></#if>
   <div style="margin-left: ${(indentLevel+1)*indentMargin}px">params: "${constraint.params?js_string}"</div>
   <div style="margin-left: ${indentLevel*indentMargin}px">}<#if !last>,</#if></div>
</#escape>
</#macro>

<#macro dumpField field indentLevel=1 last=false>
<#escape x as jsonUtils.encodeJSONString(x)>
<div style="margin-left: ${indentLevel*indentMargin}px">{</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">id: "${field.id?js_string}",</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">kind: "${field.kind?js_string}",</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">name: "${field.name?js_string}",</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">configName: "${field.configName?js_string}",</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">type: "${field.type?js_string}",</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">label: "${field.label?js_string}",</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">value: <@dumpValue value=field.value />,</div>
<#if field.description??><div style="margin-left: ${(indentLevel+1)*indentMargin}px">field.description = "${field.description?js_string}",</div></#if>
<#if field.help??><div style="margin-left: ${(indentLevel+1)*indentMargin}px">field.help = "${field.help?js_string}",</div></#if>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">dataKeyName: "${field.dataKeyName?js_string}",</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">mandatory: ${field.mandatory?string},</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">disabled: ${field.disabled?string},</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">repeating: ${field.repeating?string},</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">transitory: ${field.transitory?string},</div>
<#if field.endpointDirection??><div style="margin-left: ${(indentLevel+1)*indentMargin}px">field.endpointDirection = "${field.endpointDirection?js_string}",</div></#if>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">control:</div>
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">{</div>
<div style="margin-left: ${(indentLevel+2)*indentMargin}px">template: "${field.control.template?js_string}",</div>
<div style="margin-left: ${(indentLevel+2)*indentMargin}px">params:</div>
<@dumpHash hash=field.control.params indentLevel=(indentLevel+2) />
<div style="margin-left: ${(indentLevel+1)*indentMargin}px">}</div>
<div style="margin-left: ${indentLevel*indentMargin}px">}<#if !last>,</#if></div>
</#escape>
</#macro>