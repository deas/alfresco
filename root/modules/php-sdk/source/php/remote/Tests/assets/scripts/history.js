schedule('history', 'attachHistoryBehaviour()');
function attachHistoryBehaviour() {
	var el = $('history');
	for(var i = 0; i < el.childNodes.length; i++) {
		if(el.childNodes[i].nodeName == 'LI') {
			Event.observe(el.childNodes[i], 'mouseover', hoverOption, false);
			Event.observe(el.childNodes[i], 'mouseout', endHoverOption, false);
			Event.observe(el.childNodes[i], 'click', chooseHistory, false);
		}
	}
	var toggleEl = $('testHistoryToggle').childNodes[0];
	Event.observe(toggleEl, 'click', toggleHistory, true);
	toggle($('testHistory'));
}

function toggleHistory(e) {
	toggle($('historyWrap'));
}

function toggle(el) {
	if(Element.hasClassName(el, 'closed')) {
		Element.removeClassName(el, 'closed');
	} else {
		Element.addClassName(el, 'closed');
	}
}

function hoverOption(e) {
	Element.addClassName(Event.element(e), 'hover');
}

function endHoverOption(e) {
	Element.removeClassName(Event.element(e), 'hover');
}

function chooseHistory(e) {
	var targ = Event.element(e);
	var el = $('history');
	for(var i = 0; i < el.childNodes.length; i++) {
		if(el.childNodes[i].nodeName == 'LI') Element.removeClassName(el.childNodes[i], 'active');
	}
	Element.addClassName(Event.element(e), 'active');
	$('testClasses').value = targ.innerHTML;
}