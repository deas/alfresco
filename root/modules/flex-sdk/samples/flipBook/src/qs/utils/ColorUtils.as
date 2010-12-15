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

package qs.utils
{
public class ColorUtils
{
	public static function HSVToRGB(hsv:Object):Number
	{
		var src:Object = hsv;
		var dst:Number
	
		var h:Number,s:Number,v:Number;
	
		h = hsv.h;
		s = hsv.s;
		v = hsv.v;
		
		var i:Number;
		var f:Number, p:Number, q:Number, t:Number;
		var r:Number,g:Number,b:Number;
		if( s == 0 ) {
			// achromatic (grey)
			v = Math.floor(v*255);
			dst = (v << 16) + (v << 8) + v;
			return dst;
		}
		h /= 60;			// sector 0 to 5
		i = Math.floor( h );
		f = h - i;			// factorial part of h
		p = v * ( 1 - s );
		q = v * ( 1 - s * f );
		t = v * ( 1 - s * ( 1 - f ) );
		switch( i ) {
			case 0:
				r = v;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = v;
				b = p;
				break;
			case 2:
				r = p;
				g = v;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = v;
				break;
			case 4:
				r = t;
				g = p;
				b = v;
				break;
			default:		// case 5:
				r = v;
				g = p;
				b = q;
				break;
		}
		dst = (Math.floor(r*255) << 16) + (Math.floor(g*255) << 8) + (Math.floor(b*255));
		return dst;
	}

	public static function RGBToHSV(src:Number):Object
	{
		var dst:Object = new Object();
	
		var red:Number =  (src >> 16) / 255;
		var green:Number = (0xFF & (src >> 8)) / 255;
		var blue:Number = (0xFF & src)/ 255;
	
		var min:Number = Math.min( Math.min(red, green) , blue );
		var max:Number= Math.max( Math.max(red, green) , blue );
		dst.v = max;				// v
		var delta:Number = (max - min);
		if( max != 0 )
			dst.s = delta / (max);		// s
		else {
			// r = g = b = 0		// s = 0, v is undefined
			dst.s = 0;
			dst.h = -1;
			return dst;
		}
		if( red == max )
		{
			dst.h = ( green - blue ) / delta;		// between yellow & magenta
		}
		else if( green == max )
		{
			dst.h = 2 + ( blue - red ) / delta;	// between cyan & yellow
		}
		else
		{
			dst.h = 4 + ( red - green ) / delta;	// between magenta & cyan
		}
		dst.h *= 60;				// degrees
		if( dst.h < 0 )
			dst.h += 360;
	
	
		return dst;
		
	}
}
}