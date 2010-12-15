( 
  function(){
    Mobile.util.Panel = (Mobile.util.Panel || {});
    Mobile.util.Panel = function Panel(config){
      Mobile.util.Panel.superclass.constructor.call(this,config);
    };
    Mobile.util.Panel = Mobile.util.extend(Mobile.util.Panel,Mobile.util.UIControl,{
      init : function init() 
      {
        this.id = Mobile.util.trim(this.config.id);
        this.elements = [];
        this.events = this.events || [];
        this.panelEl = document.getElementById(this.id);

        this.renderPanelInitialState();
        this.elements = x$(this.panelEl);
        
        return this;
      },
      renderPanelInitialState : function()
      {
        // templates        
        var t = '<div id="{panelId}" class="panel loading">'+
                  '<div class="toolbar">' +
                    '<h1>{title}</h1>' +
                    '<a href="#" class="back button">{backButtonText}</a>' +
                  '</div>' +
                  '<div class="content">' +
                  '</div>' +
                '</div>';

        //create panel
        if (!this.panelEl && this.config.buttons)
        {
          var div = document.createElement('div');
          var dt = t;
          dt = dt.replace(/{panelId}/g,this.id.replace('.',''));
          dt = dt.replace(/{title}/g,this.config.title);
          dt = dt.replace(/{backButtonText}/g,this.config.buttons.backText||'back');
          div.innerHTML = dt;

          document.getElementById('container').appendChild(div.firstChild);
          this.panelEl = document.getElementById(this.id.replace('.',''));
          this.renderContent();
        };
      },
      renderContent : function renderContent(html)
      {
         var html = html || '<div style=""><img src="/mobile/themes/default/images/loading.gif"/> Loading</div>';
         var contentEl = x$('#' + this.panelEl.id +' .content');
         if (contentEl)
         {
            contentEl.first().innerHTML = html;
         } 
      },
      //called after panel is shown - should probably be renamed to something better
      render : function render()
      {
        //attempt to hide scroll bar appearing when js handled links are clicked
        //iUI has the problem too
          if (this.config.el.nodeName.toUpperCase()==='A')
          {
            //These hacks will be removed once the search bar appearing twice bug is fixed.
            var href = this.config.el.href.split('#');
            //get url
            href = (href.length>1) ? href[1] : href[0];
            if (href.indexOf('%')!=-1)
            {
               href = decodeURIComponent(href);
            }
            setTimeout(function (){window.location=href;},100);

            //Remove Panel as this is still shown sometimes when user 
            //clicks 'back' on next page
            var that = this;
            window.addEventListener('unload',function(e){
              that.destroy();
            });
          }
        // }
      },
      destroy : function destroy()
      {

        if (this.panelEl)
        {
          this.panelEl.parentNode.removeChild(this.panelEl);          
        }
       
      },
      slideOut : function(direction) {

        var currentWidth = window.innerWidth;
        //set timeout otherwise flicker occurs
        setTimeout(function(o){
            o.elements.css({
              webkitTransitionProperty: '-webkit-transform',            
              webkitTransitionDuration : '0.35s',
              webkitTransitionTimingFunction: 'ease-in-out',
              webkitTransform : "translateX("+(direction*currentWidth)+"px)"
            });
            x$('#'+o.id + ' .toolbar h1','#'+o.id + ' .toolbar .back').css({
              webkitTransitionProperty: 'opacity',
              webkitTransitionDuration : '0.1s',
              webkitTransitionTimingFunction: 'ease-in-out',
              opacity:0
            });
          },
          0,this);
          
        setTimeout(function(o){
          o.deactivate();
          o.elements.removeClass('selected');
          o.fireEvent('hide');
          },350,this);

      },
      slideIn : function() {
        var currentWidth = window.innerWidth;
        this.activate();
        //set timeout otherwise flicker occurs
        setTimeout(function(o){
            o.elements.css({
               opacity:1,
               webkitTransitionProperty: '-webkit-transform',
               webkitTransitionDuration : '0.35s',
               webkitTransitionTimingFunction: 'ease-in-out',
               webkitTransform : "translateX("+0+"px)"          
             });

            x$('#'+o.id + ' .toolbar h1','#'+o.id + ' .toolbar .back').css({
              webkitTransitionProperty: 'opacity',
              webkitTransitionDuration : '0.1s',
              webkitTransitionTimingFunction: 'ease-in-out',                          
              opacity:1
            });
          }
        ,0,this);
      

        setTimeout(function(o){
          o.activate();
          o.render();
          o.elements.addClass('selected');
          o.fireEvent('show');          
         },350,this);
      },
      show : function show() {
        this.slideIn();
      },
      /**
       * Hides panel
       * 
       * @param hide {Number} Denotes number (and direction) of panel widths to slide current panel. Normally either -1 (left) or 1 (right)
       *  
       */
      hide : function hide(direction) {
        this.slideOut(direction);
      }
    });
  }()
);
Mobile.util.Panel.BEHAVIOUR_NAME = 'panel';
Mobile.util.Panel.NAME_PREFIX = 'panel-';
window.addEventListener('DOMContentLoaded',function(){
   App.registerBehaviour(Mobile.util.Panel.BEHAVIOUR_NAME,function(rootNode)
      {
         var handler = function showPanel(e){
            
               var el = e.srcElement;
               //make sure el is a link (shouldn't it be srcElement??)
               while(el && el.nodeName.toUpperCase()!='A')
               {
                 el = el.parentNode;
               };
               if (el===null)
               {
                  return false;
               }
               //create panel if valid target
               if (x$(el).is('a.panelLink'))
               {
                  e.preventDefault();
                  var panel = App.addPanel(el.id,new Mobile.util.Panel( 
                      { 
                       el : el,
                       id:Mobile.util.Panel.NAME_PREFIX+el.id,
                       title: el.title,
                       buttons : {
                         backText: 'Back'
                       },
                       href:el.href
                     }
                   )).init();
                   App.hideBrowserNavBar();
                   App.next();               
               };
               return false;
             }; 
         // handle new panels

         var containerEl = x$(rootNode);
         // (window.Touch) ? containerEl.touchend(handler) : containerEl.click(handler);
         containerEl.click(handler);
      });
   });