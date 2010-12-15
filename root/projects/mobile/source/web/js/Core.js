Mobile.util = (Mobile.util || {});

Mobile.util.merge = function merge() {
  var o={}, a=arguments, l=a.length, i;
  for (i=0; i<l; i=i+1) {
      L.augmentObject(o, a[i], true);
  }
  return o;
};
Mobile.util.augment = function augment(r, s) {
  if (!s||!r) {
      throw new Error("Absorb failed, verify dependencies.");
  }
  var a=arguments, i, p, overrideList=a[2];
  if (overrideList && overrideList!==true) { 
      for (i=2; i<a.length; i=i+1) {
          r[a[i]] = s[a[i]];
      }
  } else {
      for (p in s) { 
          if (overrideList || !(p in r)) {
              r[p] = s[p];
          }
      }
  }
};
Mobile.util.trim = function trim(s){
    try {
        return s.replace(/^\s+|\s+$/g, "");
    } catch(e) {
        return s;
    }
};
Mobile.util.extend = function extend(subc, superc, overrides) {
    if (!superc||!subc) {
        throw new Error("extend failed, please check that " +
                        "all dependencies are included.");
    }
    var F = function() {};
    F.prototype=superc.prototype;
    subc.prototype=new F();
    subc.prototype.constructor=subc;
    subc.superclass=superc.prototype;
    if (superc.prototype.constructor == Object.prototype.constructor) {
      superc.prototype.constructor=superc;
    }

    if (overrides) 
    {
      for (i in overrides) 
      {
        subc.prototype[i]=overrides[i];  
      }
    }
    return subc;
};
/**
 * Substitutes named tokens within specified string.
 * 
 * @param {String} s String to substitute tokens within
 * @param {Object} o Object with keys containing values that should replace tokens in specified string
 * 
 * @returns {Object} Object literal with new string (s) and a flag (isFullyResolved) denoting whether all tokens in string was replaced
 */ 
Mobile.util.substitute = function substitute(s,o)
{
  var token = new RegExp('{(.*?)}','g');
  s.replace(token,function(token,key,pos,string) {
    if (o[key]!==undefined) {
      s = s.replace(token,o[key]);
    }
  });
  return {
    s:s,
    isFullyResolved : (s.match(token)==null)
  };
};
/**
 * Provides AOP style functionality (before,after,around)
 *  
 */
(Do = function() {
    var aAspects = [];
    aAspects["before"]=function(oTarget,sMethodName,fn) {
        var fOrigMethod = oTarget[sMethodName];
        
        oTarget[sMethodName] = function() {
          fn.apply(oTarget, arguments);
          return fOrigMethod.apply(oTarget, arguments);
        };
    }; 
    //after
    aAspects["after"]=function(oTarget,sMethodName,fn){
        var fOrigMethod = oTarget[sMethodName];
        oTarget[sMethodName] = function() {
            var rv = fOrigMethod.apply(oTarget, arguments);
            return fn.apply(oTarget, [rv]);
        };
    }; 
    //around
    aAspects["around"]=function(oTarget,sMethodName,aFn){
        var fOrigMethod = oTarget[sMethodName];
        oTarget[sMethodName] = function() {
              if (aFn && aFn.length==2) {
                aFn[0].apply(oTarget, arguments);
                var rv = fOrigMethod.apply(oTarget, arguments);
                return aFn[1].apply(oTarget, [rv]);
              }
              else {
                return fOrigMethod.apply(oTarget, arguments);
              }
              
            };
    };
    var advise = function(oTarget,sAspect,sMethod,fAdvice) {
      if (oTarget && sAspect && sMethod && fAdvice && aAspects[sAspect]) {
          //decorate specified method
          aAspects[sAspect](oTarget,sMethod,fAdvice);
      }
      return oTarget;
    };
    return {
      before : function after(oTarget,sMethod,fAdvice)
      {
        return advise(oTarget,Do.BEFORE,sMethod,fAdvice);
      },
      after : function after(oTarget,sMethod,fAdvice)
      {
        return advise(oTarget,Do.AFTER,sMethod,fAdvice);
      },
      around : function after(oTarget,sMethod,aAdvices)
      {
        return advise(oTarget,Do.AROUND,sMethod,aAdvices);
      }
    };
}());

Do.BEFORE = 'before';
Do.AFTER = 'after';
Do.AROUND = 'around';

/**
 * Formats a Freemarker datetime into more UI-friendly format
 *
 * @method Mobile.util.formatDate
 * @param date {string} Optional: Date as returned from data webscript. Today used if missing.
 * @param mask {string} Optional: Mask to use to override default.
 * @return {string} Date formatted for UI
 * @static
 */
Mobile.util.formatDate = function(date)
{
   try
   {
      return Mobile.thirdparty.dateFormat.apply(this, arguments);
   }
   catch(e)
   {
      return date;
   }
};

Mobile.thirdparty = (Mobile.thirdparty) || {};
/**
 * Format a date object to a user-specified mask
 * Modified to retrieve i18n strings from Mobile.messages
 *
 * Original code:
 *    Date Format 1.1
 *    (c) 2007 Steven Levithan <stevenlevithan.com>
 *    MIT license
 *    With code by Scott Trenda (Z and o flags, and enhanced brevity)
 *
 * http://blog.stevenlevithan.com/archives/date-time-format
 *
 * @method Mobile.thirdparty.dateFormat
 * @return {string}
 * @static
 */
Mobile.thirdparty.dateFormat = function()
{
   /*** dateFormat
   	Accepts a date, a mask, or a date and a mask.
   	Returns a formatted version of the given date.
   	The date defaults to the current date/time.
   	The mask defaults ``"ddd mmm d yyyy HH:MM:ss"``.
   */
   var dateFormat = function()
   {
   	var   token        = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloZ]|"[^"]*"|'[^']*'/g,
      		timezone     = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
      		timezoneClip = /[^-+\dA-Z]/g,
      		pad = function (value, length)
      		{
      			value = String(value);
      			length = parseInt(length, 10) || 2;
      			while (value.length < length)
      			{
      				value = "0" + value;
      			}
      			return value;
      		};

   	// Regexes and supporting functions are cached through closure
   	return function (date, mask)
   	{
   		// Treat the first argument as a mask if it doesn't contain any numbers
   		if (arguments.length == 1 &&
   			   (typeof date == "string" || date instanceof String) &&
   			   !/\d/.test(date))
   		{
   			mask = date;
   			date = undefined;
   		}

   		date = date ? new Date(date) : new Date();
   		if (isNaN(date))
   		{
   			throw "invalid date";
   		}

   		mask = String(this.masks[mask] || mask || this.masks["default"]);

   		var d = date.getDate(),
   			 D = date.getDay(),
   			 m = date.getMonth(),
   			 y = date.getFullYear(),
   			 H = date.getHours(),
   			 M = date.getMinutes(),
   			 s = date.getSeconds(),
   			 L = date.getMilliseconds(),
   			 o = date.getTimezoneOffset(),
   			 flags =
   			 {
   				 d:    d,
   				 dd:   pad(d),
   				 ddd:  this.i18n.dayNames[D],
   				 dddd: this.i18n.dayNames[D + 7],
   				 m:    m + 1,
   				 mm:   pad(m + 1),
   				 mmm:  this.i18n.monthNames[m],
   				 mmmm: this.i18n.monthNames[m + 12],
   				 yy:   String(y).slice(2),
   				 yyyy: y,
   				 h:    H % 12 || 12,
   				 hh:   pad(H % 12 || 12),
   				 H:    H,
   				 HH:   pad(H),
   				 M:    M,
   				 MM:   pad(M),
   				 s:    s,
   				 ss:   pad(s),
   				 l:    pad(L, 3),
   				 L:    pad(L > 99 ? Math.round(L / 10) : L),
   				 t:    H < 12 ? this.TIME_AM.charAt(0) : this.TIME_PM.charAt(0),
   				 tt:   H < 12 ? this.TIME_AM : this.TIME_PM,
   				 T:    H < 12 ? this.TIME_AM.charAt(0).toUpperCase() : this.TIME_PM.charAt(0).toUpperCase(),
   				 TT:   H < 12 ? this.TIME_AM.toUpperCase() : this.TIME_PM.toUpperCase(),
   				 Z:    (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
   				 o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4)
   			 };

   		return mask.replace(token, function ($0)
   		{
   			return ($0 in flags) ? flags[$0] : $0.slice(1, $0.length - 1);
   		});
   	};
   }();

   /**
    * Alfresco wrapper: delegate to wrapped code
    */
   return dateFormat.apply(arguments.callee, arguments);
};
/* TODO
Mobile.thirdparty.dateFormat.DAY_NAMES = (Alfresco.messages("days.medium") + "," + Alfresco.messages("days.long")).split(",");
Mobile.thirdparty.dateFormat.MONTH_NAMES = (Alfresco.messages("months.short") + "," + Alfresco.messages("months.long")).split(",");
Mobile.thirdparty.dateFormat.TIME_AM = Alfresco.messages("date-format.am");
Mobile.thirdparty.dateFormat.TIME_PM = Alfresco.messages("date-format.pm");
Mobile.thirdparty.dateFormat.masks =
{
	"default":       Alfresco.messages("date-format.default"),
	shortDate:       Alfresco.messages("date-format.shortDate"),
	mediumDate:      Alfresco.messages("date-format.mediumDate"),
	longDate:        Alfresco.messages("date-format.longDate"),
	fullDate:        Alfresco.messages("date-format.fullDate"),
	shortTime:       Alfresco.messages("date-format.shortTime"),
	mediumTime:      Alfresco.messages("date-format.mediumTime"),
	longTime:        Alfresco.messages("date-format.longTime"),
	isoDate:         "yyyy-mm-dd",
	isoTime:         "HH:MM:ss",
	isoDateTime:     "yyyy-mm-dd'T'HH:MM:ss",
	isoFullDateTime: "yyyy-mm-dd'T'HH:MM:ss.lo"
};
*/
Mobile.thirdparty.dateFormat.i18n =
{
	dayNames: Mobile.thirdparty.dateFormat.DAY_NAMES,
	monthNames: Mobile.thirdparty.dateFormat.MONTH_NAMES
};