Mobile.util.CustomEvent = function CustomEvent(type,context) {
  this.type = type;
  this.scope = context || window;
  this.subscribers = [];
};
/**
 *  
 */
Mobile.util.CustomEvent.prototype = {
  subscribe : function subscribe(fn,obj,overrideContext) 
  {
    if (!fn) {
      return null;
    }    
    this.subscribers.push( new Mobile.util.EventSubscriber(fn, obj, overrideContext) );
  },
  unsubscribe : function unsubscribe(fn,obj)
  {
    if (!fn) {
      return this.unsubscribeAll();
    }

    var found = false;
    for (var i=0, len=this.subscribers.length; i<len; ++i) {
        var s = this.subscribers[i];
        if (s && s.contains(fn, obj)) {
            this._delete(i);
            found = true;
        }
    }
    return found;    
  },
  unsubscribeAll : function unsubscribeAll()
  {
    var l = this.subscribers.length, i;
    for (i=l-1; i>-1; i--) {
        this._delete(i);
    }
    this.subscribers=[];
    return l;    
  },
  fire : function fire (o)
  {
    this.lastError = null;
    var errors = [],
    len=this.subscribers.length;

    if (!len) {
        return true;
    }

    var args=[].slice.call(arguments, 0), ret=true, i, rebuild=false;

    // make a copy of the subscribers so that there are
    // no index problems if one subscriber removes another.
    var subs = this.subscribers.slice();

    for (i=0; i<len; ++i) {
        var s = subs[i];
        if (!s) {
            rebuild=true;
        } else {

            var scope = s.getScope(this.scope);

            var param = null;
            if (args.length > 0) {
                param = args[0];
            }

            try {
                ret = s.fn.call(scope, param, s.obj);
            } catch(e) {
                this.lastError = e;
            } 

            if (false === ret) {
                break;
            }
        }
    }

    return (ret !== false);    
  },
  _delete : function _delete(index)
  {
    var s = this.subscribers[index];
    if (s) {
        delete s.fn;
        delete s.obj;
    }
    this.subscribers.splice(index, 1);    
  }
};

Mobile.util.EventSubscriber = function EventSubscriber(fn,obj,overrideContext)
{
  this.fn = fn;
  this.obj = obj || null;
  this.overrideContext = overrideContext;
};

Mobile.util.EventSubscriber.prototype = {
  getScope : function(defaultScope)
  {
    if (this.overrideContext) {
      if (this.overrideContext === true) 
      {
          return this.obj;
      } else 
      {
          return this.overrideContext;
      }
    }
    return defaultScope;
  },
  contains : function(fn, obj) {
    if (obj) 
    {
      return (this.fn == fn && this.obj == obj);
    } 
    else 
    {
      return (this.fn == fn);
    }
  }
};