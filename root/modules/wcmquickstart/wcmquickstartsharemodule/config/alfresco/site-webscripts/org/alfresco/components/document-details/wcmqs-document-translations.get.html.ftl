<script type="text/javascript">
YAHOO.util.Event.addListener(window, "load", function() {
    YAHOO.example.EnhanceFromMarkup = new function() {
        var myColumnDefs = [
            {key:"lang",label:"Language",sortable:true},
            {key:"name",label:"Name", sortable:true},
            {key:"action",label:"Action", sortable:false},
        ];
   
        this.myDataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get("languages"));
        this.myDataSource.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
        this.myDataSource.responseSchema = {
            fields: [{key:"lang"},
                    {key:"name"},
                    {key:"action"}
            ]
        };

        this.myDataTable = new YAHOO.widget.DataTable("markup", myColumnDefs, this.myDataSource,
                {}
        );
    };
});
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
	<h2>Translations</h2>
	<div id="markup">
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
                  <#assign tranlsation = translations[locale.id]>
						<td>${translation.name}</td>
						<td><a href="inline-edit?nodeRef=workspace://SpacesStore/${translation.id}">Edit</a></td>
					<#else>
						<td>N/A</td>

						<#if parents[locale.id]?has_content>
							<td>
								<a href="create-content?itemKind=type&sectionconfig=${parent.sectionConfig}&orderindex=${parent.orderIndex}&translationof=${noderef}&orphan=${parent.orphan}&localeId=${locale.id}<#if parentNodeRef??>&destination=workspace://SpacesStore/${parent.id}</#if>&name=${originalName}&itemId=ws:section&mode=create&submitType=json&formId=doclib-common&showCancelButton=true">Create</a>
							</td>
						<#else>
							<td>
<#--
								<a href="create-content?mimeType=text/html&orphan=${parent.orphan}&name=${originalName}&translationof=${noderef}&localeId=${locale.id}<#if parentNodeRef??>&destination=workspace://SpacesStore/${parent.id}</#if>&itemKind=type&itemId=${translation.type}">Create</a>
--> Create
							</td>	
						</#if>					
					</#if>
				</tr>
			</#list>
         </tbody>
		</table>
	</div>
	</div>
	<#--<#else>
		<h1>There are no translations available for this document</h1>
		<p><a href="?mode=createTranslation&nodeRef=${noderef}">Make this document multilingual</a></p>-->
	<#-- </#if> -->
<#else>
<h1>There are no language definitions loaded - unable to load multilingual console.</h1>
</#if>
