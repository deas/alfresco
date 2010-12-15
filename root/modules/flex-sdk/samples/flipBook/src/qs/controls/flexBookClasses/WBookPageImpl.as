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

	public class WBookPageImpl extends UIComponent
	{
		private var _content:IFlexDisplayObject;
		private var _border:Border;
		
		private var _side:String;
		private var _isCover:Boolean = false;
		private var _isStiff:Boolean = false;
		
		private function updateBorders():void
		{
		}

		public function set isCover(value:Boolean):void
		{
			_isCover = value;
			invalidateProperties();
		}
		public function get isCover():Boolean
		{
			return _isCover;
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
			
		public function WBookPageImpl():void
		{
		}

		override protected function commitProperties():void
		{
			if(_isCover)
			{
				clearStyle("borderSides");
			}
			else
			{
				switch(_side)
				{
					case "left":
						setStyle("borderSides","left top bottom");
						break;
					case "right":
						setStyle("borderSides","right top bottom");
						break;
				}
			}

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
		public function set content(value:IFlexDisplayObject):void
		{
			if(value == _content)
				return;
				
			if(_content != null)
				removeChild(DisplayObject(_content));
			_content = value;
			if(_content != null)
				addChild(DisplayObject(_content));
			invalidateDisplayList();
		}
		public function get content():IFlexDisplayObject
		{
			return _content;
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
			
			left = (side == "right"? paddingSpine:isNaN(left)? 0:left) + metrics.left;
			right = (side == "left"? paddingSpine:isNaN(right)? 0:right) + metrics.right;
			top = (isNaN(top)? 0:top) + metrics.top
			bottom = (isNaN(bottom)? 0:bottom) + metrics.bottom;
			
			
			_border.setActualSize(unscaledWidth,unscaledHeight);
			
			if(_content != null)
			{
				_content.move(left,top);
				_content.setActualSize(unscaledWidth - left - right, unscaledHeight - top - bottom);
			}
		}
	}
}