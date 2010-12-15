package org.alfresco.ace.control.swipe
{
	import mx.containers.Canvas;
	import mx.events.ChildExistenceChangedEvent;
	import mx.events.FlexEvent;
	import flash.events.Event;
	import mx.core.UIComponent;

    /**
     * General purpose Swipe control
     */
	public class Swipe extends Canvas
	{
		/** Internal swipe control */
		private var swipe:SwipeInternal;
		
		/** Control labels */
		[Inspectable]
		private var _primaryStateLabel:String;
		[Inspectable]
		private var _secondaryStateLabel:String;
		
		/**
		 * Constructor
		 */
		public function Swipe()
		{
			super();
			
			// Register interest in the creation complete event
			this.addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
		}	
		
		public function get primaryStateLabel():String
		{
			return this._primaryStateLabel;	
		}			
		
		public function set primaryStateLabel(label:String):void
		{
			this._primaryStateLabel = label;
		}		
		
		public function get secondaryStateLabel():String
		{
			return this._secondaryStateLabel;	
		}			
		
		public function set secondaryStateLabel(label:String):void
		{
			this._secondaryStateLabel = label;
		}
		
		/**
		 * Create complete event handler
		 */
		public function onCreationComplete(event:Event):void
		{
			var children:Array = this.getChildren();
			if (children.length != 2)
			{
				throw new Error("Control expects two child UI objects");
			}
			
			var childOne:UIComponent = children[0];
			var childTwo:UIComponent = children[1];			
			
			this.removeAllChildren();
			
			this.swipe = new SwipeInternal();
			swipe.setChildOne(childOne, this._primaryStateLabel);
			swipe.setChildTwo(childTwo, this._secondaryStateLabel);
			
			// Add the configured swipe control
			this.addChild(swipe);	
		}
		
		/**	
		 * Shows the primary state
		 */
		public function showPrimaryState():void
		{
			swipe.showPrimaryState();	
		}
		
		/**
		 * Shows the secondard state
		 */
		public function showSecondaryState():void
		{
			swipe.showSecondaryState();	
		}		
	}
}