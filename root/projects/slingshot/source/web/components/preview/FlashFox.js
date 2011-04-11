/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * This is the "FlashFox" plugin used to display video that requires a FlashPlayer of version 9 or above.
 * It should mainly be considered a fallback solution, used when the user doesn't have FlashPlayer v10 and can use
 * the more feature rich and configurable StrobeMediaPlayback plugin.
 *
 * Supports the following mime types: "video/mp4", "video/flv"
 *
 * For more information, please visit:
 * http://code.google.com/p/flashfox/
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.FlashFox = function(wp, attributes)
{
   this.wp = wp;
   this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
   this.swfDiv = null;
   return this;
};

Alfresco.WebPreview.prototype.Plugins.FlashFox.prototype =
{
   /**
    * Attributes
    */
   attributes:
   {
      /**
       * Decides if the node's content or one of its thumbnails shall be displayed.
       * Leave it as it is if the node's content shall be used.
       * Set to a custom thumbnail definition name if the node's thumbnail contains the video to display.
       *
       * @property src
       * @type String
       * @default null
       */
      src: null,

      /**
       * Decides if a poster (an image representing the movie) shall be displayed before the movie is loaded or played.
       * Leave it as it is if no poster shall be used.
       * Set to a thumbnail definition name if the node's thumbnail shall be used.
       *
       * Example value: "imgpreview"
       *
       * @property poster
       * @type String
       * @default null
       */
      poster: null,

      /**
       * If a poster is used we must tell FlashFox what type of image it is by appending a file suffix on the url
       * when requesting the poster thumbnail. Must be given if a poster is in use.
       *
       * Example value: ".png"
       *
       * @property posterFileSuffix
       * @type String
       * @default null
       */
      posterFileSuffix: null,

      /**
       * Decides if the controls shall be visible in the player.
       * Leave it as it is if the controls shall be visible.
       * Set to "false" if the controls shall be hidden.
       *
       * @property controls
       * @type String
       * @default "true"
       */
      controls: "true"
   },

   /**
    * Tests if the plugin can be used in the users browser.
    *
    * @method report
    * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
    *         it cant be used as a string.
    * @public
    */
   report: function FlashFox_report()
   {      
      if (!Alfresco.util.hasRequiredFlashPlayer(9, 0, 124))
      {
         return this.wp.msg("label.noFlash");
      }
   },

   /**
    * Display the node.
    *
    * @method display
    * @public 
    */
   display: function FlashFox_display()
   {
      var ctx = this.resolveUrls();

      // Create flash web preview by using swfobject
      var swfId = "FlashFox_" + this.wp.id;
      var so = new YAHOO.deconcept.SWFObject(Alfresco.constants.URL_CONTEXT + "components/preview/flashfox.swf",
            swfId, "100%", "100%", "9.0.45");
      so.addVariable("src", ctx.src);
      if (ctx.poster)
      {
         so.addVariable("poster", ctx.poster);
      }
      so.addVariable("controls", this.attributes.controls);

      so.addParam("allowScriptAccess", "sameDomain");
      so.addParam("allowFullScreen", "true");
      so.addParam("wmode", "transparent");

      // Finally create (or recreate) the flash web preview in the new div
      so.write(this.wp.widgets.previewerElement.id);
   },

   /**
    * Helper method to get the urls to use depending on the given attributes.
    *
    * @method resolveUrls
    * @return {Object} An object containing urls.
    */
   resolveUrls: function FlashFox_resolveUrls()
   {
      var ctx = {
         src: this.attributes.src ? this.wp.getThumbnailUrl(this.attributes.src) : this.wp.getContentUrl()
      };      
      if (this.attributes.poster && this.attributes.poster.length > 0 && this.attributes.posterFileSuffix)
      {
         ctx.poster = this.wp.getThumbnailUrl(this.attributes.poster, this.attributes.posterFileSuffix);
      }
      return ctx;
   }

};