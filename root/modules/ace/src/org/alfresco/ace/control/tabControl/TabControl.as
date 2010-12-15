package org.alfresco.ace.control.tabControl
{
	import mx.containers.Canvas;
	import mx.containers.HBox;
	import mx.controls.Button;
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	import mx.core.UIComponent;
	import mx.controls.Alert;

	/**
	 * ACE tab control UI component
	 * 
	 * @author Roy Wetherall
	 */
	public class TabControl extends HBox
	{
		/** The currently selected item */
		private var _selectedItem:TabControlItem;
		
		/** The items and separators */
		private var _items:Array;
		private var _separators:Array;

		/**
		 * Getter for the currently selected item
		 */
		public function get selectedItem():TabControlItem
		{
			return this._selectedItem;
		}

		/**
		 * Create children override
		 */
		protected override function createChildren():void
		{
			super.createChildren();
			
			// Create a list of all the tab control items
			var tabSelected:Boolean = false;
			var numChildren:int = this.getChildren().length;
			this._items = new Array(numChildren);
			this._separators = new Array(numChildren+1);
			var itemIndex:int = 0;
			var sepIndex:int = 0;
			var maxIndex:int = (numChildren*2)+1;
			for (var i:int = 0; i < maxIndex; i++) 
			{
	    		var child:DisplayObject = getChildAt(i);	
				if (child is TabControlItem)
				{
					var item:TabControlItem = child as TabControlItem;
					this._items[itemIndex] = item;	
					itemIndex++;
					
					if (i == 0)
					{
						// Add a separator before the first tab
						var firstSep:TabControlSeparator = new TabControlSeparator();
						firstSep.showDivider = false;
						this._separators[sepIndex] = firstSep;
						sepIndex ++;
						this.addChildAt(firstSep, i);
						i++;						
					}
					
					// Add a separator after the item
					var sep:TabControlSeparator = new TabControlSeparator();
					if (sepIndex == numChildren)
					{
						sep.showDivider = false;
					}
					this._separators[sepIndex] = sep;
					sepIndex ++;					
					this.addChildAt(sep, i+1);
					i++;
					
					// Register interest in the click event
					item.addEventListener(MouseEvent.CLICK, onClick);
					
					// Check for seleted state
					if (item.selected == true)
					{
						if (tabSelected == false)
						{
							tabSelected = true;
							this._selectedItem = item;
						}
						else
						{
							// A tab had already been specified as selected so set subsequent ones to be unslected
							item.selected = false;
						}
					}
				}
				else
				{
					throw new Error("All children of the TabControl control must be TabControlItem's");
				}
			}
			
			// Select the first tab if none have been selected
			if (tabSelected == false)
			{
				(this._items[0] as TabControlItem).selected = true;
				this._selectedItem = this._items[0] as TabControlItem;
			}
			
			// Refresh the state of the separators
			refreshSeparators();
		}	
		
		/**
		 * Refresh the separator display states based on current selection
		 */
		private function refreshSeparators():void
		{
			var selectedItemIndex:int = this._items.indexOf(this._selectedItem);
			for (var index:int = 0; index < this._separators.length; index++)
			{
				var sep:TabControlSeparator = this._separators[index] as TabControlSeparator;
				if (index == selectedItemIndex)
				{
					// Set in left hand state
					sep.currentState = "highlightedLeft";					
				}	
				else if (index == selectedItemIndex+1)
				{
					sep.currentState = "highlightedRight";
				}
				else
				{
					sep.currentState = "";
				}
			}
		}
		
		/**
		 * On click event handler for tab control item
		 */
		private function onClick(event:MouseEvent):void
		{
			var selectedItem:TabControlItem = event.target as TabControlItem;
			if (selectedItem != null)
			{
				// Update the selection
				updateSelection(selectedItem);	
			}
		}	
		
		/**
		 * Updates the current selection.
		 * 
		 * Throws the selection change event
		 */
		private function updateSelection(selectedItem:TabControlItem):void
		{
			if (selectedItem != this._selectedItem)
			{
				// Update the selected item
				this._selectedItem.selected = false;
				selectedItem.selected = true;
				this._selectedItem = selectedItem;	
				
				// Refresh the separators
				refreshSeparators();
				
				// Dispatch the selection change event
				dispatchEvent(new TabControlSelectionChangeEvent(TabControlSelectionChangeEvent.SELECTION_CHANGE, this._selectedItem));
			}
		}
	}
}