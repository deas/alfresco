if (typeof Surf == "undefined")
{
	var Surf = {};
}

Surf.Mimetypes = function()
{
	this.test = "hello";
}

Surf.Mimetypes.prototype.getIcon16 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 16);
}

Surf.Mimetypes.prototype.getIcon24 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 24);
}

Surf.Mimetypes.prototype.getIcon32 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 32);
}

Surf.Mimetypes.prototype.getIcon48 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 48);
}

Surf.Mimetypes.prototype.getIcon64 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 64);
}

Surf.Mimetypes.prototype.getIcon72 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 72);
}

Surf.Mimetypes.prototype.getIcon96 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 96);
}

Surf.Mimetypes.prototype.getIcon128 = function(filename, mimetype)
{
	return this.getIcon(filename, mimetype, 128);
}

Surf.Mimetypes.prototype.getIcon = function(filename, mimetype, size)
{
	var path = this.getIconRoot(filename, mimetype);
	
	path += "-" + size + ".png";
	
	return path;
}

Surf.Mimetypes.prototype.getIconRoot = function(filename, mimetype)
{
	var core = null;
	
	// make first guess simply based on the filename extension
	var ext = null;
	if(filename.indexOf(".") > -1)
	{
		var i = filename.indexOf(".");
		ext = filename.substring(i+1);
	}
	
	if(ext != null)
	{
		core = this.getIconByExtension(ext);
	}
	
	// either we couldn't search by extension
	// or the extension search returned nothing of interest
	if(core == null)
	{
		core = this.getIconByMimetype(mimetype);
	}
	
	return url.context + "/images/common/filetypes/" + core;
}

Surf.Mimetypes.prototype.getIconByExtension = function(ext)
{
	var core = null;
	
	if(ext == "aac")
	{
		core = "aac";
	}
	else if(ext == "mdb")
	{
		core = "mdb";
	}
	else if(ext == "avi")
	{
		core = "avi";
	}
	else if(ext == "bmp")
	{
		core = "bmp";
	}
	else if(ext == "chm")
	{
		core = "chm";
	}
	else if(ext == "css")
	{
		core = "css";
	}
	else if(ext == "dll")
	{
		core = "dll";
	}
	else if(ext == "xls")
	{
		core = "excel";
	}
	else if(ext == "xlsx")
	{
		core = "excel";
	}
	else if(ext == "gif")
	{
		core = "gif";
	}
	else if(ext == "html")
	{
		core = "html";
	}
	else if(ext == "htm")
	{
		core = "html";
	}
	else if(ext == "ini")
	{
		core = "ini";
	}
	else if(ext == "jpg")
	{
		core = "jpg";
	}
	else if(ext == "jpeg")
	{
		core = "jpg";
	}
	else if(ext == "js")
	{
		core = "js";
	}
	else if(ext == "mov")
	{
		core = "mov";
	}
	else if(ext == "qt")
	{
		core = "mov";
	}
	else if(ext == "mp3")
	{
		core = "mp3";
	}
	else if(ext == "mpg")
	{
		core = "mpg";
	}
	else if(ext == "mpeg")
	{
		core = "mpeg";
	}
	else if(ext == "pdf")
	{
		core = "pdf";
	}
	else if(ext == "png")
	{
		core = "png";
	}
	else if(ext == "ppt")
	{
		core = "powerpoint";
	}
	else if(ext == "pptx")
	{
		core = "powerpoint";
	}
	else if(ext == "real")
	{
		core = "real";
	}
	else if(ext == "rm")
	{
		core = "real";
	}
	else if(ext == "ra")
	{
		core = "real";
	}
	else if(ext == "rtf")
	{
		core = "rtf";
	}
	else if(ext == "txt")
	{
		core = "text";
	}
	else if(ext == "wav")
	{
		core = "wav";
	}
	else if(ext == "wma")
	{
		core = "wma";
	}
	else if(ext == "wmv")
	{
		core = "wmv";
	}
	else if(ext == "doc")
	{
		core = "word";
	}
	else if(ext == "docx")
	{
		core = "word";
	}
	else if(ext == "xml")
	{
		core = "xml";
	}
	else if(ext == "xsl")
	{
		core = "xsl";
	}
	
	return core
}

Surf.Mimetypes.prototype.getIconByMimetype = function(mimetype)
{
	var core = "default";
	
	if(mimetype != null)
	{
		var i = mimetype.indexOf("/");
		if(i == -1)
		{
			return core;
		}

		var car = mimetype.substring(0, i);

		if(car == "image")
		{
			core = "other_image";
		}
		else if(car == "video")
		{
			core = "other_movie";
		}
		else if(car == "audio")
		{
			core = "other_music2";
		}
		else if(car == "text")
		{
			core = "text";		
		}
	}
	else
	{
		// assume it is a folder
		core = "folder";
	}
	
	return core;
}
