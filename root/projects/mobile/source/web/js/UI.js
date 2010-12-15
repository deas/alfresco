(function() {
  Mobile.util.UIControl = (Mobile.util.UIControl) || {};
  Mobile.util.UIControl = function (config){
    this.events = [];
    this.elements = null;
    if (config)
    {
      this.config = config;
      this.config.setUpCustomEvents = this.config.setUpCustomEvents || [];
      // if (this.config.setUpCustomEvents)
      // {
        //set up around advice for default methods  and any specified methods
        //These fire custom events
        function _setupEvent(mth) {
          if (!this[mth])
          {
             return;
          }
          var capitalizedMthName = mth.slice(0,1).toUpperCase() + mth.slice(1);
          var beforeMthd = Do.BEFORE+capitalizedMthName;
          var afterMthd = Do.AFTER+capitalizedMthName;
          for (var p in (evtNames = [mth,beforeMthd,afterMthd]))
          {
             this.createEvent(evtNames[p]);
          }
          Do.around(this,mth,[
          //before
          function(){
            this.fireEvent(beforeMthd,{e:beforeMthd,target:this});
            return this;
          },
          //after
          function(){
            this.fireEvent(afterMthd,{e:afterMthd,target:this});
            return this;
          }]);
        };
        var evts = (['init','render','destroy','activate','deactivate','hide','show']).concat(this.config.setUpCustomEvents);
        for (var i=0,len=evts.length;i<len;i++)
        {
          _setupEvent.apply(this,[evts[i]]);
        };
    };
    return this;
};

  Mobile.util.UIControl.prototype = {
    init : function init()
    {
      return this;
    },

    render : function render() 
    {
      return this;
    },

    destroy : function destroy()
    {
      return this;
    },

    on : function on(e,fn,obj,scope)
    {
      if (!fn)
      {
         return this;
      }
      
      if (this.events[e])
      {
        this.events[e].subscribe(fn,obj,scope || window);
      }
      else if (this.elements)//presume native browser events eg click, touchstart
      {
        this.elements.first().addEventListener(e,function()
        {
          fn.apply(obj,arguments);
        });
      }
      return this;      
    },

    activate : function activate()
    {
      this.elements.addClass('active');
      this.elements.removeClass('deactive');
      return this;
    },
    deactivate : function deactivate()
    {
      this.elements.removeClass('active');
      this.elements.addClass('deactive');      
      return this;
    },
    show : function show()
    {
      return this;
    },

    hide : function hide()
    {
      return this;      
    },
    bind : function bind(fn)
    {
      return function() 
      {
        fn.apply(this,arguments);
      };
    },
    fireEvent : function fireEvent(e,args)
    {
       if (this.events[e])
       {
          this.events[e].fire(e,args);
       }
    },
    createEvent : function createEvent(e)
    {
       if (!this.events[e])
       {
          this.events[e] = new Mobile.util.CustomEvent(e,this);                           
       }
    }
  };
})();

