<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/footer/footer.css" group="footer"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="pre">
</@>

<@markup id="widgets">
   <#-- <@createWidgets/> -->
</@>

<@markup id="post">
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign fc=config.scoped["Edition"]["footer"]>
      <div class="footer ${fc.getChildValue("css-class")!"footer-com"}">
         <span class="copyright">
            <img src="${url.context}/components/images/${fc.getChildValue("logo")!"alfresco-share-logo.png"}" alt="${fc.getChildValue("alt-text")!"Alfresco Community"}" height="27" width="212" />
            <span>${msg(fc.getChildValue("label")!"label.copyright")}</span>
         </span>
      </div>
   </@>
</@>

