<#include "../../utils.ftl" />
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
    <@toolbar title="${msg('label.assignWorkflow')}" parentTitle="${page.url.args.site}" />
    <div class="content">
         <form action="${url.context}/p/documents" method="post">
           <h2>${msg('label.type')}</h2>
           <select name="type">
             <option value="wf:review">${msg('label.reviewApprove')}</option>
             <option value="wf:adhoc">${msg('label.adhoc')}</option>             
           </select>
           <h2>${msg('label.description')}</h2>
           <textarea name="description" rows="4" cols="40"></textarea>
           <h2>${msg('label.user')}</h2>
           <input type="text" value="" name="username" id="username" class="typeAhead display"/>
           <h2>${msg('label.dueDate')}</h2>
           <input name="datePicker" id="datePicker" value="${msg('label.pickDueDate')}" type="button" class="datepicker"/>
           <div>
             <input type="submit" value="${msg('label.assign')}" class="button actionBut">
             <#--   Changed to used same action as back button
             <input type="button" value="${msg('label.cancel')}" class="button">
             -->
             <#if (backButton??)>
               <a class="back button">${msg('label.cancel')}</a>
             </#if>
           </div>
           <input type="hidden" name="nodeRef" value="${page.url.args.nodeRef}"/>
           <input type="hidden" name="site" value="${page.url.args.site}"/>
           <input type="hidden" name="user" value="" id="user" class="typeAheadValue">
           <input type="hidden" name="date" value="" id="date">
         </form>
    </div>
   </div>
</div>