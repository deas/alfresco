<#if nodeRef??>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.Events("${args.htmlid}").setOptions(
      {
         nodeRef: new Alfresco.util.NodeRef("${(page.url.args.nodeRef)?js_string}"),
         siteId: "${page.url.templateArgs.site!""}"
      }).setMessages(
         ${messages}
      );

   //]]></script>
   <#assign el=args.htmlid>

   <div id="${el}" class="events">
      <h2 class="thin dark">${msg("events.heading")}</h2>

      <div id="${el}-message" class="hidden"></div>

      <div id="${el}-completed" class="hidden">
         <div class="header">${msg("title.completedEvents")}</div>
         <ul id="${el}-completed-events" class="completed-events">
            <li id="${el}-completedEventTemplate" class="event completed">
               <div class="icons"></div>
               <div class="info">
                  <div class="field name">
                     <span class="value"></span>
                  </div>
                  <div class="field automatic">
                     <span class="value"></span>
                  </div>
                  <div class="field completed-at">
                     <span class="label">${msg("label.completedAt")}:</span>
                     <span class="value"></span>
                  </div>
                  <div class="field completed-by">
                     <span class="label">${msg("label.completedBy")}:</span>
                     <span class="value"></span>
                  </div>
               </div>
               <div class="buttons">
                  <span class="yui-button undo-button inline-button">
                     <span class="first-child">
                        <button type="button">${msg("button.undo")}</button>
                     </span>
                  </span>
               </div>
            </li>
         </ul>
      </div>

      <div id="${el}-incomplete" class="hidden">
         <div class="header">${msg("title.incompleteEvents")}</div>
         <ul id="${el}-incomplete-events" class="incomplete-events">
            <li id="${el}-incompleteEventTemplate" class="event incomplete">
               <div class="icons"></div>
               <div class="info">
                  <div class="field name">
                     <span class="value"></span>
                  </div>
                  <div class="field automatic">
                     <span class="value"></span>
                  </div>
                  <div class="field asof">
                     <span class="label">${msg("label.asOf")}:</span>
                     <span class="value"></span>
                  </div>
               </div>
               <div class="buttons">
                  <span class="yui-button complete-button inline-button">
                     <span class="first-child">
                        <button type="button">${msg("button.completeEvent")}</button>
                     </span>
                  </span>
               </div>
            </li>
         </ul>
      </div>

      <div id="complete-event-panel" class="events complete-event-panel">
         <div class="hd">${msg("title.completeEvent")}</div>
         <div class="bd">
            <form id="${el}-completeEvent-form">
               <input type="hidden" id="${el}-eventName" value="" />
               <div class="section">
                  <label for="${el}-completedAtDate">${msg("label.completedAt")}:</label>
               </div>
               <div class="section yui-g">
                  <div class="yui-u first">
                     <span id="${el}-completedAtContainer">
                        <input id="${el}-completedAtDate" type="text" name="completedAt" readonly="readonly" value="" disabled/>
                        <a id="${el}-completedAtPicker" class="completedAtPicker">&nbsp;</a>
                     </span>
                  </div>
                  <div class="yui-u overflow">
                     <span>
                        <input id="${el}-completedAtTime" name="completedAtTime" class="completedAtTime" value="" type="text" size="10" tabindex="0"/>
                     </span>
                  </div>
               </div>
               <div class="bdft">
                  <input type="submit" id="${el}-completeEvent-ok-button" value="${msg("button.ok")}" tabindex="0"/>
                  <input type="submit" id="${el}-completeEvent-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
               </div>
            </form>

         </div>
      </div>

   </div>
</#if>