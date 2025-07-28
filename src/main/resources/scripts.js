function openExpanded(cell) {
	var w = window.open("", "", "left=10,top=10,height=800,width=800,menubar=no,toolbar=no,location=no,directories=no,status=no,resizable=no,scrollbars=yes");
	var txt = cell.getAttribute('full_value');

	w.document.body.innerHTML = "<pre>"+txt+"</pre>";
	w.focus();
}