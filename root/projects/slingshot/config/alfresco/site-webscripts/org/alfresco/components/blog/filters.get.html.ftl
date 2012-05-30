<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="pre">
   <#-- No pre-instantiation JavaScript required -->
</@>

<@markup id="widgets">
   <@createWidgets group="blog"/>
</@>

<@markup id="post">
   <#-- No post-instantiation JavaScript required -->
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="filter blog-filter">
         <h2>${msg("header.browseposts")}</h2>
         <ul class="filterLink">
         <#list filters as filter>
            <li><span class="${filter.id}"><a class="filter-link" href="#">${msg(filter.label)}</a></span></li>
         </#list>
         </ul>
      </div>
   </@>
</@>