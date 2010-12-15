<script type="text/javascript" src="${url.context}/js/Tasks.js"></script>
<script type="text/javascript" charset="utf-8">//<![CDATA[
   window.addEventListener('DOMContentLoaded',function TasksInit()
      {
         Tasks.init();
         App.initBehaviour(Mobile.util.TabPanel.BEHAVIOUR_NAME);
         App.initBehaviour(Mobile.util.TaskPanel.BEHAVIOUR_NAME,x$('#tasksPage').first());         
      }
   );
//]]></script>