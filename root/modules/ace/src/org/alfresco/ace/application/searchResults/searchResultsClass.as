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
 
package org.alfresco.ace.application.searchResults
{
	import mx.controls.SWFLoader;
	import mx.controls.Alert;
	import mx.core.Repeater;
	import mx.containers.Canvas;
	import mx.controls.Label;
	import mx.containers.VBox;
	import flash.events.Event;
	import mx.collections.ArrayCollection;
	import mx.printing.*;
	import mx.effects.Resize;
	import mx.effects.WipeLeft;
	import mx.effects.WipeRight;
	import mx.containers.Panel;
	import mx.events.DragEvent;
	import mx.controls.HSlider;
	import flash.display.MovieClip;
	import flash.display.Loader;
	import flash.net.URLRequest;
	import org.alfresco.ace.application.searchDetails.searchDetailsClickEvent;
	import org.alfresco.framework.service.error.ErrorService;
	import org.alfresco.framework.service.authentication.AuthenticationService;
	import org.alfresco.ace.service.articlesearchservice.ArticleSearchCompleteEvent;
	import org.alfresco.ace.service.articlesearchservice.ArticleSearchService;
	import org.alfresco.ace.control.hyperlink.HyperLink;
	import org.alfresco.framework.service.authentication.LogoutCompleteEvent;
	import mx.events.FlexEvent;
	import mx.events.ResizeEvent;
	import mx.effects.Resize;
	import mx.events.EffectEvent;

	
	/**
	 * SearchResults Class
	 * 
	 * This provides an encapsulation for the SearchResults
	 * 
	 * @author Saravanan Sellathurai
	 */	 
	
	public class searchResultsClass extends Canvas
	{
		[Bindable]
	   	public var results:Repeater;
		public var myframe:SWFLoader;
		public var swfPanel:VBox;
		public var resultsDispPanel:Canvas;
		public var labelResultsFound:Label;
		public var moveNext:HyperLink;
		public var movePrevious:HyperLink;
		public var contentPanel:Panel;
		public var zoomer:HSlider;
		
		private var _resultObj:Object;
		private var _url:String;
		private var _myList:ArrayCollection;
		private var _currentSelectedItem:String;
		private var _currentSelectedItemIndex:int;
		private var _closeWidth:int = 0;
		private var _finalWidth:int = 0;
		private var _totalResults:String;
		public var clip:MovieClip;
		public var loader:Loader;
		
		private var swfWidth:Number=0;
        private var swfHeight:Number=0
	
	    /** default Constructor */
	    public function searchResultsClass()
	    {
	       super();
	     	// Register interest in events
			ArticleSearchService.instance.addEventListener(ArticleSearchCompleteEvent.SEARCH_COMPLETE, doSearchComplete); 	       		
       		this.addEventListener(searchDetailsClickEvent.SEARCH_LINK_CLICK_EVENT, onSearchDetailsClick);
       		AuthenticationService.instance.addEventListener(LogoutCompleteEvent.LOGOUT_COMPLETE, onLogoutComplete);
        }
     	
     	public function onLogoutComplete(event:LogoutCompleteEvent):void
		{
			swfPanel.percentWidth = 0;
          	resultsDispPanel.percentWidth = 100;
          	this._totalResults = null;
          	this.setResultsLabel();
          	results.dataProvider = null;
		}
		
      
		/** Result Click event for the Repeater */
		private function onSearchDetailsClick(oEvent:searchDetailsClickEvent):void
        {
        	this._currentSelectedItem = oEvent.data.toString();
        	
          	for(var i:int=0; i<this._myList.length; i++)
            {
           		if(this._currentSelectedItem == this._myList[i].href)
           		{
           			this._currentSelectedItemIndex = i;
           		}
            }
           
           	if(this._currentSelectedItemIndex == this._myList.length-1)
           	{
           		this.moveNext.enabled = false;
           	} 
           	else
           	{
           		this.moveNext.enabled = true;
           	}
           	
           	if(this._currentSelectedItemIndex == 0) 
           	{
           		this.movePrevious.enabled = false;	
           	}
           	else
           	{
           		this.movePrevious.enabled = true;
		 	}          	
          	
          	if (swfPanel.visible == false)
          	{
	          	swfPanel.visible = true;
	          	swfPanel.includeInLayout = true;
	           	myframe.visible = true;
	          	var resizeEffect:Resize = new Resize(resultsDispPanel);
	           	resizeEffect.widthFrom = resultsDispPanel.width;
	           	resizeEffect.widthTo = 30*this.width/100;
	           	resizeEffect.suspendBackgroundProcessing = true;
	           	resizeEffect.duration = 1000;
	           	resizeEffect.play();	        
	           	resultsDispPanel.percentWidth = 30;
	           	swfPanel.percentWidth = 70;
	      	}
			
			this._url = this._currentSelectedItem + "?ticket=" + AuthenticationService.instance.ticket;  
			myframe.source = this._url;
         }	
        
       
   		// Create a PrintJob instance.
        public function doPrint():void 
        {
            // Create an instance of the FlexPrintJob class.
            var printJob:FlexPrintJob = new FlexPrintJob();

            // Start the print job.
            if (printJob.start() != true) return;

            // Add the object to print. Scale it to match the width.
            printJob.addObject(contentPanel, FlexPrintJobScaleType.MATCH_WIDTH);
			
            // Send the job to the printer.
            printJob.send();
        }
        
        /**Close Button Click event for the swf panel */
        public function CloseBtnClick():void
        {
 			swfPanel.percentWidth = 0;
          	resultsDispPanel.percentWidth = 100;
          	
          	this._closeWidth = resultsDispPanel.width;
          	var resize:Resize = new Resize(swfPanel);
          	resize.duration = 1000;
          	
          	resize.widthFrom = swfPanel.width;
          	resize.widthTo = 0;
          	resize.suspendBackgroundProcessing = true;
          	
          	resize.play();
          	myframe.source = ''; 
         }
       
       /** get method for url */
       public function geturl():String 
       {
       		return this._url;
       }	
       
       /** set method for url */
       public function set url(str_url:String):void
       {
       		this._url = str_url;	
       }
       
      
        /**
		 * Event handler called when search is successfully completed
		 * 
		 * @event	search complete event
		 */
		private function doSearchComplete(event:ArticleSearchCompleteEvent):void
		{
			try
			{	
				swfPanel.percentWidth = 0;
				swfPanel.visible = false;
				swfPanel.includeInLayout = false;
				resultsDispPanel.percentWidth = 100;
				this._resultObj = event.result.feed.entry;
				this.results.dataProvider = this._resultObj;
				this._totalResults = event.totalresults;
				this.setResultsLabel();
				
				if(event.totalresults == "0") 
				{
					Alert.show("Result not found");
				}
				else
				{				
					this._myList = new ArrayCollection();
					for(var i:int=0; i<event.result.feed.entry.length; i++)
	                {
	               		this._myList.addItem({href:event.result.feed.entry[i].link.href , rel:event.result.feed.entry[i].link.rel});
	               	}
	  			}
       		}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error.message);	
			}
		}
		
		
		 // function to set results label
		 		
		private function setResultsLabel():void
		{
			this.labelResultsFound.text = "Search Results :  "+ this._totalResults + " Items Found ";
		}
		/**
		 *@onNextClick 	   - function to handle page next
		 *@onPreviousClick - function to handle page previous
		 */		
		 
		public function onNextClick():void
		{
			if(this.moveNext.enabled)
			{
				this.myframe.source = this._myList[this._currentSelectedItemIndex + 1].href + "?ticket=" + AuthenticationService.instance.ticket;
				this._currentSelectedItemIndex = this._currentSelectedItemIndex + 1;
				if(this._currentSelectedItemIndex == this._myList.length-1)
	           	{
	           		this.moveNext.enabled = false;
	           		this.movePrevious.enabled = true;
	           	} 
	           	else
	           	{
	           		this.moveNext.enabled = true;
	           		this.movePrevious.enabled = true;
	           	}
   			}
   			
		}
		
		public function onPreviousClick():void
		{
			if(this.movePrevious.enabled)
			{
				this.myframe.source = this._myList[this._currentSelectedItemIndex - 1].href + "?ticket=" + AuthenticationService.instance.ticket;
				this._currentSelectedItemIndex = this._currentSelectedItemIndex - 1;
				if(this._currentSelectedItemIndex == 0) 
	           	{
	           		this.movePrevious.enabled = false;
	           		this.moveNext.enabled = true;	
	           	}
	           	else
	           	{
	           		this.movePrevious.enabled = true;
	           		this.moveNext.enabled = true;
	           	}
   			}
		}
		
        /**
         * function to zoom content
         * 
         */        
        public function zoomContent():void
        {
        	myframe.scaleX = zoomer.value;
        	myframe.scaleY = zoomer.value;
        }
        
     }

}