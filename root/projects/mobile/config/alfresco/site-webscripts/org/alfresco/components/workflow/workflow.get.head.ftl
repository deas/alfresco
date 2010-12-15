<script type="text/javascript" src="${url.context}/js/thirdparty/spinningwheel.js"></script>
<script type="text/javascript" charset="utf-8" src="${url.context}/js/typeahead.js"></script>
<script type="text/javascript" charset="utf-8" src="${url.context}/js/finder.js"></script>
<script type="text/javascript" charset="utf-8" src="${url.context}/js/thirdparty/iscroll.js"></script>
<link rel="stylesheet" href="${url.context}/themes/${theme}/typeahead.css" type="text/css" charset="utf-8">
<script type="text/javascript" charset="utf-8">//<![CDATA[
   window.addEventListener('DOMContentLoaded',function()
      {
         App.initBehaviour(Mobile.util.TypeAhead.BEHAVIOUR_NAME);            
         App.registerBehaviour('datePicker',function(rootNode)
            {
               x$(rootNode).find('.datepicker').each(function(el) {
                  //get button value so we can reinstate if user clicks Clear
                  var defaultButtonText = document.getElementById('datePicker').value;
                  function openDate(date) {
                   var date = date || new Date();
                   var days = { };
                   var years = { };
                   //i18n
                   var months = { 1: 'Jan', 2: 'Feb', 3: 'Mar', 4: 'Apr', 5: 'May', 6: 'Jun', 7: 'Jul', 8: 'Aug', 9: 'Sep', 10: 'Oct', 11: 'Nov', 12: 'Dec' };
 
                   for( var i = 1; i < 32; i += 1 ) {
                     days[i] = i;
                   }

                   for( i = date.getFullYear(),end=date.getFullYear()+5; i < end; i++ ) {
                     years[i] = i;
                   }
                   var SpinningWheel = Mobile.thirdparty.SpinningWheel;
                   SpinningWheel.addSlot(years, 'right', date.getFullYear());
                   SpinningWheel.addSlot(months, '', date.getMonth()+1);
                   SpinningWheel.addSlot(days, 'right', date.getDate());
 
                   SpinningWheel.setCancelAction(function(e) { });
                   SpinningWheel.setDoneAction(function (e) { 
                     function padZeros(value) {
                       return (value<10) ? '0' + value : value;
                     }
                     var results = SpinningWheel.getSelectedValues().keys;
                      //add date to hidden field
      	             document.getElementById('date').value = results[0]+'/'+padZeros(results[1])+'/'+padZeros(results[2]);
      	             //add date to view (button)
      	             document.getElementById('datePicker').value = padZeros(results[2])+'/'+padZeros(results[1])+'/'+results[0];//i18n
                      
                   });
                   SpinningWheel.setClearAction(function (e) {
                      //clear hidden field
                      document.getElementById('date').value = '';
                      //clear button
      	             document.getElementById('datePicker').value = defaultButtonText
      	          });
                   SpinningWheel.open();
                  }

                  x$(el).on('click',function(e) {
                    openDate(new Date());
              
                  });
                });
            });
         App.initBehaviour('datePicker');
      }
   );
//]]></script>
