$(document).ready(function() {
	$("input[name^='item_']").change(function(){
		var itemid = $(this).attr('id').split('_')[1];
		
		if($(this).attr('checked')) {
			$("#item_text_"+itemid).css("text-decoration", "line-through");
		} else {
			$("#item_text_"+itemid).css("text-decoration", "none");
		}
	});
});