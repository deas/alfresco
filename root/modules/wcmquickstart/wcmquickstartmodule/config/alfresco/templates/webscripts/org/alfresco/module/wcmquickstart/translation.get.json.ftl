<#escape x as jsonUtils.encodeJSONString(x)>
{
   data: {
      "locales" : [
         <#list locales as locale>
            { 
              "id":"${locale}",
              "name":"${message('content_filter_lang.'+locale)}" 
            }<#if locale_has_next>,</#if>
         </#list>
      ],
      "translations": {
         <#list translations?keys as locale>
            "${locale}": "${translations[locale]}",
            <#if locale_has_next>,</#if>
         </#list>
      },
      "parents": {
         <#list translationParents?keys as locale>
            "${locale}": "${translationParents[locale]}",
            <#if locale_has_next>,</#if>
         </#list>
      }
   }
}
</#escape>
