<?xml version="1.0" encoding="UTF-8"?>
<service xmlns:atom="http://www.w3.org/2005/Atom" xmlns="http://purl.org/atom/app#">
	<workspace>
		<atom:title type="text">${workspace.getTitle()}</atom:title>
		<collection href="${collection.getHref()}">
			<atom:title type="text">${collection.getTitle()}</atom:title>
		</collection>
	</workspace>
</service>