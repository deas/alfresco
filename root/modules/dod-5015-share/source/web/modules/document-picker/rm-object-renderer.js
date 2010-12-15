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
 * RM_ObjectRenderer component
 * 
 * Overrides certain methods so RM doc picker can display RM icons
 * 
 * @namespace Alfresco
 * @class Alfresco.RM_ObjectRenderer
 */
(function RM_ObjectRenderer()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * RM_ObjectRenderer componentconstructor.
    * 
    * @param {object} Instance of the DocumentPicker
    * @return {Alfresco.module.ObjectRenderer} The new ObjectRenderer instance
    * @constructor
    */
   Alfresco.module.RM_ObjectRenderer = function RM_ObjectRenderer_constructor(DocumentPicker)
   {
      Alfresco.module.RM_ObjectRenderer.superclass.constructor.call(this,DocumentPicker);

      return this;
   };
    
   YAHOO.extend(Alfresco.module.RM_ObjectRenderer, Alfresco.module.ObjectRenderer,
   {
/**
    * Generate item icon URL - displays RM icons depending on type
    *
    * @method getIconURL
    * @param item {object} Item object literal
    * @param size {number} Icon size (16, 32)
    */
   getIconURL : function RM_ObjectRenderer_getIconURL(item, size)
   {
      var types = item.type.split(':');
      if (types[0] !== 'rma' && types[0] !== 'dod')
      {
         return Alfresco.module.RM_ObjectRenderer.superclass.getIconURL.call(this, item, size);
      }
      else
      {
         var type = "";
         switch (types[1])
         {
            case "recordSeries":
            {
               type = 'record-series';
               break;
            }
            case "recordCategory":
            {
               type = 'record-category';
               break;
            }
            case "recordFolder":
            {
               type = 'record-folder';
               break;
            }
            case "nonElectronicDocument":
            {
               type = 'non-electronic';
               break;
            }
            case "metadataStub":
            {
               type = 'meta-stub';
               break;
            }
            default:
            {
               return Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(item.name, item.type, size); 
            }
         }
         return Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/' + type + '-'+size+'.png';
      }
   }      
   });
   
})();