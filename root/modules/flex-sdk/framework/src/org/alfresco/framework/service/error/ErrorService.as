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
	import flash.events.EventDispatcher;
	
	/**
	 * Error service.
	 * 
	 * @author Roy Wetherall
	 */
	public class ErrorService extends EventDispatcher
	{
		/** Error type constants */
		public static const APPLICATION_ERROR:String = "ApplicationError";
		
		/** Singleton instance */
		private static var _instance:ErrorService;
		
		/**		
		 * Getter for static instance property
		 */
		public static function get instance():ErrorService
		{
			if (ErrorService._instance == null)
			{
				ErrorService._instance = new ErrorService();
			}			
			return ErrorService._instance;
		}
		
		/**
		 * Raise an error with the error service
		 */
		public function raiseError(errorType:String, error:Error):void
		{
			// Raise the errorRaisedEvent
			this.dispatchEvent(new ErrorRaisedEvent(ErrorRaisedEvent.ERROR_RAISED, errorType, error));
		}
	}
}