<script type="text/javascript">
YAHOO.util.Event.addListener(window, "load", function() {
    YAHOO.example.EnhanceFromMarkup = new function() {
        var myColumnDefs = [
            {key:"lang",label:"Language",sortable:true},
            {key:"name",label:"Name", sortable:true},
            {key:"action",label:"Action", sortable:false},
        ];
   
        this.myDataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get("languages"),
         {
            responseType: YAHOO.util.DataSource.TYPE_HTMLTABLE,
            responseSchema : {
               fields: [{key:"lang"},
                  {key:"name"},
                  {key:"action"}
	           ]
            }
         });

        this.myDataTable = new YAHOO.widget.DataTable("${args.htmlid}-markup", myColumnDefs, this.myDataSource);
    };
});

function nodeFormURL(nodeRef) {
   return Alfresco.constants.PROXY_URI + "api/node/" +
        nodeRef.replace(":/","") + "/formprocessor";
};

function markAsInitialTranslation(locale) {
   Alfresco.util.Ajax.jsonRequest({
      method: "post",
      url: nodeFormURL("${nodeRef}"),
      dataObj: {
         "prop_ws_language": locale
      },
      successCallback: {
         fn: function() {
            location.reload();
         },
         scope: this
      }
   });

   return false;
};
</script>
<style type="text/css">h1{
font-family:Helvetica,Arial,sans-serif;
font-size:146.5%;
font-weight:normal;
margin:1em 0;
}

h2{
margin:1em 0;
}
</style>

<#if locales?has_content>
	<div class="rule-edit">
	<h1 class="create-header">Multilingual Manager</h1>

   <#if !translationEnabled>
      <h2>This document is not currently enabled for translations</h2>

      <#if currentLocale?has_content>
         <p><a href="#" onclick="return markAsInitialTranslation('${currentLocale}')">Mark this document as the 
            ${currentLocaleName} translation</a></p>
      </#if>
   </#if>

	<h2>Translations</h2>
	<div id="${args.htmlid}-markup">
		<table id="languages">
			<thead> 
				<tr>
					<th><strong>Language</strong></th>
					<th><strong>URL</strong></th>
					<th>Action</th>
				</tr>
			</thead>
  			<tbody>
			<#list locales as locale>
				<tr>
					<td title="${locale.id}">${locale.name}</td>
					<#if translations[locale.id]?has_content>
                  <#assign translation = translations[locale.id]>
						<td>${translation.name}</td>
						<td><a href="inline-edit?nodeRef=workspace://SpacesStore/${translation.nodeRef}">Edit</a></td>
					<#else>
						<td>N/A</td>

						<td>
                  <#if translationEnabled>
					      <#if parents[locale.id]?has_content>
                        <#assign parent = parents[locale.id]>
                        <#assign orphan = !parent.allPresent>
					   		<a href="create-content?mimeType=text/html&name=${name}&translationOf=${nodeRef}&language=${locale.id}&destination=${parent.nodeRef}&orphan=${orphan?string}&itemKind=type&itemId=${type}">Create</a>
   						<#else>
   							<a href="create-content?mimeType=text/html&name=${name}&translationOf=${nodeRef}&language=${locale.id}&destination=${parentNodeRef}&itemKind=type&itemId=${type}">Create</a>
						   </#if>					
					   <#else>
                     <a href="#" onclick="markAsInitialTranslation('${locale.id}')">Mark this as the ${locale.name} translation</a>
						</#if>					
					</#if>
   				</td>	
				</tr>
			</#list>
         </tbody>
		</table>
	</div>
	</div>
<#else>
<h1>There are no language definitions loaded - unable to load multilingual console.</h1>
</#if>
