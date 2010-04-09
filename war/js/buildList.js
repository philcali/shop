function tag_list() {
    $(".add_item").click(function() {
    	$.post("/additem/" + $(".listid").val(), function(data) {
    		$('#items').prepend(data);
    		
    		// Grab the id that was just added
        	var itemid = $(data).attr('id').split('_')[1];
        	
            tag_input(itemid);
            tag_remove(itemid);
    	});
    });
    
    $('.item').each(function(index) {
    	var itemid = $(this).attr('id').split('_')[1];
    	tag_input(itemid);
    	tag_remove(itemid);
    });
    add();
}

function tag_input(itemid) {
	$("input[name$='_"+itemid+"']").focusout(function() {
		var name = $('#name_'+itemid).val();
		var qt = $('#qt_'+itemid).val();
		var price = $('#price_'+itemid).val();
		
		$.post('/edititem/'+itemid+'/'+name+'/'+qt+'/'+price, function(data) {
			add();
		});
    });
}

function tag_remove(itemid) {
	$('.remove_'+itemid).click(function() {
        // Remove item and subtract the price
		$.post('/deleteitem/' + itemid, function(data) {
			$('#item_'+itemid).remove();
	        add();
		});
    });
}

function add() {
    var total = 0.00;
    $("input[name^='price']").each(function(index) {
        var id = $(this).attr('name').split('_');
        var multiplier = parseInt($('#qt_'+id[1]).val());
        var add = parseFloat($('#price_'+id[1]).val());
        total += add * multiplier;
    });

    $('.total').text(total);
}
