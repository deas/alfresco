<div id="appBar">
   <a id="homeButton" class="" href="${url.context}/p/home"><img src="${url.context}/themes/${theme}/images/alfresco-logo2.png" /></a>
   <form action="${url.context}/p/search" id="Search" title="${msg("label.search")}" class="searchform">
      <label for="term"><input type="search" name="term" value="${page.url.args.term!""}" id="term" placeholder="${msg("label.search")}"/></label>
   </form>
</div>