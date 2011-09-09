<#if translationData??>
   <#assign id = args.htmlid?html>
   <script type="text/javascript">
      YAHOO.util.Event.addListener(window, "load", function()
      {
         Alfresco.component.ManageTranslations = new function()
         {
            var myColumnDefs = [
               { key: "lang", label: "Language", sortable: true },
               { key: "name", label: "Name", sortable: true },
               { key: "action", label: "Action", sortable: false }
            ];

            this.myDataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get("${id}-languages"),
            {
               responseType: YAHOO.util.DataSource.TYPE_HTMLTABLE,
               responseSchema:
               {
                  fields: [
                     { key: "lang" },
                     { key: "name" },
                     { key: "action" }
                  ]
               }
            });

            this.myDataTable = new YAHOO.widget.DataTable("${args.htmlid}-markup", myColumnDefs, this.myDataSource);
          };
      });

      function nodeFormURL(nodeRef)
      {
         return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.replace(":/", "") + "/formprocessor";
      };

      function markAsInitialTranslation(locale)
      {
         Alfresco.util.Ajax.jsonRequest(
         {
            method: "post",
            url: nodeFormURL("${nodeRef}"),
            dataObj:
            {
               "prop_ws_language": locale
            },
            successCallback:
            {
               fn: function()
               {
                  window.location.reload();
               },
               scope: this
            }
         });

         return false;
      };
   </script>

   <style type="text/css" media="screen">
      .manage-translations h2
      {
         margin: 1em 0;
      }

      .manage-translations .status-banner
      {
         margin-top: 0.75em;
         padding: 0.5em 1em;
      }

      .manage-translations .status-banner span
      {
         background-repeat: no-repeat;
         line-height: 1.5em;
         padding-left: 20px;
      }

      .manage-translations .status-banner .info
      {
         background-image: url(../documentlibrary/images/info-16.png);
      }
   </style>

   <div class="manage-translations">

   <#if !translationData.translationEnabled>
      <div class="status-banner theme-bg-color-2 theme-border-4">
         <span>${msg("message.translations-not-enabled")}</span>
      </div>

      <#if translationData.locale??>
      <p>
         <a href="#" onclick="return markAsInitialTranslation('${translationData.locale?js_string}')">
            ${msg("message.mark-translation", translationData.localeName?html)}
         </a>
      </p>
      </#if>
   </#if>

      <h2>${msg("header.translations")}</h2>
      <div id="${id}-markup">
         <table id="${id}-languages">
            <thead>
               <tr>
                  <th>${msg("label.language")}</th>
                  <th>${msg("label.url")}</th>
                  <th>${msg("label.action")}</th>
               </tr>
            </thead>
            <tbody>
   <#list translationData.locales as locale>
               <tr>
                  <td title="${locale.id?html}">${locale.name?html}</td>
      <#if translationData.translations[locale.id]??>
               <#assign translation = translationData.translations[locale.id]>
                  <td>${translation.name?html}</td>
                  <td><a href="inline-edit?nodeRef=${translation.nodeRef}">${msg("button.edit")}</a></td>
      <#else>
                  <td>${msg("label.not-applicable")}</td>
                  <td>
         <#assign createContentURL>create-content?mimeType=text/html&name=${translationData.name?url}&translationOf=${nodeRef}&language=${locale.id?url}&itemKind=type&itemId=${translationData.type?url}</#assign>
         <#if translationData.translationEnabled>
            <#if translationData.parents[locale.id]??>
               <#assign parent = translationData.parents[locale.id]>
               <#assign orphan = !translationData.parent.allPresent>
                     <a href="${createContentURL}&destination=${parent.nodeRef}&orphan=${orphan?string}">${msg("button.create")}</a>
            <#else>
                     <a href="${createContentURL}&destination=${translationData.parentNodeRef}">${msg("button.create")}</a>
            </#if>
         <#else>
                     <a href="#" onclick="markAsInitialTranslation('${locale.id?js_string}')">${msg("message.mark-translation", locale.name?html)}</a>
         </#if>
      </#if>
                  </td>
               </tr>
   </#list>
            </tbody>
         </table>
      </div>
<#else>
      <h1>${msg("message.no-definitions")}</h1>
</#if>
   </div>
