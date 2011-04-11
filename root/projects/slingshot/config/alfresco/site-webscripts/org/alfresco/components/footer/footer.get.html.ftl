<#assign fc=config.scoped["Edition"]["footer"]>
<div class="footer ${fc.getChildValue("css-class")!"footer-com"}">
   <span class="copyright">
      <img src="${url.context}/components/images/${fc.getChildValue("logo")!"alfresco-share-logo.png"}" alt="${fc.getChildValue("alt-text")!"Alfresco Community"}" height="27" width="212" />
      <span>${msg(fc.getChildValue("label")!"label.copyright")}</span>
   </span>
</div>
