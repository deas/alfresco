model.title = instance.properties["title"];
if(model.title == null)
{
	model.title = "";
}

model.description = instance.properties["description"];
if(model.description == null)
{
	model.description = "";
}

var separatorChar = instance.properties["separatorChar"];
if(separatorChar == null)
{
	separatorChar = ">";
}
model.separatorChar = separatorChar;


// the current page
var page = context.page;
model.currentPageId = context.page.id;

// construct the array
var array = new Array();
do
{
	if(page != null)
	{
		array[array.length] = page;
		
		var pages = sitedata.findParentPages(page.id);
		if(pages != null && pages.length > 0)
		{
			page = pages[0];
		}
		else
		{
			page = null;
		}
	}	
}
while(page != null);

// reverse the array
model.pages = array.reverse();
