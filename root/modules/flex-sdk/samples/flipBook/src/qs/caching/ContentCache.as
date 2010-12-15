/*Copyright (c) 2006 Adobe Systems Incorporated

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/
package qs.caching
{
import flash.utils.Dictionary;
import flash.net.URLRequest;
import flash.display.Loader;
import flash.display.DisplayObject;
import flash.display.Bitmap;
import qs.utils.DLinkedListNode;
import qs.utils.DLinkedList;
import mx.controls.Image;
	



public class ContentCache
{
	static private var caches:Dictionary;
	static private var defaultCache:ContentCache;

	private var _caches:Dictionary;
	private var _mruList:DLinkedList;
	private var _maximumSize:Number = 200;



	static public function getCache(name:String = null):ContentCache
	{
		if (name == "" || name == null)
		{
			if(defaultCache == null)
				defaultCache = new ContentCache();
				return defaultCache;
		}
		if(caches == null)
			caches = new Dictionary();
		if(name in caches)
			return caches[name];
		var cache:ContentCache = new ContentCache();
		caches[name] = cache;
		return cache;
	}	
	
	
	
	public function get maximumSize():Number
	{
		return _maximumSize;
	}
	public function set maximumSize(value:Number):void
	{
		_maximumSize = value;
		checkLimit();
	}
	public function ContentCache():void
	{
		_caches = new Dictionary();
		_mruList = new DLinkedList();
	}
	
	public function clear():void
	{
		_caches = new Dictionary();
		_mruList = new DLinkedList();
	}
	
	public function removeInstance(instance:DisplayObject,urlValue:*):void
	{
		var request:URLRequest;
		var url:String;
		var cachedNode:ContentCacheNode;

		if(urlValue is String)
		{
			url = urlValue;
		}
		else if (urlValue is URLRequest)
		{
			request = urlValue;
			url = request.url;
		}
					
		cachedNode = _caches[url];
		if(cachedNode == null)
			return;
		var instances:Array = cachedNode.value;
		for(var i:int = 0;i<instances.length;i++)
		{
			if(instances[i] == instance)
			{
				instances.splice(i,1);
				break;
			}
		}
		if(instances.length == 0)
		{
			delete _caches[url];
			_mruList.remove(cachedNode);
		}
	}
	public function removeContent(value:*):Array
	{
		var request:URLRequest;
		var url:String;
		var cachedItems:Array;
		var cachedNode:ContentCacheNode;
		if(value is String)
		{
			url = value;
		}
		else if (value is URLRequest)
		{
			request = value;
			url = request.url;
		}
		
		cachedNode = _caches[url];
		if(cachedNode != null)
		{
			cachedItems = cachedNode.value;
			delete _caches[url];
			_mruList.remove(cachedNode);
		}
		return cachedItems;
	}
	
	public function hasContent(value:*):Boolean
	{
		var request:URLRequest;
		var url:String;
		var cachedNode:ContentCacheNode;
		
		if(value is String)
		{
			url = value;
		}
		else if (value is URLRequest)
		{
			request = value;
			url = request.url;
		}

		
		
		cachedNode = _caches[url];
		return (cachedNode != null && cachedNode.value.length > 0);
	}
	
	public function returnContentInstance(instance:DisplayObject,url:*):void
	{		
		if(!(instance is Loader))
			return;
		
		var loader:Loader = Loader(instance);
			
		if(loader.contentLoaderInfo.bytesLoaded == 0 || loader.contentLoaderInfo.bytesLoaded < loader.contentLoaderInfo.bytesTotal)
		{
			try {
				loader.close();	
			} catch(e:Error) 
			{}
			removeInstance(instance,url);
		}					
	}
	
	public function preloadContent(value:*):void
	{
		getContent(value);
	}

	public function getContent(value:*):DisplayObject
	{
		var request:URLRequest;
		var url:String;
		var cachedItems:Array;
		var cachedNode:ContentCacheNode;
		var cachedItem:DisplayObject;
		
		if (value is XML || value is XMLList)
		{
			value = value.toString();
		}
		if(value is String)
		{
			url = value;
			request = new URLRequest(url);
		}
		else if (value is URLRequest)
		{
			request = value;
			url = request.url;
		}

		
		//xace("*** Checking Cache for " + url);		
		var result:DisplayObject;
		var loader:Loader;
		var bitmap:Bitmap;
		
		cachedNode = _caches[url];
		if(cachedNode == null)
		{
			_caches[url] = cachedNode = new ContentCacheNode(url);
			_mruList.unshift(cachedNode);
			//xace("\t no previous cache");
		}
		else
		{
			_mruList.remove(cachedNode);
			_mruList.unshift(cachedNode);
		}
		
		cachedItems = cachedNode.value;
		
		for(var i:int = 0;i<cachedItems.length;i++)
		{
			cachedItem = cachedItems[i];

			if (cachedItem.parent == null)
			{
				//xace("\t unparented");
				result = cachedItem;		
				break;
			}
			else
			{
				if(bitmap == null && cachedItem is Bitmap)
				{
					bitmap = Bitmap(cachedItem);
				}
				if(loader == null && cachedItem is Loader)
				{
					loader = Loader(cachedItem);
				}
			}
		}
		
		if(result == null)
		{
			if (bitmap != null)
			{
				//xace("\tduplicating bitmap");
				result = new Bitmap(bitmap.bitmapData,bitmap.pixelSnapping,bitmap.smoothing);
				cachedItems.push(result);
			}
			else if (loader != null)
			{
				try {
					if(loader.contentLoaderInfo.childAllowsParent)
					{
						//xace("\t accessible bitmapdata through loader, duplicating bitmap");
						if(loader.content is Bitmap)
						{
							bitmap = Bitmap(loader.content);
							result = new Bitmap(bitmap.bitmapData,bitmap.pixelSnapping,bitmap.smoothing);
							cachedItems.push(bitmap);
						}
					}
					else
					{
						//xace("\t unaccessible Loader content");
					}
				} catch(e:Error) {
						//xace("\t unknown security on Loader");
					// if the content isn't fully loaded yet, it won't know if we have access, and will throw an error.
				}
			}
			if (result == null)
			{
				//xace("\t not in cache or inaccessible: creating new Loader");
				loader = new Loader();
				loader.load(request);
				cachedItems.push(loader);
				result = loader;
			}
		}
		checkLimit();
		
		return result;		
	}

	private function checkLimit():void
	{
		if(_maximumSize <= 0 || _mruList.length <= _maximumSize)
			return;
		//xace("dropping " + (_mruList.length - _maximumSize) + " items");
		for(var i:int = _mruList.length;i>_maximumSize;i--)
		{
			var node:ContentCacheNode = ContentCacheNode(_mruList.pop());
			delete _caches[node.url];
		}
	}
}
}

import qs.utils.DLinkedListNode;
	

class ContentCacheNode extends DLinkedListNode
{
	public var url:String;
	public function ContentCacheNode(url:String):void
	{
		super([]);
		this.url = url;
	}	
}
