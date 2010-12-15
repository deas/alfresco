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
package qs.controls
{
	import flash.geom.Matrix;
	
	public class Zoomer extends Wrapper
	{
		private var _maintainAspectRatio:Boolean = true;
		public function set maintainAspectRatio(value:Boolean):void
		{
			if(_maintainAspectRatio != value)
			{
				_maintainAspectRatio = value;
				invalidateDisplayList();
			}
		}
		public function get maintainAspectRatio():Boolean
		{
			return _maintainAspectRatio;
		}

		public function Zoomer()
		{
		}

		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			if(child == null)
				return;
			var m:Matrix;
			
			if(_maintainAspectRatio)
			{
				child.setActualSize(child.getExplicitOrMeasuredWidth(),child.getExplicitOrMeasuredHeight());
				m = child.transform.matrix;
				var scale:Number= Math.min(unscaledWidth/child.getExplicitOrMeasuredWidth(),unscaledHeight/child.getExplicitOrMeasuredHeight());
				m.a = scale;
				m.d = scale;
				child.transform.matrix = m;
				child.move( unscaledWidth/2 - child.getExplicitOrMeasuredWidth()*scale/2,
							unscaledHeight/2 - child.getExplicitOrMeasuredHeight()*scale/2);
			}
			else
			{
				child.setActualSize(child.getExplicitOrMeasuredWidth(),child.getExplicitOrMeasuredHeight());
				m = child.transform.matrix;
				m.a = unscaledWidth/child.getExplicitOrMeasuredWidth();
				m.d = unscaledHeight/child.getExplicitOrMeasuredHeight();
				child.transform.matrix = m;
				child.move(0,0);
			}
		}
	}
}