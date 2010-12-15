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
package org.alfresco.ace.service.userdetails
{
	import org.alfresco.framework.service.authentication.AuthenticationService;
	import org.alfresco.framework.service.authentication.LoginCompleteEvent;
	import org.alfresco.framework.service.authentication.LogoutCompleteEvent;
	import org.alfresco.framework.service.webscript.SuccessEvent;
	import flash.events.EventDispatcher;
	import org.alfresco.framework.service.webscript.WebScriptService;
	import org.alfresco.framework.service.webscript.ConfigService;
	
	/**
	 * User details class.  Holds details of the currently authenticated user.
	 */
	public class UserDetails extends EventDispatcher
	{
		/** Visibility levels */
		public static const VISIBILITY_INTERNAL:String = "kb:visibility-internal";
		public static const VISIBILITY_TIER_1:String = "kb:visibility-tier-one";
		public static const VISIBILITY_TIER_2:String = "kb:visibility-tier-two";
		public static const VISIBILITY_TIER_3:String = "kb:visibility-tier-three";
		
		/** Singleton instance */
		private static var _instance:UserDetails;
		
		/** User visibility */
		private var _visibility:String;
		
		/** User's first name */
		private var _firstName:String;
		
		/** User's last name */
		private var _lastName:String;
		
		/**
		 * Getter for the singleton instance
		 */
		public static function get instance():UserDetails
		{
			if (UserDetails._instance == null)
			{
				UserDetails._instance = new UserDetails();
			}	
			return UserDetails._instance;
		}	
		
		/**
		 * Constructor
		 */
		public function UserDetails():void
		{
			// Register interest in the login/logout events
			AuthenticationService.instance.addEventListener(LoginCompleteEvent.LOGIN_COMPLETE, onLoginComplete);			
			AuthenticationService.instance.addEventListener(LogoutCompleteEvent.LOGOUT_COMPLETE, onLogoutComplete);
			
			// If we are already loged in, populate the user details
			if (AuthenticationService.instance.isLoggedIn == true)
			{
				populateUserDetails();
			}
		}
		
		/**
		 * Getter for the visibility of the user
		 */
		public function get visibility():String
		{
			return this._visibility;
		}
		
		/**
		 * Getter for the users first name
		 */
		public function get firstName():String
		{
			return this._firstName;
		}
		
		/**
		 * Getter for the users last name
		 */
		public function get lastName():String
		{
			return this._lastName;
		}
		
		/**
		 * onLoginComplete event handler
		 */
		private function onLoginComplete(event:LoginCompleteEvent):void
		{
			// Populate the user details
			populateUserDetails();	
		}
		
		/**
		 * onLogoutComplete event handler
		 */
		private function onLogoutComplete(eventL:LogoutCompleteEvent):void
		{
			// Clear the user details
			this._firstName = null;
			this._lastName = null;
			this._visibility = null;	
			
			// Raise the user details change event
			dispatchEvent(new UserDetailsChangedEvent(UserDetailsChangedEvent.USER_DETAILS_CHANGED));
		}
		
		/**
		 * Populate the user's details
		 */
		private function populateUserDetails():void
		{
			// Call the user details web script
			var url:String = ConfigService.instance.url +  "/alfresco/service/kb/userdetails";
			var webScript:WebScriptService = new WebScriptService(url, WebScriptService.GET, onPopulateSuccess);;
			webScript.execute();	
		}		
		
		/**
		 * Called when web script had been successfully executed
		 */
		private function onPopulateSuccess(event:SuccessEvent):void
		{
			// Set the user details
			this._firstName = event.result.userdetails.firstname;	
			this._lastName = event.result.userdetails.lastname;
			this._visibility = event.result.userdetails.visibility;
			
			// Raise the user details change event
			dispatchEvent(new UserDetailsChangedEvent(UserDetailsChangedEvent.USER_DETAILS_CHANGED));
		}
	}
}