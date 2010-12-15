(
  function(){
    Mobile.util.TypeAhead  = (Mobile.util.TypeAhead) || {};
    Mobile.util.TypeAhead = function TypeAhead(config)
    {
      Mobile.util.TypeAhead.superclass.constructor.call(this,config);
    };
    // Mobile.util.TypeAhead = Mobile.util.extend(Mobile.util.TypeAhead,Mobile.util.UIControl,{
    //       /**
    //        * Initialises the typeahead component 
    //        */
    //       init : function init()
    //       {               
    //         x$('body').bottom('<div id="'+this.config.id+'" class="typeahead"><div class="header"><input type="text" class="searchField" name="term" value="" id="term" placeholder="Type here"><div id="sw-cancel">Cancel</div></div><div class="wrapper"><ul class="scrollContent"></ul></div></div>');
    //         this.elements = x$('#'+this.config.id);
    //         this.els = [];
    //         this.els['typeAhead']=x$('#'+this.config.id);
    //         this.els['header'] = x$('#'+this.config.id + ' .header');
    //         this.els['content'] = x$('#'+this.config.id + ' .scrollContent');
    //         this.els['searchField'] = x$('#'+this.config.id + ' .searchField');
    //         x$('#sw-cancel').on('click',function(e) 
    //          { 
    //            that.onCancel(e);
    //          }
    //         );
    //         //this.bind() not working correctly
    //         var that = this;
    //         this.els['searchField'].on('focus',function(e) 
    //          { 
    //            that.onFocus(e);
    //          }
    //         );
    //         // this.els['searchField'].on('blur',function(e) 
    //         //  { 
    //         //    that.onBlur(e);
    //         //  }
    //         // );
    //            
    //         this.els['searchField'].on('keyup',function(e) 
    //          { 
    //             that.onKeyUp(e);
    //          }
    //         );
    //         this.els['content'].on('click',function(e) 
    //          { 
    //            that.onClick(e);
    //          }
    //         );
    //         this.createEvent('selected');
    //         this.dataSource = new this.config.dataSource(
    //           {
    //             id:this.els['searchField'].first(),
    //             dataUri : this.config.dataUri
    //           }
    //         );
    //         this.selectedItem = null;
    //         this.data = [];
    //         this.currentSearch = '';
    //
    //         this.scroller = new Mobile.thirdparty.iScroll(this.els['content'].first());
    // 
    //         return this;
    //       },
    //       destroy : function destroy()
    //       {
    //         this.els.first().removeEventListener(this.onClick);
    //       },
    //       activate : function activate()
    //       {
    //         // App.hideBrowserNavBar(350);
    // 
    //         Mobile.util.TypeAhead.superclass.activate.call(this);
    //         //explicit display as superclass call not working?
    //         this.elements.first().style.display='block';
    //         this.disableScrolling();
    //         setTimeout(function(){ window.scrollTo(0,43)},500);
    //         return this;
    //       },
    //       deactivate : function activate()
    //       {
    //         
    //         Mobile.util.TypeAhead.superclass.deactivate.call(this);
    //         //explicit display as superclass call not working?
    //         this.elements.first().style.display='none';
    //         this.enableScrolling();
    //         return this;
    //       },
    //       onFocus : function onFocus(e) 
    //       {
    //         App.hideBrowserNavBar(350);
    //         this.disableScrolling();
    //       },
    //       onBlur : function onBlur(e) 
    //       {
    //         // App.hideBrowserNavBar();
    //         this.enableScrolling();
    //       },
    //       onKeyUp : function onKeyUp(e)
    //       {
    //         var text = e.target.value;
    //         if (text.length>=this.config.charThreshold)
    //         {
    //           this.search(text);
    //         }
    //       },
    //       onSuccess : function onSuccess(data)
    //       {
    //         var data = eval ('(' + data + ')');
    //         var htmlTemplate = '<li><p><a href="{url}" class="{userName}"><strong>{firstName} {lastName}</strong></a>{jobtitle},{organization}</p></li>';
    //         var html = '';
    //         this.data = [];
    //         if (data.people.length>0)
    //         {
    //           var ppl = data.people;
    //           for (var i=0,len = ppl.length;i<len;i++)
    //           {
    //             var p = ppl[i];
    //             this.data[p.url] = p;
    //             html+=Mobile.util.substitute(htmlTemplate,p).s;
    //           }
    //         }
    //         this.els['content'].inner(html);        
    //       },
    //       onCancel : function onCancel(e)
    //       {
    //          this.deactivate();
    //       },
    //       onClick : function onClick(e)
    //       {
    //         var targetEl = e.srcElement;
    //         e.preventDefault();
    //         if (targetEl.nodeName.toUpperCase()=='LI')
    //         {
    //           targetEl = targetEl.getElementsByTagName('P')[0];
    //         }
    //         else if (targetEl.nodeName.toUpperCase()!=='P')
    //         {
    //           while(targetEl.nodeName.toUpperCase()!='P')
    //           {
    //              targetEl = targetEl.parentNode;
    //           }
    //         }
    //         targetEl = targetEl.getElementsByTagName('A')[0];
    // 
    //         this.selectedItem = this.data['/'+targetEl.href.split('/').slice(3).join('/')];
    //         this.enableScrolling();
    //         this.fireEvent('selected');
    //         
    //       },
    //       getSelectedItem : function getSelectedItem()
    //       {
    //         return this.selectedItem;
    //       },
    //       getItem : function getItem(key)
    //       {
    //         return this.data[key] || false;
    //       },      
    //       search : function search(text)
    //       {
    //         if (this.currentSearch!==text)
    //         {
    //           this.currentSearch=text;
    //           var that = this;
    //           this.dataSource.find(this.config.requestCallback(this.currentSearch), function(e){
    //             if (e.srcElement.readyState === 4)
    //             {
    //               that.onSuccess(e.srcElement.responseText);
    //             }
    //           });
    //         }
    //       },
    //       disableScrolling : function disableScroll() 
    //       {
    //         document.addEventListener('touchmove', this.disableTouchScroll, false);
    //       },
    //       enableScrolling : function enableTouchScroll()
    //       {
    //         document.removeEventListener('touchmove', this.disableTouchScroll, false);
    //       },
    //       disableTouchScroll : function disableTouchScroll(e)
    //       {
    //         e.preventDefault();
    //         return false;
    //       }
    //     });
    //     Mobile.util.TypeAhead.BEHAVIOUR_NAME = 'typeAhead';
    //   }()
    // );
    
Mobile.util.TypeAhead = Mobile.util.extend(Mobile.util.TypeAhead,Mobile.util.UIControl,{
      /**
       * Initialises the typeahead component 
       */
      init : function init()
      {               
        // x$('body').bottom('<div id="'+this.config.id+'" class="typeahead"><div id="sw-cancel">Cancel</div><div class="wrapper"><ul class="scrollContent"></ul></div></div>');
        var taEl = document.createElement('div');
        taEl.id=this.config.id;
        taEl.className='typeahead';
        
        taEl.innerHTML = '<div class="wrapper"><ul class="scrollContent"></ul></div><div id="sw-cancel">Cancel</div>';
        var inputEl = this.config.inputEl;
        if (inputEl == inputEl.parentNode.lastChild)
        {
           inputEl.parentNode.appendChild(taEl)
        }
        else
        {
           inputEl.parentNode.insertBefore(taEl,inputEl.nextSibling);           
        }
        this.elements = x$('#'+this.config.id);
        this.els = [];
        this.els['typeAhead']=x$('#'+this.config.id);
        // this.els['header'] = x$('#'+this.config.id + ' .header');
        this.els['content'] = x$('#'+this.config.id + ' .scrollContent');
        this.els['inputEl'] = x$('#'+inputEl.id);
        x$('#sw-cancel').on('click',function(e)
         { 
           that.onCancel(e);
         }
        );
        //this.bind() not working correctly
        var that = this;
        // this.els['searchField'].on('focus',function(e) 
        //  { 
        //    that.onFocus(e);
        //  }
        // );
           
        this.els['inputEl'].on('keyup',function(e) 
         { 
            that.onKeyUp(e);
         }
        );
        this.els['content'].on('click',function(e) 
         { 
           that.onClick(e);
         }
        );
        this.createEvent('selected');
        this.dataSource = new this.config.dataSource(
          {
            id : this.config.inputEl.id,// this.els['searchField'].first(),
            dataUri : this.config.dataUri
          }
        );
        this.selectedItem = null;
        this.data = [];
        this.currentSearch = '';
        
        this.scroller = new Mobile.thirdparty.iScroll(this.els['content'].first());

        return this;
      },
      destroy : function destroy()
      {
        this.els.first().removeEventListener(this.onClick);
      },
      activate : function activate()
      {
        // App.hideBrowserNavBar(350);
        Mobile.util.TypeAhead.superclass.activate.call(this);

        //explicit display as superclass call not working?
        this.elements.first().style.display='block';
        var inputEl = this.els['inputEl'].first();
        var yPos = inputEl.offsetTop;
        this.disableScrolling();
        x$('body').addClass('activeTypeAhead');
        inputEl.offsetTop.value='';
        this.els['content'].first().innerHTML='';

        setTimeout(function() { window.scrollTo(0,yPos+30)} ,400);  
        return this;
      },
      deactivate : function activate()
      {
        
        Mobile.util.TypeAhead.superclass.deactivate.call(this);
        //explicit display as superclass call not working?
        this.elements.first().style.display='none';
        x$('body').removeClass('activeTypeAhead');        
        this.enableScrolling();
        return this;
      },
      onFocus : function onFocus(e) 
      {
        App.hideBrowserNavBar(350);
        this.disableScrolling();
      },
      onBlur : function onBlur(e) 
      {
        // App.hideBrowserNavBar();
        this.enableScrolling();
      },
      onKeyUp : function onKeyUp(e)
      {
        var text = e.target.value;
        if (text.length>=this.config.charThreshold)
        {
          this.search(text);
        }
      },
      onSuccess : function onSuccess(data)
      {
        var data = eval ('(' + data + ')');
        //var htmlTemplate = '<li><p><a href="{url}" class="{userName}"><strong>{firstName} {lastName}</strong></a>{jobtitle},{organization}</p></li>';
        var htmlTemplate = '<li><p><a href="{url}" class="{userName}"><strong>{firstName} {lastName}</strong></a></p></li>';
        var html = '';
        this.data = [];
        if (data.people.length>0)
        {
          var ppl = data.people;
          for (var i=0,len = ppl.length;i<len;i++)
          {
            var p = ppl[i];
            this.data[p.url] = p;
            html+=Mobile.util.substitute(htmlTemplate,p).s;
          }
        }
        this.els['content'].inner(html);        
      },
      onCancel : function onCancel(e)
      {
        this.els['inputEl'].first().value='';
        this.deactivate();
      },
      onClick : function onClick(e)
      {
        var targetEl = e.srcElement;
          e.preventDefault();
          if (targetEl.nodeName.toUpperCase()=='LI')
          {
            targetEl = targetEl.getElementsByTagName('P')[0];
          }
          else if (targetEl.nodeName.toUpperCase()!=='P')
          {
            while(targetEl.nodeName.toUpperCase()!='P')
            {
               targetEl = targetEl.parentNode;
            }
          }
          targetEl = targetEl.getElementsByTagName('A')[0];

          this.selectedItem = this.data['/'+targetEl.href.split('/').slice(3).join('/')];
          this.enableScrolling();
          this.fireEvent('selected');
        
      },
      getSelectedItem : function getSelectedItem()
      {
        return this.selectedItem;
      },
      getItem : function getItem(key)
      {
        return this.data[key] || false;
      },      
      search : function search(text)
      {
        if (this.currentSearch!==text)
        {
          this.currentSearch=text;
          var that = this;
          this.dataSource.find(this.config.requestCallback(this.currentSearch), function(e){
            if (e.srcElement.readyState === 4)
            {
              that.onSuccess(e.srcElement.responseText);
            }
          });
        }
      },
      disableScrolling : function disableScroll() 
      {
        document.addEventListener('touchmove', this.disableTouchScroll, false);
      },
      enableScrolling : function enableTouchScroll()
      {
        document.removeEventListener('touchmove', this.disableTouchScroll, false);
      },
      disableTouchScroll : function disableTouchScroll(e)
      {
        e.preventDefault();
        return false;
      }
    });
    Mobile.util.TypeAhead.BEHAVIOUR_NAME = 'typeAhead';
  }()
);
window.addEventListener('DOMContentLoaded',function(){
   App.registerBehaviour(Mobile.util.TypeAhead.BEHAVIOUR_NAME,function(rootNode)
      {
         // var handler = function showTypeAhead(e)
         // {
         //    var el = e.srcElement;
         //    if (this.ta==undefined)
         //    {
         //       this.ta = new Mobile.util.TypeAhead({
         //        id : 'typeahead',
         //        dataSource : PeopleFinder,
         //        dataUri : Mobile.constants.PROXY_URI+'api/people?filter={filter}&maxResults={maxResults}',
         //        requestCallback : function requestCallback(searchTerm) {
         //          return {
         //            filter : searchTerm,
         //            maxResults : 10
         //          };
         //        },
         //        charThreshold : 1,
         //        delayTimeout : 1
         //      });
         //      this.ta.init();
         //      this.ta.render();
         //      var that = this;
         //      this.ta.on('selected',function(e,args){
         //        var userItem = that.ta.getSelectedItem();
         //        x$('.typeAhead.display').first().value = userItem.firstName + ' ' + userItem.lastName;
         //        x$('.typeAheadValue').first().value = userItem.username;
         //        that.ta.deactivate();
         //        x$('.typeAheadTrigger').first().focus();
         //      });
         //   };
         //   this.ta.activate();
         // };
         var handler = function showTypeAhead(e) {
            var el = e.srcElement;
            if (this.ta==undefined)
            {
               this.ta = new Mobile.util.TypeAhead({
                id : 'typeahead',
                dataSource : PeopleFinder,
                dataUri : Mobile.constants.PROXY_URI+'api/people?filter={filter}&maxResults={maxResults}',
                requestCallback : function requestCallback(searchTerm) {
                  return {
                    filter : searchTerm,
                    maxResults : 10
                  };
                },
                charThreshold : 1,
                delayTimeout : 1,
                inputEl : el
              });
              this.ta.init();
              this.ta.render();
              var that = this;
              this.ta.on('selected',function(e,args){
                var userItem = that.ta.getSelectedItem();
                // Changed to use username
                x$('.typeAhead.display').first().value = userItem.firstName + ' ' + userItem.lastName;
                //x$('.typeAhead.display').first().value = userItem.userName;
                x$('.typeAheadValue').first().value = userItem.userName;
                that.ta.deactivate();
                x$('.typeAheadTrigger').first().focus();
              });
           };
            // el.style.marginBottom = '480px';
            this.ta.activate();
         }
         x$('.typeAhead').on('focus',handler);
      });
   });
   
