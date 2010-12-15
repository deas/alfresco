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
package org.alfresco.ace.control.dashedline
{
	import mx.containers.Canvas
	import mx.controls.HRule;
	import mx.core.UIComponent;
	import flash.display.Sprite;
	
	
	public class DrawLine extends  Canvas
	{			
		public function DrawLine()
		{			
			var line:Canvas = new Canvas();
			line.percentWidth = 100;
			drawHorizontal(line, 0, 1250, 0, 3, 0, 0xafa0a0);
			this.addChild(line);
		}
		
		private function drawHorizontal(target:*, x1:uint, x2:uint, y:uint, lineLength:uint, lineThickness:uint, lineColor:uint):void 
		{
			drawDashedLine(target, x1, y, x2, y, lineLength, lineThickness, lineColor);
		}
		
	    private function drawDashedLine(target:*, x1:uint, y1:uint, x2:uint, y2:uint, lineLength:uint, lineThickness:uint, lineColor:uint):void 
	 	{
	 	    var complete:Boolean = false;
			var child:Canvas = new Canvas();
			child.width = target.width;
			child.height = target.height;
			child.horizontalScrollPolicy = "off";
			child.verticalScrollPolicy = "off";
			child.graphics.lineStyle(lineThickness, lineColor, 1);
			
			var tempVal:uint = 0;


			if (y1 == y2) 
			{
				if (x1 > x2) 
				{
					tempVal = x1;
					x1 = x2;
					x2 = tempVal;
				}
			} 
			else 
			{
				if (y1 > y2) 
				{
					tempVal = y1;
					y1 = y2;
					y2 = tempVal;
				}
			}
		
			child.graphics.moveTo(x1, y1)
			
			function drawLine():void 
			{   
				if (y1 == y2) 
				{
					child.graphics.lineTo(x1 + lineLength, y1);
					x1 += lineLength;
				} 
				else 
				{
					child.graphics.lineTo(x1, y1 + lineLength);
					y1 += lineLength;
				}
			}
				
			// this function moves the graphic cursor ahead of the previous drawing point
			function drawSpace():void 
			{
				if (y1 == y2) 
				{
					child.graphics.moveTo(x1 + 2, y1);
					x1 += 2;
				} 
				else 
				{
					child.graphics.moveTo(x1, y1 + 2);
					y1 += 2;
				}
			} 
		
			while (!complete) 
			{
				if (y1 == y2) 
				{
					if (x1 + lineLength >= x2) 
					{
						lineLength -= ((x1 + lineLength) - x2);
						complete = true;
					}
				} 
				else 
				{
					if (y1 + lineLength >= y2)
					{
						lineLength -= ((y1 + lineLength) - y2);
						complete = true;
					}
				}
		
				drawLine();
				drawSpace();
			}
					
			target.addChild(child);
   
  		} 		
	}
}