/*
Copyright 2006 Adobe Systems Incorporated

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.


THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/
package qs.controls
{
	import mx.core.UIComponent;
	import flash.display.DisplayObject;
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.system.ApplicationDomain;
	import flash.display.Loader;
	import mx.rpc.soap.LoadEvent;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.net.URLRequest;
	import mx.skins.RectangularBorder;
	import mx.core.EdgeMetrics;
	import qs.caching.ContentCache;
	import mx.core.IDataRenderer;
	

[Style(name="backgroundAlpha", type="Number", inherit="no")]
[Style(name="backgroundColor", type="uint", format="Color", inherit="no")]
[Style(name="borderColor", type="uint", format="Color", inherit="no")]
[Style(name="borderSides", type="String", inherit="no")]
[Style(name="borderStyle", type="String", enumeration="inset,outset,solid,none", inherit="no")]
[Style(name="borderThickness", type="Number", format="Length", inherit="no")]
[Style(name="cornerRadius", type="Number", format="Length", inherit="no")]
[Style(name="dropShadowEnabled", type="Boolean", inherit="no")]
[Style(name="dropShadowColor", type="uint", format="Color", inherit="yes")]
[Style(name="shadowDirection", type="String", enumeration="left,center,right", inherit="no")]
[Style(name="shadowDistance", type="Number", format="Length", inherit="no")]
[Style(name="dropShadowColor", type="uint", format="Color", inherit="yes")]
[Style(name="shadowDirection", type="String", enumeration="left,center,right", inherit="no")]
[Style(name="shadowDistance", type="Number", format="Length", inherit="no")]

[Event(name="complete", type="flash.events.Event")]

/* The SuperImage Class, a replacement for Image.  This doesn't fully support all the features of Image...call it a work in progress.
*/
public class SuperImage extends UIComponent implements IDataRenderer
{
	

/*--------------------------------------------------------------------------------------------------------------------
*  Constructor
*-------------------------------------------------------------------------------------------------------------------*/
	public function SuperImage():void
	{
		//xace("creating superimage");
		super();
	}


/*--------------------------------------------------------------------------------------------------------------------
*  Private Properties
*-------------------------------------------------------------------------------------------------------------------*/
	private var _source:*;
	private var _oldSource:*;
	private var _sourceChanged:Boolean = false;
	private var _content:DisplayObject;
	private var _cacheName:String = "";
	private var _maintainAspectRatio:Boolean = true;
	private var _border:RectangularBorder;
	private var _loadedFromCache:Boolean = false;
	
/*--------------------------------------------------------------------------------------------------------------------
*  Public Properties
*-------------------------------------------------------------------------------------------------------------------*/
	
	[Bindable] public function set maintainAspectRatio(value:Boolean):void
	{
		_maintainAspectRatio = value;
		invalidateSize();
	}
	public function get maintainAspectRatio():Boolean
	{
		return _maintainAspectRatio;
	}

	/*  the image cache to use.  You can use this to segment different groups of SuperImages
	*	into different caches, each with their own caching rules and limits. The default value is the empty
	*	string, which is the global cache.  a value of null tells the SuperImage not to cache at all.
	*/
	[Bindable] public function set cacheName(value:String):void
	{
		_cacheName = value;
	}
	public function get cacheName():String
	{
		return _cacheName;
	}
	
	/** What to display. Options are:  Bitmap, BitmapData, url, URLRequest, or a Class or ClassName that when instantiated matches one of the other
	* 	options.
	*/
	[Bindable] public function set source(value:*):void
	{
		if(value == _source)
			return;
			
		if(_content is Loader)
		{
			Loader(_content).contentLoaderInfo.removeEventListener(Event.COMPLETE,loadCompleteHandler);
			Loader(_content).contentLoaderInfo.removeEventListener(IOErrorEvent.IO_ERROR,loadErrorHandler);
		}
		
		_source = value;
		_sourceChanged = true;
		invalidateProperties();
	}
	
	public function get source():*
	{
		return _source;
	}
	[Bindable] public function set data(value:Object):void
	{
		source = value;
	}
	public function get data():Object
	{
		return source;
	}
	

/*--------------------------------------------------------------------------------------------------------------------
*  private methods
*-------------------------------------------------------------------------------------------------------------------*/
	override protected function createChildren():void
	{
		createBorderSkin();
	}


	private function createBorderSkin():void
	{
		var borderClass:Class = getStyle("borderSkin");
		if(borderClass != null)
		{
			_border  = new borderClass();
			_border.styleName = this;
			addChild(_border);
		}
	}

	override protected function commitProperties():void
	{
		if(_sourceChanged)
		{
			_sourceChanged= false;
			if(_content != null)
			{
				// remove any old content.
				removeChild(_content);					
			}
			_loadedFromCache = false;
			_content = null;
			
			// now examine our source property and convert it into something we can render.
			var newSource:* = _source;
			if (newSource is XML || newSource is XMLList)
			{
				newSource = newSource.toString();
			}
			
			if (newSource is String)
			{
				// first check and see if its the name of a class.
				try {
					var c:Class = (ApplicationDomain.currentDomain.getDefinition(newSource) as Class);
					if(c != null)
						newSource = c;
				} catch(e:Error) {
				}
			}
			if(newSource is Class)
			{
				// if it's a class, instantiate it.
				newSource = new newSource();
			}
			
			// if it's bitmap or bitmap data, we know how to render that.
			if(newSource is Bitmap)
			{
				_content = newSource;
			}
			else if (newSource is BitmapData)
			{
				_content = new Bitmap(newSource);
			}
			
			else if (newSource is String || newSource is URLRequest)
			{
				// it's an url that needs to be loaded.	
				var cachedContent:DisplayObject;
				
				if(_cacheName == null)
				{
					// if we don't have a cache, just load it up into a loader.
					cachedContent = new Loader();
					Loader(cachedContent).load((newSource is URLRequest)? newSource:new URLRequest(newSource));
				}
				else
				{
					// we have a cache, so delegate to the cache to do the loading.
					cachedContent = ContentCache.getCache(_cacheName).getContent(newSource);
				}
				_loadedFromCache = true;
				// now the cache can give us back different types of display objects. If they gave us back a Loader,
				// and the loader is actively loading, we need to listen to it to know when its completed.
				if (cachedContent is Loader)
				{
					var l:Loader = Loader(cachedContent);
					if(l.contentLoaderInfo.bytesTotal == 0 || (l.contentLoaderInfo.bytesLoaded < l.contentLoaderInfo.bytesTotal))
					{
						l.contentLoaderInfo.addEventListener(Event.COMPLETE,loadCompleteHandler);
						l.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR,loadErrorHandler);
					}
				}
				else
				{
					dispatchEvent(new Event(Event.COMPLETE));
				}
				_content = cachedContent;
			}
			_oldSource = newSource;
			
			if(_content != null)
				addChild(_content);
			invalidateSize();
			invalidateDisplayList();
		}
	}
	
	private function loadCompleteHandler(e:Event):void
	{
		invalidateSize();
		invalidateDisplayList();
		dispatchEvent(new Event(Event.COMPLETE));
	}

	private function loadErrorHandler(e:Event):void
	{
	}
	
	
	override protected function measure():void
	{
		var contentWidth:Number;
		var contentHeight:Number;

		if(_content == null)
		{
			// we have no content...if we also don't have source, just set our size to zero.
			if(_source == null || _source == "")
			{
				contentWidth = 0;
				contentHeight = 0;
			}
		}
		else
		{
			contentWidth = 0;
			contentHeight = 0;
			var metrics:EdgeMetrics;
			
			if(_border != null)
			{
				// if we have a border, first find out how big our border is.
				metrics = _border.borderMetrics;
			}

			if(_content is Loader)
			{
				try {
					// we have a loader...ask the loader how big his content is. It's possible he doesn't know yet.
					contentWidth = Loader(_content).contentLoaderInfo.width;
					contentHeight = Loader(_content).contentLoaderInfo.height;
				} catch(e:Error) {
				
				}
			}
			else
			{
				// assuming that we only contain simple flash display objects, their 'measured' size is their unscaled width/height.
				contentWidth = _content.width / _content.scaleX;
				contentHeight = _content.height / _content.scaleY;
			}
			
			if(contentWidth > 0 && contentHeight > 0)
			{
				// now adjust to maintain aspect ratio.
				if(_maintainAspectRatio)
				{
					if(!isNaN(percentWidth))
					{
						// if we have a percent width
						if(isNaN(percentHeight) && isNaN(explicitHeight))
						{
							// and no explicit size, assume that our current width is our final width, and 
							// report an appropriate height. If it's not our final width, we'll come back through this codepath later to adjust.
							contentHeight = (unscaledWidth - metrics.left - metrics.right)/contentWidth * contentHeight;
						}
					}
					else if (!isNaN(percentHeight))
					{
						// if we have a percent height
						if(isNaN(percentWidth) && isNaN(explicitWidth))
						{
							// and no explciitly controled width, report an appropriate width. Again, if our height changes, we'll deal with it later.
							contentWidth = (unscaledHeight - metrics.top - metrics.bottom)/contentHeight * contentWidth;
						}
					}
					// if we have an explicit width or height but not both, we're pretty sure we're going to end up at that width/height, and 
					// the other dimension will be whatever our measured size is.  So report our measured sizes based on that explicit size.
					if(!isNaN(explicitWidth))
					{
						if(isNaN(explicitHeight))
						{
							contentHeight = (explicitWidth-metrics.left - metrics.right)/contentWidth * contentHeight;
						}
					}
					else if (!isNaN(explicitHeight))
					{
						contentWidth = (explicitHeight - metrics.top - metrics.bottom)/contentHeight * contentWidth;
					}
				}
			}				
		}	
		if(!isNaN(contentWidth) || !isNaN(contentHeight))
		{		
			// add in the size of our border.
			if(metrics != null)
			{
				contentHeight += metrics.top + metrics.bottom;
				contentWidth += metrics.left + metrics.right;
			}
			measuredWidth = contentWidth;
			measuredHeight = contentHeight;
		}
	}
	
	override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
	{
		if(_content != null)
		{
			var borderMetrics:EdgeMetrics;
			var contentWidth:Number = unscaledWidth;
			var contentHeight:Number = unscaledHeight;
			if(_border != null)
			{
				_border.visible = true;
				borderMetrics = _border.borderMetrics;
				contentWidth -= borderMetrics.left + borderMetrics.right;
				contentHeight -= borderMetrics.top + borderMetrics.bottom;
			}
			
			if(_maintainAspectRatio)
			{
				var myAR:Number = contentWidth/contentHeight;			
				// if we've scaled content down to 0, it will have 0 scale. We don't want that, so let's reset it.
				_content.width = _content.height = 100;
				var contentAR:Number = (_content.width / _content.scaleX) / (_content.height / _content.scaleY);
				if(!isNaN(contentAR))
				{						
					if(contentAR > myAR)
					{
						_content.width = contentWidth;
						_content.height = contentHeight = contentWidth / contentAR;						
					}
					else
					{
						_content.height = contentHeight;
						_content.width = contentWidth = contentHeight * contentAR;
					}
				
					if(!isNaN(percentWidth))
					{
						if(isNaN(percentHeight) && isNaN(explicitHeight))
						{
							if(myAR != contentAR)
								invalidateSize();
						}
					}
					else if (!isNaN(percentHeight))
					{
						if(isNaN(percentWidth) && isNaN(explicitWidth))
						{
							if(myAR != contentAR)
								invalidateSize();								
						}
					}
				}
				
				
			}
			else
			{
				_content.width = contentWidth;
				_content.height = contentHeight;
			}				
			if(_border != null)
			{
				_border.setActualSize(contentWidth + borderMetrics.left + borderMetrics.right,
										contentHeight + borderMetrics.top + borderMetrics.bottom);
				_content.x = borderMetrics.left;
				_content.y = borderMetrics.top;
				
			}
			else
			{
				_content.x = 0;
				_content.y = 0;
			}

		}
		else
		{
			if(_border != null)
				_border.visible = false;
		}
		
	}
}
}

