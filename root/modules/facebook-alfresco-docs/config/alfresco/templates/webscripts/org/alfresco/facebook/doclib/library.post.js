model.library = search.findNode("workspace://SpacesStore/" + url.extension);
if (model.library == undefined)
{
  status.code = 500;
  status.message = "Library " + url.extension + " does not exist.";
  status.redirect = true;
}