package org.alfresco.ace.application.search
{
	import mx.controls.Alert;
	import mx.containers.Canvas;
	import org.alfresco.ace.service.articlesearchservice.ArticleSearchService;
	import mx.controls.TextInput;
	
	/**
	 * Search UI backing class
	 * 
	 * @author Saravanan Sellathurai
	 */	
	 
	public class SearchClass extends Canvas
	{
		
		public var searchTxt:TextInput;
		
		public function SearchClass()
		{
			super();
		}
		
		/**
		 * Set focus to the first text box when it's ready
		 */
		protected override function initializationComplete():void
		{
			super.initializationComplete();
			
			// Focus the user input box
			focusManager.setFocus(searchTxt);
		}
		
		/**
	 	* 
	 	* Makes call to search service and redirects user to search results display
	 	* 
	 	*/
		public function onSearchButtonLinkClick(searchText:String):void
		{
			var pattern:RegExp = /"£"/; //TODO VALIDATION FOR SPECIAL CHARACTERS
			var numPattern:RegExp = /\d+/;
			if(searchText.length < 3) Alert.show("Input string needs minimum 3 characters");
			else if (searchText.length == 0) Alert.show("Null string not allowed");
			else if (searchText.search(pattern)!=-1) Alert.show("Special Characters not allowed");
			else if (searchText.search(numPattern)!=-1) Alert.show("Numbers not allowed");
			else ArticleSearchService.instance.search(searchText);	
			searchTxt.text = "";
		}
	}
}