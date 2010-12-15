package org.alfresco.ace.control.textAccordian
{
	import mx.containers.Canvas;
	import mx.controls.Label;
	import mx.containers.VBox;
	import flash.events.MouseEvent;
	import flash.events.Event;
	import mx.controls.Alert;
	import org.alfresco.ace.control.swipe.Swipe;
	import mx.effects.WipeDown;
	import mx.effects.WipeUp;
	import mx.effects.Move;
	import mx.controls.Image;
	import mx.effects.Sequence;
	import mx.effects.Fade;

	/**
	 * Text accordian item UI control
	 *
	 * @author Roy Wetherall
	 */
	public class TextAccordianItemClass extends Canvas
	{
		/** Effect durtaion */
		private static const EFFECT_DURATION:Number = 400;
		
		/** UI controls */
		public var itemTitle:Label;
		public var itemContent:Label;
		public var contentVBox:VBox;
		public var itemPointer:Image;
		public var itemPointerExpanded:Image;
		
		/** Title property */
		[Inspectable]
		private var _title:String = "";
		
		/** Content property */
		[Inspectable]
		private var _content:String = "";
		
		/** Value slot of the item */
		[Inspectable]
		private var _value:Object;
						
		/** Indicates whether the control has been created or not */				
		private var _created:Boolean = false;
		
		/** Indicates wehther the control is expanded or not */
		private var _expanded:Boolean = false;
		
		/**
		 * create children override
		 */
		protected override function createChildren():void
		{
			super.createChildren();
			
			// Show hand cursor over clickable controls
			this.itemTitle.useHandCursor = true;
			this.itemTitle.buttonMode = true;
			this.itemTitle.mouseChildren = false;
			this.itemPointer.useHandCursor = true;
			this.itemPointer.buttonMode = true;
			this.itemPointer.mouseChildren = false;
			this.itemPointerExpanded.useHandCursor = true;
			this.itemPointerExpanded.buttonMode = true;
			this.itemPointerExpanded.mouseChildren = false;
			
			// Set-up display
			this._created = true;
			refreshDisplay();	
			
			// Create and assign effect		
			var fadeEffect:Fade = new Fade();
			fadeEffect.duration = EFFECT_DURATION;
			
			this.contentVBox.setStyle("showEffect", fadeEffect);
			this.contentVBox.setStyle("hideEffect", fadeEffect);
			this.itemPointer.setStyle("showEffect", fadeEffect);
			this.itemPointer.setStyle("hideEffect", fadeEffect);			
			this.itemPointerExpanded.setStyle("showEffect", fadeEffect);
			this.itemPointerExpanded.setStyle("hideEffect", fadeEffect);
			
			var moveEffect:Move = new Move(this);	
			moveEffect.duration = EFFECT_DURATION;	
			this.setStyle("moveEffect", moveEffect);
			
			// Register interest in events
			this.itemTitle.addEventListener(MouseEvent.CLICK, onClick);	
			this.itemPointer.addEventListener(MouseEvent.CLICK, onClick);
			this.itemPointerExpanded.addEventListener(MouseEvent.CLICK, onClick);
		}
		
		/**
		 * Title property getter
		 */
		public function get title():String
		{
			return this._title;	
		}
		
		/**
		 * Title property setter
		 */
		public function set title(value:String):void
		{
			this._title = value;
			refreshDisplay();
		}
		
		/**
		 * Content property getter
		 */
		public function get content():String
		{
			return this._content;
		}
		
		/**
		 * Content property setter
		 */
		public function set content(value:String):void
		{
			this._content = value;
			refreshDisplay();
		}
		
		/**
		 * Value property getter
		 */
		public function get value():Object
		{
			return this._value;
		}
		
		/**
		 * Value property setter
		 */
		public function set value(value:Object):void
		{
			this._value = value;
		}
		
		/**
		 * Indicates whether the content of the control is currently being shown
		 */
		public function get expanded():Boolean
		{
			return this._expanded;
		}
		
		/**
		 * Set value to hide/show the content of the item
		 */
		public function set expanded(value:Boolean):void
		{
			if (value != this._expanded)
			{
				this._expanded = value;
				this.contentVBox.includeInLayout = value;
				this.contentVBox.visible = value;
				
				if (value == true)
				{
					itemPointer.visible = false;
					itemPointerExpanded.visible = true;
					itemTitle.styleName = "titleText";
				}
				else
				{
					itemPointer.visible = true;
					itemPointerExpanded.visible = false;
					itemTitle.styleName = "collapsedTitleText";
				}
			}
		}
		
		/**
		 * Refreshes the display of the item
		 */
		private function refreshDisplay():void
		{
			if (this._created == true)
			{
				this.itemTitle.text = this._title;
				this.itemContent.text = this._content;
			}
		}
		
		/**
		 * onClick event handler
		 */
		private function onClick(event:Event):void
		{
			this.dispatchEvent(new MouseEvent(MouseEvent.CLICK));
		}
		
	}
}