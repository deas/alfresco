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
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/PopupSearch.html",
        "alfresco/core/Core",
        "dijit/form/Textarea"], 
        function(declare, _WidgetBase, _TemplatedMixin, template,  AlfCore, Textarea) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      i18nScope: "org.alfresco.PopupSearch",
      cssRequirements: [{cssFile:"./css/PopupSearch.css"}],
      i18nRequirements: [{i18nFile: "./i18n/PopupSearch.properties"}],
      templateString: template,

      textArea: null,
     
      postCreate: function() {
         
         this.labelNode.innerHTML = this.encodeHTML(this.message("search.label"));
         
         this.textArea = new Textarea({
            value: this.message("search.instruction"),
            style: "width:200px;"
         });
         this.textArea.placeAt(this.textAreaNode);
      }
   });
});