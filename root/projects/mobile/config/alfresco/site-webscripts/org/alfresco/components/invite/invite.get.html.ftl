<#include "../../utils.ftl" />
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${msg('label.inviteUser')}" parentTitle="${page.url.args.site}" />
      <div class="content">
        <div class="tabs">
            <ul class="tablinks">
              <li><a href="#ShareUser" class="button active">${msg('label.shareUser')}</a></li>
              <li><a href="#ExternalUser" class="button">${msg('label.externalUser')}</a></li>
            </ul> 
            <div class="tabcontent">
              <div id="ShareUser" class="active">
                <form action="${url.context}/p/home" method="post">
                  <label for="name" class="typeAheadLabel">${msg('label.name')}:</label>
                  <input type="text" value="" id="name" class="typeAhead display"/>
                  <label for="role">${msg('label.role')}:</label>
                    <select name="role" id="role" size="1" class="typeAheadTrigger">
                      <option value="SiteManager">${msg('label.manager')}</option>
                      <option value="SiteCollaborator">${msg('label.collaborator')}</option>
                      <option value="SiteContributer">${msg('label.contributer')}</option>
                      <option value="SiteConsumer">${msg('label.consumer')}</option>
                    </select>
                  <div>
                    <input type="hidden" value="${site}" name="site" /> 
                    <input type="submit" value="${msg('label.invite')}" class="button actionBut"/>
                    <#--  Changed to used same action as back button
                    <input type="button" onclick="<#if (backButton??)>${backButton}</#if>" value="${msg('label.cancel')}" class="button"/>
                    -->
                    <#if (backButton??)>
                         <a class="back button">${msg('label.cancel')}</a>
                    </#if>
                  </div>
                </form>               
              </div>
              <div id="ExternalUser">
                <form action="${url.context}/p/home" method="post">
                  <label for="firstName">${msg('label.firstName')}:</label>
                  <input type="text" id="firstName" name="firstName" value=""/>
                  <label for="lastName">${msg('label.lastName')}:</label>
                  <input type="text" id="lastName" name="lastName" value=""/>
                  <label for="email">${msg('label.email')}:</label>
                  <input type="text" id="email" name="email" value=""/>                  
                  <label for="role">${msg('label.role')}:</label>
                    <select name="role" id="role" size="1">
                      <option value="SiteManager">${msg('label.manager')}</option>
                      <option value="SiteCollaborator">${msg('label.collaborator')}</option>
                      <option value="SiteContributer">${msg('label.contributer')}</option>
                      <option value="SiteConsumer">${msg('label.consumer')}</option>                      
                    </select>

                  <div>
                    <input type="hidden" value="${site}" name="site" /> 
                    <input type="text" id="name" name="name" value="" class="typeAheadValue"/>
                    <input type="submit" value="${msg('label.invite')}" class="button actionBut"/>
                    <#-- Changed to used same action as back button
                    <input type="button" value="${msg('label.cancel')}" class="button"/>
                    -->
                    <#if (backButton??)>
                         <a class="back button">${msg('label.cancel')}</a>
                    </#if>
                  </div>
                </form>
              </div>
            </div>
          </div>
      </div>
   </div>
</div>