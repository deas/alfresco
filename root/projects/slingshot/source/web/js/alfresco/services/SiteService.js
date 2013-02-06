define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "dojo/request/xhr",
        "dojo/json",
        "dojo/_base/lang"],
        function(declare, AlfCore, AlfXhr, xhr, JSON, lang) {
   
   return declare([AlfCore, AlfXhr], {
      
      /**
       * Sets up the subscriptions for the SiteService
       * 
       * @constructor 
       * @param {array} args The constructor arguments.
       */
      constructor: function alf_services_SiteService__constructor(args) {
         this.alfSubscribe("ALF_GET_SITE_DETAILS", lang.hitch(this, "getSiteDetails"));
         this.alfSubscribe("ALF_BECOME_SITE_MANAGER", lang.hitch(this, "becomeSiteManager"));
         this.alfSubscribe("ALF_JOIN_SITE", lang.hitch(this, "joinSite"));
         this.alfSubscribe("ALF_LEAVE_SITE", lang.hitch(this, "leaveSite"));
         this.alfSubscribe("ALF_CREATE_SITE", lang.hitch(this, "createSite"));
         this.alfSubscribe("ALF_EDIT_SITE", lang.hitch(this, "editSite"));
         this.alfSubscribe("ALF_ADD_FAVOURITE_SITE", lang.hitch(this, "addSiteAsFavourite"));
         this.alfSubscribe("ALF_REMOVE_FAVOURITE_SITE", lang.hitch(this, "removeSiteFromFavourites"));
         
         // Make sure that the edit-site.js file is loaded. This is required for as it handles legacy site
         // editing. At some stage this will not be needed when a new edit site dialog is provided.
         var _this = this;
         require([Alfresco.constants.URL_RESCONTEXT + "modules/edit-site.js"], function() {
            _this.alfLog("log", "Edit Site JavaScript resource loaded");
         });
      },
      
      /**
       * This function makes a request to obtain the details of a specific site. Unlike the other functions
       * in this service it requires a specific callback function and scope to be provided in the request
       * as it doesn't make sense to just publish site information.
       * 
       * @method getSiteDetails
       * @param {object} config An object with the details of the site to retrieve the data for.
       */
      getSiteDetails: function alf_services_SiteService__getSiteDetails(config) {
         if (config && 
             config.site && 
             config.responseTopic)
         {
            var url = Alfresco.constants.PROXY_URI + "api/sites/" + config.site;
            this.serviceXhr({url : url,
                             method: "GET",
                             site: config.site,
                             responseTopic: config.responseTopic,
                             successCallback: this.publishSiteDetails,
                             callbackScope: this});
            
         }
         else
         {
            this.alfLog("error", "A request to get the details of a site was made, but either the 'site' or 'responseTopic' attributes was not provided", config);
         }
      },
      
      /**
       * Publishes the details of a site on the requested topic. This is called
       * 
       * @method publishSiteDetails
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      publishSiteDetails: function alf_services_SiteService__publishSiteDetails(response, originalRequestConfig) {
         if (originalRequestConfig && originalRequestConfig.responseTopic)
         {
            this.alfLog("log", "Publishing site details", originalRequestConfig);
            this.alfPublish(originalRequestConfig.responseTopic, response);
         }
         else
         {
            this.alfLog("error", "It was not possible to publish requested site details because the 'responseTopic' attribute was not set on the original request", response, originalRequestConfig);
         }
      },
      
      /**
       * Handles requesting that a site be made a favourite.
       * 
       * @method addSiteAsFavourite
       * @param {config} config The payload containing the details of the site to add to the favourites list
       */
      addSiteAsFavourite: function alf_services_SiteService__addSiteAsFavourite(config) {
        
         if (config.site && config.user)
         {
            // Set up the favourites information...
            var url = Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(config.user) + "/preferences",
                favObj = {org:{alfresco:{share:{sites:{favourites:{}}}}}};
            favObj.org.alfresco.share.sites.favourites[config.site] = true;
            this.serviceXhr({url : url,
                             site: config.site,
                             user: config.user,
                             data: favObj,
                             method: "POST",
                             successCallback: this.favouriteSiteAdded,
                             callbackScope: this});
         }
         else
         {
            // Handle error conditions...
            this.alfLog("error", "A request to make a site a favourite but either the site or user was not specified", config);
         }
      },
      
      /**
       * This handles successfully completed requests to remove a site from the favourites list for a user. It publishes the
       * details on "ALF_FAVOURITE_SITE_ADDED" topic.
       * 
       * @method favouriteSiteAdded
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      favouriteSiteAdded: function alf_services_SiteService__favouriteSiteAdded(response, originalRequestConfig) {
         this.alfLog("log", "Favourite Site Added Successfully", response, originalRequestConfig);
         this.alfPublish("ALF_FAVOURITE_SITE_ADDED", { site: originalRequestConfig.site, user: originalRequestConfig.user});
      },
     
      
      /**
       * Handles requesting that a site be removed from favourites
       * 
       * @method removeSiteFromFavourites
       * @param {config} config The payload containing the details of the site to remove from the favourites list
       */
      removeSiteFromFavourites: function alf_services_SiteService__removeSiteFromFavourites(config) {
         if (config.site && config.user)
         {
            // Set up the favourites information...
            var url = Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(config.user) + "/preferences?pf=org.alfresco.share.sites.favourites." + config.site;
            this.serviceXhr({url : url,
                             site: config.site,
                             user: config.user,
                             method: "DELETE",
                             successCallback: this.favouriteSiteRemoved,
                             callbackScope: this});
         }
         else
         {
            // Handle error conditions...
            this.alfLog("error", "A request to remove a site from the favourites list but either the site or user was not specified", config);
         }
      },
      
      /**
       * This handles successfully completed requests to remove a site from the favourites list for a user. It publishes the
       * details on "ALF_FAVOURITE_SITE_REMOVED" topic.
       * 
       * @method favouriteSiteRemoved
       * @param {object} response The response from the XHR request to remove the site.
       * @param {object} originalRequestConfig The original configuration passed when the request was made.
       */
      favouriteSiteRemoved: function alf_services_SiteService_favouriteSiteRemoved(response, originalRequestConfig) {
         this.alfLog("log", "Favourite Site Removed Successfully", response, originalRequestConfig);
         this.alfPublish("ALF_FAVOURITE_SITE_REMOVED", { site: originalRequestConfig.site, user: originalRequestConfig.user});
      },
      
      /**
       * Handles XHR posting to make a user a site manager.
       * 
       * @method becomeSiteManager
       * @param {object} data The payload containing the user status to post.
       */
      becomeSiteManager: function alf_services_SiteService__becomeSiteManager(config) {
         if (config.site)
         {
            this.alfLog("log", "A request has been made for a user to become the manager of a site: ", config);
            var url = Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(config.site) + "/memberships";
            var data = {
                  role: (config.role) ? config.role : "SiteManager",
                  person: {
                     userName: config.user
                  }
            };
            this.serviceXhr({url : url,
                             data: data,
                             method: "POST",
                             successCallback: this.reloadPage,
                             callbackScope: this});
         }
         else
         {
            // Handle error conditions...
            this.alfLog("error", "A request was made for a user to become the manager of a site, but no site was specified", config);
         }
      },
      
      /**
       * Performs and XHR put request to make the user a member of the site. The argument supplied must include
       * the attributes "site" and "user" and can optionally include an attribute "role".
       * 
       * @method joinSite
       * @param {object} config The configuration for the join request. 
       */
      joinSite: function alf_services_SiteService__joinSite(config) {
         
         if (config.site && config.user)
         {
            // PLEASE NOTE: The default role for joining a site is "SiteConsumer", however this can be overridden
            // if a role is included in the supplied configuration...
            this.alfLog("log", "A request has been made for a user to join a site: ", config);
            var url = Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(config.site) + "/memberships";
            var data = {
               role: (config.role) ? config.role : "SiteConsumer",
               person: {
                  userName: config.user
               }
            };
            
            // Make the XHR request...
            this.serviceXhr({url : url,
                             method: "PUT",
                             site: config.site,
                             user: config.user,
                             data: data,
                             successCallback: this.siteJoined,
                             callbackScope: this});
         }
         else
         {
            // Handle error conditions...
            if (!config.site)
            {
               this.alfLog("error", "A request was made to join a site but no site was specified", config);
            }
            if (!config.user)
            {
               this.alfLog("error", "A request was made to join a site but no user was specified", config);
            }
         }
      },
      
      /**
       * This function is called when a user successfully joins a site.
       * 
       * @method siteJoined
       * @param {object} response The response from the XHR request to join the site.
       * @param {object} originalRequestConfig The original configuration passed when the request was made.
       */
      siteJoined: function alf_services_SiteService__siteJoined(response, originalRequestConfig) {
         this.alfLog("log", "User has successfully joined a site", response, originalRequestConfig);
         this.alfPublish("ALF_SITE_JOINED", { site: originalRequestConfig.site, user: originalRequestConfig.user});
         this.reloadPage();
      },
      
      /**
       * This method delegates site creation to the legacy YUI popup.
       * 
       * @method createSite
       * @param {string} site
       */
      createSite: function alf_services_SiteService__editSite(config) {
         // TODO: When an edit site request is received we should display the edit site dialog.
         //       We need to wrap the existing YUI widget in a Dojo object.
         this.alfLog("log", "A request has been made to create a site: ", config);
         
         // Just use the old YUI popup...
         if (Alfresco && Alfresco.module && typeof Alfresco.module.getCreateSiteInstance === "function")
         {
            Alfresco.module.getCreateSiteInstance().show();
         }
         else
         {
            this.alfLog("error", "Could not find the 'Alfresco.module.getCreateSiteInstance' function - has 'create-site.js' been included in the page?");
         }
      }, 
      
      /**
       * This method delegates site editing to the legacy YUI popup.
       * 
       * @method editSite
       * @param {string} site
       */
      editSite: function alf_services_SiteService__editSite(config) {
         // TODO: When an edit site request is received we should display the edit site dialog.
         //       We need to wrap the existing YUI widget in a Dojo object.
         this.alfLog("log", "A request has been made to edit a site: ", config);
         
         // Just use the old YUI popup...
         if (Alfresco && Alfresco.module && typeof Alfresco.module.getEditSiteInstance === "function")
         {
            Alfresco.module.getEditSiteInstance().show({
               shortName: config.site
            });
         }
         else
         {
            this.alfLog("error", "Could not find the 'Alfresco.module.getEditSiteInstance' function - has 'edit-site.js' been included in the page?");
         }
      }, 
      
      /**
       * 
       * @method leaveSite
       * @param {string} site The name of the site to leave
       * @param {string} user The name of the user to leave the site
       */
      leaveSite: function alf_services_SiteService__leaveSite(config) {
         if (config.site && config.user)
         {
            this.alfLog("log", "A request has been made for a user to leave a site: ", config);
            var url = Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(config.site) + "/memberships/" + encodeURIComponent(config.user);
            this.serviceXhr({url : url,
                             method: "DELETE",
                             site: config.site,
                             user: config.user,
                             successCallback: this.siteLeft,
                             callbackScope: this});
         }
         else
         {
            // Handle error conditions...
            if (!config.site)
            {
               this.alfLog("error", "A request was made to leave a site but no site was specified", config);
            }
            if (!config.user)
            {
               this.alfLog("error", "A request was made to leave a site but no user was specified", config);
            }
         }
      },
      
      /**
       * This function is called when a user has successfully left a site.
       * 
       * @method siteLeft
       * @param {object} response The response from the XHR request to leave the site.
       * @param {object} originalRequestConfig The original configuration passed when the request was made.
       */
      siteLeft: function alf_services_SiteService__siteJoined(response, originalRequestConfig) {
         this.alfLog("log", "User has successfully left a site", response, originalRequestConfig);
         this.alfPublish("ALF_SITE_LEFT", { site: originalRequestConfig.site, user: originalRequestConfig.user});
         this.reloadPage();
      },
      
      /**
       * This is a catch all success handler for both the join site and become site manager. It simply reloads
       * the current page. It is ** INCORRECTLY ** assumed that the current user will always be on the site
       * referenced in the request. This method needs to be updated accordingly.
       */
      reloadPage: function alf_services_SiteService__reloadPage(response, requestConfig) {
         // TODO: Check user in request is current user and that site in request is current site
         this.alfPublish("ALF_RELOAD_PAGE", {});
      },
      
      /**
       * When a request is made for a user to leave a site we should determine whether or not the current user is the
       * user removed and whether or not they are currently viewing that site. If they are then we should navigate
       * them away from the site and back to their dashboard.
       * 
       * In a future where notifications are generated based on events generated by other users, this would mean
       * that a user can be immediately ejected from a site as soon as they are removed from it (e.g. A Site Manager
       * removes a user from a site and if that user is viewing the site they are "ejected").
       * 
       * @method leaveSiteSuccess
       * 
       */
      leaveSiteSuccess: function alf_services_SiteService__leaveSiteSuccess(response, requestConfig) {
         
         // TODO: Check that the current user is viewing the site...
         // TODO: Check that the user removed is the current user...
         window.location.href = Alfresco.constants.URL_CONTEXT;
      }
   });
});
