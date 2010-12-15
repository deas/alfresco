// Login Action Script 
package org.alfresco.ace.application.login
{
	import mx.controls.Alert;
	import flash.events.Event;
	import flash.utils.describeType;
	import mx.core.ClassFactory;
	import mx.containers.Canvas;
	import mx.controls.Text;
	import mx.controls.TextInput;
	import mx.states.State;
	import mx.rpc.mxml.IMXMLSupport;
	import mx.containers.VBox;
	import org.alfresco.framework.service.error.ErrorRaisedEvent;
	import org.alfresco.framework.service.error.ErrorService;
	import org.alfresco.framework.service.authentication.AuthenticationService;
	import org.alfresco.framework.service.authentication.InvalidCredentialsError;
	import org.alfresco.framework.service.authentication.LoginCompleteEvent;
	import mx.effects.Fade;
	import mx.states.SetEventHandler;

	/**
	 * Login class
	 * 
	 * @author Roy Wetherall
	 */
    public class LoginClass extends VBox
	{
		/** UI controls */
		public var username:TextInput;
		public var password:TextInput;
		public var errorMessage:Text;
		
		/** Effects */
		private var fadeIn:Fade;
		
		/** 
		 * Constructor
		 */
		public function LoginClass()
		{
			// Call the super class
			super();
			
			// Register interest with the error service
			ErrorService.instance.addEventListener(ErrorRaisedEvent.ERROR_RAISED, onErrorRaised);	
			
			// Register interest in the authentication service login success event
			AuthenticationService.instance.addEventListener(LoginCompleteEvent.LOGIN_COMPLETE, onLoginComplete);
		}
		
		/**
		 * Set focus to the first text box when it's ready
		 */
		protected override function initializationComplete():void
		{
			super.initializationComplete();
			
			// Create the effects
			this.fadeIn = new Fade();
			this.fadeIn.alphaFrom = 0;
			this.fadeIn.alphaTo = 1;
			
			// Exectue the fade effect
			this.fadeIn.play(new Array(this));
			
			// Focus the user input box
			focusManager.setFocus(username);
		}
	
		/**
		 * Event handler for login button link.  
		 * 
		 * Makes call to authenitcation service and redirects user to search dialog.
		 */
		public function onLoginButtonLinkClick():void
		{	
			if (username.text != null && username.text.length == 0)
			{
				// Remind the user to enter a username
				showErrorMessage("Enter a user name.");
				//focusManager.setFocus(username);			
			}
			else
			{
				// Get the password assuring is it now passed as null
				var password:String = password.text;
				if (password == null)
				{
					password = "";
				}
				
				// Call authentication service to log user in
				AuthenticationService.instance.login(username.text, password);
			}
		}
		
		/**
		 * On log in complete event handler
		 */
		public function onLoginComplete(event:Event):void
		{
			// Return the control to its base state
			username.text = "";
			password.text = "";
			errorMessage.text = " ";
		}
		
		/**
		 * On error raised event handler
		 */
		public function onErrorRaised(event:ErrorRaisedEvent):void
		{
			if (event.errorType == InvalidCredentialsError.INVALID_CREDENTIALS)
			{
				// Show the error message
				showErrorMessage(event.error.message);
				//focusManager.setFocus(username);
			}
		}
		
		private function showErrorMessage(message:String):void
		{
			// Set the new error message
			errorMessage.text = message;
			
			// Fade in the error text
			fadeIn.play(new Array(errorMessage));
		}
	}	
}