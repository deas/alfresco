<#if page.title??>
    <title>${webSite.title!context.page.id} - ${msg(page.title)!page.title}</title>
<#elseif asset?? && asset.name != 'index.html'>
    <title>${webSite.title!context.page.id} - ${asset.title!asset.name}</title>
    <#if asset.description??>
        <meta name="description" content="${asset.description}"/>
    </#if>
<#elseif section?? && section.id != webSite.id>
    <title>${webSite.title!context.page.id} - ${section.title!section.name}</title>
    <#if section.description??>
        <meta name="description" content="${section.description}"/>
    </#if>
<#else>
    <title>${webSite.title!context.page.id}</title>
    <#if webSite.description??>
        <meta name="description" content="${webSite.description}"/>
    </#if>
</#if>
