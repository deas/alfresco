/**
 * Patch to Menu to allow empty groups to remain in the menu structure.
 * Required by: Sites dynamic drop-down menu.
 * Patches: YUI 2.6.0 to 2.9.0
 * Escalated: Yes, but closed as "by design"
 */
(function()
{
   var Lang = YAHOO.lang,
      Dom = YAHOO.util.Dom,
      _FIRST_OF_TYPE = "first-of-type";

   YAHOO.widget.Menu.prototype._removeItemFromGroupByIndex = function (p_nGroupIndex, p_nItemIndex, p_keepEmptyGroup) {

       var nGroupIndex = Lang.isNumber(p_nGroupIndex) ? p_nGroupIndex : 0,
           aGroup = this._getItemGroup(nGroupIndex),
           aArray,
           oItem,
           oUL;

       if (aGroup) {

           aArray = aGroup.splice(p_nItemIndex, 1);
           oItem = aArray[0];

           if (oItem) {

               // Update the index and className properties of each member
               this._updateItemProperties(nGroupIndex);

               if (aGroup.length === 0 && !p_keepEmptyGroup) {

                   // Remove the UL
                   oUL = this._aListElements[nGroupIndex];

                   if (oUL && oUL.parentNode) {
                       oUL.parentNode.removeChild(oUL);
                   }

                   // Remove the group from the array of items
                   this._aItemGroups.splice(nGroupIndex, 1);

                   // Remove the UL from the array of ULs
                   this._aListElements.splice(nGroupIndex, 1);

                   /*
                        Assign the "first-of-type" class to the new first UL
                        in the collection
                   */
                   oUL = this._aListElements[0];

                   if (oUL) {
                       Dom.addClass(oUL, _FIRST_OF_TYPE);
                   }
               }

               this.itemRemovedEvent.fire(oItem);
               this.changeContentEvent.fire();
           }
       }

      // Return a reference to the item that was removed
      return oItem;
   };

   YAHOO.widget.Menu.prototype._removeItemFromGroupByValue = function (p_nGroupIndex, p_oItem, p_keepEmptyGroup) {

       var aGroup = this._getItemGroup(p_nGroupIndex),
           nItems,
           nItemIndex,
           returnVal,
           i;

       if (aGroup) {
           nItems = aGroup.length;
           nItemIndex = -1;

           if (nItems > 0) {
               i = nItems-1;
               do {
                   if (aGroup[i] == p_oItem) {
                       nItemIndex = i;
                       break;
                   }
               }
               while (i--);

               if (nItemIndex > -1) {
                   returnVal = this._removeItemFromGroupByIndex(p_nGroupIndex, nItemIndex, p_keepEmptyGroup);
               }
           }
       }
       return returnVal;
   };

   YAHOO.widget.Menu.prototype.removeItem = function (p_oObject, p_nGroupIndex, p_keepEmptyGroup) {
       var oItem,
          returnVal;

       if (!Lang.isUndefined(p_oObject)) {
           if (p_oObject instanceof YAHOO.widget.MenuItem) {
               oItem = this._removeItemFromGroupByValue(p_nGroupIndex, p_oObject, p_keepEmptyGroup);
           }
           else if (Lang.isNumber(p_oObject)) {
               oItem = this._removeItemFromGroupByIndex(p_nGroupIndex, p_oObject, p_keepEmptyGroup);
           }

           if (oItem) {
               oItem.destroy();
               returnVal = oItem;
           }
       }

      return returnVal;
   };
})();

/**
 * Patch to Container to prevent IE6 trying to set properties on elements that have been removed from the DOM.
 * This function is called via a setTimer(), so this patch fixes a race condition.
 * Required by: Document List "Loading Document Library..." pop-up.
 * Patches: YUI 2.7.0 to 2.9.0
 */
(function()
{
   /**
   * Adjusts the size of the shadow based on the size of the element.
   * @method sizeUnderlay
   */
   YAHOO.widget.Panel.prototype.sizeUnderlay = function()
   {
       var oUnderlay = this.underlay,
           oElement;

       if (oUnderlay) {
           oElement = this.element;
           if (oElement) {
              oUnderlay.style.width = oElement.offsetWidth + "px";
              oUnderlay.style.height = oElement.offsetHeight + "px";
           }
       }
   };
})();