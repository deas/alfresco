function PeopleFinder (config)
{
  this.config = config;
  this.element = x$(this.config.id);
}
PeopleFinder.prototype.find = function(searchArgs,callback) {
  var uriSegment = new RegExp('{(.*?)}','g');
  var uri = Mobile.util.substitute(this.config.dataUri,searchArgs);
  // if all template has been fully resolved do xhr
  if (uri.isFullyResolved)
  {
    this.element.xhr(uri.s,{callback:callback});
    return true;
  }
  return false;
};