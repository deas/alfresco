// ActionScript filepackage{
	import org.alfresco.ace.application.logout.logout;
	import mx.controls.Alert;
	import mx.core.Application;
	import mx.containers.Canvas;
	import mx.containers.Panel;
	import org.alfresco.ace.application.login.login;
	import mx.core.Repeater;
	import mx.controls.SWFLoader;
	import mx.controls.CheckBox;
	import mx.rpc.events.FaultEvent;
	import org.alfresco.ace.control.swipe.Swipe;
	import flash.events.Event;
	
	import mx.containers.Box;

	import org.alfresco.framework.service.authentication.LoginCompleteEvent;
	import org.alfresco.framework.service.authentication.LogoutCompleteEvent;
	import org.alfresco.framework.service.error.ErrorRaisedEvent;
	import org.alfresco.framework.service.error.ErrorService;
	import org.alfresco.framework.service.authentication.AuthenticationService;
	import org.alfresco.framework.service.webscript.ConfigService;
	import org.alfresco.ace.service.articlesearchservice.ArticleSearchService;
	import org.alfresco.ace.service.articlesearchservice.ArticleSearchCompleteEvent;
	import mx.controls.TextInput;
	import mx.effects.Fade;

	/**	 * Main class	 */	public class MainClass extends Application	{		/** UI components */		public var mainCanvas:Canvas;		public var loginPanel:Box;		public var myframe:SWFLoader;		public var cb1:CheckBox;		public var resultsDispPanel:Panel;				public var searchResults:Repeater;			public var swipe:Swipe;			public var searchTxt:TextInput;				/** Effects */		private var fadeIn:Fade;		private var fadeOut:Fade;				/**		 * Constructor		 */		public function MainClass():void		{			// Register interest in the error service events			ErrorService.instance.addEventListener(ErrorRaisedEvent.ERROR_RAISED, onErrorRaised);						// Register interest in authentication service events			AuthenticationService.instance.addEventListener(LoginCompleteEvent.LOGIN_COMPLETE, doLoginComplete);			AuthenticationService.instance.addEventListener(LogoutCompleteEvent.LOGOUT_COMPLETE, doLogoutComplete);							// Register interest in search service events			ArticleSearchService.instance.addEventListener(ArticleSearchCompleteEvent.SEARCH_COMPLETE, doSearchComplete); 
			// Create the configuration class			ConfigService.instance;
		}				protected override function createChildren():void		{			// Create the children			super.createChildren();						// Create the effects			this.fadeIn = new Fade();			this.fadeOut = new Fade();						// Assign the effects to the controls			mainCanvas.setStyle("showEffect", this.fadeIn);			loginPanel.setStyle("showEffect", this.fadeIn);						mainCanvas.setStyle("hideEffect", this.fadeOut);			loginPanel.setStyle("hideEffect", this.fadeOut);			
		}
				/**		 * Event handler called when login is successfully completed		 * 		 * @param	event	login complete event		 */
		private function doLoginComplete(event:LoginCompleteEvent):void
		{
			mainCanvas.visible = true;			loginPanel.visible = false;		}
				/**		 * Event handler called when logout is successfully completed		 * 		 * @param	event	logout complete event		 */
		private function doLogoutComplete(event:LogoutCompleteEvent):void
		{
			mainCanvas.visible = false;		    loginPanel.visible = true;		    this.drawFocus(true);			this.swipe.showPrimaryState();		}		
		/**		 * Event handler called when search is successfully completed		 * 		 * @param	event	search complete event		 */		private function doSearchComplete(event:ArticleSearchCompleteEvent):void		{			try			{				this.drawFocus(true);						this.swipe.showSecondaryState();	   			}			catch (error:Error)			{				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error.message);				}		}				/**		 * Event handler called when error is raised		 * 		 */		private function onErrorRaised(event:ErrorRaisedEvent):void		{			// TODO figure out how we react to ApplicationError's raised			if (event.errorType == ErrorService.APPLICATION_ERROR)			{				Alert.show("Application Error: " + event.error.message + "\n\nStack Trace:\n" + event.error.getStackTrace());			}
		}	}}

