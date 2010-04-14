$(document).ready(function() {
  $('.create_list').click(function() {
    var html = ''+ 
      '<form id="listform" method="get">' +
      ' List <input class="listname" type="text" name="listname"><br/>' +
      '<input type="submit" value="submit">'+
      '</form>';
    $('#dynamic').html(html);

    $('#listform').submit(function () {
      $.get('/createlist/' + $('.listname').val(), function (data) {
        $('#dynamic').html(data);
        tag_list();
      });
      return false;
    });
  });

  $('.manage_list').click(function() {
    $.get('/lists', function(data) {
      $('#dynamic').html(data);
    });
  });
  
  $('.shared_list').click(function() {
	  $.get("/requests", function(data) {
		  $('#dynamic').html(data);
		  
		  $('.accept').each(function(index) {
			  $(this).click(function(){
				  var key = $(this).attr('id').split('_')[1];
				  $.post('/acceptlist/' + key, function(data){
					  if(data == "success") {
						  $('#list_' + jquerySafe(key)).remove();
						  $('.success').html("Accepted Request").fadeIn(30).fadeOut(2000);
					  } else {
						  $('.error').html("You requested this one.").fadeIn(30).fadeOut(2000);
					  }
				  });  
			  });
		  });
		  
		  $('.deny').each(function(index){
			  $(this).click(function (){
				  var key = $(this).attr('id').split('_')[1];
				  $.post('/denyrequest/' + key, function(data){
					  $('#list_' + jquerySafe(key)).remove();
					  $('.success').html("Removed request").fadeIn(30).fadeOut(2000);
				  });
			  });
		  });
	  });
  });
  
  $('.edit').live('click', function() {
      var id = $(this).attr('id').split('_')[1];
      $.get('/list/' + id, function(innerdata) {
        $('#dynamic').html(innerdata);
        tag_list();
      });
  });

  $('.delete').live('click', function() {
      var id = $(this).attr('id').split('_')[1];
      $.post('/delete/' + id, function(innerdata) {
        $('.success').text(innerdata).fadeIn(30).fadeOut(2000);
        $('#list_'+id).remove();
      });
  });
  
  $('.share').live('click', function() {
		  var id = $(this).attr('id').split('_')[1];
		  $('.results').html('<form id="shareform">'+
		    'Share with <input id="user" type="text" name="user"><br/>' +
		    '<input type="submit" value="Share">' + 
		  '</form>');
		  $('#shareform').submit(function(){
			 var user = $("#user").val();
			 $.post("/sharelist/" + id + "/" + user, function(data){
				 $('.success').html(data).fadeIn(30).fadeOut(2000);
			 });
			 return false;
		  });
  });
  
  $('.merge_list').live('click', function() {
		  $('.action').hide();
		  $('.merge').show();
		  $('.process').click(function() {
			  var lists = $('input:checked');
			  if(lists.length < 2) {
				  alert('Must merge at least two lists!');
				  return;
			  }
			  var result = $(lists).map(function(){
				  return this.name.split('_')[1];
			  }).get().join('-');
			  $.post('/mergelist/' + result, function(data){
				  $('table').append(data);
				  clearMerge();
			  });
		  });
		  $('.stop').click(function() {
			  clearMerge();
		  });
  });
  
  $("input[name^='merge_']").live('change', function(){
	  var id = $(this).attr('name').split('_')[1];
	  var checked = $(this).attr('checked');
	  if(checked) {
		  $('#list_' + id).children().addClass('merged');
	  } else {
		  $('#list_' + id).children().removeClass('merged');
	  }
  });
});


function clearMerge() {
	$("input[name^='merge_']").each(function(index){
		  $(this).attr('checked', false);
		  $(this).change();
	});
	$('.merge').hide();
	$('.action').show();
}

function jquerySafe(string) {
	var possible = ['@', '.', '!', ':', '~', '#', ';', '&', '$', '^', '|', 
	                '=', '>' ,'(', ')', '[',']', "'", '"', '+', '*', ',', '/'];
	for (var i = 0; i < possible.length; i++) {
		var regexp = new RegExp('\\' + possible[i], 'gi');
		string = string.replace(regexp, '\\' + possible[i]);
	}
	return string;
}