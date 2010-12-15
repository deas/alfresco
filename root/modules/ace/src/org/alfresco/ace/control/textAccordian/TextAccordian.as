package org.alfresco.ace.control.textAccordian
{
	import mx.containers.Canvas;
	import mx.containers.VBox;
	import mx.core.UIComponent;
	import mx.core.IUIComponent;
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	import flash.events.Event;

	/**
	 * Text accoridan UI control
	 * 
	 * @author Roy Wetherall
	 */
	public class TextAccordian extends VBox
	{
		/** Array containing text accordian items */
		private var _items:Array;
		
		/** The currently selected text accordian item */
		private var _selectedItem:TextAccordianItem;
		
		/**
		 * Create children method override
		 */
		protected override function createChildren():void
		{
			super.createChildren();
			
			this.verticalScrollPolicy = "off";
			this.horizontalScrollPolicy = "off";
			
			var numChildren:int = this.getChildren().length;
			var i:int;
			var itemSelected:Boolean = false;
			this._items = new Array(numChildren);
			for (i = 0; i < numChildren; i++) 
			{
	    		var child:DisplayObject = getChildAt(i);	
				if (child is TextAccordianItem)
				{
					var item:TextAccordianItem = child as TextAccordianItem;
					this._items[i] = item;	
					
					if (item.expanded == true)
					{
						if (itemSelected == false)
						{
							this._selectedItem = item;
						}	
						else
						{
							// We already have an expanded item so shrink this one
							item.expanded = false;
						}
					}
					
					// Register interest in events
					item.addEventListener(MouseEvent.CLICK, onClick);
				}
				else
				{
					throw new Error("All children of the TextAccordian control must be TextAccordianItem's");
				}
			}
			
			// Expand the first text item if none have been expanded
			if (itemSelected == false)
			{
				(this._items[0] as TextAccordianItem).expanded = true;
				this._selectedItem = this._items[0] as TextAccordianItem;
			}
		}	
		
		/**
		 * Set the currently selected item
		 */
		public function set selectedItem(value:TextAccordianItem):void
		{
			if (value != this._selectedItem)
			{
				if (this._selectedItem != null)
				{
					this._selectedItem.expanded = false;
				}
				if (value != null)
				{
					this._selectedItem = value;
					this._selectedItem.expanded = true;
					
				}
				
				// Dispatch the selection change event
				dispatchEvent(new TextAccordianSelectionChangeEvent(TextAccordianSelectionChangeEvent.SELECTION_CHANGE, this._selectedItem));
			}			
		}
		
		/**
		 * Get the currently selected item
		 */
		public function get selectedItem():TextAccordianItem
		{
			return this._selectedItem;
		}
		
		/**
		 * The click event handler
		 */
		private function onClick(event:Event):void
		{
			if (event.target as TextAccordianItem)
			{
				var clickedItem:TextAccordianItem = event.target as TextAccordianItem;
				if (clickedItem != this._selectedItem)
				{
					this.selectedItem = clickedItem;
				}
			}	
		}
	}
}