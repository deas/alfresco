// Logout Action Script 

package org.alfresco.ace.application.logout
{
	import mx.controls.Alert;
	import mx.containers.Canvas;
	import mx.controls.Label;
	
	import mx.events.FlexEvent;
	import mx.effects.Fade;
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import org.alfresco.framework.service.authentication.AuthenticationService;
	import org.alfresco.framework.service.authentication.LogoutCompleteEvent;
	import org.alfresco.framework.service.authentication.AuthenticationService;
	import org.alfresco.ace.service.userdetails.UserDetails;
	import org.alfresco.ace.service.userdetails.UserDetailsChangedEvent;
	import org.alfresco.ace.control.hyperlink.HyperLink;
	import mx.controls.Image;
	
	/**
	 * Logout backing class
	 */
	public class LogoutClass extends Canvas
	{
		/** UI controls */
		public var logoutBtn:HyperLink;
		public var visibilityImage:Image;
		
		/** Icon used in the confirmation dialog */
		[Embed(source='/images/user_icon.png')]
		private var confirmIcon:Class;
		
		/**
		 * Constructor
		 */
		public function LogoutClass():void
		{
			// Register interest in the user details change event
			UserDetails.instance.addEventListener(UserDetailsChangedEvent.USER_DETAILS_CHANGED, onUserDetailsChanged);
		}		
		
		/**
		 * onUserDetailsChanged event handler
		 */
		private function onUserDetailsChanged(event:UserDetailsChangedEvent):void
		{
			// Get the text we want to display on the link
			var text:String = "Logout";
			var firstName:String = UserDetails.instance.firstName;
			if (firstName != null)
			{
				text = text + ", " + firstName;
			}
			
			// Set the text of the logout link
			logoutBtn.text = text;		
			
			var visibility:String = UserDetails.instance.visibility;
			if (visibility == UserDetails.VISIBILITY_INTERNAL)
			{
				// set the alfresco image
				visibilityImage.source = "images/user_label_alfresco.png";
			}
			else if (visibility == UserDetails.VISIBILITY_TIER_1)
			{
				// set the author image
				visibilityImage.source = "images/user_label_author.png";
			}
			else if (visibility == UserDetails.VISIBILITY_TIER_2)
			{
				// set the parnter image
				visibilityImage.source = "images/user_label_partner.png";
			}
			else
			{
				// set the customer image
				visibilityImage.source = "images/user_label_customer.png";
			}
		}
		
		/**
		 * Logout behaviour
		 */
		public function logout():void 
		{
			// instantiate the Alert box
			var a:Alert = Alert.show(
								"Are you sure to Logout ?", 
								"Confirmation", 
								Alert.YES|Alert.NO, 
								this, 
								doLogout, 
								confirmIcon, 
								Alert.NO);
			
			// modify the look of the Alert box
			a.setStyle("backgroundColor", 0xffffff);
			a.setStyle("backgroundAlpha", 0.50);
			a.setStyle("borderColor", 0xffffff);
			a.setStyle("borderAlpha", 0.75);
			a.setStyle("color", 0x000000); // text color
		}
		
		/**
		 * Confirmation event handler that logs the current user out of the application
		 */
		private function doLogout(event:CloseEvent):void 
		{
			if (event.detail == Alert.YES) 
			{
				// Make call to authentication service to logout user
				AuthenticationService.instance.logout();
			}
		}
	}
}
