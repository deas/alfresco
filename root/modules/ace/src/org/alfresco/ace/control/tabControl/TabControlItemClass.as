package org.alfresco.ace.control.tabControl
{
	import mx.containers.Canvas;
	import mx.containers.HBox;
	import mx.controls.Label;
	import flash.events.MouseEvent;
	import flash.events.Event;
	import mx.controls.Alert;
	
	/**
	 * The tab control item class
	 * 
	 * @author Roy Wetherall
	 */
	public class TabControlItemClass extends HBox
	{
		/** The label UI control */
		public var itemLabel:Label;
		
		/** Indicates whether the control has been created or not */
		private var _created:Boolean = false;
		
		/** Indicates whether the item has been selected or not */
		[Inspectable]
		private var _selected:Boolean = false;
		
		/** The value of the tab control */
		[Inspectable]
		private var _value:Object;
		
		/**
		 * Create children override
		 */
		protected override function createChildren():void
		{
			// Ensure the hand cursor is shown on roll over
			this.useHandCursor = true;
			this.buttonMode = true;
			this.mouseChildren = false;
			
			super.createChildren();
			
			// Set the value of the label
			this.itemLabel.text = this.label;
			
			// Update the control with the current selected state
			drawSelectedState();
			
			// Set the created flag
			this._created = true;
		}
		
		/**
		 * Getter for selected value.
		 * 
		 * True is selected, false otherwise
		 */
		public function get selected():Boolean
		{
			return this._selected;
		}
		
		/**
		 * Setter for selected value.
		 */
		public function set selected(value:Boolean):void
		{
			if (value != this._selected)
			{
				this._selected = value;
				
				if (this._created == true)
				{
					drawSelectedState();	
				}
			}
		}
		
		/**
		 * Getter for value property
		 */
		public function get value():Object
		{
			return this._value;
		}
		
		/**
		 * Settter for value property
		 */
		public function set value(value:Object):void
		{
			this._value = value;
		}
		
		/**
		 * Draw's the currently selected state
		 */
		private function drawSelectedState():void
		{
			if (this._selected == true)
			{
				this.currentState = "highlighted";	
			}
			else
			{
				this.currentState = "";
			}
		}
		
		/**
		 * On click event handler
		 */
		public function onClick():void
		{
			this.dispatchEvent(new MouseEvent(MouseEvent.CLICK));
		}				
	}
}