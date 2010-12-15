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
package org.alfresco.ace.control.gradient
{
	import flash.display.*;
	import flash.geom.*;
	import flash.utils.*;
	
	import mx.core.EdgeMetrics;
	import mx.skins.halo.HaloBorder;
	import mx.utils.ColorUtil;
	import mx.utils.GraphicsUtil;
	
	public class GlossGradientBorder extends HaloBorder 
	{
		
		private var topCornerRadius:Number;		// top corner radius
		private var bottomCornerRadius:Number;	// bottom corner radius
		private var fillColors:Array;			// fill colors (two)
		private var setup:Boolean;
		
		// ------------------------------------------------------------------------------------- //
		
		private function setupStyles():void
		{
			fillColors = getStyle("fillColors") as Array;
			if (!fillColors) fillColors = [0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF];
			
			if (getStyle("cornerRadius") != null)
			{
				topCornerRadius = getStyle("cornerRadius") as Number;
			}
			else
			{				
				topCornerRadius = 0;	
			}

			if (getStyle("bottomCornerRadius") != null)
			{
				bottomCornerRadius = getStyle("bottomCornerRadius") as Number;
			}
			else
			{
				bottomCornerRadius = topCornerRadius;		
			}		
		}
		
		// ------------------------------------------------------------------------------------- //
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);	
			
			setupStyles();
			
			var g:Graphics = graphics;
			var b:EdgeMetrics = borderMetrics;
			var w:Number = unscaledWidth - b.left - b.right;
			var h:Number = unscaledHeight - b.top - b.bottom;
			var m:Matrix = verticalGradientMatrix(0, 0, w, h);
				
		
			g.beginGradientFill("linear", fillColors, [1, 1, 1, 1], [0, 127, 127, 255], m);
			
			var tr:Number = Math.max(topCornerRadius-2, 0);
			var br:Number = Math.max(bottomCornerRadius-2, 0);
			
			GraphicsUtil.drawRoundRectComplex(g, b.left, b.top, w, h, tr, tr, br, br);
			g.endFill();
				
		}
		
	}
}
