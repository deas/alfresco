<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <div class="toolbar">
         <h1>${msg('label.task')}</h1>
         <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
         </#if>
         <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>
         </#if>
      </div>
      <div class="content">   
         <#-- <ul class="rr info">
                      <li>Task Description</li>
                      <li>${msg('label.dueToday')}</li>
                  </ul>
                  <ul class="rr details">
                      <li e><a href="static/testfiles/test.pdf">Press-release.pdf</a></li>
                  </ul>

                  <form action="tasks.html">
                  <h3><label for="comment">${msg('Comment')}:</label></h3>
                    <input type="text" id="comment" name="comment" value=""/>
                    <div>
                      <input type="submit" value="${msg('label.approve')}" class="button">            
                      <input type="button" value="${msg('label.reject')}" class="button">
                    </div>
                  </form> -->
      </div>
   </div>
</div>