<#if articles??>
    <#if articles.title??>
        <div class="interior-header">
            <h2 class="full-h">${articles.title}</h2>
            <#if articles.description??><p class="intheader-paragraph">${articles.description}</p></#if>
        </div>
    </#if>
    
    <div class="interior-content">
        <#if articles.assets?size == 0>
            ${msg('list.none')}
        <#else>
            <#if subTitle??><h3>${msg(subTitle)}</h3></#if>                
            <ul class="portfolio-wrapper">
                <#list articles.assets as article>      
                    <li>
                        <a href="<@makeurl asset=article force='long'/>${linkParam!''}"><img src="<@makeurl asset=article rendition='mediumPublicationThumbnail'/>" alt="${asset.title!''}" class="img-border" /></a>
                        <h3><a href="<@makeurl asset=article force='long'/>${linkParam!''}">${article.title!article.name}</a></h3>
                        <#if article.description??><p><@truncate value=article.description chars=100/></p></#if>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
</#if>