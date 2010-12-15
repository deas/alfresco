<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <div class="toolbar">
         <h1>${msg('label.tasks')}</h1>
         <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
         </#if>
         <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>
         </#if>
      </div>
      <div class="content">   
         <div id="tasksTabs" class="tabs">
           <ul class="tablinks">
             <li><a href="#Today" class="button active">${msg('label.today')}</a></li>
             <li><a href="#All" class="button">${msg('label.all')}</a></li>
             <li><a href="#Overdue" class="button">${msg('label.overdue')}</a></li>
           </ul>
           <div class="tabcontent">
             <ul id="Today" class="e2e list active">
             </ul>
             <ul id="All" class="e2e list">
             </ul>
             <ul id="Overdue" class="e2e list">
             </ul>
           </div>
         </div>
      </div>
   </div>
</div>