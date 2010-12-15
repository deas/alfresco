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
package qs.controls.flexBookClasses
{
	import mx.core.UIComponent;
	import mx.skins.Border;
	import mx.skins.halo.HaloBorder;
	import mx.core.EdgeMetrics;
	import mx.core.IFlexDisplayObject;
	import flash.display.DisplayObject;
	import flash.display.BitmapData;
	import flash.geom.Rectangle;
	import flash.geom.Matrix;
	import flash.events.Event;
	import mx.events.FlexEvent;

	public class FlexBookPage extends UIComponent
	{
		private var _leftRenderer:IFlexDisplayObject;
		private var _rightRenderer:IFlexDisplayObject;
		private var _allRenderer:IFlexDisplayObject;
		private var _border:Border;
		
		private var _side:String;
		private var _isStiff:Boolean = false;
		public var leftIsStiff:Boolean = false;
		public var rightIsStiff:Boolean = false;
		public var leftIndex:Number;
		public var rightIndex:Number;
		public var allIndex:Number;
		public var leftContent:*;
		public var rightContent:*;
		public var allContent:*;
		
		
		private function updateBorders():void
		{
		}


		public function set isStiff(value:Boolean):void
		{
			_isStiff = value;
		}
		public function get isStiff():Boolean
		{
			return _isStiff;
		}
		
		public function set side(value:String):void
		{
			_side= value;
			invalidateProperties();
		}
		public function get side():String
		{
			return _side;
		}
			
		public function FlexBookPage():void
		{
		}

		override protected function createChildren():void
		{
			super.createChildren();
			var borderClass:Class = getStyle("borderSkin");
			if(borderClass == null)
				borderClass = HaloBorder;
			_border = new borderClass();
			_border.styleName = this;
			addChildAt(_border,0);		
		}

		public function set leftRenderer(value:IFlexDisplayObject):void
		{
			if(value == _leftRenderer)
				return;
				
			if(_leftRenderer != null)
			{
				removeChild(DisplayObject(_leftRenderer));
				_leftRenderer.removeEventListener(FlexEvent.UPDATE_COMPLETE,updateCompleteHandler);
			}
			_leftRenderer = value;
			if(_leftRenderer != null)
			{
				_leftRenderer.addEventListener(FlexEvent.UPDATE_COMPLETE,updateCompleteHandler);
				addChild(DisplayObject(_leftRenderer));
			}
			invalidateDisplayList();
		}
		public function get leftRenderer():IFlexDisplayObject
		{
			return _leftRenderer;
		}
		public function set rightRenderer(value:IFlexDisplayObject):void
		{
			if(value == _rightRenderer)
				return;
				
			if(_rightRenderer != null)
			{
				removeChild(DisplayObject(_rightRenderer));
				_rightRenderer.removeEventListener(FlexEvent.UPDATE_COMPLETE,updateCompleteHandler);
			}
			_rightRenderer = value;
			if(_rightRenderer != null)
			{
				addChild(DisplayObject(_rightRenderer));
				_rightRenderer.addEventListener(FlexEvent.UPDATE_COMPLETE,updateCompleteHandler);
			}
				
			invalidateDisplayList();
		}
		public function get rightRenderer():IFlexDisplayObject
		{
			return _rightRenderer;
		}

		public function set allRenderer(value:IFlexDisplayObject):void
		{
			if(value == _allRenderer)
				return;
				
			if(_allRenderer != null)
			{
				_allRenderer.removeEventListener(FlexEvent.UPDATE_COMPLETE,updateCompleteHandler);
				removeChild(DisplayObject(_allRenderer));
			}
			_allRenderer = value;
			if(_allRenderer != null)
			{
				_allRenderer.addEventListener(FlexEvent.UPDATE_COMPLETE,updateCompleteHandler);
				addChild(DisplayObject(_allRenderer));
			}
			invalidateDisplayList();
		}
		public function get allRenderer():IFlexDisplayObject
		{
			return _allRenderer;
		}
		public function clearContent():void
		{
			leftRenderer = null;
			rightRenderer=  null;
			allRenderer = null;
		}
		
		public function get hasContent():Boolean
		{
			return (_leftRenderer != null || _rightRenderer != null || _allRenderer != null);
		}
		public function get hasLeftContent():Boolean
		{
			return (_leftRenderer != null || _allRenderer != null);
		}
		public function get hasRightContent():Boolean
		{
			return (_rightRenderer != null || _allRenderer != null);
		}

		private function updateCompleteHandler(e:FlexEvent):void
		{
			dispatchEvent(new FlexEvent(e.type,e.bubbles,e.cancelable));
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			var left:Number = getStyle("paddingLeft");
			var top:Number = getStyle("paddingTop");
			var right:Number = getStyle("paddingRight");
			var bottom:Number = getStyle("paddingBottom");
			var paddingSpine:Number = getStyle("paddingSpine");

			var metrics:EdgeMetrics = _border.borderMetrics;
			
			paddingSpine = isNaN(paddingSpine)? 0:paddingSpine;
			
			left = (isNaN(left)? 0:left) + metrics.left;
			right = (isNaN(right)? 0:right) + metrics.right;
			top = (isNaN(top)? 0:top) + metrics.top
			bottom = (isNaN(bottom)? 0:bottom) + metrics.bottom;
			
			
			if(_allRenderer != null || (_leftRenderer != null && _rightRenderer != null))
			{
				_border.setActualSize(unscaledWidth,unscaledHeight);
				_border.move(0,0);
			}
			else if (_leftRenderer == null)
			{
				_border.setActualSize(unscaledWidth/2,unscaledHeight);
				_border.move(unscaledWidth/2,0);
			}
			else
			{
				_border.setActualSize(unscaledWidth/2,unscaledHeight);
				_border.move(0,0);
			}

			if(_allRenderer != null)
			{
				_allRenderer.move(left,top);
				_allRenderer.setActualSize(unscaledWidth - left - right, unscaledHeight - top - bottom);
				if(_allRenderer is UIComponent && UIComponent(_allRenderer).initialized == false)
					UIComponent(_allRenderer).initialized = true;
			}
			if(_leftRenderer != null)
			{
				_leftRenderer.move(left,top);
				_leftRenderer.setActualSize(unscaledWidth/2 - left - paddingSpine,unscaledHeight - top - bottom);
				if(_leftRenderer is UIComponent && UIComponent(_leftRenderer).initialized == false)
					UIComponent(_leftRenderer).initialized = true;
			}
			if(_rightRenderer != null)
			{
				_rightRenderer.setActualSize(unscaledWidth/2 - right - paddingSpine,unscaledHeight - top - bottom);
				_rightRenderer.move(unscaledWidth/2 + paddingSpine,top);
				if(_rightRenderer is UIComponent && UIComponent(_rightRenderer).initialized == false)
					UIComponent(_rightRenderer).initialized = true;
			}
		}
		
		public function copyInto(bitmap:BitmapData,side:String):void
		{
			cacheAsBitmap = false;
			validateNow();
			if(_allRenderer is UIComponent)
				UIComponent(_allRenderer).validateNow();
			if(_leftRenderer is UIComponent)
				UIComponent(_leftRenderer).validateNow();
			if(_rightRenderer is UIComponent)
				UIComponent(_rightRenderer).validateNow();

			var m:Matrix = new Matrix();
			var rc:Rectangle;
			if(side == "left")
			{
				if(bitmap.width < unscaledWidth)
				{
//					m.translate(unscaledWidth/2,0);
					var x:int = 0;
				}
				bitmap.draw(this,m,null,null,new Rectangle(0,0,unscaledWidth/2,unscaledHeight));
			}
			else if (side == "right")
			{
				if(bitmap.width < unscaledWidth)
				{
					m.translate(-unscaledWidth/2,0);
					rc = new Rectangle(0,0,unscaledWidth/2,unscaledHeight)
				}
				else
				{
					rc = new Rectangle(unscaledWidth/2,0,unscaledWidth/2,unscaledHeight)
				}
				bitmap.draw(this,m,null,null,rc);
			}
			else
				bitmap.draw(this);
				
			cacheAsBitmap = true;
		}
	}
}