(  
  function(){
    Mobile.util.Button  = (Mobile.util.Button) || {};
    Mobile.util.Button = function Button(config)
    {
      Mobile.util.Button.superclass.constructor.call(this,config);
    };
    Mobile.util.Button = Mobile.util.extend(Mobile.util.Button,Mobile.util.UIControl,{
      init : function init()
      {        
        return this;
      },
      destroy : function destroy()
      {
        this.elements.first().removeEventListener(this.onClick);
      }
    });
  }()
);
(
  function()
  {
    Mobile.util.TabPanel  = (Mobile.util.TabPanel) || {};
    Mobile.util.TabPanel = function TabPanel(config)
    {
      Mobile.util.TabPanel.superclass.constructor.call(this,config);
    };
    Mobile.util.TabPanel = Mobile.util.extend(Mobile.util.TabPanel,Mobile.util.UIControl,{
      init : function()
      {
        //this.config.el should reference to .tabs element
        this.elements = x$(this.config.el);
        if (!(this.elements.is('.tabs')))
        {
          this.config.el = this.elements.find('.tabs').first();
          this.elements = x$(this.config.el);
        };
            
        //this.elements is .tabs element
        var that = this;
        this.elements.on('click',function(e)
        {
          if (e.srcElement.nodeName.toUpperCase()==='A' && e.srcElement.className.indexOf('button')!=-1)
          {
            e.preventDefault();
            that.onClick(e.srcElement);
          }
        });
        //get active elements as specifed by css class in DOM.
        this.activeEls = x$(this.config.el).find('.active');

        //override if a hash is passed in url
        var hash = window.location.hash;
        if (hash!='')
        {
           var tabLinks = x$('.tablinks').find('.button');
           tabLinks.each(function(el)
              {
               if (el.href.indexOf(hash)!=-1)
               {
                  if (x$(hash)) {
                     this.activeEls = x$(el,x$(hash).first());
                     that.onClick(this.activeEls.elements[0]);
                  }
               }
              }
           );
        }; 
      },
      onClick : function(el)
      {
        if (x$(el).is('.button'))
        {
          var href =  el.href;
          if (href.indexOf('#')!==-1)
          { 
            var contentId = href.split('#')[1];
            var contentEl = x$('#'+contentId);

            if (contentEl.elements.length>0)
            {
              this.activeEls.removeClass('active');
              this.activeEls = x$(el,contentEl.first()).addClass('active');
            }
          }
        }
      }
    });
 }()
);
Mobile.util.TabPanel.BEHAVIOUR_NAME = 'tabs';
window.addEventListener('DOMContentLoaded',function(){
App.registerBehaviour(Mobile.util.TabPanel.BEHAVIOUR_NAME,function(rootNode)
   {
      x$(rootNode).find('.tabs').each(function(el)
          {
            var tb = new Mobile.util.TabPanel({el:el,id:el.id}).init();
          }
      );
   });
});