var filename = "#empty#";
var content = "#empty#";

for each (field in formdata.fields) {
    if (field.name == "file" && field.isFile) {
        filename = field.filename;
        content = field.content;
    }
}

model.filename = filename;
model.content = content;