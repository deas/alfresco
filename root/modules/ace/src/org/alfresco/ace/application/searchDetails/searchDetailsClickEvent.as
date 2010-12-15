package org.alfresco.ace.application.searchDetails
{
	 import flash.events.Event;
	 /**
	 * This simple custom event works with the searchDetailsClass 
	 * 
	 * @author Saravanan Sellathurai
	 */
	 public class searchDetailsClickEvent extends Event
	 {
		  public var data:Object;
		  public static const SEARCH_LINK_CLICK_EVENT:String = "linkClickEvent";
		  
		  /** Constructor */
		  public function searchDetailsClickEvent(type:String, data:Object, bubbles:Boolean=false, cancelable:Boolean=false)
		  {
		  	super(type, bubbles, cancelable);
		   	this.data = data;
		  }
	
		   /** Override the inherited clone() method. */
		   override public function clone():Event 
		   {
		      return new searchDetailsClickEvent(type, data, this.bubbles, this.cancelable);
		   }
	  
	 }
}