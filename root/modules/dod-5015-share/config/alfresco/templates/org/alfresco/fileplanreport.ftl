<#include "include/alfresco-template.ftl" />
<#import "import/alfresco-layout.ftl" as layout />
<@templateHeader />

<@templateBody>
<div id="bd">
   <@region id="fileplanreport" scope="template" protected=true />
   <script type="text/javascript">//<![CDATA[
       window.print();   
   //]]></script>
</div>
</@>
