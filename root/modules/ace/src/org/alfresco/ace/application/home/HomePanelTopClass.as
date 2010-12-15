package org.alfresco.ace.application.home
{
	import mx.containers.Canvas;
	import mx.controls.Label;
	import flash.events.MouseEvent;
	import flash.events.Event;

	public class HomePanelTopClass extends Canvas
	{
		public static const MINMAX_CLICK_EVENT:String = "minMaxClickEvent";
		public static const CLOSE_CLICK_EVENT:String = "closeClickEvent";
		
		/** UI controls */
		public var labelTitle:Label;
		public var minMaxButton:mmButton;
		// TODO
		//public var closeButton:Label;
		
		/** Panel title string */
		private var _title:String;
		
		/**
		 * Constructor
		 */
		public function HomePanelTopClass()
		{
			super();
		}				
			
		/**	
		 * Panel title property setter
		 */
		public function set title(value:String):void
		{
			this._title = value;
		}		
	
		/**
		 * createChildren override
		 */	
		override protected function createChildren():void
		{
			super.createChildren();
			
			// Set the title
			this.labelTitle.text = this._title;
						
			// Register event handlers
			this.minMaxButton.addEventListener(MouseEvent.CLICK, onMinMaxClick);
			// TODO
			//this.closeButton.addEventListener(MouseEvent.CLICK, onCloseClick);
		}
	
		/**
		 * On minMax button click event handler
		 */
		private function onMinMaxClick(event:Event):void
		{
			this.dispatchEvent(new Event(MINMAX_CLICK_EVENT));		
		}
		
		// TODO
		/**
		 * On close button click event handler
		 */
		//private function onCloseClick(event:Event):void
		//{
		//	this.dispatchEvent(new Event(CLOSE_CLICK_EVENT));	
		//}		
	}
}