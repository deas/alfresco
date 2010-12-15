<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>FDK Unit Tests</title>
      <meta http-equiv="X-UA-Compatible" content="Edge" />

      <style type="text/css" media="screen">
         @import "${url.context}/yui/reset-fonts-grids/reset-fonts-grids.css";
         @import "${url.context}/yui/assets/skins/default/skin.css";
         @import "${url.context}/res/fdk/fdk.css";
      </style>
      
      <script type="text/javascript" src="${url.context}/res/fdk/fdk.js"></script>
   </head>
   <body class="yui-skin-default">
      <div class="fdk">
         <div class="header">
            <img src="${url.context}/res/fdk/images/logo.png" />
            <span>FDK Unit Tests</span>
         </div>
         <div class="body">
            <div class="intro">
               <p>All the tests below require a valid NodeRef pointing to an instance
               of an fdk:everything type to successfully execute. When a test link is 
               clicked the test will load in the Form Console and present you with the form.</p>
               <p>Please enter the NodeRef you want to use below then click
               the test you want to run.</p>
            </div>
            <div class="item">
               <input type="text" id="noderef" name="noderef" <#if url.args.nodeRef??>value="${url.args.nodeRef}"</#if> />
            </div>
            <div class="tests">
               <div class="yui-g">
                  <div class="yui-u first">
                     <div class="test-container">
                        <div class="test-category">Field Data Types</div>
                        <div class="test-list">
                           <ul>
                              <li><a onclick="FDK.runUnitTest('text');">Text</a></li>
                              <li><a onclick="FDK.runUnitTest('numbers');">Numbers</a></li>
                              <li><a onclick="FDK.runUnitTest('datetime');">Date &amp; Time</a></li>
                              <li><a onclick="FDK.runUnitTest('boolean');">Boolean</a></li>
                              <li><a onclick="FDK.runUnitTest('associations');">Associations</a></li>
                              <li><a onclick="FDK.runUnitTest('tags-categories');">Tags &amp; Categories</a></li>
                              <li><a onclick="FDK.runUnitTest('content');">Content</a></li>
                           </ul>
                        </div>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="test-container">
                        <div class="test-category">Field Appearance</div>
                        <div class="test-list">
                           <ul>
                              <li><a onclick="FDK.runUnitTest('field-label');">Labels</a></li>
                              <li><a onclick="FDK.runUnitTest('field-tooltips');">Tooltips</a></li>
                              <li><a onclick="FDK.runUnitTest('field-help');">Help Text</a></li>
                              <li><a onclick="FDK.runUnitTest('field-read-only');">Read Only</a></li>
                              <li><a onclick="FDK.runUnitTest('field-mandatory');">Mandatory</a></li>
                              <li><a onclick="FDK.runUnitTest('field-info');">Info</a></li>
                           </ul>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="yui-g">
                  <div class="yui-u first">
                     <div class="test-container">
                        <div class="test-category">Sets</div>
                        <div class="test-list">
                           <ul>
                              <li><a onclick="FDK.runUnitTest('simple-sets');">Appearance</a></li>
                              <li><a onclick="FDK.runUnitTest('set-labels');">Labels</a></li>
                              <li><a onclick="FDK.runUnitTest('nested-sets');">Nested</a></li>
                           </ul>
                        </div>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="test-container">
                        <div class="test-category">Constraints</div>
                        <div class="test-list">
                           <ul>
                              <li><a onclick="FDK.runUnitTest('constraint-defaults');">Defaults</a></li>
                              <li><a onclick="FDK.runUnitTest('constraint-handlers');">Handlers</a></li>
                              <li><a onclick="FDK.runUnitTest('constraint-events');">Events</a></li>
                              <!--<li><a onclick="FDK.runUnitTest('constraint-messages');">Messages</a></li>-->
                           </ul>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="yui-g">
                  <div class="yui-u first">   
                     <div class="test-container">
                        <div class="test-category">Control Parameters</div>
                        <div class="test-list">
                           <ul>
                              <li><a onclick="FDK.runUnitTest('association-parameters');">association.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('authority-parameters');">authority.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('text-content-parameters');">content.ftl (Plain Text)</a></li>
                              <li><a onclick="FDK.runUnitTest('html-content-parameters');">content.ftl (HMTL)</a></li>
                              <li><a onclick="FDK.runUnitTest('number-parameters');">number.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('richtext-parameters');">richtext.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('selectone-parameters');">selectone.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('selectmany-parameters');">selectmany.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('textarea-parameters');">textarea.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('textfield-parameters');">textfield.ftl</a></li>
                              <li><a onclick="FDK.runUnitTest('styleClass-parameters');">styleClass</a></li>
                              <li><a onclick="FDK.runUnitTest('style-parameters');">style</a></li>
                              <li><a onclick="FDK.runUnitTest('startLocation-parameter');">startLocation</a></li>
                           </ul>
                        </div>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="test-container">
                        <div class="test-category">Miscellaneous</div>
                        <div class="test-list">
                           <ul>
                              <li><a onclick="FDK.runUnitTest('field-names');">Field Names</a></li>
                              <li><a onclick="FDK.runUnitTest('empty-config');">Empty Configuration</a></li>
                              <li><a onclick="FDK.runUnitTest('missing-config');">Missing Configuration</a></li>
                              <li><a onclick="FDK.runUnitTest('hide-fields');">Hide Fields</a></li>
                              <li><a onclick="FDK.runUnitTest('transient-fields');">Transient Fields</a></li>
                           </ul>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
   </body>
</html>