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

if (typeof FDK == "undefined" || !FDK)
{
   var FDK = {};
}

/**
 * Event handler called when the user clicks on a unit test to run.
 * 
 * @param formId The id of the form to show
 */
FDK.runUnitTest = function FDK_runUnitTest(formId)
{
   var nodeRef = document.getElementById("noderef").value;
   
   if (nodeRef.length == 0)
   {
      alert("You must enter a NodeRef to run the unit test!");
   }
   else
   {
      var url = "form-console?itemKind=node&itemId=" + nodeRef + "&formId=" + formId + "&redirect=";
      
      var redirectUrl = window.location.href;
      if (redirectUrl.indexOf("?nodeRef=") == -1)
      {
         redirectUrl = redirectUrl + "?nodeRef=" + nodeRef;
      }
      
      window.location.href = url + encodeURIComponent(redirectUrl);
   }
}

/**
 * Event handler called when the form console content is ready.
 *  
 * @param layer Not used
 * @param args Event arguments
 */
FDK.formConsoleContentReady = function FDK_formConsoleContentReady(layer, args)
{
   // add a handler to the cancel button
   var cancelButton = args[1].buttons.cancel;
   cancelButton.addListener("click", FDK.cancelUnitTest, null, null);
}

/**
 * Event handler called when a user cancels the form console.
 * 
 * @param type Not used
 * @param args Not used
 */
FDK.cancelUnitTest = function FDK_cancelUnitTest(type, args)
{
   // go back to unit tests page
   window.location.href = "fdk-unit-tests?nodeRef=" + YAHOO.util.Dom.get("itemId").value;
}
