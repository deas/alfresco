package org.alfresco.core
{
	import flash.external.ExternalInterface;
	
	public class Logger
	{
		/**
		 * Error log level
		 */
		public static var ERROR:String = "error";

		/**
		 * True if logging is enalbed
		 */
		private static var enabled:Boolean = false;
		
		/**
		 * The JavaScript callback to call and supply the log messsage to
		 */
		private static var javaScriptCallback:String = null;
		
		/**
		 * Enables logging for JavaScript with the passed in callback.
		 */
		public static function enableJavaScriptLogging(jsLogger:String):void
		{
			enabled = true;
			javaScriptCallback = jsLogger;
			log("Javascript logging has been enabled.");
		}
		
		/**
		 * The log method where "debug" is default log level.
		 * 
		 * @param msg The message to log
		 * @param level the log level
		 */
		public static function log(msg:String, level:String="debug"):void
		{
			if (enabled)
			{
				if(javaScriptCallback != null && ExternalInterface.available)
				{													
					ExternalInterface.call(javaScriptCallback, msg, level);
				}
			}
		}	
	}
}