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
package qs.effects.effectClasses
{
	import mx.effects.effectClasses.TweenEffectInstance;
	import mx.core.mx_internal;
	import qs.utils.ColorUtils;
	
	use namespace mx_internal;
		
	public class AnimateColorPropertyInstance extends TweenEffectInstance
	{
		public function AnimateColorPropertyInstance(target:Object)
		{
			super(target);
		}
		public var property:String;
		public var toValue:Number;
		public var fromValue:Number;
		public var isStyle:Boolean = false;
		
		private var fromHSV:Object;
		private var toHSV:Object;
			
		override public function play():void
		{
			// Do what effects normally do when they start, namely
			// dispatch an 'effectStart' event from the target.
			super.play();

			fromHSV = ColorUtils.RGBToHSV(fromValue);
			toHSV = ColorUtils.RGBToHSV(toValue);			
			// Create a Tween 
			tween = /*mx_internal::*/createTween(this, 0, 1, duration);
	
			// If the caller supplied their own easing equation, override the
			// one that's baked into Tween.
			if (easingFunction != null)
				tween.easingFunction = easingFunction;
	
			onTweenUpdate(Number(tween.mx_internal::getCurrentValue(0)));
		}
		
		
		/**
		 * @private
		 */
		override public function onTweenUpdate(value:Object):void
		{
			var newHSV:Object = {
				h: fromHSV.h + (toHSV.h - fromHSV.h)*Number(value),
				s: fromHSV.s + (toHSV.s - fromHSV.s)*Number(value),
				v: fromHSV.v + (toHSV.v - fromHSV.v)*Number(value)			
			}
			var rgb:Number = ColorUtils.HSVToRGB(newHSV);
			if(isStyle)
				target.setStyle(property,rgb);
			else
				target[property] = rgb;
		}

	}
	
}