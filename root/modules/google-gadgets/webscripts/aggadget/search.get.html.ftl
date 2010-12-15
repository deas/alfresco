<#assign returl=url.full?url>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>Alfresco Search</title> 
      <link rel="stylesheet" href="/alfresco/css/gg.css" TYPE="text/css"/>
      <style type="text/css">
div.rowEven
{
   padding: 2px;
}

div.rowOdd
{
   padding: 2px;
   background-color: #F1F7FD;
}

div.results
{
   padding: 4px 4px 6px 4px;
}

div.nodeActions
{
   float: right;
   padding: 0px 4px 0px 4px;
}
      </style>
      <script type="text/javascript" src="${url.context}/scripts/ajax/mootools.v1.11.js"></script>
      <script type="text/javascript">
window.onload = pageLoaded;

function pageLoaded()
{
   $("query").focus();
}

function search()
{
   var q = $("query").value;
   if (q.length != 0)
   {
      window.location.href = "${url.service}?q=" + q;
   }
}

function searchKeypress(e)
{
   var keycode;
   if (window.event) keycode = window.event.keyCode;
   else if (e) keycode = e.which;
   if (keycode == 13)
   {
      search();
   }
}
      </script>
   </head>
   <body>
      <div class="main">
         <div class="titlebar">
            Search:&nbsp;
            <input id="query" name="query" maxlength="256" style="width:120px" onkeyup="javascript:searchKeypress(event);" value="${s.searchTerms!""}"/>
            <input type="button" name="go" value="Go" style="font-weight:bold;width:auto" onclick="javascript:search();"/>
         </div>
         <#if s.results?exists>
            <#assign count=0>
            <#assign index=0>
            <div class="results">
               Showing page <b>${s.startPage+1}</b> of <b>${s.totalPages}</b> from <b>${s.totalResults}</b> results for <b>${s.searchTerms}</b>
            </div>
            <#list s.results as row>
               <#if (index>=s.startIndex) && (index<s.startIndex+s.itemsPerPage)>
               <#assign count=count+1>
               <#assign curl=url.serviceContext + row.serviceUrl>
               <div class="${(count%2=0)?string("rowEven", "rowOdd")}">
                  <div style="float:left;height:18px;padding:2px"><a href="${curl}" target="new"><img src="${url.context}${row.icon16}" border="0" alt="" title="${row.name?html}"/></a></div>
                  <div style="margin-left:18px;padding:2px">
                     <div class="nodeActions">
                        <a href="${url.serviceContext}/aggadget/details?id=${row.id}&returl=${returl}" title="Details"><img src="${url.context}/images/icons/View_details.gif" border="0" alt="Details" title="Details"/></a>
                        <#if row.hasPermission("Write") && row.isDocument>
                        <a href="${url.serviceContext}/aggadget/update?name=${row.name?url}&id=${row.id}&returl=${returl}" title="Update"><img src="${url.context}/images/icons/update.gif" border="0" alt="Update" title="Update"/></a>
                        </#if>
                        <#if row.hasPermission("Delete")>
                        <a href="${url.serviceContext}/aggadget/delete?name=${row.name?url}&id=${row.id}&returl=${returl}" title="Delete"><img src="${url.context}/images/icons/delete.gif" border="0" alt="Delete" title="Delete"/></a>
                        </#if>
                     </div>
                     <a href="${curl}" target="new">${row.name?html}</a>
                  </div>
               <#if row.properties.description??>
                  <div style="margin-left:18px;padding-bottom:2px">${row.properties.description?html}</div>
               </#if>
               </div>
               </#if>
               <#assign index=index+1>
            </#list>
            <div style="padding:2px;color:#aaaaaa">
               <#if (s.startPage > 0)><a href="${url.service}?q=${s.searchTerms}&sp=0&pp=${s.itemsPerPage}"></#if>First<#if (s.startPage > 0)></a></#if> |
               <#if (s.startPage > 0)><a href="${url.service}?q=${s.searchTerms}&sp=${s.startPage - 1}&pp=${s.itemsPerPage}"></#if>Prev<#if (s.startPage > 0)></a></#if> | 
               <#if (s.startPage+1 < s.totalPages)><a href="${url.service}?q=${s.searchTerms}&sp=${s.startPage +1}&pp=${s.itemsPerPage}"></#if>Next<#if (s.startPage+1 < s.totalPages)></a></#if> |
               <#if (s.startPage+1 < s.totalPages)><a href="${url.service}?q=${s.searchTerms}&sp=${s.totalPages -1}&pp=${s.itemsPerPage}"></#if>Last<#if (s.startPage+1 < s.totalPages)></a></#if>
            </div>
         <#else>
            <div class="results"><i>No results to display.</i></div>
         </#if>
      </div>
   </body>
</html>