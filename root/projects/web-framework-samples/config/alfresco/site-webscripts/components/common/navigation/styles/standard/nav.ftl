<#macro render>

	<#if orientation == "horizontal">
	  <@horizontal page=rootPage showChildren=true/>
	</#if>
	<#if orientation == "vertical">
	  <@horizontal page=rootPage showChildren=true/>
	</#if>

</#macro>

<#-- Renders a Horizontal Navigation Menu -->
<#macro horizontal page showChildren>

   <div class="navigation" style="background-color: ${backgroundColor}">
      <div class="navigation-menu" id="nav-menu">
         <div class="navigation-menu-body">
            <ul class="navigation-first-of-type">
            
               <#-- Root Page -->
               <li class='navigation-topmenuitem'>
                  <#assign href = linkbuilder.page(page.id, context.formatId)>
                  <a class='navigation-topmenuitem' href='${href}' onmouseover="this.style.cursor='hand'">
                     <#assign divClassName = 'navigation-topmenuitem'>
                     <#if page.id == context.page.id>
                        <#assign divClassName = 'navigation-topmenuitem-selected'>
                     </#if>
                     <div class='${divClassName}'>${page.title}</div>
                  </a>
               </li>
               
               <#-- Show Children of Root Page at same level -->
               <#list sitedata.findChildPages(page.id) as page>
                  <li class='navigation-topmenuitem'>
                     <#assign href = linkbuilder.page(page.id, context.formatId)>
                     <a class='navigation-topmenuitem' href='${href}' onmouseover="this.style.cursor='hand'">
                        <#assign divClassName = 'navigation-topmenuitem'>
                        <#if page.id == context.page.id>
                           <#assign divClassName = 'navigation-topmenuitem-selected'>
                        </#if>
                        <div class='${divClassName}'>${page.title}</div>
                     </a>

                     <#-- Renders Sub Pages -->
                     <#if showChildren == true>
                        <#list sitedata.findChildPages(page.id) as childPage>
                           <div class='navigation-submenu'>
                              <div class='navigation-submenu'>
                                 <ul>
                                    <li class='navigation-submenuitem'>
                                       <#assign href = linkbuilder.page(childPage.id, context.formatId)>
                                       <a class='navigation-submenuitem' href='${href}' onmouseover="this.style.cursor='hand'">
                                          <#assign divClassName = 'navigation-submenuitem'>
                                          <#if childPage.id == context.page.id>
                                             <#assign divClassName = 'navigation-submenuitem-selected'>
                                          </#if>
                                          
                                          <div class='${divClassName}'>${page.title}</div>
                                       </a>
                                    </li>
                                 </ul>
                              </div>
                           </div>
                        </#list>
                     </#if>

                  </li>                                    
               </#list>
            </ul>
         </div>
      </div>
   </div>
   
</#macro>
                

