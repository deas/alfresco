<link type="text/css" rel="stylesheet" href="/alfresco/scripts/ajax/yahoo/calendar/assets/calendar.css">	
<link rel="stylesheet" type="text/css" href="/alfresco/scripts/ajax/yahoo/docs/assets/dpSyntaxHighlighter.css" />
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/yahoo/yahoo.js;"></script>
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/event/event.js" ></script>
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/dom/dom.js" ></script>
<link type="text/css" rel="stylesheet" href="/alfresco/scripts/ajax/yahoo/fonts/fonts.css">
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/calendar/calendar.js"></script>
<script type="text/javascript">

var xhr 			 = false;
var maxresults       = 5;
var status           = 'Any';
var askid	         = '';
var article_type     = '';
var article_modifier = '';
var category		 = '';
var modified         = '';
var searchText   	 = '';
var visibility       = '';
var alfresco_version = '';

function setMaxresults(){
	maxresults = document.getElementById("maxresults").value;
}

function setStatus(){
	status    = document.getElementById("status").value;
	send();
}

function setAlfrescoversion(){
	alfresco_version= document.getElementById("alfresco_version").value;
	send();
}

function setArticletype(){
	article_type  =  document.getElementById("article_type").value;
	send();
}

function setModifier(){
	article_modifier = document.getElementById("article_modifier").value;
	send();
}

function setVisibility(){
	visibility=document.getElementById('visibility').value;
	send();
}

function categorydisplay() {
	vista = (document.getElementById("categorydisplay").style.display == 'none') ? 'block' : 'none';
	document.getElementById("categorydisplay").style.display = vista;
	//Change the text on the basic/advanced hyperlink
	toggle = (document.getElementById("toggleadvanced").innerHTML == 'Advanced&gt;&gt;') ? '&lt;&lt;Basic' : 'Advanced&gt;&gt;';
	document.getElementById("toggleadvanced").innerHTML = toggle;
	if (toggle == "Advanced&gt;&gt;") {
			resetCategories();
	}
}

<!-- For Yahoo YUI Calendar control -->
YAHOO.namespace("example.calendar");

function init() {
    //Modified Date Handler
	YAHOO.example.calendar.cal2 = new YAHOO.widget.Calendar("YAHOO.example.calendar.cal2", "cal2Container");
	YAHOO.example.calendar.cal2.onSelect = function(selected) {
	    document.getElementById("cal2Container").style.display = 'none';
		var dateString = selected;
		var dateArray=dateString.toString().split(",");
		var theYear = dateArray[0];
		var theMonth = dateArray[1];
		var theDay = dateArray[2];
		document.getElementById("modified").value = theDay + "/" + theMonth + "/" + theYear;
	    modified=  document.getElementById("modified").value;
	    send();
	}
	YAHOO.example.calendar.cal2.onDeselect = function(deselected) {}
	YAHOO.example.calendar.cal2.render();
}
	YAHOO.util.Event.addListener(window, "load", init);

function showCalendar2() {
	var pos = YAHOO.util.Dom.getXY("link2");
	document.getElementById("cal2Container").style.display = 'block';
}
<!-- End of calender handling stuff -->

function resetCategories() {
	document.getElementById("alfresco_version").options[0].selected = true;
	document.getElementById("visibility").options[0].selected = true;
	document.getElementById("cal2Container").style.display='none'
	document.getElementById("modified").value ='';
}

function resetSearchArticles() {
	document.getElementById("alfresco_version").options[0].selected = true;
   	document.getElementById("status").options[0].selected = true;
   	document.getElementById("article_modifier").options[0].selected = true;
    document.getElementById("visibility").options[0].selected = true;
	document.getElementById("article_type").options[0].selected = true;
    document.getElementById("maxresults").options[0].selected = true;
	resetCategories('');
    document.getElementById("categorydisplay").style.display = 'none'
	document.getElementById("toggleadvanced").innerHTML = 'Advanced&gt;&gt;';
	document.getElementById("modified").value = "";
	document.getElementById("searchText").value = "";
	document.getElementById("askid").value = "";
    document.getElementById("searchResults").innerHTML='';
    maxresults       = 5;
    status           = 'Any';
    askid	         = '';
    article_type     = '';
    article_modifier = '';
    modified         = '';
    searchText  	 = '';
    visibility       = '';
    alfresco_version = '';
	document.getElementById("cal2Container").style.display='none'
    document.getElementById("modified").value ='';
}

function askidsearch(){
	askid=document.getElementById('askid').value;
	document.getElementById('askid').value='';
	if(askid!=''){
		var url="/alfresco/service/kb/advancedsearchresults?askid="+askid+"&maxresults="+maxresults;
		makeRequest(url);
		askid='';
	}
	else {
		alert("ASKID cannot be null");
		return false;
	}
}

function pagination(p){
     var url="/alfresco/service/kb/advancedsearchresults?searchText="+searchText+"&askid="+askid+"&maxresults="+maxresults+"&status="+status+"&modifier="+article_modifier+"&article_type="+article_type+"&article_modifier="+article_modifier+"&visibility="+visibility+"&modified="+modified+"&alfresco_version="+alfresco_version+"&p="+p;
     makeRequest(url);
     return false;
}

function send(){
	searchText=document.getElementById('searchText').value;
	var url="/alfresco/service/kb/advancedsearchresults?searchText="+searchText+"&askid="+askid+"&maxresults="+maxresults+"&status="+status+"&modifier="+article_modifier+"&article_type="+article_type+"&article_modifier="+article_modifier+"&visibility="+visibility+"&modified="+modified+"&alfresco_version="+alfresco_version;
	makeRequest(url);
	return false;
}

function textsearch(){
	searchText=document.getElementById('searchText').value;
	var url="/alfresco/service/kb/advancedsearchresults?searchText="+searchText+"&askid="+askid+"&maxresults="+maxresults+"&status="+status+"&modifier="+article_modifier+"&article_type="+article_type+"&article_modifier="+article_modifier+"&visibility="+visibility+"&modified="+modified+"&alfresco_version="+alfresco_version;
	makeRequest(url);
	return false;
}

function searchcheckWFForm(nodeid, returnid) {
	var reviewer = document.getElementById("workflowAssignee").value;
    if (document.getElementById("workflowAssignee").options[0].selected == true)	{
    	alert("Please select a reviewer");
    	//return false;
   	}
    else {		
  			var url="/alfresco/command/script/execute?scriptPath=/Company%20Home/Data%20Dictionary/Knowledge%20Base%20Templates/Scripts/start_workflow.js&workflowAssignee=" + reviewer + "&nodeid=" + nodeid + "&returnid=" + returnid;
   			makeRequest(url);
   	}
}

function searchCancelWFForm(nodeid,returnid) {
	document.getElementById('searchResults').innerHTML = "";
}

function searchsndWFReq(nodeid) {
	   var url ="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/start_ask_approval_workflow.ftl&returnid=search" + nodeid + "&nodeid=" + nodeid+"";
	   makeRequest(url);
}   

function getNewFile() {
	makeRequest(url);
	return false;
}

function makeRequest(url) {
	if (window.XMLHttpRequest) {
		xhr = new XMLHttpRequest();
	}
	else {
		if (window.ActiveXObject) {
			try {
				xhr = new ActiveXObject("Microsoft.XMLHTTP");
			}
			catch (e) { }
		}
	}
	if (xhr) {
		xhr.onreadystatechange = showContents;
		xhr.open("GET", url, true);
		xhr.send(null);
	}
	else {
		document.getElementById("searchResults").innerHTML = "Sorry, but I couldn't create an XMLHttpRequest";
	}
}

function showContents() {
	if (xhr.readyState == 4) {
		if (xhr.status == 200) {
			var outMsg = (xhr.responseXML && xhr.responseXML.contentType=="text/xml") ? xhr.responseXML.getElementsByTagName("choices")[0].textContent : xhr.responseText;
		}
		else {
			var outMsg = "There was a problem with the request " + xhr.status;
		}
		document.getElementById("searchResults").innerHTML = outMsg;
	}
}

function reset(){
	document.getElementById("askid").value='';
	document.getElementById("searchResults").innerHTML ="";
}



</script>
<#-- Form for collecting search criteria -->
<h3>Alfresco Knowledge Base Search</h3> 
<form id="searcharticles" name="searcharticles" method="get" action="javascript:send('');">	

<table width="100%" border="0">
	<tr valign="top">
		<td align="left" valign="middle"><img valign="top" width="32" height="32" src="/alfresco/images/logo/AlfrescoLogo32.png"/></td>
		<td align="left" valign="top">
			<input type="text" id="searchText" name="searchText" value="" onKeyPress="{if (event.keyCode==13) textsearch();}"/>
			<input type="button"  value="Search" onclick="javascript:textsearch();"/>
			<input onclick="javascript:resetSearchArticles();" type="button" value="Reset"/>
			| Show <SELECT id="maxresults" NAME="maxresults" onchange="javascript:setMaxresults('');">
	        			<option id="5" name="5" value=5>5</option>
		       			<option id="10" name="10" value=10>10</option>
				        <option id="15" name="15" value=15>15</option>
				        <option id="20" name="20" value=20>20</option>
				        <option id="50" name="50" value=50>50</option>
				    </select> Items
	    </td>
	    <td valign="top" align="right"><td>ASK ID:
	    <input  type="text" name="askid" id="askid" onKeyPress="{if (event.keyCode==13) askidsearch();}"/><input type="button" value="Search" onclick="javascript:askidsearch();">
	    </tr>
</table>
<table border="0" valign="top">
	<tr>
		<td valign="top">Status
			<select name="status" id="status" onchange="javascript:setStatus('');">
			  <option value="Any">Any</option>
				  <#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""]?sort_by("name")?reverse as child>            		
				      	  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
							<#if rootnode.nodeRef = child.nodeRef>
								<#list rootnode.immediateSubCategories  as all>
								  <#if all.properties.name = "Article Status">
									<#list all.subCategories as mylist>
										<option value="${mylist.nodeRef}">${mylist.properties.name}</option>
									</#list>
								 </#if>
							</#list>
						</#if>
					  </#list>
				</#list>
			</select>
		</td>
		<td valign="top">&nbsp;| Type&nbsp;</td><td valign="top">

			<select name="article_type" id="article_type" onchange="javascript:setArticletype('');">
				 <#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""]?sort_by("name")?reverse as child>            		
				      	  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
							<#if rootnode.nodeRef = child.nodeRef>
								<#list rootnode.immediateSubCategories  as all>
								  <#if all.properties.name = "Article Type">
									<#list all.subCategories as mylist>
										<option value="${mylist.nodeRef}">${mylist.properties.name}</option>
									</#list>
								 </#if>
							</#list>
						</#if>
					  </#list>
				</#list>
			</select>
		
		</td>

		<td valign="top">&nbsp;| Modifier&nbsp;</td><td valign="top">
			<select name="article_modifier" id="article_modifier" onchange="javascript:setModifier('');">
				<option value="Any">Any</option>
				<#assign currentUser="${person.properties.userName}">
				<#list companyhome.childrenByLuceneSearch["+TYPE:\"{http://www.alfresco.org/model/content/1.0}person\""]?sort_by(['properties', 'userName']) as child>
					<option value="${child.properties.userName}">${child.properties.userName} <#if child.properties.userName = currentUser>(CURRENT)</#if></option>
				</#list>						
			</select>
		</td>
	</tr>
</table>	

<div><a id="toggleadvanced" href="javascript:categorydisplay();">Advanced&gt;&gt;</a></div>
<div id="categorydisplay"  style="display:none">
<table border="0">
	<tr>
		<td valign="top">
			<label for="alfresco_version">Alfresco Version(s)</label>
				<select name="alfresco_version" id="alfresco_version"  onchange="javascript:setAlfrescoversion('');">
					<option value="Any" selected=selected>Any</option>
						 <#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""]?sort_by("name")?reverse as child>            		
					      	  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
								<#if rootnode.nodeRef = child.nodeRef>
									<#list rootnode.immediateSubCategories  as all>
							  			<#if all.properties.name = "Alfresco Versions">
											<#list all.subCategories as mylist>
												<option value="${mylist.nodeRef}">${mylist.properties.name}</option>
											</#list>
										 </#if>
									</#list>
								</#if>
							  </#list>
						</#list>
				</select>
			</td>
			<td valign="top">&nbsp;| Visibility&nbsp;</td><td valign="top">
				<select name="visibility" id="visibility" onchange="javascript:setVisibility();">
					<option selected=selected>Any</option>
                         <#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""]?sort_by("name")?reverse as child>            		
					      	  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
									<#if rootnode.nodeRef = child.nodeRef>
										<#list rootnode.immediateSubCategories  as all>
							  				<#if all.properties.name = "Article Visibility">
												<#list all.subCategories as mylist>
													<option value="${mylist.nodeRef}">${mylist.properties.name}</option>
												</#list>
							 				</#if>
										</#list>
									</#if>
							  </#list>
						</#list>
				</select>
				</td><td valign="top">&nbsp;| Modified&nbsp;</td><td valign="top"><input name="modified" id="modified" size="8"></input></td>
				<td valign="top">
					<div id="cal2Container" name="cal2Container"  style="position:absolute;display:none"></div>
					<div>
						  <a href="javascript:void(null)" onclick="showCalendar2()"><img id="link2" name="link2" src="/alfresco/scripts/ajax/yahoo/calendar/assets/pdate.gif" border="0" style="vertical-align:middle;margin:5px"/>
					</div>
				</td>
	</tr>
	<tr></td></tr>
</table>
</form>
</div>
<#-- Search results will be show in the following search results div tag via AJAX -->
<div id="searchResults">
</div>







