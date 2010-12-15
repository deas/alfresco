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
	import flash.events.Event;
	import mx.core.IFlexDisplayObject;

	public class FlexBookEvent extends Event
	{
		public function FlexBookEvent(type:String, bubbles:Boolean, cancelable:Boolean, index:Number, content:*, renderer:IFlexDisplayObject)
		{
			this.index = index;
			this.content = content;
			this.renderer = renderer;
			super(type, bubbles, cancelable);
		}
		
		public var index:Number;
		public var content:*;
		public var renderer:IFlexDisplayObject;
		
		public static const TURN_START:String = "turnStart";
		public static const TURN_END:String = "turnEnd";
		
	}
}