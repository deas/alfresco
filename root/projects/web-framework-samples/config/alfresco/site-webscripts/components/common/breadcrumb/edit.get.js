<import resource="/include/support.js">

var properties = instance.object.properties;

model.title = form.bind("title", properties["title"], "");
model.description = form.bind("description", properties["description"], "");

// separator character
model.separatorChar = form.bind("separatorChar", properties["separatorChar"], "");
