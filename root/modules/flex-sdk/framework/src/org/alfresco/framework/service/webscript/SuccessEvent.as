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
 package org.alfresco.framework.service.webscript
{
	import mx.rpc.AsyncToken;
	import mx.rpc.events.ResultEvent;
	import mx.messaging.messages.IMessage;

    /**
     * Web script success event class
     * 
     * @author Roy Wetherall
     */
	public class SuccessEvent extends ResultEvent
	{
		/** Event name */
		public static const SUCCESS:String = "success";	
		
		/**
		 * Constructor
		 */
		public function SuccessEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=true, result:Object=null, token:AsyncToken=null, message:IMessage=null)
		{
			super(type, bubbles, cancelable, result, token, message);
		}
		
	}
}