<#assign el=args.htmlid?html>
<#assign aboutConfig=config.scoped["Edition"]["about"]>
<div id="${el}-dialog" class="about-share">
   <div class="bd">
      <div id="${el}-logo" class="${aboutConfig.getChildValue("css-class")!logo-com}">
         <div class="about">
            <#assign split=serverVersion?index_of(" ")>
            <div class="header">Alfresco ${serverEdition?html} v${serverVersion?substring(0, split)?html}</div>
            <div>${serverVersion?substring(split+1)?html} schema ${serverSchema?html}</div>
            <#assign split=server.version?index_of(" ")>
            <div class="header">Spring Surf and ${server.edition?html} v${server.version?substring(0, split)?html}</div>
            <div>${server.version?substring(split+1)?html}</div>
            <div class="copy padtop">&copy; 2005-2011 Alfresco Software Inc. All rights reserved.</div>
            <div class="copy">
               <a href="http://www.alfresco.com" target="new">www.alfresco.com</a>
               <a href="http://www.alfresco.com/legal/agreements/" target="new">Legal and License</a>
            </div>
         </div>
      </div>
   </div>
</div>