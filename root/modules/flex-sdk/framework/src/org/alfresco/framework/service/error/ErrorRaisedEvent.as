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
 package org.alfresco.framework.service.error
{
	import flash.events.Event;

	/**
	 * Error raised event class
	 *
	 * @author Roy Wetherall
	 */
	public class ErrorRaisedEvent extends Event
	{
		/** Event name */
		public static const ERROR_RAISED:String = "errorRaised";
		
		/** The error being raised */
		private var _error:Error;
		
		/** The error type used to filter when handling the error */
		private var _errorType:String;
		
		/**
		 * Constructor
		 */
		public function ErrorRaisedEvent(type:String, errorType:String, error:Error, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this._error = error;
			this._errorType = errorType;
		}
		
		/**
		 * Getter for the error property
		 */
		public function get error():Error
		{
			return this._error;
		}
		
		/**
		 * Getter for the errorType property
		 */
		public function get errorType():String
		{
			return this._errorType;	
		}
		
	}
}