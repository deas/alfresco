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
 * @module alfresco/tests/menus/AdvancedMenusTestService
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
    * - AlfCheckableMenuItem
    * - AlfFilteringMenuItem
    * - AlfMenuBarSelect
    * - AlfMenuBarSelectItems
    * - AlfMenuBarToggle
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
               name: "Test Simple Checkable Menu Items",
               timeout: 40000,
               runTest: this.testSimpleCheckableMenuItems,
               scope: this
            },
            {
               name: "Test Simple Checkable Menu Item Events",
               timeout: 40000,
               runTest: this.testSimpleCheckableMenuItemEvents,
               scope: this
            },
            {
               name: "Test Grouped Checkable Menu Items",
               timeout: 40000,
               runTest: this.testGroupedCheckableMenuItems,
               scope: this
            },
            {
               name: "Test Menu Bar Select",
               timeout: 40000,
               runTest: this.testSelectMenuBarItems,
               scope: this
            },
            {
               name: "Test Menu Bar Toggles",
               timeout: 40000,
               runTest: this.testMenuBarToggle,
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

         this.scope.findTestObjects(["MENU_BAR_POPUP1",
                                     "SIMPLE_CHECKABLE_1",
                                     "SIMPLE_CHECKABLE_2",
                                     "GROUPED_CHECKABLE_1",
                                     "GROUPED_CHECKABLE_2",
                                     "GROUPED_CHECKABLE_3",
                                     "MENU_BAR_SELECT",
                                     "SELECT_MENU_ITEM_1",
                                     "SELECT_MENU_ITEM_2",
                                     "MENU_BAR_SELECT_VALUE",
                                     "SELECT_MENU_ITEM_3",
                                     "SELECT_MENU_ITEM_4",
                                     "MENU_BAR_TOGGLE_CUSTOM_LABEL",
                                     "MENU_BAR_TOGGLE_WITH_ICON"]);
      },
      
      /**
       * This menu checks the toggling of simple checkable menu items by both keyboard and mouse
       * 
       * @instance
       */
      testSimpleCheckableMenuItems: function(doh) {
         var d = new doh.Deferred();
         
         // Subscribe to publications from both simple check boxes. The payloads will be captured for both.
         var _this = this;
         this.scope.checkableItems = [];
         this.scope.alfSubscribe("SIMPLE_CHECKABLE1", function(payload) {
            _this.scope.alfLog("log", "Received checkable item event", payload);
            _this.scope.checkableItems.push({value: payload.value, selected: payload.selected});
            _this.scope.alfLog("log", "Results: ", _this.scope.checkableItems);
         });
         this.scope.alfSubscribe("SIMPLE_CHECKABLE2", function(payload) {
            _this.scope.alfLog("log", "Received checkable item event", payload);
            _this.scope.checkableItems.push({value: payload.value, selected: payload.selected});
            _this.scope.alfLog("log", "Results: ", _this.scope.checkableItems);
         });
         
         robot.keyPress(keys.TAB, 200);          // Select the menu bar
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.SPACE, 200);        // Select first item (menu should close)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item
         robot.keyPress(keys.SPACE, 200);        // Select second item (menu should close)
         
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_POPUP1.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Open first drop down menu
         robot.mouseMoveAt(this.scope.testObjects.SIMPLE_CHECKABLE_1.domNode, 200);
         robot.mouseClick({left:true}, 200);                                        // Click the first menu item
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_POPUP1.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Open first drop down menu
         robot.mouseMoveAt(this.scope.testObjects.SIMPLE_CHECKABLE_2.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Click the second menu item
         
         robot.sequence(d.getTestCallback(function(){
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = [{value:"OP1",selected:true},{value:"OP2",selected:true},{value:"OP1",selected:false},{value:"OP2",selected:false}];
            doh.assertEqual(expectedResults.length, _this.scope.checkableItems.length, "The number of checkable menu items events did not match expectations");
            _this.scope.alfLog("log", "Results: ", _this.scope.checkableItems);
            array.forEach(expectedResults, function(expectedResult, index) {
               _this.scope.alfLog("log", "Expected: ", expectedResult, " Actual: ", _this.scope.checkableItems[index]);
               doh.assertEqual(expectedResult.value, _this.scope.checkableItems[index].value, "Unexpected checkable menu value");
               doh.assertEqual(expectedResult.selected, _this.scope.checkableItems[index].selected, "Unexpected checkable menu selection");
            });
         }), 900);
         
         return d;
      },
      
      /**
       * Checks that checkable menu items respond to external events.
       * 
       * @instance
       */
      testSimpleCheckableMenuItemEvents: function(doh) {
         this.scope.alfPublish("SIMPLE_CHECKABLE1", {selected: true});
         doh.assertTrue(this.scope.testObjects.SIMPLE_CHECKABLE_1.checked);
         this.scope.alfPublish("SIMPLE_CHECKABLE1", {selected: false});
         doh.assertFalse(this.scope.testObjects.SIMPLE_CHECKABLE_1.checked);
      },
      
      /**
       * This menu checks the toggling of grouped checkable menu items by both keyboard and mouse
       * 
       * @instance
       */
      testGroupedCheckableMenuItems: function(doh) {
         var d = new doh.Deferred();
         
         // Subscribe to publications from both simple check boxes. The payloads will be captured for both.
         var _this = this;
         this.scope.checkableItems = [];
         this.scope.alfSubscribe("GROUPED_CHECKABLE", function(payload) {
            _this.scope.alfLog("log", "Received checkable item event", payload);
            _this.scope.checkableItems.push({value: payload.value, selected: payload.selected});
            _this.scope.alfLog("log", "Results: ", _this.scope.checkableItems);
         });
         
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to third item (first in group)
         robot.keyPress(keys.SPACE, 200);        // Select first item in group
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to third item (first in group)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to fourth item (second in group)
         robot.keyPress(keys.SPACE, 200);        // Select second item in group
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to third item (first in group)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to fourth item (second in group)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to fifth item (third in group)
         robot.keyPress(keys.SPACE, 200);        // Select third item in group

         
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_POPUP1.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Open first drop down menu
         robot.mouseMoveAt(this.scope.testObjects.GROUPED_CHECKABLE_2.domNode, 200);
         robot.mouseClick({left:true}, 200);                                        // Click the SECOND grouped item
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_POPUP1.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Open first drop down menu
         robot.mouseMoveAt(this.scope.testObjects.GROUPED_CHECKABLE_1.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Click the FIRST grouped item
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_POPUP1.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Open first drop down menu
         robot.mouseMoveAt(this.scope.testObjects.GROUPED_CHECKABLE_3.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Click the THIRD menu item

         robot.sequence(d.getTestCallback(function(){
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = [{value:"CHECKED_OP1",selected:true},
                                   {value:"CHECKED_OP2",selected:true},
                                   {value:"CHECKED_OP3",selected:true},
                                   {value:"CHECKED_OP2",selected:true},
                                   {value:"CHECKED_OP1",selected:true},
                                   {value:"CHECKED_OP3",selected:true}];
            doh.assertEqual(expectedResults.length, _this.scope.checkableItems.length, "The number of checkable menu items events did not match expectations");
            _this.scope.alfLog("log", "Results: ", _this.scope.checkableItems);
            array.forEach(expectedResults, function(expectedResult, index) {
               _this.scope.alfLog("log", "Expected: ", expectedResult, " Actual: ", _this.scope.checkableItems[index]);
               doh.assertEqual(expectedResult.value, _this.scope.checkableItems[index].value, "Unexpected checkable menu value");
               doh.assertEqual(expectedResult.selected, _this.scope.checkableItems[index].selected, "Unexpected checkable menu selection");
            });
            
            // Check that the other two options in the group aren't checked
            doh.assertFalse(_this.scope.testObjects.GROUPED_CHECKABLE_1.checked);
            doh.assertFalse(_this.scope.testObjects.GROUPED_CHECKABLE_2.checked);
            
            // Publish an event to select the first item...
            _this.scope.alfPublish("GROUPED_CHECKABLE", {value: "CHECKED_OP1", selected: true});
            
            // Now check that the other two aren't selected...
            doh.assertFalse(_this.scope.testObjects.GROUPED_CHECKABLE_2.checked);
            doh.assertFalse(_this.scope.testObjects.GROUPED_CHECKABLE_3.checked);
         }), 900);
         
         return d;
      },
      
      /**
       * This test checks that select menu items update correctly.
       * 
       * @instance
       */
      testSelectMenuBarItems: function(doh) {
         var d = new doh.Deferred();
         
         // Subscribe to publications from both simple check boxes. The payloads will be captured for both.
         var _this = this;
         this.scope.checkableItems = [];
         this.scope.alfSubscribe("MENU_BAR_SELECT", function(payload) {
            _this.scope.alfLog("log", "Received menu bar select item event", payload);
            _this.scope.checkableItems.push({label: payload.label});
         });
         
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Should still be on the first drop down, so move to the next one...
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.SPACE, 200);        // Select first item (menu should close)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item
         robot.keyPress(keys.SPACE, 200);        // Select second item (menu should close)
         
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_SELECT_VALUE.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        // Open select bar item that uses values (rather than labels)
         robot.mouseMoveAt(this.scope.testObjects.SELECT_MENU_ITEM_3.domNode, 200);
         robot.mouseClick({left:true}, 200);                                        // Click the first item
         
         robot.sequence(d.getTestCallback(function(){
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = [{label:"menu2.item1.label"},{label:"menu2.item2.label"}];
            doh.assertEqual(expectedResults.length, _this.scope.checkableItems.length, "The number of menu bar select events did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               _this.scope.alfLog("log", "Expected: ", expectedResult, " Actual: ", _this.scope.checkableItems[index]);
               doh.assertEqual(expectedResult.label, _this.scope.checkableItems[index].label, "Unexpected menu bar select label");
            });
            
            // Check the label has been updated...
            doh.assertEqual("Select Item 2", _this.scope.testObjects.MENU_BAR_SELECT.get("label"), "The menu bar select label was not updated correctly (with item label)");
            doh.assertEqual("Alpha", _this.scope.testObjects.MENU_BAR_SELECT_VALUE.get("label"), "The menu bar select label was not updated correctly (with item value)");
         }), 900);
         
         return d;
      },
      
      /**
       * This tests the toggling menu bar item
       * 
       * @instance
       */
      testMenuBarToggle: function(doh) {
         var d = new doh.Deferred();
         
         // Subscribe to publications from both simple check boxes. The payloads will be captured for both.
         var _this = this;
         this.scope.toggledItems = [];
         this.scope.alfSubscribe("CLICK", function(payload) {
            _this.scope.alfLog("log", "Received menu bar toggle event", payload);
            _this.scope.toggledItems.push({clicked: payload.clicked, value: payload.value});
         });
         
         // Check the initial labels...
         doh.assertEqual("High", _this.scope.testObjects.MENU_BAR_TOGGLE_CUSTOM_LABEL.get("label"), "The menu bar select label was not updated correctly (with item label)");
         doh.assertEqual("Down", _this.scope.testObjects.MENU_BAR_TOGGLE_WITH_ICON.get("label"), "The menu bar select label was not updated correctly (with item value)");
         
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_TOGGLE_CUSTOM_LABEL.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_TOGGLE_WITH_ICON.domNode, 200);
         robot.mouseClick({left:true}, 200);                                        
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_TOGGLE_CUSTOM_LABEL.domNode, 200); 
         robot.mouseClick({left:true}, 200);                                        
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_TOGGLE_WITH_ICON.domNode, 200);
         robot.mouseClick({left:true}, 200);                                        
         
         robot.sequence(d.getTestCallback(function(){
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = [{clicked: "TOGGLE_WITH_LABEL", value: "OFF"},
                                   {clicked: "TOGGLE_WITH_ICON", value: "ON"},
                                   {clicked: "TOGGLE_WITH_LABEL", value: "ON"},
                                   {clicked: "TOGGLE_WITH_ICON", value: "OFF"}];
            doh.assertEqual(expectedResults.length, _this.scope.toggledItems.length, "The number of menu bar select events did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               _this.scope.alfLog("log", "Expected: ", expectedResult, " Actual: ", _this.scope.toggledItems[index]);
               doh.assertEqual(expectedResult.label, _this.scope.toggledItems[index].label, "Unexpected menu bar select label");
            });
            
            // Check the label has been updated...
            doh.assertEqual("High", _this.scope.testObjects.MENU_BAR_TOGGLE_CUSTOM_LABEL.get("label"), "The menu bar select label was not updated correctly (with item label)");
            doh.assertEqual("Down", _this.scope.testObjects.MENU_BAR_TOGGLE_WITH_ICON.get("label"), "The menu bar select label was not updated correctly (with item value)");
         }), 900);
         
         return d;
      }
   });
});
