<#if field.value?is_number><#assign progress=field.value?c><#else><#assign progress=0></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && !(field.value?is_number) && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <div class="progress-container">
            <div style="width: ${progress}%"><span>${progress}%</span></div>
         </div>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${progress}" />
      <div id="slider-bg" class="yui-h-slider" tabindex="-1"> 
         <div id="slider-thumb" class="yui-slider-thumb"><img src="${url.context}/yui/slider/assets/thumb-n.gif"></div>
      </div>
      
      <script type="text/javascript">
      (function() 
      {
         var Event = YAHOO.util.Event,
             Dom   = YAHOO.util.Dom,
             slider;
      
         Event.onDOMReady(function() 
         {
            slider = YAHOO.widget.Slider.getHorizSlider("slider-bg", "slider-thumb", 0, 200, 20);
      
            // set the slider to represent the current value
            slider.setValue(${progress} * 2, true);
            
            // sliders with ticks can be animated without YAHOO.util.Anim
            slider.animate = true;
      
            slider.getRealValue = function() 
            {
               return Math.round(this.getValue() * 0.5);
            }
      
            slider.subscribe("change", function(offsetFromStart) 
            {
               // use the scale factor to convert the pixel offset into a real value
               var actualValue = slider.getRealValue();
      
               // update the hidden field with the actual value
               Dom.get("${fieldHtmlId}").value = actualValue;
            });
         });
      })();
      </script>
   </#if>
</div>
