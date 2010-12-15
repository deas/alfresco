/*  
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
 
package org.alfresco.ace.control.hyperlink
{
	import mx.controls.Text;
	import flash.events.MouseEvent;
	
	/**
	 * Hyper link control
	 * 
	 * @author Saravanan Sellathurai
	 * @author Roy Wetherall
	 */

	public class HyperLink extends Text
	{
		
		/** The rolled over state name */
		[Inspectable]
		private var _rolledOverStyleName:Object;
		
		/** Indicates whether the control is currently rolled over or not */
		private var _rolledOver:Boolean = false;
		
		/** The origional style name, used to recover after roll out */
		private var _origionalStyleName:Object;
			
		/**		
		 * Constructor
		 */
		public function HyperLink()
		{
			// Ensure the hand cursor is shown on roll over
			this.useHandCursor = true;
			this.buttonMode = true;
			this.mouseChildren = false;
			
			// Call the super class
			super();
			
			// Register interest in the event handlers
			addEventListener(MouseEvent.ROLL_OVER, onRollOver);
			addEventListener(MouseEvent.ROLL_OUT, onRollOut);
		}
	  	
		
		/**
		 * On roll over event handler
		 */
		private function onRollOver(event:MouseEvent):void
		{
			if (this._rolledOver == false)
			{
				this._origionalStyleName = this.styleName;	
				this.styleName = this._rolledOverStyleName;
				this._rolledOver = true;
			}		
		}

		/**
		 * On roll out event handler
		 */
		private function onRollOut(event:MouseEvent):void
		{
			if (this._rolledOver == true)
			{
				this.styleName = this._origionalStyleName;
				this._rolledOver = false;
			}	
		}
		
		/**
		 * Getter for the rolled over style name
		 */
		public function get rolledOverStyleName():Object
		{
			return this._rolledOverStyleName;
		}
		
		/** 
		 * Setter for the rolled over style name
		 */		
		public function set rolledOverStyleName(value:Object):void
		{
			this._rolledOverStyleName = value;
		}
	}

}