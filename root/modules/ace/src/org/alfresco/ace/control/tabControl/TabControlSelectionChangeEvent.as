package org.alfresco.ace.control.tabControl
{
	import flash.events.Event;

	/**
	 * Tab control selection change event
	 *
	 * @author Roy Wetherall
	 */
	public class TabControlSelectionChangeEvent extends Event
	{
		/** Selection change event name */
		public static const SELECTION_CHANGE:String = "tabControlSelectionChange";
		
		/** Selected tab control item */
		private var _selectedItem:TabControlItem;
		
		/** 
		 * Constructor
		 */
		public function TabControlSelectionChangeEvent(type:String, selectedItem:TabControlItem, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this._selectedItem = selectedItem;
		}
		
		/**
		 * Getter for selected item
		 */
		public function get selectedItem():TabControlItem
		{
			return this._selectedItem;
		}
	}
}