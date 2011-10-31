<#include "../component.head.inc">
<!-- Wiki -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/wiki/wiki.css" />
<link rel="alternate" type="application/wiki" href="${page.url.servletContext}/site/${page.url.templateArgs.site}/wiki-page?title=${(page.url.args.title!"")?url}&amp;action=edit" />
<@script type="text/javascript" src="${page.url.context}/res/components/wiki/parser.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/wiki/page.js"></@script>

<!-- Document Picker for Document links -->
<@script type="text/javascript" src="${page.url.context}/res/modules/document-picker/document-picker.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/object-finder/object-finder.js"></@script>

<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/document-picker/document-picker.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/object-finder/object-finder.css" />

<!-- Wiki Editor -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/simple-editor.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-editor.js"></@script>
<!-- Tag -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/taglibrary/taglibrary.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/taglibrary/taglibrary.js"></@script>
<!-- Wiki Versioning -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/wiki/revert-wiki-version.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/wiki/revert-wiki-version.js"></@script>