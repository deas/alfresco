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

function getListTypes()
{
   var types = [],
      result = remote.call("/api/classes/dl_dataListItem/subclasses");
   
   if (result.status == 200)
   {
      var classes = eval('(' + result + ')'),
         subclass;
      
      for (var i = 0, ii = classes.length; i < ii; i++)
      {
         subclass = classes[i];
         if (subclass.name == "dl:dataListItem")
         {
            // Ignore abstract parent type
            continue;
         }

         types.push(
         {
            name: subclass.name,
            title: subclass.title,
            description: subclass.description
         });
      }
   }

   return types;
}

model.listTypes = getListTypes();