package org.alfresco.ace.control.textAccordian
{
	import flash.events.Event;

	/**
	 * Text accordian selection change event.
	 * 
	 * @author Roy Wetherall
	 */
	public class TextAccordianSelectionChangeEvent extends Event
	{
		/** Event name */
		public static const SELECTION_CHANGE:String = "textAccordianSelectionChange";
		
		/** The selected item */
		private var _selectedItem:TextAccordianItem;
		
		/**
		 * Constructor
		 */
		public function TextAccordianSelectionChangeEvent(type:String, selectedItem:TextAccordianItem, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			// Set the selected item
			this._selectedItem = selectedItem;
		}
		
		/**
		 * Selected item getter
		 */
		 public function get selectedItem():TextAccordianItem
		 {
		 	return this._selectedItem;
		 }
		
	}
}