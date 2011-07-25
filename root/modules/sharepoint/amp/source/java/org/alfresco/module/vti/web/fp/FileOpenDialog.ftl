<!-- _lcid="1033" _version="11.0.5510" _dal="1" -->
<!-- _LocalBinding -->
<html dir="ltr">
<HEAD>
    <META Name="GENERATOR" Content="Microsoft SharePoint">
    <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8">
    <META HTTP-EQUIV="Expires" content="0">
    <link rel="stylesheet" href="${alfContext}/resources/css/main.css" type="text/css">
    <link rel="stylesheet" href="${alfContext}/resources/css/picker.css" type="text/css">
    <Title ID=onetidTitle>File Properties</Title>
    <script language="JavaScript">
       function checkScroll()
       {
          if (document.body.scrollHeight > document.body.offsetHeight || document.body.scrollWidth > document.body.offsetWidth)
             document.body.scroll="yes";
       }
    </script>
    <style type="text/css">
      .deselected {
         background-color: white;
      }
      .selected {
         background-color: #BBDDFF;
      }
    </style>
    <script type="text/javascript">
       var oldSelect = null;
       function selectrow(rowId) {
          var selectedRow = document.getElementById(rowId);
             if (oldSelect != null && selectedRow != oldSelect) {
                oldSelect.className = "deselected";
             }
             selectedRow.className = "selected";
             oldSelect = selectedRow;
       }
    </script>
    <script type="text/javascript">
       function changeStyle(id)
       {
          document.getElementById(id).style.cursor = "pointer";
          document.getElementById(id).style.textDecoration = "underline";
       }

       function revertStyle(id)
       {
          document.getElementById(id).style.cursor = "default";
          document.getElementById(id).style.textDecoration = "none";
       }
    </script>
</HEAD>

<BODY topmargin=5 leftmargin=5 scroll=no serverType=OWS onload="checkScroll()" onresize="checkScroll()">
   
   <table width="100%">
      <tr> <td width="100%" align="rigth"> <img src='${alfContext}/resources/images/logo/AlfrescoLogo200.png' width=200 height=58 alt="Alfresco" title="Alfresco"> </td> </tr>
   </table>
   
   <table ID="FileDialogViewTable" width="100%" class="recordSet" style="cursor: default;" cellspacing=0>
      <tr>
         <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortDocIcon" TITLE="${sort_by_type}" SORTINGFIELDS="RootFolder=${location}&SortField=DocIcon&SortDir=${DialogUtils.getSortDir("DocIcon", sortField, sort)}&View=FileDialog" onmouseover="changeStyle('diidSortDocIcon');" onmouseout="revertStyle('diidSortDocIcon');">
         ${type} <#if sortField.equals(DialogUtils.getSortFieldValue("DocIcon"))><img src='${alfContext}/resources/images/icons/<#if sort.equals(DialogUtils.getSortValue("Asc"))>sort_up.gif<#else>sort_down.gif</#if>' width='10' height='6' alt='' style='border-width:0px;'/> </#if></a></th>
         <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortBaseName" TITLE="${sort_by_name}" SORTINGFIELDS="RootFolder=${location}&SortField=BaseName&SortDir=${DialogUtils.getSortDir("BaseName", sortField, sort)}&View=FileDialog" onmouseover="changeStyle('diidSortBaseName');" onmouseout="revertStyle('diidSortBaseName');">
         ${name} <#if sortField.equals(DialogUtils.getSortFieldValue("BaseName"))><img src='${alfContext}/resources/images/icons/<#if sort.equals(DialogUtils.getSortValue("Asc"))>sort_up.gif<#else>sort_down.gif</#if>' width='10' height='6' alt='' style='border-width:0px;'/> </#if></a></th>
         <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortEditor" TITLE="${sort_by_modified_by}" SORTINGFIELDS="RootFolder=${location}&SortField=Editor&SortDir=${DialogUtils.getSortDir("Editor", sortField, sort)}&View=FileDialog" onmouseover="changeStyle('diidSortEditor');" onmouseout="revertStyle('diidSortEditor');">
         ${modified_by} <#if sortField.equals(DialogUtils.getSortFieldValue("Editor"))><img src='${alfContext}/resources/images/icons/<#if sort.equals(DialogUtils.getSortValue("Asc"))>sort_up.gif<#else>sort_down.gif</#if>' width='10' height='6' alt='' style='border-width:0px;'/> </#if></a></th>   
         <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortLast_x0020_Modified" TITLE="${sort_by_modified}" SORTINGFIELDS="RootFolder=${location}&SortField=Last_x0020_Modified&SortDir=${DialogUtils.getSortDir("Last_x0020_Modified", sortField, sort)}&View=FileDialog" onmouseover="changeStyle('diidSortLast_x0020_Modified');" onmouseout="revertStyle('diidSortLast_x0020_Modified');">
         ${modified} <#if sortField.equals(DialogUtils.getSortFieldValue("Last_x0020_Modified"))><img src='${alfContext}/resources/images/icons/<#if sort.equals(DialogUtils.getSortValue("Asc"))>sort_up.gif<#else>sort_down.gif</#if>' width='10' height='6' alt='' style='border-width:0px;'/> </#if></a></th>
        <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortCheckedOutTitle" TITLE="${sort_by_checked_out_to}" SORTINGFIELDS="RootFolder=${location}&SortField=CheckedOutTitle&SortDir=${DialogUtils.getSortDir("CheckedOutTitle", sortField, sort)}&View=FileDialog" onmouseover="changeStyle('diidSortCheckedOutTitle');" onmouseout="revertStyle('diidSortCheckedOutTitle');">
         ${checked_out_to} <#if sortField.equals(DialogUtils.getSortFieldValue("CheckedOutTitle"))><img src='${alfContext}/resources/images/icons/<#if sort.equals(DialogUtils.getSortValue("Asc"))>sort_up.gif<#else>sort_down.gif</#if>' width='10' height='6' alt='' style='border-width:0px;'/> </#if></a></th>
      </tr>

      <tr height="5">
         <td colspan="5"> </td>
      </tr>
   
   <#list items as item>
      <#if item.isFolder()>
         <tr class="recordSetRow"  fileattribute=folder ID="${scheme}://${host}${context}/${item.getPath()}" onmousedown="selectrow('${scheme}://${host}${context}/${item.getPath()}')">
            <td style="padding: 2px; text-align: left"><IMG BORDER=0 ALT="Icon" SRC="${alfContext}/resources/images/icons/space-icon-default-16.gif"></td>
            <td style="text-align: left">${item.getName()}</td>
            <td style="text-align: left">${item.getModifiedBy()}</td>
            <td style="text-align: left">${item.getModifiedTime()}</td>
            <td style="text-align: left">&nbsp;</td>
         </tr>                
      <#else>
         <tr class="recordSetRow" fileattribute=file ID="${scheme}://${host}${context}/${item.getPath()}" onmousedown="selectrow('${scheme}://${host}${context}/${item.getPath()}')">
            <td style="padding: 2px; text-align: left"><IMG BORDER=0 ALT="Icon" SRC="${alfContext}/resources/${DialogUtils.getFileTypeImage(item.getName())}"></td>
            <td style="text-align: left">${item.getName()}</td>
            <td style="text-align: left">${item.getModifiedBy()}</td>
            <td style="text-align: left">${item.getModifiedTime()}</td>
            <td style="text-align: left"><#if item.getCheckedOutTo() == "">&nbsp;<#else>${item.getCheckedOutTo()}</#if></td>
         </tr>
      </#if>             
   </#list>
   
</BODY>

</html>