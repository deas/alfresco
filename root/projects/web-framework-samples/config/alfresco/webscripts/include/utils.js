Array.prototype.clear=function()
{
	this.length = 0;
};

function generateID()
{
	var d = new Date();
	return d.getTime();
};

function getJsonArrayValue(formElements, elementName, matchValue)
{

    for(var elementsIndx=0;elementsIndx < formElements.length; elementsIndx++)
    {
        if(formElements[elementsIndx].name.match(matchValue))
        {
            return formElements[elementsIndx].value;
        }
    }
    
    return null;

};