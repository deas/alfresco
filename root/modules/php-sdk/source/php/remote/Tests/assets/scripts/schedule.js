function schedule(objectID, functionCall) {
	if (document.getElementById(objectID)) eval(functionCall);
	else setTimeout("schedule('" + objectID + "', '" + functionCall + "')", 20);	
	return true;
}