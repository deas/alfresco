(
function(){
    App = function App(config){
      App.superclass.constructor.call(this,config || {
         id : 'App',
         setUpCustomEvents : ['next','previous']
      });
    };
    App = new (Mobile.util.extend(App,Mobile.util.UIControl,
      {
        panelStack : [],
        currentPanelIndex : 0,
        behaviours : {},
        messages : [],
        init : function init(config)
        {
          //initialise panel for current page so it can be slid out
          this.panels = x$('.panel');
          this.panels.each(function setupPanel(el) {
            var p = new Mobile.util.Panel({
              el:el,
              id:el.id,
              title: el.title
            });
            p.init();
            App.addPanel(el.id,p);
            p.on('show',
               function(e,panel){
                  App.showMessage();
               },
               p,
               App
            );
            // p.hide(-1);
          });          

          window.addEventListener('load',function AppSetup() {
            setTimeout(function AppSetup() {
              App.hideBrowserNavBar();
              App.onOrientationChange();
            },0);
          });
          
          x$('.searchform').each(function setupSearchForm(el){

            x$(el).on('submit',function searchSubmitHandler(e)
            {
              new Mobile.util.Panel( 
              { 
               el : e.srcElement,
               id:Mobile.util.Panel.NAME_PREFIX+el.id,
               title: el.title,
               buttons : {
                 backText: 'Back'
               },
               href:el.href
              }).init();
              // e.preventDefault();
            });
          });

          x$(document).click(function(e) {
            // App.hideBrowserNavBar();
            if (x$(e.srcElement).is('.back.button'))
            {
               e.preventDefault();
               App.previous();
            }
          });
          
          document.body.addEventListener('orientationchange',this.onOrientationChange);          
          return this;          
        },
        /**
         * 
         *  
         */
        previous : function previous()
        {
          //slide right -> inactive
          if(this.currentPanelIndex>0)
          {
            this.panelStack[this.currentPanelIndex].o.hide(1);
            //this.panelStack[this.currentPanelIndex].o.destroy();
            this.currentPanelIndex = Math.max(0,(this.currentPanelIndex-1));
            //slide right -> active
            this.panelStack[this.currentPanelIndex].o.show();
          }
          else 
          {
            history.go(-1);
          }
          return this;
        },
        next : function next() 
        {
          if(this.currentPanelIndex>=0)
          {
            //slide left <- inactive
            this.panelStack[this.currentPanelIndex].o.hide(-1);
            this.currentPanelIndex = Math.min((this.panelStack.length-1),(this.currentPanelIndex+1));
            //slide left <- active
            this.panelStack[this.currentPanelIndex].o.show();
            return this;
          }
        },
        addPanel: function addPanel(name,o) 
        {
          this.panelStack.push({name:name,o:o});
          o.on('beforeHide',this.onBeforePanelHide,this);
          o.on('afterHide',this.onAfterPanelHide,this);
          o.on('beforeShow',this.onBeforePanelShow,this);
          o.on('afterShow',this.onAfterPanelShow,this);
          return o;
        },
        removePanel : function removePanel(name)
        {
          name = name.replace(Mobile.util.Panel.NAME_PREFIX,'');
          var a = [];
          var panelToRemove;
          for (var i=0,len = this.panelStack.length;i<len;i++)
          {
            var panel = this.panelStack[i];
            if (panel.name!==name)
            {
              a.push(panel);
            }
            else {
              panel.o.destroy();
            }
          }
          this.panelStack = a;
          return this;          
        },
        onAfterPanelHide : function afterPanelHide(e) {
        },
        onBeforePanelHide : function beforePanelHide(e) {
        },
        onAfterPanelShow : function afterPanelHide(e) {
        },
        onBeforePanelShow : function beforePanelShow(e) {
        },    
        onOrientationChange : function onOrientationChange(e) {
          var bodyEl = x$(document.body);
          if (window.orientation!=undefined)
          {
             (window.orientation ==0 | window.orientation==180) ? bodyEl.removeClass('landscape').addClass('portrait') : bodyEl.removeClass('portrait').addClass('landscape');             
          }

          App.hideBrowserNavBar();
        },
        hideBrowserNavBar : function hideBrowserNavBar(timeout)
        {
          var timeout = timeout || 100;
          setTimeout(function() { window.scrollTo(0, 1) }, timeout);
          return this;
        },
        registerBehaviour : function registerBehaviour(name,behaviour)
        {
           if (!this.behaviours[name])
           {
              this.behaviours[name] = behaviour;
           }
        },
        initBehaviour : function initBehaviour(name,rootNode)
        {
           if (this.behaviours[name])
           {
              this.behaviours[name](rootNode || document);
           }
        },
        addMessage : function addMessage(msg)
        {
           this.messages.push(msg);
        },
        showMessage : function showMessage()
        {
           var msg = this.messages.pop();
           if (msg)
           {
              if (!this.messageEl)
              {
                 x$('body').bottom('<div id="Message"><span>' +  msg + '</span></div>');
                 this.messageEl = x$('#Message');
              }
              else 
              {
                 this.messageEl.html('inner','<span>'+ msg +'</span>');
              }

              setTimeout(function fadeInMessage() 
              {
                 setTimeout(function () 
                 {
                  x$('#Message').css(
                     {
                        display:'inline'
                     }
                     );
                  },
                  0);
                 
                 x$('#Message').css(
                 {
                    opacity:'0',
                    webkitTransitionProperty: 'opacity',
                    webkitTransitionDuration : '1s',
                    webkitTransitionTimingFunction: 'ease-in-out',
                    opacity:'1' 
                 },
                 100);
              });
              //hide after some time
              setTimeout(function fadeOutMessage()
               {
                 x$('#Message').css(
                    {
                       opacity:'1',
                       webkitTransitionProperty: 'opacity',
                       webkitTransitionDuration : '1s',
                       webkitTransitionTimingFunction: 'ease-in-out',
                       opacity:'0'
                    }
                  );
                 setTimeout(function () 
                 {
                  x$('#Message').css(
                     {
                        display:'none'
                     }
                     );
                 },
                 1000);
               },4000);
           }
        }
      }
    ))();
  }()
);

//Behaviour to allow 'click' on any child of a list item of an edge-to-edge list
App.registerBehaviour('Edge2EdgeListAction', function setupEdge2EdgeBehaviour(rootNode)
   {
      var handler = function e2eClickHandler(e) 
      {
         var targetEl = e.srcElement;
         //if not a img, go to href of first child anchor of parent LI.
         if (targetEl.nodeName.toUpperCase()!=='IMG')
         {
            e.stopPropagation();
            while(targetEl.nodeName.toUpperCase()!='LI')
            {
               targetEl = targetEl.parentNode;
            }
            var el = x$(targetEl).find('a').first();
            if (el.href)
            {
               window.location=el.href;
            }
         }
      };
      x$(rootNode).find('ul.e2e').click(handler);      
   }
);
