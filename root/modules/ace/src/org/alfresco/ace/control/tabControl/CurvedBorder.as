package org.alfresco.ace.control.tabControl
{
	import flash.display.*;
	import flash.geom.*;
	import flash.utils.*;
	
	import mx.core.EdgeMetrics;
	import mx.skins.halo.HaloBorder;
	import mx.utils.ColorUtil;
	import mx.utils.GraphicsUtil;
	
	/**
	 * Custom border class used in the creation of the tab control
	 *
	 * @author Roy Wetherall
	 */
	public class CurvedBorder extends HaloBorder 
	{
		
		private var cornerRadius:Number;		
		private var fillColors:Array;			
		private var setup:Boolean;
		private var side:String = "right";
		
		private function setupStyles():void
		{
			fillColors = getStyle("fillColors") as Array;
			if (!fillColors) fillColors = [0xFFFFFF, 0xFFFFFF];
			
			if (getStyle("cornerRadius") != null)
			{
				cornerRadius = getStyle("cornerRadius") as Number;
			}
			else
			{				
				cornerRadius = 0;	
			}	
			
			if (getStyle("side") != null)
			{
				this.side = getStyle("side") as String;
			}	
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);	
			
			setupStyles();
			
			var g:Graphics = graphics;
			var b:EdgeMetrics = borderMetrics;
			var w:Number = unscaledWidth - b.left - b.right;
			var h:Number = unscaledHeight - b.top - b.bottom;
			var m:Matrix = verticalGradientMatrix(0, 0, w, h);
						
			g.beginGradientFill("linear", fillColors, [1, 1], [0, 255], m);
			
			var cr:int = Math.max(cornerRadius-2, 0);
			
			if (this.side == "right")
			{
				GraphicsUtil.drawRoundRectComplex(g, b.left, b.top, w, h, 0, 0, 0, cr);
			}
			else if (this.side == "left")
			{
				GraphicsUtil.drawRoundRectComplex(g, b.left, b.top, w, h, 0, 0, cr, 0);			
			}
			g.endFill();
				
		}
		
	}
}
