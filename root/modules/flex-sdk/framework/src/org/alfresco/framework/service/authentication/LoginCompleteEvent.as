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
 package org.alfresco.framework.service.authentication
{
	import flash.events.Event;

	/**
	 * Login complete event object
	 * 
	 * @author Roy Wetherall
	 */
	public class LoginCompleteEvent extends Event
	{
		/** Event name */
		public static const LOGIN_COMPLETE:String = "loginComplete";
		
		/** The ticket created during login */
		private var _ticket:String;
		
		/** The user name logged in */
		private var _userName:String;
		
		/**
		 * Constructor
		 */
		public function LoginCompleteEvent(type:String, ticket:String, userName:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this._ticket = ticket;
			this._userName = userName;		
		}
		
		/**
		 * Getter for the ticket property
		 */
		public function get ticket():String
		{
			return this._ticket;
		}
		
		/**
		 * Getter for the userName property
		 */
		public function get userName():String
		{
			return this._userName;
		}
	}
}