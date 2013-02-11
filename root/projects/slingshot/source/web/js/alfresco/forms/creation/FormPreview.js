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
        "dojo/text!./templates/FormPreview.html",
        "alfresco/core/Core",
        "alfresco/forms/PublishForm"], 
        function(declare, _Widget, _Templated, template, AlfCore, PublishForm) {
   
   return declare([_Widget, _Templated, AlfCore], {
      cssRequirements: [{cssFile:"./css/FormPreview.css"}],
      i18nRequirements: [{i18nFile: "./i18n/FormPreview.properties"}],
      templateString: template,
      
      postCreate: function() {
         this.labelNode.innerHTML = this.message("form.preview.title");
         var previewForm = new PublishForm({widgets: this.widgets});
         previewForm.placeAt(this.previewNode);
      },
      
      closePreview: function() {
         // This is provided for the calling widget to add an aspect to.
      }
   });
});