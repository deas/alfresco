<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
   <channel>
      <title>${msg("linksrss.title")}</title>
      <link>${absurl(url.context)}/service/components/links/rss?site=${site}</link>
      <description>${msg("linksrss.description")}</description>
      <language>${lang}</language>

      <#if (items?size > 0)>
         <#list items as link>
            <item>
               <title>${link.title?html}</title>
               <link>${absurl(url.context)}/page/site/${site}/links-view?linkId=${link.name}</link>
               <description>URL:${link.url?html}</description>
            </item>
         </#list>
      <#else>
         <item><title>${msg("linksrss.noposts")}</title></item>
      </#if>
   </channel>
</rss>