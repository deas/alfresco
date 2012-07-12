<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/accept-invite.css" group="invite"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="invite"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="page-title theme-bg-color-1 theme-border-1">
         <div class="title">
            <h1 class="theme-color-3"><span>${msg("header.title")}</span></h1>
         </div>
      </div>
      
      <div class="accept-invite-body">
      <#if (!doRedirect)>
         <h1>${msg("error.acceptfailed.title")}</h1>
         <p>${msg("error.acceptfailed.text")}</p>
      <#else>
         <script type="text/javascript">//<![CDATA[
            window.location = "${url.context}/page/site-index?site=${siteShortName}";
         //]]></script>
         <h1>${msg("acceptregistered.title")}</h1>
         <p>${msg("acceptregistered.text")}</p>
         <br />
         <a href="${url.context}/page/site-index?site=${siteShortName}">${url.context}/page/site-index?site=${siteShortName}</a>
      </#if>
      </div>
   </@>
</@>

