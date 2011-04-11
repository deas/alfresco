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
 * Drag n Drop manager
 *
 * Helper class for making YUI's drag n drop easier to handle for  <ul> & <li> elements.
 * Supports multiple targets and sources and also provides automatic functionality for
 * the most common use cases:
 *
 * - Reordering inside a list.
 *   (see rules-list.js & Alfresco.util.DragAndDrop.GROUP_MOVE)
 *
 * - Moving an element from one list to another.
 *   (see customise-dashlets.js & Alfresco.util.DragAndDrop.GROUP_MOVE)
 *
 * - Deleting an element by dragging it to "delete" target.
 *   (see customise-dashlets.js & Alfresco.util.DragAndDrop.GROUP_DELETE)
 *
 * - Having a list that act as a factory and creates a copy of the element that is being dragged rather than moving it from the list.
 *   (see customise-dashlets.js & the "protect" attribute for draggables)
 *
 * - Having a maximum number of elements that fit inside a target.
 *   (see customise-dashlets.js & the "maximum" attribute for targets)
 *
 * Also note that as long as a <a href> element is inside the <li> element all functionality
 * is provided using only the arrow keys. When the <a href> element gets focus it can be:
 *
 * - Moved up or down inside the list using the up & down arrow keys
 * - Moved to another list using the left & right arrow keys
 * - Deleted by using the delete key
 * - Getting copied to the first target that hasn't reached its maximum number of elements (see customise-dashlets.js & the "duplicatesOnEnterKey" attribute for draggables)
 *
 * WARNING!!
 * To avoid problems in IE6 & IE7 make sure that you do NOT use the following strategy to create your <li> elements:
 * Using a <li> element with an id attribute as a "template" and later use HTMLElement.clone to create the actual instances.
 * Note that it IS ok to use HTMLElement.clone as long as the id attribute ISN'T set.
 *
 * Tested in the following browsers: >IE6, >FF3 & >SF3.
 *
 * @namespace Alfresco
 * @cssClass Alfresco.util.DragAndDrop
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element,
      KeyListener = YAHOO.util.KeyListener,
      DDM = YAHOO.util.DragDropMgr;

   /**
    * Alfresco.util.DragAndDrop constructor.
    *
    * @param config {object} The object describing how the drag n drop shall be setup
    *        config.shadow  {HTMLElement}
    *        config.draggables {array}
    *        config.draggables[].container {HTMLElement}
    *        config.draggables[].groups {string} Alfresco.util.DragAndDrop.GROUP_XXX
    *        config.draggables[].cssClass {string}
    *        config.draggables[].protect {boolean}
    *        config.draggables[].duplicatesOnEnterKey {boolean}
    *        config.targets {array}
    *        config.targets[].container {HTMLElement}
    *        config.targets[].group {string} Alfresco.util.DragAndDrop.GROUP_MOVE
    *        config.targets[].maximum {int}
    * @return {Alfresco.util.DragAndDrop} The new DragAndDrop instance
    * @constructor
    */
   Alfresco.util.DragAndDrop = function(config)
   {
      // Instance variables
      this.config = config;
      if (config.shadow)
      {
         this.shadow = config.shadow;
      }
      else
      {
         this.shadow = document.createElement("li");
      }
      Dom.addClass(this.shadow, "dnd-shadow");
      this.targets = {};
      this.draggables = {};
      this.keyListeners = {};

      // Create YUI Drag n Drop resources
      var i, il, j, jl, elements, container;
      for (i = 0, il = (config.targets ? config.targets.length : 0); i <il; i++)
      {
         // Create the targets 
         new YAHOO.util.DDTarget(config.targets[i].container, config.targets[i].group);
         config.targets[i]._index = i;
         this.targets[config.targets[i].container.getAttribute("id")] = config.targets[i];
      }
      for (i = 0, il = (config.draggables ? config.draggables.length : 0); i <il; i++)
      {
         // Create the draggables (the li elements found in the container)
         container = config.draggables[i].container;
         elements = new Element(container).getElementsByTagName("li");
         if (!container.getAttribute("id"))
         {
            Alfresco.util.generateDomId(container);
         }
         if (config.draggables[i].cssClass)
         {
            Dom.addClass(this.shadow, config.draggables[i].cssClass);
         }
         config.draggables[i]._index = i;
         this.draggables[container.getAttribute("id")] = config.draggables[i];
         for (j = 0, jl = elements.length; j < jl; j++)
         {
            this._createDraggable(elements[j], config.draggables[i].groups);
         }
      }

      // Return instance
      return this;
   };


   /**
   * Alias to self
   */
   var DD = Alfresco.util.DragAndDrop;

   /**
   * Predefined Drag and Drop groups that provides built in functionality.
   */
   YAHOO.lang.augmentObject(DD,
   {
      /**
       * Constant to use when configuring groups for draggables and targets.
       * 
       * This group covers the "normal" use case: reordering within one or several containers where the group of the
       * draggables and targets meatch each other.
       * 
       * The dragged element will get added to the target it was dropped over and removed from its original source
       * (unless the source was protected in which case it will get duplicated).
       *
       * @property GROUP_MOVE
       * @type {string}
       * @public
       */
      GROUP_MOVE: "dnd-move",

      /**
       * Constant to use when configuring groups for draggables and targets.
       * 
       * This group covers the use case when one of the dragged elements shall be deleted by being dragged on to a
       * target that represents deletion (such as a trashcan or the source form where the element orginially came).
       * 
       * Using this group will make the dragged element dissapear when drop on a target with this group 
       * (unless it originated from a protected container OR was dropped on the same place where it first was dragged).
       *
       * @property GROUP_DELETE
       * @type {string}
       * @public
       */
      GROUP_DELETE: "dnd-delete"
   });

   Alfresco.util.DragAndDrop.prototype =
   {

      /**
       * Cache for YAHOO.util.KeyListener objects for each draggable-li.
       *
       * @property keyListeners
       * @type object
       */
      keyListeners: null,

      /**
       * To let various methods know what draggable that is currently selected, if any.
       *
       * @property currentEl
       * @type HTMLElement of type li
       */
      currentEl: null,

      /**
       * A reference to the "invisible" object that is used to "make space" for the dragged element.
       *
       * @property shadow
       * @type HTMLElement of type li
       */
      shadow: null,

      /**
       * An object to store info about the draggables that needs to be accessed easily.
       *
       * @property draggables
       * @type object
       */
      draggables: {},

      /**
       * An object to store info about the draggables that needs to be accessed easily.
       *
       * @property targets
       * @type object
       */
      targets: {},

      /**
       * Fired when the user tabs from a "draggable" (or selects another draggable or something else).
       * Since browsers only gives focus on links and form elements its actually a hidden link that loses 
       * the focus and makes this method get called.
       *
       * Removes the "dnd-focused" css class from the draggable-li so it appears to be de-focused or de-selected.
       * Removes the keylistener for the draggable.
       *
       * @method onDraggableBlur
       * @param event {object} a "blur" event
       */
      onDraggableBlur: function DD_onDraggableBlur(event, li)
      {
         // Remove the "dnd-focused" class from the draggable so it doesn't appera to be selected
         if (this.currentEl)
         {
            Dom.removeClass(this.currentEl, "dnd-focused");
         }
         Dom.removeClass(li, "dnd-focused");
         this.currentEl = null;

         // Stop listening to key events
         var kl = this.keyListeners[li.id];
         if (kl !== undefined)
         {
            kl.disable();
         }
      },

      /**
       * Fired when the user tabs to a draggable (or clicks the draggable).
       * Since browsers only gives focus on links and form elements its actually a hidden link that gets the focus 
       * and makes this method get called.
       *
       * Adds the "dnd-focused" class to the draggable-li so it appears to be focused or selected.
       * Adds a keylistener for this draggable so we can listen for keystrokes.
       *
       * @method onDraggableFocus
       * @param event {object} a "focus" event
       */
      onDraggableFocus: function DD_onDraggableFocus(event, li)
      {
         // Remove the focused class from no longer selected draggables and add it to the current
         if (this.currentEl)
         {
            Dom.removeClass(this.currentEl, "dnd-focused");
         }
         this.currentEl = li;
         Dom.addClass(this.currentEl, "dnd-focused");

         /**
          * Add key listeners to the a href tag that actually is the element
          * with the focus, receive events in the onKeyPressed() method.
          * Note that we cannot attach the KeyListener to a global element
          * such as the document since several components that listens to key
          * events might live on the same page.
          */
         var a = new Element(li).getElementsByTagName("a")[0];

         var kl = this.keyListeners[li.id];
         if (kl === undefined)
         {
            kl = new KeyListener(a,
            {
               keys:
               [
                  KeyListener.KEY.UP,
                  KeyListener.KEY.DOWN,
                  KeyListener.KEY.LEFT,
                  KeyListener.KEY.RIGHT,
                  KeyListener.KEY.ESCAPE,
                  KeyListener.KEY.DELETE,
                  KeyListener.KEY.ENTER
               ]
            },
            {
               fn: this.onKeyPressed,
               scope: this,
               correctScope: true
            });
            this.keyListeners[li.id] = kl;
         }
         kl.enable();
      },

      /**
       * Listens to key events for the currently selected draggable
       * (or in reality currently focused a href element).
       *
       * Will do the following for draggable in a column:
       * - move the draggable between columns when LEFT or RIGHT is clicked.
       * - mode the draggable in the current column when UP or DOWN is clicked.
       * - delete the draggable when DELETE is clicked.
       * - deselect a draggable when ESCAPE is clicked.
       *
       * Will do the following for draggable available to columns:
       * - add a draggable to the first target column with free space if ENTER is clicked.
       *
       * @method onKeyPressed
       * @param event {object} a "key" event
       */
      onKeyPressed: function DD_onKeyPressed(event, id)
      {
         var currentDraggable = this.currentEl,            
            relativeNode,
            target;

         if (id[1].keyCode === KeyListener.KEY.ESCAPE)
         {
            // Deselect the current draggable when escape is clicked
            this.focusDraggableAfterDomChange(currentDraggable, false);
         }
         else if (id[1].keyCode === KeyListener.KEY.ENTER)
         {
            // Was enter hit for a draggable available to columns?
            var metadata = this.draggables[this.getContainer(currentDraggable).getAttribute("id")];
            if (metadata.duplicatesOnEnterKey)
            {
               var ds = this.config.draggables;
               // Yes it was, find the first column with space for a new column.
               for (var i = 0, il = (ds ? ds.length : 0); i < il; i++)
               {
                  target = ds[i].container;
                  if (!ds[i].protect && !this.isTargetFull(target))
                  {
                     /**
                      * We have found a visible column with free space, make a copy
                      * of the draggable and insert it in the first position.
                      */
                     var children = Dom.getChildrenBy(target, this.isRealDraggable);
                     this.copyAndInsertDraggable(currentDraggable, target, children.length > 0 ? children[0] : null);
                     return;
                  }
               }
               // No columns with free space was found, alert the user
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.dnd.allColumnsAreFull", this.name)
               });
            }

         }
         else if (id[1].keyCode === KeyListener.KEY.DELETE)
         {
            // Make sure we don't delete any draggable from the available list
            if (this.isDraggableInGroup(currentDraggable, DD.GROUP_DELETE))
            {
               this.deleteDraggable(currentDraggable);
            }
         }
         else if (!this.isDraggableProtected(currentDraggable))
         {
            // UP, DOWN, LEFT & RIGHT key events apply only to non protected draggables.
            var fireMovedEvent = false;
            if (id[1].keyCode === KeyListener.KEY.UP)
            {
               relativeNode = Dom.getPreviousSiblingBy(currentDraggable, this.isRealDraggable);
               if (relativeNode)
               {
                  // Found a draggable above, move the current one above it
                  Dom.insertBefore(currentDraggable, relativeNode);
                  this.focusDraggableAfterDomChange(currentDraggable, true);
                  fireMovedEvent = true;
               }
            }
            else if (id[1].keyCode === KeyListener.KEY.DOWN)
            {
               relativeNode = Dom.getNextSiblingBy(currentDraggable, this.isRealDraggable);
               if (relativeNode)
               {
                  // Found a draggable below, move the current one beneath it
                  Dom.insertAfter(currentDraggable, relativeNode);
                  this.focusDraggableAfterDomChange(currentDraggable, true);
                  fireMovedEvent = true;
               }
            }
            else
            {
               // Find a column index for the column to the left or right of the current draggable
               var containerIndex = this.getTargetIndex(currentDraggable);
               if (id[1].keyCode === KeyListener.KEY.LEFT)
               {
                  containerIndex--;
               }
               else if (id[1].keyCode === KeyListener.KEY.RIGHT)
               {
                  containerIndex++;
               }
               else
               {
                  containerIndex = -1;
               }
               // Look for the column and make sure its not protected and has free space
               target = this.getTargetByIndex(containerIndex);
               if (target && !this.isTargetFull(target) && !this.isDraggableProtected(target))
               {
                  // Insert the draggable in the same position as it had in the previous column
                  var draggableIndex = this._getDraggableIndex(currentDraggable);
                  relativeNode = this._getDraggable(target, draggableIndex);
                  if (relativeNode)
                  {
                     Dom.insertBefore(currentDraggable, relativeNode);
                  }
                  else
                  {
                     target.appendChild(currentDraggable);
                  }
                  /**
                   * When the Dom changes we need to "remind" the browser what
                   * element that has the current focus.
                   */
                  this.focusDraggableAfterDomChange(currentDraggable, true);
                  fireMovedEvent = true;
               }
            }
            if (fireMovedEvent)
            {
               // Fire event to inform any listening components that draggable has been moved
               YAHOO.Bubbling.fire("draggableMoved",
               {
                  eventGroup: this,
                  draggable: currentDraggable
               });
            }
         }
      },

      /**
       * Creates a draggable Alfresco.util.DraggableProxy
       * that listens to blur, focus and click elements.
       *
       * @method _createDraggable
       * @param li {HTMLElement} of type li
       * @private
       */
      _createDraggable: function DD__createDraggable(li, groups)
      {
         // Setup drag n drop support
         var d = new Alfresco.util.DraggableProxy(li, this.shadow, this);
         for (var i = 0, il = groups ? groups.length : 0; i < il; i++)
         {
            d.addToGroup(groups[i]);
         }

         // Add class so it appears to be draggable
         Dom.addClass(li, "dnd-draggable");

         // Find hidden link to add tab support
         var links = new Element(li).getElementsByTagName("a");
         if (links.length > 0)
         {
            var linkEl = new Element(links[0]);

            linkEl.addListener("focus", this.onDraggableFocus, li, this);
            linkEl.addListener("blur", this.onDraggableBlur, li, this);

            // Add select support when using mouse
            var liEl = new Element(li);
            liEl.addListener("click", function(e, obj)
            {
               obj.focus(); // will call selectDraggable
            }, links[0], this);
         }
      },

      /**
        * Creates a copy of the dragged draggable 'srcEl' and inserts in column
        * 'destUl' before 'insertBeforeNode'.
        *
        * @method copyAndInsertDraggable
        * @param srcEl {HTMLElement} of type li
        * @param destUl {HTMLElement} of type ul
        * @param insertBeforeNode {HTMLElement} of type li
        */
      copyAndInsertDraggable: function DD_copyAndInsertDraggable(srcEl, destUl, insertBeforeNode)
      {
         /**
          * Don't do a cloneNode copy since it will make IE point the
          * new draggables a.focus handler to elSrc.
          * Create a new one and use innerHTML instead.
          */
         var draggableConfig = this.draggables[destUl.getAttribute("id")],
            copy = document.createElement("li");         
         Alfresco.util.generateDomId(copy);
         if (draggableConfig.cssClass)
         {
            Dom.addClass(copy, draggableConfig.cssClass);
         }
         copy.innerHTML = srcEl.innerHTML + "";

         // Make the draggable draggable and selectable/focusable.
         this._createDraggable(copy, draggableConfig ? draggableConfig.groups : []);

         // Make sure the new draggable is visible to the user.
         Dom.setStyle(copy, "visibility", "");
         Dom.setStyle(copy, "display", "");
         if (insertBeforeNode)
         {
            // Insert it after the specified node
            destUl.insertBefore(copy, insertBeforeNode);
         }
         else
         {
            // Or last if no node was specified.
            destUl.appendChild(copy);
         }
         // Make sure the new draggable gets the focus.
         this.focusDraggableAfterDomChange(copy, true);

         // Fire event to inform any listening components that draggable has been duplicated
         YAHOO.Bubbling.fire("draggableDuplicated",
         {
            eventGroup: this,
            draggable: srcEl
         });
      },

      /**
       * Gives or takes the focus from 'li' depending on 'focus'.
       * Should be called when a Dom change has occured to "restore" the focus.
       *
       * @method focusDraggableAfterDomChange
       * @param li {HTMLElement} of type li
       * @param focus {boolean} true if li should get focus, false if it should loose focus
       */
      focusDraggableAfterDomChange: function DD_focusDraggableAfterDomChange(li, focus)
      {
         var doFocus = focus;
         var a = new Element(li).getElementsByTagName("a")[0];

         // Don't call it directly, give the browser 50 ms to fix the Dom first.
         YAHOO.lang.later(50, a, function()
         {
            if (doFocus)
            {
               a.focus();
            }
            else
            {
               a.blur();
            }
         });
      },

      /**
       * Deletes the draggable from the Dom.
       *
       * @method deleteDraggable
       * @param li {HTMLElement} of type li (the draggable to be deleted)
       */
      deleteDraggable: function DD_deleteDraggable(li)
      {
         // Remove the draggable from the Dom.
         li.parentNode.removeChild(li);

         // Hide the shadow object
         Dom.setStyle(this.shadow, "display", "none");

         // Fire event to inform any listening components that draggable has been deleted
         YAHOO.Bubbling.fire("draggableDeleted",
         {
            eventGroup: this
         });
      },

      /**
       * Helper function to get a draggable from a specific index in a column.
       *
       * @method _getDraggable
       * @param ul {HTMLElement} of type ul (the draggable's column)
       * @param index {int} index position of the draggable
       * @return {HTMLElement} of type li (the draggable)
       */
      _getDraggable: function DD__getDraggable(ul, index)
      {
         var className = this.draggables[ul.getAttribute("id")].cssClass;
         return Dom.getElementsByClassName(className, "li", ul)[index];
      },

      /**
       * Helper function to get a draggable's index in it's column.
       *
       * @method _getDraggableIndex
       * @param li {HTMLElement} of type li (the draggable)
       * @return {int} index position of the draggable
       */
      _getDraggableIndex: function DD__getDraggableIndex(li)
      {
         var ul = li.parentNode,
            className = this.draggables[ul.getAttribute("id")].cssClass;
         var ds = Dom.getElementsByClassName(className, "li", ul);
         for (var i = 0; i < ds.length; i++)
         {
            if (ds[i] === li)
            {
               return i;
            }
         }
         return -1;
      },

      /**
       * Helper function to get the index of the draggable's column.
       *
       * @method getDraggableColumnIndex
       * @param el {HTMLElement} of type li or ul (the draggable)
       * @return {int} the column index
       */
      getDraggableColumnIndex: function DD_getDraggableColumnIndex(el)
      {
         var metadata = this.draggables[this.getContainer(el).getAttribute("id")];
         return (metadata && metadata._index) ? metadata._index : -1;
      },

      /**
       * Helper function to get the index of the draggable's column.
       *
       * @method getTargetIndex
       * @param el {HTMLElement} of type li or ul (the draggable)
       * @return {int} the column index
       */
      getTargetIndex: function DD_getTargetIndex(el)
      {
         var metadata = this.targets[this.getContainer(el).getAttribute("id")];
         return (metadata && metadata._index) ? metadata._index : null;
      },


      /**
       * Helper function to get the index of the draggable's column.
       *
       * @method getTargetByIndex
       * @param index {int} of type li or ul (the draggable)
       * @return {HTMLElement} the target element at position defined by index
       */
      getTargetByIndex: function DD_getTargetByIndex(index)
      {
         for (var id in this.targets)
         {
            if (this.targets[id]._index == index)
            {
               return this.targets[id].container;
            }
         }
         return null;
      },


      /**
       * Helper function to get the container.
       *
       * @method getContainer
       * @param el {HTMLElement} of type li (the draggable) or ul (the container)
       * @return {int} the column index
       */
      getContainer: function DD_getContainer(el)
      {
         if (el.nodeName.toLowerCase() == "li")
         {
            return el.parentNode;
         }
         return el;
      },

      /**
       * Helper function to determine if a column can't fit anymore draggables.
       *
       * @method isTargetFull
       * @param ul {HTMLElement} of type ul (the draggable column)
       * @return {boolean} true if column is full
       */
      isTargetFull: function DD_isTargetFull(ul)
      {
         var max = this.targets[ul.getAttribute("id")].maximum;
         if (max)
         {
            return Dom.getChildrenBy(ul, this.isRealDraggable).length >= max;
         }
         else
         {
            return true;
         }
      },

      /**
       * Helper function to determine if draggable is "protected", in other word that a "original" of the dragable
       * shall be left and that a "copy" of the dragged around instead.
       *
       * @method isDraggableProtected
       * @param li {HTMLElement} of type li (the draggable)
       * @return {int} the column index
       */
      isDraggableProtected: function DD_isDraggableProtected(li)
      {
         var metadata = this.draggables[this.getContainer(li).getAttribute("id")];
         return (metadata && metadata.protect == true);
      },

      /**
       * Helper function to determine if el is a draggable.
       * Checked performed by looking of it hasn't got class "dnd-shadow".
       *
       * @method isRealDraggable
       * @param el {HTMLElement} element to test
       * @return {boolean} true if el hasn't got class "dnd-shadow"
       */
      isRealDraggable: function DD_isRealDraggable(el)
      {
         return el.nodeName.toLowerCase() == "li" && !Dom.hasClass(el, "dnd-shadow");
      },

      /**
       * Helper function to determine if an element (el) is a certain type (tagType).
       *
       * @method isOfTagType
       * @param el {HTMLElement} element to test tag type of
       * @param tagType {string} tag type
       * @return {boolean} true if el's tag type is same as tagType
       */
      isOfTagType: function DD_isOfTagType(el, tagType)
      {
         var tagTypes = YAHOO.lang.isArray(tagType) ? tagType : [tagType];
         for (var i = 0, il = tagTypes.length; i < il; i++)
         {
            if (el.nodeName.toLowerCase() == tagTypes[i].toLowerCase())
            {
               return true;
            }
         }
         return false;
      },

      /**
       * Helper function to determine if an element is an add drop target.
       *
       * @method isTargetInGroup
       * @param el {HTMLElement} of type li or ul to test
       * @param group {string} The group to match against
       * @return {boolean} true if el should be considered as a memeber of group
       */
      isTargetInGroup: function DD_isTargetInGroup(el, group)
      {
         el = this.getContainer(el);
         return this.targets[el.getAttribute("id")].group == group;
      },

      /**
       * Helper function to determine if an element is an add drop target.
       *
       * @method isDraggableInGroup
       * @param el {HTMLElement} of type li or ul to test
       * @param group {string} The group to match against
       * @return {boolean} true if el should be considered as a memeber of group
       */
      isDraggableInGroup: function DD_isDraggableInGroup(el, group)
      {
         el = this.getContainer(el);
         return Alfresco.util.arrayContains(this.draggables[el.getAttribute("id")].groups, group);
      }

   };


   /**
    * Alfresco.util.DraggableProxy constructor.
    *
    * Alfresco.util.DraggableProxy is a class that represents the actual dragged element.
    * It extends the yui class YAHOO.util.DDProxy that gives access to most of
    * the needed properties during a drag n drop operation.
    *
    * @param li {HTMLElement} of type li, a shared "invisible" draggable that creates "space" in the list during drag n drop.
    * @param shadow {Alfresco.util.DragAndDrop} the component (for helper functions and the current context such as selected draggable etc)
    * @param dndComponent {string} the component (for helper functions and the current context such as selected draggable etc)
    * @return {Alfresco.util.DraggableProxy} The new DraggableProxy instance
    * @constructor
    */
   Alfresco.util.DraggableProxy = function(li, shadow, dndComponent)
   {
      Alfresco.util.DraggableProxy.superclass.constructor.call(this, li);

      // Make the drag proxy slightly transparent
      var el = this.getDragEl();
      Dom.setStyle(el, "opacity", 0.67); // The proxy is slightly transparent

      // Keep track of mouse drag movements
      this.goingUp = false;
      this.lastY = 0;

      // Save a local copy of the shared shadow element.
      this.srcShadow = shadow;

      // Property to remember the element that the proxy was dropped on.
      this.droppedOnEl= null;

      this.isOver = false;

      // Save a reference to the component.
      this.dndComponent = dndComponent;

   };

   YAHOO.extend(Alfresco.util.DraggableProxy, YAHOO.util.DDProxy,
   {
      /**
       * Callback for when the user drags the draggable.
       * Will style the proxy to match the draggable.
       *
       * @method startDrag
       * @param x {int} the x position of where the drag started
       * @param y {int} the y position of where the drag started
       */
      startDrag: function DD_DP_startDrag(x, y)
      {
         // A new drag operation has started, make sure the droppedOnEl is reset.
         this.droppedOnEl = null;

         // Remove the selection of the previously focused draggable.
         if (this.dndComponent.currentEl)
         {
            this.dndComponent.currentEl.blur();
            this.dndComponent.currentEl = null;
         }

         // Make the proxy look like the source element.
         var dragEl = this.getDragEl(),
            srcEl = this.getEl(),
            className = this.dndComponent.draggables[this.dndComponent.getContainer(srcEl).getAttribute("id")].cssClass;
         dragEl.innerHTML = srcEl.innerHTML;
         if (className)
         {
            Dom.addClass(dragEl, className);
         }
         Dom.addClass(dragEl, "dnd-focused");

         // Reset YUI default border style for dragged elements
         Dom.setStyle(dragEl, "border-style", "");
         Dom.setStyle(dragEl, "border-width", "");
         Dom.setStyle(dragEl, "border-color", "");

         if (!this.dndComponent.isDraggableProtected(srcEl))
         {
            // Since the proxy looks like the draggable we can hide the actual draggable
            Dom.setStyle(srcEl, "visibility", "hidden");
         }

         // Prepare shadow for drag n drop session
         this._resetSrcShadow();
      },

      /**
       * Reset the shadow so its ready to be used when the proxy is
       * dragged over other draggables.
       *
       * @method _resetSrcShadow
       */
      _resetSrcShadow: function DD_DP__resetSrcShadow()
      {
         var srcEl = this.getEl();
         var p = srcEl.parentNode;
         if (this.dndComponent.isDraggableProtected(srcEl))
         {
            Dom.setStyle(this.srcShadow, "display", "none");
         }
         //Dom.setStyle(this.srcShadow, "visibility", "hidden");
         var height;
         if (YAHOO.env.ua.ie)
         {
            height = srcEl.offsetHeight;
         }
         else
         {
            height = Dom.getStyle(srcEl, "height");
         }
         if (height)
         {
            Dom.setStyle(this.srcShadow, "height", height);
         }
         p.insertBefore(this.srcShadow, srcEl);
      },

      /**
       * Callback for when the drag n drop session is over, is called even if
       * the proxy wasn't dropped on a target.
       *
       * Will either delete, add or leave the draggable depending on where the
       * draggable was dropped.
       *
       * @method endDrag
       * @param e {int}
       * @param id {string}
       */
      endDrag: function DD_DP_endDrag(e, id)
      {
         // Get the actual draggable and the proxy
         var srcEl = this.getEl();
         var proxy = this.getDragEl();

         // Check if the draggable was dropped on a delete target and should be deleted
         if (this.droppedOnEl && this.dndComponent.isTargetInGroup(this.droppedOnEl, DD.GROUP_DELETE))
         {
            // Only delete the draggable if its a "non protected draggable"
            if (!this.dndComponent.isDraggableProtected(srcEl))
            {
               // It was, delete it
               this.dndComponent.deleteDraggable(srcEl);
            }
            // Make sure to remove delete indication from target
            var dropColumn = this.droppedOnEl;
            if (this.dndComponent.isOfTagType(dropColumn, "li"))
            {
               dropColumn = dropColumn.parentNode;
            }
            Dom.removeClass(dropColumn, "deleteDrag");

            // Return so we don't add the draggable.
            return;
         }

         /**
          * If we get here, the draggable was either dropped on a add target,
          * the original column or just "dropped" outside a any target.
          * Either way animate the proxy to "fly" towards the shadow.
          * Since we have used the shadow to make space for the draggable during
          * the drag we can rely on that the shadow is in the position we're
          * the draggable should be placed.
          *
          * We will decide later if its an add or move that has been performed.
          */

         // Show the proxy element and animate it towards the shadow.
         Dom.setStyle(proxy, "visibility", "");
         var xy = Dom.getXY(this.srcShadow);
         if (xy)
         {
            var a = new YAHOO.util.Motion(proxy,
            {
               points:
               {
                  to: xy
               }
            }, 0.3, YAHOO.util.Easing.easeOut);

            // Save the scope of this for the callback after the animation.
            var myThis = this;

            a.onComplete.subscribe(function()
            {
               var srcShadow = myThis.srcShadow;

               // Hide proxy
               Dom.setStyle(proxy, "visibility", "hidden");

               // Insert and show the real draggable
               myThis.insertSrcEl(srcEl);

               // Hide shadow
               Dom.setStyle(srcShadow, "display", "none");
            });
            a.animate();
         }
         else
         {
            /**
             * Skip animations for browsers (IE7 and below) that doesn't return coordinates when proxy
             * is "thrown" away in a "sloppy" manner towards a clear direction
             * rather than releasing the proxy "carefully".
             */

            // Hide proxy
            Dom.setStyle(proxy, "visibility", "hidden");

            // Insert and show the real draggable
            this.insertSrcEl(srcEl);

            // Hide shadow
            Dom.setStyle(this.srcShadow, "display", "none");
         }
      },

      /**
       * Checks what was dragged and to where, so it knows if to add or move the draggable.
       *
       * @method insertSrcEl
       * @param srcEl {HTMLElement}
       */
      insertSrcEl: function DD_DP_insertSrcEl(srcEl)
      {
         // Find out to where and from the draggable was dragged.
         var destUl = this.srcShadow.parentNode;
         if (this.dndComponent.isDraggableProtected(srcEl))
         {
            // It was an "protected draggable" that was dragged, should it be added?
            if (!this.dndComponent.isDraggableProtected(this.srcShadow))
            {
               // Yes, add it since it was dropped over a none protected target.
               this.dndComponent.copyAndInsertDraggable(srcEl, destUl, this.srcShadow);
            }
         }
         else
         {
            // It was a "none protected draggale" that was dragged, move it.
            destUl.insertBefore(srcEl, this.srcShadow);
            this.dndComponent.focusDraggableAfterDomChange(srcEl, true);

            // Fire event to inform any listening components that draggable has been moved
            YAHOO.Bubbling.fire("draggableMoved",
            {
               eventGroup: this.dndComponent,
               draggable: srcEl
            }); 

         }
         // Show the new draggable.
         Dom.setStyle(srcEl, "visibility", "");
         Dom.setStyle(srcEl, "display", "");
      },

      /**
       * Callback that gets called when a element was dropped over a target.
       *
       * @method onDragDrop
       * @param event {HTMLElement}
       * @param id {string} The id of the target element the proxy was dropped over.
       */
      onDragDrop: function DD_DP_onDragDrop(event, id)
      {
         // Find the drop target and save it for later.
         var destEl = Dom.get(id);
         this.droppedOnEl = destEl;

         if (Dom.hasClass(destEl, "target"))
         {
            Dom.removeClass(destEl, "target");
         }

         if (!this.dndComponent.isTargetInGroup(destEl, DD.GROUP_MOVE))
         {
            // If it wasn't a drop target do nothing...
            return;
         }

         /**
          * Ok, it was dropped on an add target.
          *
          * Normally we would know this if the proxy was dragged above other
          * draggables ("li" elements) since we in that case would have placed
          * the shadow inside that column to "give space" for the new draggable.
          *
          * However, if the column was empty OR the proxy only was dragged over
          * the columns "free space" (not over a "li" element) the shadow
          * would not have been placed inside the column. The proxy can also
          * have been dropped over the original draggable.
          *
          * Below is the code where we check that and if that is the case, add
          * the shadow to the column so we later can decide where the draggable
          * should be placed.
          *
          */
         if (DDM.interactionInfo.drop.length === 1)
         {
            // The position of the cursor at the time of the drop (YAHOO.util.Point)
            var pt = DDM.interactionInfo.point;

            // The region occupied by the source element at the time of the drop
            var region = YAHOO.util.Region.getRegion(this.srcShadow);

            /**
             * Check to see if we are over the source element's location.
             * We will append to the bottom of the list once we are sure it
             * was a drop in the negative space (the area of the list without any list items)
             */
            if (!region.intersect(pt))
            {
               // Add only to the list if it isn't full
               destEl = Dom.get(id);
               if (!this.dndComponent.isTargetFull(destEl))
               {
                  // Add to the list
                  destEl.appendChild(this.srcShadow);

                  // Refresh the drag n drop managers cache.
                  var destDD = DDM.getDDById(id);
                  destDD.isEmpty = false;
               }
            }
         }
      },

       /**
       * Changes the cursor to give indication to user if the dragged element
       * can be dropped or not.
       *
       * @method _changeCursor
       * @param cursorState {string} A state constant from Alfresco.util.Cursor
       * @private
       */
      _changeCursor: function DD_DP__changeCursor(cursorState)
      {
         var proxy = this.getDragEl();
         var proxyEl = new Element(proxy, {});
         var span = proxyEl.getElementsByTagName("div")[0];
         Alfresco.util.Cursor.setCursorState(span, cursorState);
      },

      /**
       * Callback that gets called when the proxy is dragged out from a drop target.
       *
       * @method onDragOut
       * @param event {HTMLelement}
       * @param id {string} The id of the target element the proxy was dragged out of
       */
      onDragOut: function DD_onDragOut(event, id)
      {
         this.isOver = false;

         // Reset the droppedOn proprerty
         this.droppedOnEl = null;

         var prevDestEl = Dom.get(id);
         if (this.dndComponent.isOfTagType(prevDestEl, ["ul", "ol"]))
         {
            // Place the shadow in the draggables original position
            this._resetSrcShadow();
            this._changeCursor(Alfresco.util.Cursor.DRAG);

            if (this.dndComponent.isTargetInGroup(prevDestEl, DD.GROUP_DELETE))
            {
               Dom.removeClass(prevDestEl, "deleteDrag");
            }
         }
         if (this.dndComponent.isOfTagType(prevDestEl, "li"))
         {
            // Do nothing
         }
         else
         {
            this._changeCursor(Alfresco.util.Cursor.DRAG);
            Dom.removeClass(prevDestEl, "target");
         }
      },

      /**
       * Callback that gets called repeatedly when the proxy is dragged.
       * Keeps track of the direction the user is drawing the mouse so we on
       * the dragOver can decide if the shadow element should be placed above
       * or over other draggables.
       *
       * @method onDrag
       * @param event {HTMLElement}
       */
      onDrag: function DD_DP_onDrag(event)
      {
         // Keep track of the direction of the drag for use during onDragOver
         var y = Event.getPageY(event);
         if (y < this.lastY)
         {
            this.goingUp = true;
         }
         else if (y > this.lastY)
         {
            this.goingUp = false;
         }
         this.lastY = y;
      },

      /**
       * Callback that gets called when the proxy is over a drop target.
       * Places out the "invisible" shadow element to make space for the new
       * draggable in the column.
       *
       * @method onDragOver
       * @param event {HTMLelement}
       * @param id {string} The id of the target element the proxy was dragged over.
       */
      onDragOver: function DD_DP_onDragOver(event, id)
      {
         this.isOver = true;

         // Get the element the proxy was dragged over
         var destEl = Dom.get(id);
         var srcEl = this.getEl();

         // We are only concerned with list items, we ignore the dragover
         // notifications for the list since those are handled by onDragDrop().
         if (this.dndComponent.isOfTagType(destEl, "li"))
         {
            /**
             * Check what columns we dragged from and drag above and make sure
             * the dest columns isn't full.
             */
            if (!this.dndComponent.isTargetFull(this.dndComponent.getContainer(destEl)) ||
                this.dndComponent.getContainer(srcEl) == this.dndComponent.getContainer(destEl))
            {
               // Make sure we only add the shadow to a column and not the available draggables
               if (!this.dndComponent.isDraggableProtected(destEl))
               {
                  // Hide the original draggable since we are about to show it as a shadow somewhere else
                  if (!this.dndComponent.isDraggableProtected(srcEl))
                  {
                     Dom.setStyle(srcEl, "display", "none");
                  }

                  // Show shadow instead of original draggable next to its target
                  if (Dom.getStyle(this.srcShadow, "display") == "none")
                  {
                     Dom.setStyle(this.srcShadow, "display", "");
                  }
                  if (this.goingUp)
                  {
                     // Insert shadow before hovered li
                     destEl.parentNode.insertBefore(this.srcShadow, destEl);
                  }
                  else
                  {
                     // Insert shadow after hovered li
                     destEl.parentNode.insertBefore(this.srcShadow, destEl.nextSibling);
                  }
               }
            }
         }
         else if (this.dndComponent.isOfTagType(destEl, ["ul", "ol"]))
         {
            var destElColumnIsFull = this.dndComponent.isTargetFull(destEl),
               destElColumnIsProtected = this.dndComponent.isDraggableProtected(destEl),
               srcElColumnIsProtected = this.dndComponent.isDraggableProtected(srcEl);
            if ((!srcElColumnIsProtected && destElColumnIsProtected) || // delete: from column over available.
                  (!destElColumnIsProtected && (!destElColumnIsFull || this.dndComponent.getContainer(srcEl) == this.dndComponent.getContainer(destEl))))
            {
               // Set the cursor to indicate that the user may drop the draggable here.
               this._changeCursor(Alfresco.util.Cursor.DROP_VALID);
               Dom.addClass(destEl, "target");
               if (this.dndComponent.isTargetInGroup(destEl, DD.GROUP_DELETE))
               {
                  // Indicate that a drop means a delete
                  Dom.addClass(destEl, "deleteDrag");
               }
            }
            else if (!destElColumnIsProtected && destElColumnIsFull)
            {
               // Set the cursor to indicate that the user may NOT drop the draggable here.
               this._changeCursor(Alfresco.util.Cursor.DROP_INVALID);
            }
            else
            {
               // Cursor should be the drag cursor, keep it.
            }
         }
         else
         {
            this._changeCursor(Alfresco.util.Cursor.DROP_VALID);
            Dom.addClass(destEl, "target");
         }
      }
   });
})();
