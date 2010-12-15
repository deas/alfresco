package org.alfresco.ace.service.articlesearchservice
{	
	import mx.rpc.http.HTTPService;
	import mx.controls.Alert;
	import flash.events.EventDispatcher;
	import mx.controls.Alert;
	import mx.core.Repeater;
	import mx.rpc.events.ResultEvent;
	import mx.core.Application;
	import org.alfresco.framework.service.webscript.SuccessEvent;
	import org.alfresco.framework.service.webscript.FailureEvent;
	import org.alfresco.framework.service.webscript.ConfigService;
	import org.alfresco.framework.service.webscript.WebScriptService;
	import org.alfresco.framework.service.error.ErrorService;
	
	/**
	 * Article serach service
	 */
	public class ArticleSearchService extends EventDispatcher
	{
		/** Search text */
		private var _searchtext:String;
			
		/** Static instance of the article search service */
		private static var _instance:ArticleSearchService;
		
		
		/**
		 * Singleton method to get the instance of the Search Service
		 */
		public static function get instance():ArticleSearchService
		{
			if (ArticleSearchService._instance == null)
			{
				ArticleSearchService._instance = new ArticleSearchService();
			}
			return ArticleSearchService._instance;
		}
		
		/**
		 * Getter for searchText property
		 */
		public function get searchtext():String
		{
			return this._searchtext;
		}
		
		/**
		 * Searches the repository for articles
		 */
		public function search(searchtext:String):void
		{
			try
			{					
				var url:String = ConfigService.instance.url +  "/alfresco/service/kb/search.atom";
				var webScript:WebScriptService = new WebScriptService(url, WebScriptService.GET, onSearchSuccess);
				
				var params:Object = new Object();
				params.q = searchtext;
				
				webScript.execute(params);
			}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error);
			}
		}
		
		/**
		 * onSearchSuccess event handler
		 */
		public function onSearchSuccess(event:SuccessEvent):void
		{
			// Dispatch the search complete event
			dispatchEvent(new ArticleSearchCompleteEvent(ArticleSearchCompleteEvent.SEARCH_COMPLETE, event.result));
		}
	}
	
}