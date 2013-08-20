/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * The QuickShare renderer is a customization of the [toggle renderer]{@link module:alfresco/renderers/Toggle} that allows
 * non-container nodes to be publicly shared. It renders as one of two states, unshared being the basic "toggle off" state
 * and shared being a [menu bar]{@link module:alfresco/menus/AlfMenuBar} containing [menu items]{@link module:alfresco/menus/AlfMenuItem}
 * for removing the share, viewing the shared location and sharing via social publishing.
 * 
 * The social publishing links are provided by requesting them by publishing on [QuickShare service mixin topics]{@link module:alfresco/services/_QuickShareServiceTopicMixin}.
 * 
 * @module alfresco/renderers/QuickShare
 * @extends module:alfresco/renderers/Toggle
 * @mixes module:alfresco/services/_QuickShareServiceTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Toggle",
        "alfresco/services/_QuickShareServiceTopicMixin",
        "dojo/text!./templates/QuickShare.html",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-class",
        "dijit/registry",
        "alfresco/menus/AlfMenuBar",
        "alfresco/menus/AlfMenuBarPopup",
        "alfresco/menus/AlfCascadingMenu",
        "alfresco/menus/AlfMenuGroup",
        "alfresco/menus/AlfMenuItem",
        "alfresco/menus/AlfMenuTextForClipboard"], 
        function(declare, Toggle, _QuickShareServiceTopicMixin, template, lang, array, domClass, registry, AlfMenuBar, AlfMenuBarPopup, AlfCascadingMenu, AlfMenuGroup, AlfMenuItem, AlfMenuTextForClipboard) {

   return declare([Toggle, _QuickShareServiceTopicMixin], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/QuickShare.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/QuickShare.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/QuickShare.css"}]
       */
      cssRequirements: [{cssFile:"./css/QuickShare.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * The label to show when the toggle is on
       * @instance
       * @type {string}
       * @default "quick-share.enabled.label"
       */
      onLabel: "quick-share.enabled.label",
      
      /**
       * The label to show when the toggle is on
       * @instance
       * @type {string} 
       * @default "quick-share.disabled.label"
       */
      offLabel: "quick-share.disabled.label",
      
      /**
       * The tooltip to show when the toggle is on
       * @instance
       * @type {string} 
       * @default "quick-share.enabled.description"
       */
      onTooltip: "quick-share.enabled.description",
      
      /**
       * The tooltip to show when the toggle is on
       * @instance
       * @type {string} 
       * @default "quick-share.disabled.description"
       */
      offTooltip: "quick-share.disabled.description",
      
      /**
       * The CSS class to apply for the on display
       * @instance
       * @type {string}
       * @default "quick-share"
       */
      toggleClass: "quick-share",
      
      /**
       * Set up the attributes to be used when rendering the template.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_QuickShare__postMixInProperties() {
         // Set up the toggle topics..
         // If no instantiation overrides have been provided then just default to the standard topics
         // provided by the "alfresco/services/_RatingsServiceTopicMixin" class...
         this.toggleOnTopic = (this.toggleOnTopic != null) ? this.toggleOnTopic : this.addQuickShareTopic;
         this.toggleOnSuccessTopic = (this.toggleOnSuccessTopic != null) ? this.toggleOnSuccessTopic : this.addQuickShareSuccessTopic;
         this.toggleOnFailureTopic = (this.toggleOnFailureTopic != null) ? this.toggleOnFailureTopic : this.addQuickShareFailureTopic;
         this.toggleOffTopic = (this.toggleOffTopic != null) ? this.toggleOffTopic : this.removeQuickShareTopic;
         this.toggleOffSuccessTopic = (this.toggleOffSuccessTopic != null) ? this.toggleOffSuccessTopic : this.removeQuickShareSuccessTopic;
         this.toggleOffFailureTopic = (this.toggleOffFailureTopic != null) ? this.toggleOffFailureTopic : this.removeQuickShareFailureTopic;
         
         // It is necessary to capture the toggle off request and call the onclick function...
         // This is because the toggle off is triggered by a menu item added to the toggled on view rather
         // than being triggered directly by a bubbling DOM event...
         this.alfSubscribe(this.toggleOffTopic, lang.hitch(this, "renderToggledOff"));
         
         // Perform the standard setup...
         this.inherited(arguments);
      },
      
      /**
       * Overridden to get the liked state of the current item.
       * 
       * @instance
       * @returns {boolean} Indicating the initial state of the toggle.
       */
      getInitialState: function alfresco_renderers_QuickShare__getInitialState() {
         return typeof this.currentItem.jsNode.properties["qshare:sharedId"] != "undefined";
      },
      
      /**
       * 
       * @instance
       */
      postCreate: function alfresco_renderers_QuickShare__postCreate() {
         this.inherited(arguments);
         
         // Hide folders
         if (this.currentItem.node != null && this.currentItem.node.isContainer)
         {
            domClass.add(this.domNode, "hidden");
         }
         else
         {
            // Request the QuickShare and Social links (the former is for viewing quick shared items (when appended
            // with the "qshare:sharedId" property) and the latter are for sharing the information with social media
            // such as Facebook, Twitter, etc
            this.alfPublish(this.getQuickShareLinkTopic, {
               callback: this.setQuickShareLink,
               callbackScope: this
            });
            this.alfPublish(this.getSocialLinksTopic, {
               callback: this.setSocialLinks,
               callbackScope: this
            });
            
            if (this.isToggleOn)
            {
               this.widgets = this.defineWidgets(this.currentItem.jsNode.properties["qshare:sharedId"]);
               this.processWidgets(this.widgets, this.onNode);
            }
         }
      },
      
      /**
       * This function is passed as the callback handler when publishing a request for the quick share link.
       * It simply sets the "quickShareLink" attribute with the argument passed.
       * 
       * @instance
       * @param {string} link The link to access quick shared content
       */
      setQuickShareLink: function alfresco_renderers_QuickShare__setQuickShareLink(link) {
         this.quickShareLink = link;
      },
      
      /**
       * This function is passed as the callback handler when publishing a request for the social link configuration.
       * It simply sets the "socialLinks" attribute with the argument passed.
       * 
       * @instance
       * @param {array} links The links to use when publishing quick shared content
       */
      setSocialLinks: function alfresco_renderers_QuickShare__setSocialLinks(links) {
         this.socialLinks = links;
      },
      
      /**
       * Called whenever the "toggleOnSuccessTopic" attribute is published on
       * @instance
       */
      onToggleOnSuccess: function alfresco_renderers_QuickShare__onToggleOnSuccess(payload) {
         this.inherited(arguments);
         this.widgets = this.defineWidgets(payload.response.sharedId);
         this.processWidgets(this.widgets, this.onNode);
      },
      
      /**
       * Called whenever the "toggleOffSuccessTopic" attribute is published on
       * @instance
       */
      onToggleOffSuccess: function alfresco_renderers_QuickShare__onToggleOffSuccess(payload) {
         this.inherited(arguments);
         if (this.onNode.children != null && this.onNode.children.length > 0)
         {
            var existingWidget = registry.byNode(this.onNode.children[0]);
            existingWidget.destroy();
         }
      },
      
      /**
       * Defines a JSON structure for the widgets to represent the shared state options.
       * @instance
       */
      defineWidgets: function alfresco_renderers_QuickShare__defineWidgets(sharedId) {
         var shareUrl = "http://" + window.location.host + this.quickShareLink.replace("{sharedId}", sharedId);
         var widgets = [
            {
               name: "alfresco/menus/AlfMenuBar",
               config: {
                  widgets: [
                     {
                        name: "alfresco/menus/AlfMenuBarPopup",
                        config: {
                           label: this.message("quick-share.enabled.label"),
                           iconClass: "alf-quick-share-enabled-icon",
                           widgets: [
                              {
                                 name: "alfresco/menus/AlfMenuGroup",
                                 config: {
                                    widgets: [
                                       {
                                          name: "alfresco/menus/AlfCascadingMenu",
                                          config: {
                                             label: this.message("quick-share.public-url.label"),
                                             widgets: [
                                                {
                                                   name: "alfresco/menus/AlfMenuGroup",
                                                   config: {
                                                      widgets: [
                                                         {
                                                            name: "alfresco/menus/AlfMenuTextForClipboard",
                                                            config: {
                                                               textForClipboard: shareUrl
                                                            }
                                                         }
                                                      ]
                                                   }
                                                }
                                             ]
                                          }
                                       },
                                       {
                                          name: "alfresco/menus/AlfMenuItem",
                                          config: {
                                             label: this.message("quick-share.view.label"),
                                             publishTopic: "ALF_NAVIGATE_TO_PAGE",
                                             publishPayload: {
                                                url: shareUrl,
                                                type: "FULL_PATH",
                                                target: "NEW"
                                             }
                                          }
                                       },
                                       {
                                          name: "alfresco/menus/AlfMenuItem",
                                          config: {
                                             label: this.message("quick-share.unshare.label"),
                                             publishTopic: this.toggleOffTopic,
                                             publishPayload: {
                                                node: this.currentItem,
                                                sharedId: sharedId
                                             }
                                          }
                                       },
                                       {
                                          name: "alfresco/menus/AlfCascadingMenu",
                                          config: {
                                             label: this.message("quick-share.share-with.label"),
                                             widgets: [
                                                {
                                                   name: "alfresco/menus/AlfMenuGroup",
                                                   config: {
                                                      widgets: this.defineSocialLinkWidgets(shareUrl)
                                                   }
                                                }
                                             ]
                                          }
                                       }
                                    ]
                                 }
                              }
                           ]
                        }
                     }
                  ]
               }
            }
         ]
         return widgets;
      },
      
      /**
       * Iterates over the configured social links and attempts to create a widget for each.
       * 
       * @instance
       * @param {string} shareUrl The URL to the shared content
       */
      defineSocialLinkWidgets: function alfresco_renderers_QuickShare__defineSocialLinkWidgets(shareUrl) {
         var socialLinkWidgets = [];
         if (this.socialLinks != null)
         {
            array.forEach(this.socialLinks, lang.hitch(this, "defineSocialLinkWidget", socialLinkWidgets, shareUrl));
         }
         return socialLinkWidgets;
      },
      
      /**
       * Creates the definition for a single menu item that can be used to share the quick shared content.
       * 
       * @instance
       * @param {array} socialLinkWidgets The array to add the new widget definition to
       * @param {string} shareUrl The URL to the shared content
       * @param {object} socialLinkConfig The configuration for the social link
       * @param {integer} index The index of the configuration
       */
      defineSocialLinkWidget: function alfresco_renderers_QuickShare__defineSocialLinkWidget(socialLinkWidgets, shareUrl, socialLinkConfig, index) {
         if (socialLinkConfig.id != null && socialLinkConfig.params != null)
         {
            var href = null;
            for (var i=0; i<socialLinkConfig.params.length; i++)
            {
               if (typeof socialLinkConfig.params[i].href != "undefined")
               {
                  href = socialLinkConfig.params[i].href;
               }
            }
            if (href != null)
            {
               var processedHref = lang.replace(href, lang.hitch(this, "substituteSocialConfigTokens", socialLinkConfig.id, shareUrl));
               var widget = {
                  name: "alfresco/menus/AlfMenuItem",
                  config: {
                     label: this.message("linkshare.action." + socialLinkConfig.id + ".label"),
                     iconClass: "alf-social-" + socialLinkConfig.id + "-icon",
                     publishTopic: "ALF_NAVIGATE_TO_PAGE",
                     publishPayload: {
                        url: processedHref,
                        type: "FULL_PATH",
                        target: "NEW"
                     }
                  }
               };
               socialLinkWidgets.push(widget);
            }
            else
            {
               this.alfLog("warn", "A social publishing link was configured that didn't include an 'href' parameter", socialLinkConfig);
            }
         }
         else
         {
            this.alfLog("warn", "A social publishing link was configured that didn't include an 'id' attribute", socialLinkConfig);
         }
         
      },
      
      /**
       * This is a custom function that is passed to the "lang.replace" call used in the "defineSocialLinkWidget" function. It
       * explicitly looks to substitute the "{shareUrl}" and "{displayName}" tokens (with the supplied arguments) and for 
       * all other tokens attempts to perform a substitution using a message. The message key takes the form of 
       * "linkshare.action.<action-id>.<subtitution-key>" and all messages are resolved passing the shareUrl and displayName
       * arguments as available tokens.
       * 
       * @instance
       * @param {string} id The id of the social link configuration item (e.g. "email", "facebook", etc)
       * @param {string} shareUrl The URL to the shared content
       * @param {string} _ 
       * @param {string} key The current key to substitute
       */
      substituteSocialConfigTokens: function(id, shareUrl, _, key) {
         if (key == "shareUrl")
         {
            return shareUrl;
         }
         else if (key == "displayName")
         {
            return this.currentItem.displayName;
         }
         else
         {
            var msg = this.message("linkshare.action." + id + "." + key, { "0" : shareUrl, "1": this.currentItem.displayName}); 
            return  msg;
         }
      }
   });
});