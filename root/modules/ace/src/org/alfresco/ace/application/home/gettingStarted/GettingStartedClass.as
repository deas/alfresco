package org.alfresco.ace.application.home.gettingStarted
{
	import mx.controls.VideoDisplay;
	import mx.containers.Box;
	import mx.states.State;
	import mx.states.SetProperty;
	import mx.events.VideoEvent;
	import flash.events.MouseEvent;
	import flash.events.Event;
	import mx.events.CuePointEvent;
	import mx.effects.Sequence;
	import mx.effects.Fade;
	import mx.events.EffectEvent;
	import mx.containers.VBox;
	import mx.events.MoveEvent;
	import mx.events.ResizeEvent;
	import mx.controls.Alert;
	import org.alfresco.ace.control.textAccordian.TextAccordian;
	import org.alfresco.ace.control.textAccordian.TextAccordianSelectionChangeEvent;
	import org.alfresco.ace.application.home.HomePanelContainer;
	import org.alfresco.ace.control.textAccordian.TextAccordianItem;
	import org.alfresco.framework.service.authentication.AuthenticationService;
	import org.alfresco.framework.service.authentication.LogoutCompleteEvent;
	
	/**
	 * Getting started home panel
	 * 
	 * @author Roy Wetherall
	 */
	public class GettingStartedClass extends HomePanelContainer
	{	
		/** UI Controls */
		public var videoDisplay:VideoDisplay;
		public var playBox:VBox;
		public var pauseBox:VBox;
		public var textAccordian:TextAccordian;
		public var firstItem:TextAccordianItem;
		
		/** Effects */
		private var _showEffect:Sequence;
		private var _beforePlayEffect:Fade;
		private var _afterPlayEffect:Fade;
		
		/** Indicates whether the video has been paused or not */
		private var _videoPaused:Boolean = false;
				
		/**
		 * createChildren override
		 */
		override protected function createChildren():void
		{
			super.createChildren();		
			
			// Ensure the hand cursor is shown on roll over of the video control
			this.videoDisplay.useHandCursor = true;
			this.videoDisplay.buttonMode = true;
			this.videoDisplay.mouseChildren = false;
			this.playBox.useHandCursor = true;
			this.playBox.buttonMode = true;
			this.playBox.mouseChildren = false;
			this.pauseBox.useHandCursor = true;
			this.pauseBox.buttonMode = true;
			this.pauseBox.mouseChildren = false;
			
			// Create the fade effects
			this._beforePlayEffect = new Fade();
			this._beforePlayEffect.target = this.videoDisplay;
			this._beforePlayEffect.alphaFrom = 0.25;
			this._beforePlayEffect.alphaTo = 1.0;
			this._beforePlayEffect.addEventListener(EffectEvent.EFFECT_END, onBeforePlayEffectEnd);		
			
			this._afterPlayEffect = new Fade();
			this._afterPlayEffect.target = this.videoDisplay;
			this._afterPlayEffect.alphaFrom = 1.0;
			this._afterPlayEffect.alphaTo = 0.25;	
			
			// Register interest in events
			this.videoDisplay.addEventListener(VideoEvent.COMPLETE, onComplete);
			this.videoDisplay.addEventListener(MouseEvent.CLICK, onClick);	
			this.playBox.addEventListener(MoveEvent.MOVE, onPlayBoxMove);
			this.pauseBox.addEventListener(MoveEvent.MOVE, onPauseBoxMove);
			this.playBox.addEventListener(MouseEvent.CLICK, onClick);	
			this.pauseBox.addEventListener(MouseEvent.CLICK, onClick);	
			this.textAccordian.addEventListener(TextAccordianSelectionChangeEvent.SELECTION_CHANGE, onSelectionChange);		
			AuthenticationService.instance.addEventListener(LogoutCompleteEvent.LOGOUT_COMPLETE, onLogoutComplete);
		}
		
		/**
		 * showComplete override
		 */
		override protected function showComplete():void
		{
			this._videoPaused = false;
			this.pauseBox.visible = false;
			this.playBox.visible = true;
			
			this.textAccordian.selectedItem = firstItem;
		}
		
		/**
		 * onComplete event handler
		 */
		private function onComplete(event:VideoEvent):void
		{
			this._afterPlayEffect.play();
			this.showPlay = true;				
		}
		
		/**
		 * onClick event handler for the video control
		 */
		private function onClick(event:Event):void
		{
			if (this.videoDisplay.playing == false)
			{	
				if (this._videoPaused == false)
				{			
					this.showPlay = false;
					this._beforePlayEffect.play();
				}
				else
				{										
					this.showPaused = false;
				}
			}	
			else
			{								
				this.showPaused = true;
			}
		}
		
		private function onSelectionChange(event:TextAccordianSelectionChangeEvent):void
		{
			// If the video is playing stop it
			var wasPlaying:Boolean = false;
			if (this.videoDisplay.playing == true)
			{
				this.videoDisplay.stop();
				wasPlaying = true;
			}
			
			// Set the video source
			this.videoDisplay.source = textAccordian.selectedItem.value as String;
			this.videoDisplay.alpha = 0.25;
			
			var oldVolume:Number = this.videoDisplay.volume;
			this.videoDisplay.volume = 0;
			this.videoDisplay.play();
			this.videoDisplay.stop();
			this.videoDisplay.volume = oldVolume;
	
			if (this.showPlay == false)
			{
				this._afterPlayEffect.play();
				this.showPlay = true;
			}
		}
		
		/**
		 * onLogoutComplete event handler
		 */
		private function onLogoutComplete(event:Event):void
		{
			// Pause the video
			this.showPaused = true;
		}
		
		private function onBeforePlayEffectEnd(event:Event):void
		{						
			this.videoDisplay.play();
		}
		
		private function set showPlay(value:Boolean):void
		{
			if (this.playBox.visible != value)
			{
				if (value == true)
				{		
					// Set the co-ordinates of the play box
					this.playBox.x = this.videoDisplay.x;
					this.playBox.y = this.videoDisplay.y;
					
					// Just in case, hide the pause box
					if (this._videoPaused == true)
					{
						this._videoPaused = false;
						this.pauseBox.visible = false;
					}
					
					// Show as visible	
					this.playBox.visible = true;
				}
				else
				{
					// Hide
					this.playBox.visible = false;					
				}
			}
		}
		
		private function get showPlay():Boolean
		{
			return this.playBox.visible;
		}
		
		private function set showPaused(value:Boolean):void
		{
			if (this._videoPaused != value)
			{
				this._videoPaused = value;
				
				if (value == true)
				{
					this.videoDisplay.pause();
					this.videoDisplay.alpha = 0.5;
					
					// Set the co-ordinates of the pause box
					this.pauseBox.x = this.videoDisplay.x;
					this.pauseBox.y = this.videoDisplay.y;
					
					// Show as visible	
					this.pauseBox.visible = true;
				}
				else
				{
					// Hide
					this.pauseBox.visible = false;
					
					this.videoDisplay.alpha = 1.0;
					this.videoDisplay.play();
				}
			}
		}
		
		private function onPlayBoxMove(event:Event):void
		{
			if (this.playBox.visible == true)
			{
				// Set the co-ordinates of the play box
				this.playBox.x = this.videoDisplay.x;
				this.playBox.y = this.videoDisplay.y;	
			}	
		}
		
		private function onPauseBoxMove(event:Event):void
		{
			if (this.pauseBox.visible == true)
			{
				// Set the co-ordinates of the pause box
				this.pauseBox.x = this.videoDisplay.x;
				this.pauseBox.y = this.videoDisplay.y;	
			}	
		}
	}
}