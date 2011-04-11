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
 * This is the "Flash" plugin used to display flash movies.
 *
 * Supports the following mime types: "application/x-shockwave-flash"
 *
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.Flash = function(wp, attributes)
{
   this.wp = wp;
   this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
   this.swfDiv = null;
   return this;
};

Alfresco.WebPreview.prototype.Plugins.Flash.prototype =
{
   /**
    * Attributes
    */
   attributes:
   {
      /**
       * Decides if the node's content or one of its thumbnails shall be displayed.
       * Leave it as it is if the node's content shall be used.
       * Set to a custom thumbnail definition name if the node's thumbnail contains the flash movie to display.
       *
       * @property src
       * @type String
       * @default null
       */
      src: null
   },

   /**
    * Tests if the plugin can be used in the users browser.
    *
    * @method report
    * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
    *         it cant be used as a string.
    * @public
    */
   report: function Flash_report()
   {
      // Lets make a fair guess that flash players of v 9.0.45 will be able to handle the content
      if (!Alfresco.util.hasRequiredFlashPlayer(9, 0, 45))
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
   display: function Flash_display()
   {
      var url = this.attributes.src ? this.wp.getThumbnailUrl(this.attributes.src) : this.wp.getContentUrl();

      // Create flash web preview by using swfobject
      var swfId = "Flash_" + this.wp.id;
      var so = new YAHOO.deconcept.SWFObject(url, swfId, "100%", "100%", "9.0.45");
      so.addParam("allowScriptAccess", "never");
      so.addParam("allowFullScreen", "true");
      so.addParam("wmode", "transparent");

      // Finally create (or recreate) the flash preview in the new div
      so.write(this.wp.widgets.previewerElement.id);
   }
};