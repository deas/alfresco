Alfresco.RM = Alfresco.RM || {};

/**
 * RM EmailMappings component
 * 
 * @namespace Alfresco
 * @class Alfresco.RM.References
 */
(function RM_UserRights()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;

    /**
     * Alfresco Slingshot aliases
     */
   var $html = Alfresco.util.encodeHTML;

   /**
    * RM UserRights componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.RM.UserRights = function RM_UserRights_constructor(htmlId)
   {
      Alfresco.RM.UserRights.superclass.constructor.call(this, "Alfresco.RM.UserRights", htmlId, ["button", "container", "datasource", "datatable", "paginator", "json"]);
      YAHOO.Bubbling.on('UserRights_DataLoad', this.onDataLoad, this, true);
      return this;
   };
    
   YAHOO.extend(Alfresco.RM.UserRights, Alfresco.component.Base,
   {
      /**
       * Initialises event listening and custom events
       *
       * @method initEvents
       */
      initEvents: function RM_UserRights_initEvents()
      {
         Event.on(this.id,'click', this.onInteractionEvent, null, this);
         //register event
         this.registerEventHandler('click',
         [
            {
               rule: 'button#save-mappings-button',
               o:
               {
                  handler:this.onSaveMappings,
                  scope : this
               }
            }                                
         ]);
         return this;
      },
      
      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function RM_UserRights_onReady()
      {
         this.initEvents();

         this.widgets['roles'] = Dom.get('userrightsRoles');
         this.widgets['groups'] = Dom.get('userrightsGroups');         
         
         var DS = this.widgets['datasource'] = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + 'api/rma/admin/userrightsreport',
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });
         
         DS.doBeforeCallback = function doBeforeCallback(oRequest, oFullResponse, oParsedResponse, oCallback)
         {
            var responseDataUsers = oFullResponse.data.users;
            var responseDataRoles = oFullResponse.data.roles;
            var responseDataGroups = oFullResponse.data.groups;
            var data = [];

            //massage data into a format that datatable likes
            for (var key in responseDataUsers)
            {
               var userObject = responseDataUsers[key];
               userObject.name = userObject.firstName + ' ' + userObject.lastName;
               //create a label so we can use it in the roles and groups renderer
               responseDataUsers[key].label = userObject.name;
               //convert groups to use label and not name
               var grps = [];
               for (var i = 0, len = userObject.groups.length; i < len; i++)
               {
                  grps.push(responseDataGroups[userObject.groups[i]].label);
               }
               userObject.groups = grps;
               data.push(userObject);
            }

            //User Rights components needs access to roles and groups data
            YAHOO.Bubbling.fire('UserRights_DataLoad',
            {
               users: responseDataUsers,
               roles: responseDataRoles,
               groups: responseDataGroups
            });
            return (
            {
               results:data
            });
         };

         /**
          * Custom formatter to add a space between each role
          *
          * @method rolesFormatter
          */   
         YAHOO.widget.DataTable.Formatter.roles = function rolesFormatter(elLiner, oRecord, oColumn, oData)
         {
            elLiner.innerHTML = oData.join(', ');
         };

         /**
          * Custom formatter to add a space between each role
          * 
          * @method groupsFormatter
          */   
         YAHOO.widget.DataTable.Formatter.groups = function groupsFormatter(elLiner, oRecord, oColumn, oData)
         {
            elLiner.innerHTML = oData.join(', ');
         };
         
         var DT = this.widgets['datasource'] = new YAHOO.widget.DataTable("userrightsDT",
         [
            { key: "name", label: this.msg('label.name'), sortable: true, resizeable: true },
            { key: "userName", label: this.msg('label.userid'),  sortable: true, resizeable: true },
            { key: "roles", label: this.msg('label.roles'),  formatter: "roles", sortable: true, resizeable: true },
            { key: "groups", label: this.msg('label.groups'),  formatter: "groups", sortable: true, resizeable: true }
         ], DS);
      },
      
      /**
       * Renders role and groups HTML based on data fired from datasource doBeforeCallback
       * 
       * @method onDataLoad
       * @param e {object} Dom event
       * @param args {object} Event parameters
       */
      onDataLoad: function RM_UserRights_onDataLoad(e, args)
      {
         var rolesHTML = "",
            groupsHTML = "",
            templates =
            {
               list: '<dl>{data}</dl>',
               title: '<dt class={className}>{data}</dt>',
               item: '<dd class={className}>{data}</dd>',
               link: '<a href="' + Alfresco.constants.URL_PAGECONTEXT + 'console/admin-console/users#state=panel%3Dview%26userid%3D{userId}%26search%3D{userId}">{name}</a>'
            },
            users = args[1].users,
            dataKeys =
            {
               roles: args[1].roles,
               groups: args[1].groups
            };

         for (var q in dataKeys)
         {
            if (dataKeys.hasOwnProperty(q))
            {
               var html = "",
                  ii = 0;
               for (var p in dataKeys[q])
               {
                  if (dataKeys[q].hasOwnProperty(p))
                  {
                     var data = dataKeys[q][p];
                     // render dt
                     html += YAHOO.lang.substitute(templates['title'],
                     {
                        data: $html(data.label),
                        className: (ii % 2 === 0) ? 'odd' : ''
                     });
                     var items = data.users;
                     if (items.length > 0)
                     {
                        for (var i = 0, len = items.length; i < len; i++)
                        {
                           //render link
                           var link = YAHOO.lang.substitute(templates['link'],
                           {
                              name: $html(users[items[i]].label),
                              userId: $html(items[i])
                           });
                           // render dd
                           html += YAHOO.lang.substitute(templates['item'],
                           {
                              data: link,
                              className: (ii % 2 === 0) ? 'odd' : ''
                           });
                        }
                     }
                     else
                     {
                        html += YAHOO.lang.substitute(templates['item'],
                        {
                           data: Alfresco.util.message('label.no-users', 'Alfresco.RM.UserRights'),
                           className: (ii % 2 === 0) ? 'odd' : ''
                        });                  
                     }
                     ii++;
                  }
               }
               if (html !== "")
               {
                  //render to DOM
                  this.widgets[q].innerHTML = YAHOO.lang.substitute(templates['list'],
                  {
                     data: html
                  });
               }
            }
         }
      }
  });
})();