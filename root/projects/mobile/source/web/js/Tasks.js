(
  function()
  {

    Tasks = function Tasks(config)
    {
      Tasks.superclass.constructor.call(this,config);
    };
    Tasks = new (Mobile.util.extend(Tasks,Mobile.util.UIControl,
      {
         init : function init()
         {
           this.elements = [];
           this.elements['Today'] = x$('#Today');
           this.elements['All'] = x$('#All');
           this.elements['Overdue'] = x$('#Overdue');
           this.data = {};
           var that = this;
           for (var filter in this.elements)
           {
             this.getData(filter,this.bind(function(e)
              {
                 if (e.srcElement.readyState===4)
                 {
                    that.renderData(filter,eval('(' + e.srcElement.responseText + ')').tasks);
                 }
              }));
           };
           return this;
         },
         getData : function getData(filter,callback)
         {
           //alert("filter: " + filter);
           this.elements[filter].xhr(Mobile.constants.PROXY_URI + 'slingshot/dashlets/my-tasks?filter='+filter.toLowerCase(),{
              callback:callback
           });
         },
         renderData : function renderData(filter,data)
         {
            this.data[filter] = {};
            this.data[filter].length = data.length;
            var elRef = this.elements[filter];
            if (data.length>0)
            {
               for (var i=0,len=data.length;i<len;i++)
               {
                  var o = data[i];
                  var taskId = o.id.replace('$','-');
                  this.data[filter][taskId] = o;
                  elRef.html('bottom','<li class="tasks"><a id="'+taskId+'" href="#task" title="'+o.description+'" class="panelLink">' + o.description +'</a></li>');
               }
            }
            else {
               //remove ul and add no content element
               var p = document.createElement('p');
               p.id = elRef.first().id;
               p.className='noContent';
               p.innerHTML = 'No Tasks available';
               elRef.first().parentNode.appendChild(p);
               elRef.first().parentNode.removeChild(elRef.first());
            }
         },
         getTask : function getTask(taskId,filter)
         {
            if (this.data[filter] && this.data[filter][taskId])
            {
               return new Task(taskId,filter,this.data[filter][taskId]);
            }
            return null;
         },
         removeTask : function removeTask(taskId,filter)
         {
            var taskId = taskId.replace('$','-');
            var el = document.getElementById(taskId);
            //remove containing li
            if (el)
            {
               el.parentNode.parentNode.removeChild(el.parentNode);
               delete this.data[filter][taskId];
            }
         },
         markAsComplete : function markAsComplete(taskId,filter)
         {
            var taskId = taskId.replace('$','-');
            var el = document.getElementById(taskId);
            if (el)
            {
               el.style.textDecoration='line-through';
            }
         }
      }))();

      Task = function(id,filter,config)
      {
         this.id = id.replace('-','$');
         this.filter = filter;
         for (var p in config)
         {
            this[p] = config[p];
         }
      };
      /**
       * Compares specified task object with self. If description, startDate, nodeRef and site attributes match
       * then the two tasks are assumed to be the same.
       * @param t {Task} Task to compare
       *
       * @returns {Boolean} Whether match occurred
       */
      Task.prototype.compare = function compare(t)
      {
         if ((t.description === this.description) && (t.startDate === this.startDate))
         {
            if (t.resources.length === this.resources.length)
               {
                 var res = t.resources[0];
                 var tRes = this.resources[0];
                 if (
                    (res.nodeRef === tRes.nodeRef) &&
                    (res.location.site === tRes.location.site)
                  )
                  {
                    return true;
                  }
               }
         }
         return false;
      };
      /**
       * Performs action on task. First calls API to perform specified action on Task and then again to actually close the task.
       * Normally, user would have to explicitly close the task. Here, we do it automatically. Two Ajax calls are used as
       * performing an action on a task means the task gains a new Id, so we need to get new list of tasks after performing
       * action and then identify new id and then finally close it.
       *
       * @method performAction
       *
       * @param action {String} Task action to perform
       * @param doCloseTask {Boolean} Flag to denote whether to close task
       *
       */
      Task.prototype.performAction = function performAction(action,doCloseTask)
      {
         var url = Mobile.constants.PROXY_URI + 'api/workflow/task/end/'+ this.id + ((doCloseTask===false) ? ('/' + action) : '');
         x$('#' + this.id.replace('$','-')).xhr(url,{
            method : 'post',
            callback:
            function performActionOnTask() {
               var id = this.id;
               var filter = this.filter;
               var msg='';
               switch(action)
               {
                  case 'approve':
                     msg = 'Task Approved';
                     break;
                  case 'reject':
                     msg = 'Task Rejected';
                     break;
                  case 'default':
                  case '':
                     msg = 'Task Done';
                     break;
               }
               var task = this;
               return function(e)
               {
                  if (e.srcElement.readyState===4)
                  {
                     var response = eval('('+e.srcElement.responseText+')');
                     if (doCloseTask)
                     {
                        App.addMessage(msg);
                        Tasks.removeTask(task.oldId,filter);
                        App.previous();
                     }
                     else {
                        //get new tasks for filter and compare (to get new Id).
                        Tasks.getData(filter,function getNewTaskId(e) {
                           if (e.srcElement.readyState===4)
                           {
                            data = eval('(' + e.srcElement.responseText + ')').tasks;
                            for (var i = 0,len = data.length;i<len;i++) {
                              var d = data[i];
                              if (task.compare(d))
                              {
                                 task.oldId = task.id;
                                 task.id = d.id;
                                 // mikef test
                                 task.performAction(action,true);
                                 // task.performAction(action,false);
                                 break;
                              }
                            }
                           }
                        });
                     }
                  }
               };
            }.call(this)
         });
      };
 }()
);

(
  function(){
    Mobile.util.TaskPanel = (Mobile.util.TaskPanel) || {};
    Mobile.util.TaskPanel = function TaskPanel(config){
      Mobile.util.TaskPanel.superclass.constructor.call(this,config);
    };
    Mobile.util.TaskPanel = Mobile.util.extend(Mobile.util.TaskPanel,Mobile.util.Panel,{
      render : function render()
      {
         var taskId = this.panelEl.id.replace(Mobile.util.Panel.NAME_PREFIX,'');
         this.elements.addClass('taskPanel');
         this.task = Tasks.getTask(taskId,this.config.filter);
         if (this.task)
         {
            var dueDateFormatted = (this.getDate(this.task.dueDate).getFullYear()===9999) ? 'No due date specified' : this.task.dueDate.split(' ')[0];
            var contentHTML = '<ul class="rr info">'+
            '    <li>'+this.task.description+'</li>'+
            '    <li class="taskEvent">'+dueDateFormatted+'</li>'+
            '</ul>';
            if (this.task.resources && this.task.resources.length>0)
            {
               var resource = this.task.resources[0];
               contentHTML+='<ul class="rr details">'+
               '    <li class="taskDoc"><a href="'+Mobile.constants.PROXY_URI+'api/node/content/'+ resource.nodeRef.replace(':/','')+'">'+ resource.displayName+'</a></li>'+
               '</ul>';
            };
            contentHTML += '<form id="taskForm">'+
            '<h3><label for="comment">Comment:</label></h3>'+
            '  <input type="text" id="comment" name="comment" value=""/>'+
            '  <div>';
            for (var i = 0,len = this.task.transitions.length;i<len;i++)
            {
               var trans = this.task.transitions[i];
               //assign correct class name; assumption here is that action button is always first (ie left).
               var className = (i==0) ? 'button actionBut' : 'button';
               contentHTML += '<input type="submit" value="' + trans.label + '" id="' + trans.id + '" name="' + trans.id + '" class="' + className + '">';
            }
            contentHTML += '  </div></form>';

            this.elements.removeClass('loading');
            this.renderContent(contentHTML);
            var task = this.task;
            x$('#taskForm .button').click(function() {
                return function(e)
                {
                  task.performAction(e.srcElement.id,false);
                  e.preventDefault();
                  return false;
                };
            }());
         }
      },
      getDate : function getDate(date)
      {
         var dateElems = date.split(' ')[0].split('-');
         var d = new Date(dateElems[0],dateElems[1]-1,dateElems[2]);
         return d;
      }
    });
  }()
);

Mobile.util.TaskPanel.BEHAVIOUR_NAME = 'taskPanel';
App.registerBehaviour(Mobile.util.TaskPanel.BEHAVIOUR_NAME,function(rootNode) {
   var handler = function(e){

            var el = e.srcElement;

            if (el === document) {
               return false;
            }

            // //make sure el is a link
            // while(el && el.nodeName.toUpperCase()!='A')
            // {
            //   el = el.parentNode;
            // };
            //create panel if valid target
            if (x$(el).is('a.panelLink'))
            {
               e.preventDefault();
               var parentFilter = el.parentNode;
               while(parentFilter.nodeName.toUpperCase()!='UL')
               {
                  parentFilter = parentFilter.parentNode;
               }
               var panel = App.addPanel(el.id,new Mobile.util.TaskPanel(
                   {
                    el : el,
                    id:Mobile.util.Panel.NAME_PREFIX+el.id,
                    title:'Task',
                    buttons : {
                      backText: 'Back'
                    },
                    href:el.href,
                    filter : parentFilter.id
                  }
                )).init();
                App.hideBrowserNavBar();
                panel.on('hide',
                  function(e,p){
                     App.removePanel(p.config.id);
                  },
                  panel,
                  App
                );
                App.next();
            };
            return false;
          };
      // handle new panels

      var containerEl = x$(rootNode);
      // (window.Touch) ? containerEl.touchend(handler) : containerEl.click(handler);
      containerEl.click(handler);
});
