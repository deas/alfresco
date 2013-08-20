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
 * @module alfresco/tests/doclib-widgets/DocLibWidgetsTestService
 * @extends module:alfresco/tests/CommonTestService
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/tests/CommonTestService",
        "doh/runner",
        "dojo/robot",
        "dojo/keys",
        "dijit/registry",
        "dojo/query",
        "dojo/dom-attr",
        "dojo/dom-style",
        "dojo/_base/array",
        "alfresco/menus/AlfMenuGroup",
        "dojo/domReady!"],
        function(declare, CommonTestService, doh, robot, keys, registry, query, domAttr, domStyle, array, AlfMenuGroup) {
   
   /**
    * The widgets tested are:
    * - AlfBreadCrumbTrail
    * - AlfBreadCrumb
    * - AlfCreateContentMenuItem
    * - AlfCreateTemplateContentMenu
    * - AlfDocumentActionMenuItem
    * - AlfSelectedItemsMenuBarPopup
    * 
    */
   return declare(CommonTestService, {
      
      /**
       * @instance
       */
      constructor: function() {
         
         // Create a reference to this Service for capturing and reading tests events...
         doh.register("Menu tests", [
            {
               name: "Test setup",
               timeout: 2000,
               runTest: this.testSetup,
               scope: this
            },
            {
               name: "Test Breadcrumb Trail",
               timeout: 40000,
               runTest: this.testBreadCrumbTrail,
               scope: this
            },
            {
               name: "Test Breadcrumbs",
               timeout: 40000,
               runTest: this.testBreadCrumbs,
               scope: this
            },
            {
               name: "Test Pre-Filtered Actions",
               timeout: 40000,
               runTest: this.testPreFilteredActions,
               scope: this
            },
            {
               name: "Test Post-Filtered Actions",
               timeout: 40000,
               runTest: this.testPostFilteredActions,
               scope: this
            },
            {
               name: "Test Pre-Filtered Content Creation",
               timeout: 40000,
               runTest: this.testPreFilteredContentCreation,
               scope: this
            },
            {
               name: "Test Post-Filtered Content Creation",
               timeout: 40000,
               runTest: this.testPostFilteredContentCreation,
               scope: this
            },
            {
               name: "Test Create Templated Content",
               timeout: 40000,
               runTest: this.testCreateTemplatedContentWidget,
               scope: this
            }
         ]);
         doh.run();
      },
      
      /**
       * This test makes sure that all the expected objects are in place. It does this 
       * by using the Dijit registry to find all the widgets that are expected to be on the
       * page. These are then stored as objects in the service instance for easy reference
       * later on.
       * 
       * @instance
       */
      testSetup: function(doh) {
         this.scope.findTestObjects(["MENU1",
                                     "MENU1_ITEM1",
                                     "MENU1_ITEM2",
                                     "MENU1_ITEM3",
                                     "MENU1_ITEM4",
                                     "MENU2",
                                     "MENU2_ITEM1",
                                     "MENU2_ITEM2",
                                     "MENU2_ITEM3",
                                     "MENU2_ITEM4",
                                     "MENU2_ITEM5",
                                     "BREADCRUMB_TRAIL_1",
                                     "BREADCRUMB_TRAIL_2"]);
      },
      
      /**
       * This tests that all of the "alfresco/documentlibrary/AlfDocumentActionMenuItem" widgets have been created
       * and can be successfully clicked on to register an action.
       * @instance
       */
      testPreFilteredActions: function(doh) {
         var d = new doh.Deferred();
         
         // Reset the values logged from the previous keyboard test...
         this.scope.keyboardNavResults = [];
         
         // Click on the first drop down menu...
         robot.mouseMoveAt(this.scope.testObjects.MENU1.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU1_ITEM1.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU1.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU1_ITEM2.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU1.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU1_ITEM3.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU1.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU1_ITEM4.domNode, 250); 
         robot.mouseClick({left:true}, 250);

         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            var expectedResults = ["ACTION_1","ACTION_2","ACTION_3","ACTION_4"];
            doh.assertEqual(expectedResults.length, _this.scope.keyboardNavResults.length, "The number of recorded mouse selections did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.keyboardNavResults[index], "Unexpected mouse selection logged");
            });
            doh.is(true, true);
         }), 900);
         return d;
      },
      
      /**
       * This test simulates some documents being selected and then tests that the "alfresco/documentlibrary/AlfDocumentActionMenuItems"
       * widgets have filtered themselves correctly. The first 3 actions should hide themselves because they don't meet the criteria
       * of the selected documents. The fourth should remain visible.
       * @instance
       */
      testPostFilteredActions: function(doh) {
         // Reset the values logged from the previous keyboard test...
         this.scope.keyboardNavResults = [];
         // This is a realistic example of a publication from the DocumentList.
         // It shows all aspects across all the files, common aspects across all files and the permissions granted to the use
         this.scope.alfPublish("ALF_FILTER_SELECTED_FILE_ACTIONS", {
            allAspects: ["cm:author","cm:titled","exif:exif"],
            commonAspects: ["cm:author","cm:titled"],
            userAccess: {
               CancelCheckout: false,
               ChangePermissions: true,
               CreateChildren: true,
               Delete: true,
               Write: true
            }
         });
         
         var menuItem1Display = domStyle.get(this.scope.testObjects.MENU1_ITEM1.domNode, "display"),
             menuItem2Display = domStyle.get(this.scope.testObjects.MENU1_ITEM2.domNode, "display"),
             menuItem3Display = domStyle.get(this.scope.testObjects.MENU1_ITEM3.domNode, "display"),
             menuItem4Display = domStyle.get(this.scope.testObjects.MENU1_ITEM4.domNode, "display");
         
         doh.assertEqual("none", menuItem1Display, "The first action item has not been hidden");
         doh.assertEqual("none", menuItem2Display, "The second action item has not been hidden");
         doh.assertEqual("none", menuItem3Display, "The third action item has not been hidden");
         doh.assertNotEqual("none", menuItem4Display, "The fourth action item has been incorrectly hidden");
      },
      
      /**
       * @instance
       */
      testPreFilteredContentCreation: function(doh) {
         var d = new doh.Deferred();
         
         // Reset the values logged from the previous keyboard test...
         this.scope.keyboardNavResults = [];
         
         // Click on the first drop down menu...
         robot.mouseMoveAt(this.scope.testObjects.MENU2.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU2_ITEM1.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU2.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU2_ITEM2.domNode, 250); 
         robot.mouseClick({left:true}, 250);

         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            var expectedResults = ["ACTION_5","ACTION_6"];
            doh.assertEqual(expectedResults.length, _this.scope.keyboardNavResults.length, "The number of recorded mouse selections did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.keyboardNavResults[index], "Unexpected mouse selection logged");
            });
         }), 900);
         return d;
      },
      
      /**
       * @instance
       */
      testPostFilteredContentCreation: function(doh) {
         // Reset the values logged from the previous keyboard test...
         this.scope.keyboardNavResults = [];
         // This is a realistic example of a publication from the DocumentList.
         // It shows the permissions granted to the user
         this.scope.alfPublish("ALF_DOCLIST_USER_ACCESS_CHANGED", {
            userAccess: {
               CancelCheckout: false,
               ChangePermissions: true,
               CreateChildren: true,
               Delete: true,
               Write: true
            }
         });
         
         var menuItem1Display = domStyle.get(this.scope.testObjects.MENU2_ITEM1.domNode, "display"),
             menuItem2Display = domStyle.get(this.scope.testObjects.MENU2_ITEM2.domNode, "display");
         
         doh.assertEqual("none", menuItem1Display, "The first content creation item has not been hidden");
         doh.assertNotEqual("none", menuItem2Display, "The second content creation item has been incorrectly hidden");
      },
      
      /**
       * This tests the "alfresco/documentlibrary/AlfCreateTemplateContentMenu" widget. The page contains 3 instances
       * of this widget... one that has a URL that will fail to load data, one that returns no data and one that 
       * returns some data (one template with a title and one without). This checks that the menus are loaded correctly.
       * 
       * @instance
       */
      testCreateTemplatedContentWidget: function(doh) {
         
         var d = new doh.Deferred();
         
         // Reset the values logged from the previous keyboard test...
         this.scope.keyboardNavResults = [];
         
         // Click on the first drop down menu...
         robot.mouseMoveAt(this.scope.testObjects.MENU2.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU2_ITEM3.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU2_ITEM4.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(this.scope.testObjects.MENU2_ITEM5.domNode, 250); 
         robot.mouseClick({left:true}, 250);
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            _this.scope.testObjects.MENU2_ITEM3_POPUP = _this.scope.testObjects.MENU2_ITEM3.popup;
            doh.assertNotEqual(null, _this.scope.testObjects.MENU2_ITEM3_POPUP, "No popup for failed to load templates");
            _this.scope.testObjects.MENU2_ITEM4_POPUP = _this.scope.testObjects.MENU2_ITEM4.popup;
            doh.assertNotEqual(null, _this.scope.testObjects.MENU2_ITEM4_POPUP, "No popup for no templates");
            _this.scope.testObjects.MENU2_ITEM5_POPUP = _this.scope.testObjects.MENU2_ITEM5.popup;
            doh.assertNotEqual(null, _this.scope.testObjects.MENU2_ITEM5_POPUP, "No popup for templates");
            
            // The first create content popup should have just one child (that says templates couldn't be loaded)
            var popup1Children = _this.scope.testObjects.MENU2_ITEM3_POPUP.getChildren();
            doh.assertEqual(1, popup1Children.length, "Expecting an automatically added group as the only child");
            var popup1GrandChildren = popup1Children[0].getChildren();
            doh.assertEqual(1, popup1GrandChildren.length, "Expecting the single 'fail' menu item");

            // The second create content popup should have just one child (that says that they're aren't templates)
            var popup2Children = _this.scope.testObjects.MENU2_ITEM4_POPUP.getChildren();
            doh.assertEqual(1, popup2Children.length, "Expecting an automatically added group as the only child");
            var popup2GrandChildren = popup2Children[0].getChildren();
            doh.assertEqual(1, popup2GrandChildren.length, "Expecting the single 'no templates' menu item");
            
            // The third create content popup should have two children
            var popup3Children = _this.scope.testObjects.MENU2_ITEM5_POPUP.getChildren();
            doh.assertEqual(1, popup3Children.length, "Expecting an automatically added group as the only child");
            var popup3GrandChildren = popup3Children[0].getChildren();
            doh.assertEqual(2, popup3GrandChildren.length, "Expecting two templates menu items");
         }), 900);
         return d;
      },
      
      /**
       * This tests the breadcrumb trail to ensure that it is rendered correctly. It tests both showing and not showing
       * the root node label and also the rendering of a custom root label. It also tests hiding and showing the trail.
       * 
       * @instance
       */
      testBreadCrumbTrail: function(doh) {
         
         // Reset the values logged from the previous keyboard test...
         this.scope.keyboardNavResults = [];
         
         // This is a realistic example of a publication from the DocumentList.
         // It shows the permissions granted to the user
         this.scope.alfPublish("ALF_DOCLIST_FILTER_CHANGED", {
            filterId: "path",
            filterData: "/Folder 1"
         });
         
         doh.assertEqual(1, this.scope.testObjects.BREADCRUMB_TRAIL_1.domNode.childNodes.length, "Expected only one child node of first breadcrumb trail");
         doh.assertEqual("Folder 1", this.scope.testObjects.BREADCRUMB_TRAIL_1.domNode.firstChild.innerHTML, "Unexpected innerHTML of first child of first breadcrumb trail");
         doh.assertEqual(3, this.scope.testObjects.BREADCRUMB_TRAIL_2.domNode.childNodes.length, "Expected 3 child nodes of second breadcrumb trail");
         doh.assertEqual("Home", this.scope.testObjects.BREADCRUMB_TRAIL_2.domNode.firstChild.innerHTML, "Unexpected innerHTML of first child of second breadcrumb trail");
         doh.assertEqual("Folder 1", this.scope.testObjects.BREADCRUMB_TRAIL_2.domNode.childNodes[2].innerHTML, "Unexpected innerHTML of second child of second breadcrumb trail");
         
         // This is a realistic example of a publication from the DocumentList.
         // It shows the permissions granted to the user
         this.scope.alfPublish("ALF_DOCLIST_FILTER_CHANGED", {
            filterId: "path",
            filterData: "/Folder 1/Folder2"
         });
         
         doh.assertEqual(3, this.scope.testObjects.BREADCRUMB_TRAIL_1.domNode.childNodes.length, "Expected 3 child nodes of first breadcrumb trail");
         doh.assertEqual(5, this.scope.testObjects.BREADCRUMB_TRAIL_2.domNode.childNodes.length, "Expected 5 child nodes of second breadcrumb trail");
         
         // Hide the breadcrumb trails...
         this.scope.alfPublish("ALF_DOCLIST_SHOW_PATH", {
            selected: false
         });
         
         var bc1Display = domStyle.get(this.scope.testObjects.BREADCRUMB_TRAIL_1.domNode, "display");
         doh.assertEqual("none", bc1Display, "The breadcrumb was not hidden");

         // Set the root (this is actually a prereq of the next test)...
         this.scope.alfPublish("ALF_CURRENT_NODEREF_CHANGED", {
            node: {
               parent: {
                  nodeRef: "PRETEND_NODE"
               }
            }
         });
         
         // Show the breadcrumb trails...
         this.scope.alfPublish("ALF_DOCLIST_SHOW_PATH", {
            selected: true
         });
         
         var bc1Display = domStyle.get(this.scope.testObjects.BREADCRUMB_TRAIL_1.domNode, "display");
         doh.assertNotEqual("none", bc1Display, "The breadcrumb is still hidden");
      },
      
      /**
       * This tests the handling of clicks on individual breadcrumbs.
       * 
       * @instance
       */
      testBreadCrumbs: function(doh) {
         var d = new doh.Deferred();
         
         // Reset the values logged from previous tests...
         this.scope.pageRequests = [];
         
         // Capture filter requests...
         this.scope.filterChangeRequests = [];
         this.scope.alfSubscribe("ALF_DOCLIST_FILTER_CHANGED", function(payload) {
            _this.scope.alfLog("log", "Received filter change request", payload);
            _this.scope.filterChangeRequests.push(payload.filterData);
         });
         
         var bc2b2 = this.scope.testObjects.BREADCRUMB_TRAIL_2.domNode.childNodes[2],
             bc2b3 = this.scope.testObjects.BREADCRUMB_TRAIL_2.domNode.childNodes[4];
         
         // Click on the first drop down menu...
         robot.mouseMoveAt(bc2b3, 250); 
         robot.mouseClick({left:true}, 250);
         robot.mouseMoveAt(bc2b2, 250); 
         robot.mouseClick({left:true}, 250);
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            var expectedResults = ["folder-details?nodeRef=PRETEND_NODE"];
            doh.assertEqual(expectedResults.length, _this.scope.pageRequests.length, "One page request was expected");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.pageRequests[index], "Unexpected page: " + _this.scope.pageRequests[index]);
            });
            
            expectedResults = ["/Folder 1"];
            doh.assertEqual(expectedResults.length, _this.scope.filterChangeRequests.length, "One filter change request was expected");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.filterChangeRequests[index], "Unexpected filter: " + _this.scope.filterChangeRequests[index]);
            });
            
         }), 900);
         return d;
      }
   });
});
